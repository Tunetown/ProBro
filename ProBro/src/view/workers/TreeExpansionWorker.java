package view.workers;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import view.MainPanel;
import main.Main;
import main.Messages;
import model.DirEntry;

/**
 * Loader for the file tree. This is used in a background thread when a node is opened.
 * 
 * @author tweber
 *
 */
public class TreeExpansionWorker extends CustomSwingWorker<Void, DirEntry> {

	/**
	 * The node to expand
	 */
	private DefaultMutableTreeNode node;     
	
	public TreeExpansionWorker(MainPanel gui, DefaultMutableTreeNode node) throws Throwable {
		super(gui);
		this.node = node;
	}
	
	/**
	 * Main processing for node loading
	 * 
	 */
	@Override
	public Void doInBackground() {
		try {
			createNode();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
		return null;
	}

	/**
	 * Load a file node
	 * 
	 */
	private void createNode() throws Throwable {
		DirEntry file = (DirEntry)node.getUserObject();

		if (file.isDirectory()) {
			List<DirEntry> files = file.getChildren();
			
			panel.progressBar.setIndeterminate(false);
			panel.progressBar.setMaximum(files.size());
			
			if (node.isLeaf()) {
				panel.progressBar.setValue(0);
				
				int n=0;
				for (DirEntry child : files) {
					if (child.isDirectory()) {
						if (sync) {
							List<DirEntry> chunk = new ArrayList<DirEntry>();
							chunk.add(child);
							process(chunk);
						} else {
							publish(child);
						}
					}
					panel.progressBar.setValue(n);
					n++;
				}
			}
			gui.details.table.setTableData(file);
		}
	}

	/**
	 * Add nodes for files (called by the framework, perhaps in chunks)
	 * 
	 */
	@Override
	protected void process(List<DirEntry> chunks) {
		try {
			for (File child : chunks) {
				node.add(new DefaultMutableTreeNode(child));
			}
		} catch (Throwable t) {
			Main.handleThrowable(t);
		}
	}

	/**
	 * Finished loading of children
	 * 
	 */
	@Override
	protected void done() {
		try {
			super.done();
			
			gui.tree.expandNode(node);
			gui.tree.scrollToNode(node);
			
			gui.setActive(true);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}

	/**
	 * Text for GUI panel of the worker
	 * 
	 */
	@Override
	public String[] getOutputText() throws Throwable {
		String[] ret = new String[1];
		ret[0] = Messages.getString("TreeExpansionWorker.LoadingText");  //$NON-NLS-1$
		return ret;
	}
}
