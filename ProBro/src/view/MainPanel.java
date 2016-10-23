package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import view.details.DetailsPanel;
import view.tree.Tree;
import view.workers.WorkersPanel;

/**
 * Main GUI panel (contains everything)
 * 
 * @author tweber
 *
 */
public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * (parent) JFrame instance
	 */
	public MainFrame frame;                       
	
	/**
	 * Right split panel, containing details and workers panels
	 */
	public JSplitPane rightPane;                  
	
	/**
	 * Details instance
	 */
	public DetailsPanel details;                  
	
	/**
	 * File tree instance
	 */
	public Tree tree;                             
	
	/**
	 * Workers panel instance
	 */
	public WorkersPanel workers;                  
	
	/** 
	 * Create the main GUI elements
	 * 
	 * @return
	 */
	public MainPanel(MainFrame frame) throws Throwable {
		super(new BorderLayout(3, 3));
		this.frame = frame;

		// Main border around all elements
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		// Create main panel instances. The tree instance holds the file tree, the details instance
		// holds the file table and file details.
		tree = new Tree(this);
		details = new DetailsPanel(this);
		workers = new WorkersPanel();
				

		// Bring the main panels together and add them to (this) main panel
		rightPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, details, workers);
		rightPane.setResizeWeight(1);
		JSplitPane allPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree.treeScroll, rightPane);
		add(allPane, BorderLayout.CENTER);
	}
	
	/**
	 * Reload file list and refresh GUI
	 * 
	 */
	public void refresh() throws Throwable {
		String current = details.getCurrentFile().getAbsolutePath();
		
		tree.initTree();
		tree.reload(); 
		
		tree.expandToPath(current);
		
		repaint();
	}
	
	/**
	 * En-/disable the tree
	 * 
	 */
	public void setActive(boolean active) throws Throwable {
		if (tree != null) tree.setEnabled(active);
	}
}
