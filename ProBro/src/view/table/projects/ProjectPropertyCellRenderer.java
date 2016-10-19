package view.table.projects;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import main.Main;
import model.ProjectProperty;
import model.ProjectDirEntry;
import model.ProjectPropertyExtension;

/**
 * Class for rendering size bars in table cells
 * 
 * @author tweber
 *
 */
public class ProjectPropertyCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	private Color selColor = UIManager.getColor("Table.selectionBackground");
	private Color selFColor = UIManager.getColor("Table.selectionForeground");
	private Color tabColor = UIManager.getColor("Table.background");
	private Color tabFColor = UIManager.getColor("Table.foreground");

	/**
	 * 
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object cell, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			ProjectPropertyValue cl = (ProjectPropertyValue)cell;
			ProjectProperty c = cl.getProjectProperty();
			//ProjectDirEntry folder = c.getTarget();
	
			// Get output string
			String out = cl.getOutput();
			
			// Get the desired background color 
			Color bgCol = getBgColor(c, isSelected);

			// Create JLabel from default cell renderer
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, out, isSelected, hasFocus, row, column);
			

			if (isSelected) {
				l.setForeground(selFColor);
				l.setBackground(selColor);
			} else {
				l.setBackground(bgCol);
				l.setForeground(tabFColor);
			}
			
			return l;
		} catch (Throwable t) {
			Main.handleThrowable(t);
			return null;
		}		
	}

	/**
	 * Returns the background color for the given set of found files
	 * 
	 * @param matching
	 * @return
	 * @throws Throwable 
	 */
	private Color getBgColor(ProjectProperty c, boolean isSelected) throws Throwable {
		if (c == null) return tabColor;

		// Get the base color (defined for the file or folder)
		Color baseColor;
		if (c.getTargetProperty() != null && c.getTargetProperty().getBgColor() != null) {
			baseColor = c.getTargetProperty().getBgColor();		
		} else {
			baseColor = tabColor;			
		}

		// Get the extension which this file has triggered
		List<ProjectDirEntry> files = c.getMatchingFiles();
		if (files == null || files.size() == 0) return baseColor;
		
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (ProjectDirEntry file : files) {
			ProjectPropertyExtension e = c.getMatchingExtension(file);
			if (e.getBgColor() != null) {
				r += e.getBgColor().getRed();
				g += e.getBgColor().getGreen();
				b += e.getBgColor().getBlue();
			} else {
				if (isSelected) {
					r += this.selColor.getRed();
					g += this.selColor.getGreen();
					b += this.selColor.getBlue();
				} else {
					r += baseColor.getRed();
					g += baseColor.getGreen();
					b += baseColor.getBlue();			
				}
			}
		}
		return new Color(r / files.size(), g / files.size(), b / files.size());
	}
}