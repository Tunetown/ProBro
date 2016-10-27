package view.stats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.sun.glass.events.KeyEvent;

import view.MainFrame;
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
	private MainFrame frame;
	
	public StatsDialog(MainFrame frame, ProjectDirEntry file) {
		super(frame, true);
		this.frame = frame;
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
		setLocationRelativeTo(frame);
		
		StatsDialog wrapper = this;
		getRootPane().registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				wrapper.setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		JPanel panel = new JPanel(new BorderLayout(3,3));
		add(panel);
		
		table = new JTable();
		tableModel = new StatsTableModel(file);
		table.setModel(tableModel);
		
		panel.add(table, BorderLayout.CENTER);
		tableModel.setCellSizes(this);
		
		JTableHeader header = table.getTableHeader();
		panel.add(header, BorderLayout.NORTH);
		
		// Do some size and location stuff
		pack();
		setLocationByPlatform(true);
		setMinimumSize(getSize());
		setVisible(true);

	}
	
	/**
	 * Set a column width in the table
	 * 
	 * @param column (column number)
	 * @param width (-1 for automatic width)
	 */
	public void setColumnWidth(int column, int width) throws Throwable {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		
		if (width < 0) {
			// use the preferred width of the header.
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			width = (int) preferred.getWidth() + 14;
		}
		
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}
}
