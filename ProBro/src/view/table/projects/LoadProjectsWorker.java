package view.table.projects;

import view.MainPanel;
import view.workers.CustomSwingWorker;
import main.Main;
import main.Messages;
import model.DirEntry;
import model.ProjectDirEntry;

/**
 * Worker thread class for loading projects information
 * 
 * @author tweber
 *
 */
public class LoadProjectsWorker extends CustomSwingWorker<Void, Void> {

	/**
	 * The file which started this worker thread
	 */
	public DirEntry rootFile; 
	
	public LoadProjectsWorker(MainPanel gui) throws Throwable {
		super(gui);
	}
	
	/**
	 * Load the data (wrapper)
	 * 
	 */
	@Override
	public Void doInBackground() {
		try {
			searchProjects();
			
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
	 * Recursively searches for projects in this folder, calling loadProjects on the ProjectsDirEntry instance rootFile
	 * 
	 * @throws Throwable 
	 * 
	 */
	private void searchProjects() throws Throwable {
		rootFile = gui.details.getCurrentFile(); 
		if (rootFile == null || !rootFile.isDirectory()) {
			return;
		}
		// Now we know the file name
		panel.update();

		((ProjectDirEntry)rootFile).loadProjects();
	}
	
	/**
	 * Finished loading of projects: Reactivate tree selection, and repaint the GUI.
	 * 
	 */
	@Override
	protected void done() {
		try {
			super.done();
			gui.setActive(true);

			gui.details.table.setTableData(gui.details.getCurrentFile());
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
			ret[0] = Messages.getString("LoadProjectsWorker.PreparingText"); //$NON-NLS-1$
			return ret;			
		} else {
			String[] ret = new String[2];
			ret[0] = Messages.getString("LoadProjectsWorker.SearchingText"); //$NON-NLS-1$
			ret[1] = rootFile.getName();
			return ret;
		}
	}
}
