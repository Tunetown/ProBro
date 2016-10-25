package view.stats;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;

public class StatsFrame extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTable table;
	private StatsTableModel tableModel;
	
	public StatsFrame(JFrame frame) {
		super(frame, true);
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
		tableModel = new StatsTableModel();
		table.setModel(tableModel);
		add(table);
		
		// Do some size and location stuff
		pack();
		setLocationByPlatform(true);
		setMinimumSize(getSize());
		setVisible(true);

	}
}
