package view.table.filebrowser;

import main.Main;
import model.DirEntry;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Class for rendering readable sizes in table cells
 * 
 * @author tweber
 *
 */
public class ReadableSizeCellRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * File whose size shall be showed
	 */
	private DirEntry file = null;
	
	// Colors
	private Color selColor = UIManager.getColor("Table.selectionBackground");
	private Color selFColor = UIManager.getColor("Table.selectionForeground");
	private Color tabColor = UIManager.getColor("Table.background");
	private Color tabFColor = UIManager.getColor("Table.foreground");

	public ReadableSizeCellRenderer() throws Throwable {
		this.setOpaque(true);
		this.setBorder(new EmptyBorder(0,4,0,0)); // Set margin at left, next to the size bar
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object size, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			this.file = ((ReadableSize)size).getFile();
			this.setText(file.getReadableSize());
			
			if (!isSelected) {
				this.setBackground(tabColor);
				this.setForeground(tabFColor);
				this.setEnabled(file.isFullyLoaded() ? true : false);
			} else {
				this.setBackground(selColor);
				this.setForeground(selFColor);
				this.setEnabled(true);
			}
		
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
		return this;
	}
}
