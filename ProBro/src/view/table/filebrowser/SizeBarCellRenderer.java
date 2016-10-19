package view.table.filebrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import main.Main;

/**
 * Class for rendering size bars in table cells
 * 
 * @author tweber
 *
 */
public class SizeBarCellRenderer extends JPanel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	// Custom colors
	private Color barColor = new Color(255, 160, 0);       // Color for the size bars
	private Color barBackColor = new Color(248,248,248);   // Color of bar background
	private Color barFrameColor = Color.LIGHT_GRAY;        // Color of bar frame

	// System colors
	private Color selColor = UIManager.getColor("Table.selectionBackground");
	private Color selFColor = UIManager.getColor("Table.selectionForeground");
	private Color tabColor = UIManager.getColor("Table.background");
	private Color tabFColor = UIManager.getColor("Table.foreground");

	/**
	 * SizeBar reference
	 */
	private SizeBar sizeBar = null;
	
	/**
	 * Is this line selected?
	 */
	private boolean isSelected = false;
	
	/**
	 * Teaser (not loaded)
	 */
	private JLabel teaser;
	
	public SizeBarCellRenderer() throws Throwable {
		super(new BorderLayout(0,0));
		setOpaque(true);
		teaser = new JLabel("(not loaded)");
		
		add(teaser, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object sizeBar, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			this.sizeBar = (SizeBar)sizeBar;
			this.isSelected = isSelected;
			
		} catch (Throwable t) {
			Main.handleThrowable(t);
		}
		return this;
	}
	
	/**
	 * Paint stuff
	 * 
	 */
	@Override
	public void paintComponent(Graphics g) {
		try {
			float size = sizeBar.getBarSize();
			if (size == -1) {
				// Not fully loaded: Show full light gray bar
				setBorder(BorderFactory.createEmptyBorder());
				
				if (!isSelected) {
					g.setColor(tabColor);
					teaser.setForeground(tabFColor);
					teaser.setEnabled(false);
				} else {
					g.setColor(selColor);
					teaser.setForeground(selFColor);
					teaser.setEnabled(true);
				}
				g.fillRect(0, 0, getWidth(), getHeight());
				
				teaser.setVisible(true);
			} else {
				teaser.setVisible(false);
				if (!isSelected) {
					setBorder(BorderFactory.createLineBorder(barFrameColor, 1));
					g.setColor(barBackColor);
				} else {
					setBorder(BorderFactory.createLineBorder(selColor, 1));
					g.setColor(selColor);
				}
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(barColor);
				g.fillRect(0, 0, (int)(size * getWidth()), getHeight());
			}
			
		} catch (Throwable t) {
			Main.handleThrowable(t);
		}
	}
}
