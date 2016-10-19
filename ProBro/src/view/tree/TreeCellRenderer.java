package view.tree;
import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import main.Main;
import main.Utils;

/** 
 * A TreeCellRenderer for a File.
 *  
 */
class TreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * Labels for the node names
	 */
	private JLabel label;
	
	/**
	 * Helper to get nice folder icons
	 */
	private JFileChooser j = new JFileChooser();

	public TreeCellRenderer() throws Throwable {
		label = new JLabel();
		label.setOpaque(true);
	}

	/**
	 * 
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		try {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			File file = (File)node.getUserObject();
			
			if (file != null) label.setIcon(j.getUI().getFileView(j).getIcon(file)); 
			label.setText(Utils.getFileSystemView().getSystemDisplayName(file));
			
			if (file != null) label.setToolTipText(file.getPath());
	
			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}
	
			return label;
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return null;
		}
	}
}