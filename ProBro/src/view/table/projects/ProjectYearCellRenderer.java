package view.table.projects;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import main.Main;
import model.ProjectDirEntry;

/**
 * Class for rendering size bars in table cells
 * 
 * @author tweber
 *
 */
public class ProjectYearCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	// Custom colors
	private Color estimatedColor = new Color(255, 160, 0);             // Color for compressed (background) 
	private Color estimatedForegroundColor = new Color(0, 0, 0);       // Color for compressed (foreground)
	
	private Color selColor = UIManager.getColor("Table.selectionBackground");
	private Color selFColor = UIManager.getColor("Table.selectionForeground");
	private Color tabColor = UIManager.getColor("Table.background");
	private Color tabFColor = UIManager.getColor("Table.foreground");
	
	public ProjectYearCellRenderer() {}
	
	/**
	 * 
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object cell, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			ProjectDirEntry p = ((ProjectYearCell)cell).getFile();
			
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, p.getProjectYear(), isSelected, hasFocus, row, column);
			
			if (p.isProjectYearEstimated()) {
				l.setForeground(estimatedForegroundColor);
				l.setBackground(estimatedColor);
			} else {
				if (isSelected) {
					l.setForeground(selFColor);
					l.setBackground(selColor);				
				} else {
					l.setForeground(tabFColor);
					l.setBackground(tabColor);
				}
			}
			
			return l;
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return null;
		}
	}
}