package view.table.projects;

import view.table.filebrowser.SizeBar;
import model.ProjectDirEntry;

/**
 * Class for representing size bars in table cells
 * 
 * @author tweber
 *
 */
public class ProjectSizeBar extends SizeBar {
	
	private ProjectDirEntry tabRoot;
	
	public ProjectSizeBar(ProjectDirEntry file, ProjectDirEntry tabRoot) throws Throwable {
		super(file);
		this.tabRoot = tabRoot;
		barSize = determineBarSize(); // Has to be re-called here, because in the super constructor call, tabRoot is not yet set
	}
	
	/**
	 * Calculates the bar size in [0..1]. Here, not the parent directory�s largest item is
	 * used as maximum, but the biggest project of the currently selected folder (details)
	 * 
	 * @return
	 */
	@Override
	protected float determineBarSize() throws Throwable {
		if (tabRoot == null) return -1;
		float parent = tabRoot.getLargestProject().getSize();
		float me = file.getSize();
		return me / parent;
	}
}
