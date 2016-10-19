package view.tree;

import main.Main;
import model.DirEntry;
import model.ProjectDirEntry;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import view.MainPanel;
import view.workers.TreeExpansionWorker;

/**
 * GUI model class for the file tree
 * 
 * @author tweber
 *
 */
public class Tree {

	/**
	 * Main panel reference
	 */
	private MainPanel gui;
	
	/**
	 * JTree reference
	 */
	public JTree tree;

	/**
	 * Initial width of the tree
	 */
	private int initialWidth = 300;     
	
	/**
	 * Scroll pane reference
	 */
	public JScrollPane treeScroll;
	
	/** 
	 * Tree model reference
	 */
	private DefaultTreeModel treeModel;
	
	/**
	 * Root node of the tree (holding the file system roots)
	 */
	private DefaultMutableTreeNode rootNode;
	
	/**
	 * List of file system roots
	 */
	private Collection<DirEntry> fileSystemRoots = new ArrayList<DirEntry>();
	
	/**
	 * List of nodes for each file system root
	 */
	private Collection<DefaultMutableTreeNode> fileSystemRootNodes = new ArrayList<DefaultMutableTreeNode>();

	public Tree(MainPanel gui) throws Throwable {
		this.gui = gui;
		
		// Build the tree from ground up: Start with the root and attach it
		// to the tree by its treeModel attribute
		rootNode = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(rootNode);

		// Add a selection listener which calls showChildren
		TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				try {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					
					// Show children
					showChildren(node);
					
				} catch (Throwable e) {
					Main.handleThrowable(e);
				}
			}
		};

		// Add all root files as initial nodes to the tree
		initTree();
		
		// Create the swing tree instance
		tree = new JTree(treeModel);

		tree.setRootVisible(false);
		tree.addTreeSelectionListener(treeSelectionListener);
		tree.setCellRenderer(new TreeCellRenderer());
		tree.expandRow(0);
		treeScroll= new JScrollPane(tree);
		tree.setVisibleRowCount(15);

		Dimension preferredSize = treeScroll.getPreferredSize();
		Dimension widePreferred = new Dimension(initialWidth, (int) preferredSize.getHeight());
		treeScroll.setPreferredSize(widePreferred);
	}

	/**
	 * Add all root files as initial nodes to the tree root node
	 * 
	 * @param root
	 */
	public void initTree() throws Throwable {
		rootNode.removeAllChildren();
		fileSystemRoots = new ArrayList<DirEntry>();
		fileSystemRootNodes = new ArrayList<DefaultMutableTreeNode>();
		
		// Add all file system roots as initial nodes to the tree
		File[] fileSystemRootFiles = File.listRoots();

		for (File rootFile : fileSystemRootFiles) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootFile);
			fileSystemRootNodes.add(node);
			rootNode.add(node);
			

			// Create an initial DirEntry Root for each if the file system roots
			ProjectDirEntry rootDirEntry = new ProjectDirEntry(rootFile.getAbsolutePath());
			fileSystemRoots.add(rootDirEntry);
			
			// Also expand the first level of all roots initially
			List<DirEntry> children = rootDirEntry.getChildren();
			if (children != null) {
				for (DirEntry child : children) {
					if (child.isDirectory()) {
						node.add(new DefaultMutableTreeNode(child));
					}
				}
			}
		}
	}
	
	/**
	 * Wrapper for tree setEnabled method
	 * 
	 * @param a
	 */
	public void setEnabled(boolean a) throws Throwable {
		tree.setEnabled(a);
	}
	
	/**
	 * See showChildren(.., ..)
	 * 
	 * @param node
	 */
	private void showChildren(final DefaultMutableTreeNode node) throws Throwable {
		showChildren(node, false);
	}
	
	/**
	 * Add the files that are contained within the directory of this node.
	 * 
	 * @param node
	 */
	private void showChildren(final DefaultMutableTreeNode node, boolean sync) throws Throwable {
		// Disable tree and show progress bar (this will be terminated inside 
		// the ProBroTreeLoader after finish)
		gui.setActive(false);
		
		// Create and launch the worker thread to load the new children to the tree
		TreeExpansionWorker worker = new TreeExpansionWorker(gui, node);
		if (!sync) {
			worker.execute();
		} else {
			worker.executeSync();
		}			
		
		// Update selected file to the table / details
		DirEntry n = null;
		try {
			n = (DirEntry) node.getUserObject();
			gui.details.setFileDetails(n);
		} catch (Throwable t) {
			// TODO selecting root does not work right now. 
		}
	}
	
	/**
	 * Expand to a given file path
	 * 
	 * @param path
	 */
	public void expandToPath(String path) throws Throwable {
		for (DefaultMutableTreeNode d : fileSystemRootNodes) {
			for (int num = 0; num < d.getChildCount(); num++) {
				if (d.getChildAt(num) instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode tn = (DefaultMutableTreeNode)d.getChildAt(num);
					DirEntry de = (DirEntry)tn.getUserObject();
					if (path.startsWith(de.getAbsolutePath())) {
						// Correct root: expand here
						expandToPath(tn, path);
					}
				}
			}
		}
	}
	
	/**
	 * Internal (recursive helper for expandTo)
	 * 
	 * @param node
	 * @param path
	 */
	private void expandToPath(DefaultMutableTreeNode node, String path) throws Throwable {
		if (path.equals(((DirEntry)node.getUserObject()).getAbsolutePath())) {
			showChildren(node, true);
			return;
		}

		// Trigger expansion thread
		showChildren(node, true);
		
		for (int num = 0; num < node.getChildCount(); num++) {
			if (node.getChildAt(num) instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(num);
				DirEntry d = (DirEntry)child.getUserObject();
				if (path.startsWith(d.getAbsolutePath())) {
					// Continue expanding
					expandToPath(child, path);
				}
			}
		}			
	}
	
	/**
	 * Reload the tree after changing its nodes
	 * 
	 */
	public void reload() throws Throwable {
		treeModel.reload();
	}

	/**
	 * Expand the tree to a specific node (tree only operation, no file system loading etc) 
	 *  
	 * @param node
	 */
	public void expandNode(DefaultMutableTreeNode node) throws Throwable {
		tree.expandPath(new TreePath(node.getPath()));	
	}

	/**
	 * Select a specific node (will trigger the selection event of the tree!)
	 * 
	 * @param node
	 */
	public void selectNode(DefaultMutableTreeNode node) throws Throwable {
		TreePath path = new TreePath(node.getPath());
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
	}

	/**
	 * Scroll the tree to a specific node
	 * 
	 * @param node
	 */
	public void scrollToNode(DefaultMutableTreeNode node) throws Throwable {
		TreePath path = new TreePath(node.getPath());
		tree.scrollPathToVisible(path);
	}
}
