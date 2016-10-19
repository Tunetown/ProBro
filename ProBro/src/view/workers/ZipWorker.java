package view.workers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.swing.JOptionPane;

import view.MainPanel;
import main.Main;
import main.Messages;
import model.DirEntry;

/**
 * Worker class for zipping files/folders.
 * 
 * @author tweber
 *
 */
public class ZipWorker extends CustomSwingWorker<Void, Void> {

	/**
	 * The file which started this worker thread
	 */
	public DirEntry rootFile; 
	
	/**
	 * Reference to the shell process
	 */
	private Process process;
	
	/**
	 * This works in two states: 
	 * 
	 * 	0: Fully load deep data (incl. hidden files); 
	 * 	1: ZIP file
	 * 
	 * When started, first the deep data of the folder/file is loaded to provide progress information later (state 0).
	 * Then, the ZIP command is called in the shell (state 1) providing progress information by evaluating the console 
	 * output of the shell command.
	 */
	private int state = 0;   
	
	public ZipWorker(MainPanel gui) throws Throwable {
		super(gui, true, true);
	}
	
	/**
	 * Zip the folder/file (wrapper)
	 * 
	 */
	@Override
	public Void doInBackground() {
		try {
			zip();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}		
		return null;
	}

	/**
	 * Show ZIP file on finish
	 * 
	 */
	@Override
	public void closeAFterFinish() {
		try {
			gui.tree.expandToPath(rootFile.getParentDirEntry().getAbsolutePath());
			gui.details.setFileDetails(rootFile);
			gui.workers.removeWorker(this);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}
	
	/**
	 * Zip the folder/file
	 * 
	 * @throws Throwable 
	 * 
	 */
	private void zip() throws Throwable {
		rootFile = gui.details.getCurrentFile();
		if (rootFile == null) {
			return;
		}
		panel.update();
		
		if (!rootFile.exists()) {
			JOptionPane.showMessageDialog(null, Messages.getString("ZipWorker.MsgFileNotFound", rootFile), Messages.getString("ZipWorker.ErrorTitle"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$ 
			return;
		}
		
		// Get number of files and folders (including hidden files) for 
		// initialization of the progress bar. First we need a clone of rootFile which
		// also loads hidden files!
		DirEntry tmp = new DirEntry(rootFile.getAbsolutePath(), null, true);
		List<DirEntry> first = tmp.getChildren();

		if (rootFile.isDirectory()) {
			panel.progressBar.setIndeterminate(false);
			panel.progressBar.setMaximum(first.size());
			panel.progressBar.setValue(0);
	
			for (int num = 0; num < first.size(); num++) {
				first.get(num).loadFully();
				panel.progressBar.setValue(num+1);
				gui.details.repaint();
			}
			tmp.loadFully();

			// Now, zip the file/folder (the progress bar starts again)
			panel.progressBar.setMaximum((int)(tmp.getNumOfFiles() + tmp.getNumOfFolders()));
			panel.progressBar.setValue(0);
		}
		
		state = 1;
		panel.update();
		
		System.out.println("Zipping file " + rootFile.getAbsolutePath()); //$NON-NLS-1$
		
		process = rootFile.zip();
		
		Reader inStreamReader = new InputStreamReader(process.getInputStream());
	    BufferedReader in = new BufferedReader(inStreamReader);

	    String line;
	    while((line = in.readLine()) != null) {
	        // A new line arrived: Analyze it!
	    	parseProcessOutput(line);
	    	
	    	if (isKilled()) {
	    		System.out.println("Killed zipping for " + rootFile.getAbsolutePath()); //$NON-NLS-1$
	    		process.destroy();
	    		break;
	    	}
	    }
	    in.close();
	    process.waitFor(); 
	    process = null;
	    System.out.println("Finished zipping for " + rootFile.getAbsolutePath()); //$NON-NLS-1$
	}
	
	/**
	 * Evaluate one line of console output from ZIP command
	 * 
	 * @param line
	 */
	private void parseProcessOutput(String line) throws Throwable {
		System.out.println(line);
		
		if (line.contains("adding") || line.contains("updating")) { //$NON-NLS-1$ //$NON-NLS-2$
			// One more file or folder added -> update progress bar and text 
			panel.progressBar.setValue(panel.progressBar.getValue() + 1);
			return;
		} 
		if (line.contains("name not matched")) { //$NON-NLS-1$
			// Error: File not found
			JOptionPane.showMessageDialog(null, Messages.getString("ZipWorker.CouldNotFindFile", rootFile.getAbsolutePath()), Messages.getString("ZipWorker.ErrorTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-2$
			return;
		}	
	}
	
	/**
	 * Finished zipping: Refresh file list
	 * 
	 */
	@Override
	protected void done() {
		try {
			super.done();
			
			// Update file list
			gui.refresh();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}

	/**
	 * Output text for workers GUI panel
	 * 
	 */
	@Override
	public String[] getOutputText() throws Throwable {
		if (state == 0) {
			if (rootFile == null) {
				String[] ret = new String[1];
				ret[0] = Messages.getString("ZipWorker.PrearingText");  //$NON-NLS-1$
				return ret;			
			} else {
				String[] ret = new String[2];
				ret[0] = Messages.getString("ZipWorker.LoadingText");  //$NON-NLS-1$
				ret[1] = rootFile.getName();  
				return ret;
			}
		} else {
			String[] ret = new String[2];
			ret[0] = Messages.getString("ZipWorker.ZippingText");  //$NON-NLS-1$
			ret[1] = rootFile.getName();  
			return ret;
		}
	}
}
