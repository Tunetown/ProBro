package view.stats;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;

import model.ProjectDirEntry;

/**
 * Dialog showing project list overview statistics in a simple table
 * 
 * @author tweber
 *
 */
public class StatsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTable table;
	private StatsTableModel tableModel;
	private ProjectDirEntry file;
	
	public StatsDialog(JFrame frame, ProjectDirEntry file) {
		super(frame, true);
		this.file = file;
	}
	
	/**
	 * Show the popup
	 * 
	 * @throws Throwable
	 */
	public void showStats() throws Throwable {
		init();
	}

	/**
	 * Initialize popup
	 * 
	 * @throws Throwable
	 */
	private void init() throws Throwable {
		table = new JTable();
		tableModel = new StatsTableModel(file);
		table.setModel(tableModel);
		add(table);
		
		// Do some size and location stuff
		pack();
		setLocationByPlatform(true);
		setMinimumSize(getSize());
		setVisible(true);

	}
}
