package view.workers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.Main;
import main.Messages;

/**
 * GUI model class for the workers panel. Also manages a list of workers and 
 * adding/removing workers from the list.
 * 
 * @author tweber
 *
 */
public class WorkersPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Initial workers panel width
	 */
	private int initialWidth = 200;
	
	/**
	 * Teaser: This is a label which is shown when no workers are present.
	 */
	private JPanel teaser;                   
	
	/**
	 * List of running workers
	 */
	@SuppressWarnings("rawtypes")
	private Collection<CustomSwingWorker> workers = new HashSet<CustomSwingWorker>();

	public WorkersPanel() throws Throwable {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize(new Dimension(initialWidth, -1)); 
		
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		
		// Create teser text which is shown when no worker threads are present
		teaser = new JPanel(new BorderLayout(3, 3));
		JLabel t = new JLabel(Messages.getString("WorkersPanel.NoRocessesRunning"), SwingConstants.CENTER);  //$NON-NLS-1$
		t.setEnabled(false);
		teaser.add(t, BorderLayout.CENTER);
		add(teaser);
	}
	
	/**
	 * Add a new worker thread instance to the workers panel. 
	 * Creates a JPanel for each worker (see WorkerPanel class).
	 * 
	 * @param w
	 */
	@SuppressWarnings("rawtypes")
	public void addWorker(CustomSwingWorker w) throws Throwable {
		// Add to queue
		workers.add(w);
		// Add to screen
		add(w.getPanel());
		
		revalidate();
		repaint();
	}
	
	/**
	 * Remove a worker thread from the queue
	 * 
	 * @param worker
	 */
	@SuppressWarnings("rawtypes")
	public void removeWorker(CustomSwingWorker w) throws Throwable {
		// Remove from queue
		workers.remove(w);
		// Remove from screen
		remove(w.getPanel());

		revalidate();
		repaint();
	}
	
	/**
	 * Returns all workers
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Collection<CustomSwingWorker> getWorkers() {
		return workers;
	}
	
	/**
	 * Returns all workers of a given class type
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Collection<CustomSwingWorker> getWorkers(Class c) throws Throwable {
		Collection<CustomSwingWorker> ret = new HashSet<CustomSwingWorker>();
		for(CustomSwingWorker w : workers) {
			if (w.getClass() == c) {
				ret.add(w);
			}
		}
		return ret;		
	}

	/**
	 * Before updating, also shows or hides the teaser panel
	 * 
	 */
	@Override
	public void repaint() {
		try {
			if (teaser != null) teaser.setVisible(workers.size() == 0);
			super.repaint();
			
		} catch (Throwable t) {
			Main.handleThrowable(t);
		}
	}
}
