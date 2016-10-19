package view.table.projects;

import main.Main;
import model.ProjectDirEntry;

/**
 * Class for representing year column in projects view
 * 
 * @author tweber
 *
 */
@SuppressWarnings("rawtypes")
public class ProjectYearCell implements Comparable {
	
	/**
	 * File whose size shall be showed
	 */
	private ProjectDirEntry file;
	
	public ProjectYearCell(ProjectDirEntry file) {
		this.file = file;
	}

	/**
	 * Returns the file instance
	 * 
	 * @return
	 */
	public ProjectDirEntry getFile() {
		return file;
	}
	
	/**
	 * Table sort comparator
	 * 
	 */
	@Override
	public int compareTo(Object o) {
		try {
			ProjectYearCell s = (ProjectYearCell)o;
			return file.getProjectYear().compareTo(s.getFile().getProjectYear());
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return 0;
		}
	}
}
