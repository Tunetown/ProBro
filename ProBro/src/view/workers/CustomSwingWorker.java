package view.workers;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import view.MainPanel;
import main.Main;

/**
 * Worker base class for workers which should be shown in the worker panel.
 * 
 * @author xwebert
 *
 * @param <T>
 * @param <V>
 */
public abstract class CustomSwingWorker<T, V> extends SwingWorker<T, V> {
	
	/**
	 * Main panel reference
	 */
	protected MainPanel gui;
	
	/**
	 * The GUI panel for this worker
	 */
	protected WorkerPanel panel;            
	
	/**
	 * Executed synchronously? If yes, the caller has to call executeSync() 
	 * instead of running the worker with execute() in another Thread.
	 */
	protected boolean sync = false;          
	
	/**
	 * -1: Can not be killed, 0: Can be killed, 1: is killed and just waiting 
	 * for termination (must be implemented in child classes if used)
	 */
	private int kill = -1;                   
	
	/**
	 * Do not remove the worker when finished
	 */
	private boolean stayAfterFinish = false; 

	public CustomSwingWorker(MainPanel gui) throws Throwable {
		this(gui, false, false);
	}

	public CustomSwingWorker(MainPanel gui, boolean canBeKilled) throws Throwable {
		this(gui, canBeKilled, false);
	}

	public CustomSwingWorker(MainPanel gui, boolean canBeKilled, boolean stayAfterFinish) throws Throwable {
		this.gui = gui;
		this.stayAfterFinish = stayAfterFinish;
		this.panel = new WorkerPanel(this);
		
		if (canBeKilled) kill = 0;
		
		// Automatically register this to the workers queue
		gui.workers.addWorker(this);
	}

	/**
	 * Execute the worker synchronously without a new thread
	 * 
	 * @throws Exception
	 */
	public void executeSync() throws Throwable {
		this.sync = true;
		doInBackground();
		done();
	}
	
	/** 
	 * Cancel the worker.
	 * 
	 * @return 
	 * 
	 */
	public void cancel() throws Throwable {
		if (kill == 0) kill = 1;
		panel.update();
	}
	
	/**
	 * Is the worker killed and just waiting for termination?
	 * 
	 */
	public boolean isKilled() {
		if (kill == -1) return false;
		return (kill == 1);
	}
	
	/**
	 * Can this worker be killed?
	 * 
	 * @return
	 */
	public boolean canBeKilled() {
		return !(kill == -1);
	}

	/**
	 * Finish routine (implement in child classes if necessary)
	 * 
	 */
	public void closeAFterFinish() throws Throwable {}
	
	/**
	 * Returns the text lines for the process queue list on the GUI. Can be changed, but if done, 
	 * you have to call updateText() on the GUI panel instance (attribute panel) afterwards!
	 * 
	 * @return
	 */
	public abstract String[] getOutputText() throws Throwable;
	
	/**
	 * Remove the worker from the worker list when finished
	 * 
	 */
	@Override
	protected void done() {
		try {
			panel.progressBar.setIndeterminate(false);
			panel.progressBar.setMaximum(100);
			panel.progressBar.setValue(100);
				this.panel.update();
			if (!stayAfterFinish) gui.workers.removeWorker(this);
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}
	
	/**
	 * Returns the GUI panel instance
	 * 
	 * @return
	 */
	public JPanel getPanel() {
		return panel;
	}
}
