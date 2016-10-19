package view.workers;

import java.util.List;

import view.MainPanel;
import main.Main;
import main.Messages;
import model.DirEntry;

/**
 * Worker class implementing the deep loading of folders. This is used every time the
 * user loads deep data of a folder.
 * 
 * @author tweber
 *
 */
public class LoadFullyWorker extends CustomSwingWorker<Void, Void> {

	/**
	 * The file which started this worker thread
	 */
	public DirEntry rootFile; 
	
	public LoadFullyWorker(MainPanel gui) throws Throwable {
		super(gui, true);
	}
	
	/**
	 * Load the data (wrapper)
	 * 
	 */
	@Override
	public Void doInBackground() {
		try {
			loadFully();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}		
		return null;
	}

	/**
	 * Just stop the thread
	 * 
	 */
	@Override
	public void cancel() {
		try {
			super.cancel();        // Cancel in subclass
			super.cancel(true);    // Cancel in SwingWorker (Thread kill)
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}        
	}
	
	/**
	 * Load the data. This is done child per child to be able to provide
	 * progress information and update the progress bar of the worker meaningfully.
	 * 
	 * @throws Throwable 
	 * 
	 */
	private void loadFully() throws Throwable {
		rootFile = gui.details.getCurrentFile(); 
		if (rootFile == null) {
			return;
		}
		// Now we know the file name
		panel.update();

		// We process every child separately to get progress information
		List<DirEntry> first = rootFile.getChildren();
		
		panel.progressBar.setIndeterminate(false);
		panel.progressBar.setMaximum(first.size());
		panel.progressBar.setValue(0);
		
		for (int num = 0; num < first.size(); num++) {
			if (isKilled()) return;
			first.get(num).loadFully();
			panel.progressBar.setValue(num+1);
			gui.details.repaint();
		}
		
		// Finally, load the selected file. This won´t take as long as 
		// before because the children are already all fully loaded by now.
		rootFile.loadFully();
	}
	
	/**
	 * Finished loading of children: Reactivate tree selection, and repaint the GUI.
	 * 
	 */
	@Override
	protected void done() {
		try {
			super.done();
			
			gui.setActive(true);
			
			gui.details.repaint();
			gui.repaint();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}

	/**
	 * Output text for GUI workers queue
	 * 
	 */
	@Override
	public String[] getOutputText() throws Throwable {
		if (rootFile == null) {
			String[] ret = new String[1];
			ret[0] = Messages.getString("LoadFullyWorker.PreparingText");  //$NON-NLS-1$
			return ret;			
		} else {
			String[] ret = new String[2];
			ret[0] = Messages.getString("LoadFullyWorker.LoadingText");  //$NON-NLS-1$
			ret[1] = rootFile.getName();
			return ret;
		}
	}
}
