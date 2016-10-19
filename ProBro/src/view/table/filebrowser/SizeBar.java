package view.table.filebrowser;

import main.Main;
import model.DirEntry;

/**
 * Class for representing size bars in table cells
 * 
 * @author tweber
 *
 */
@SuppressWarnings("rawtypes")
public class SizeBar implements Comparable {
	
	/**
	 * File whose size shall be showed
	 */
	protected DirEntry file;
	
	/**
	 * Bar size in [0..1]
	 */
	protected float barSize;     
	
	public SizeBar(DirEntry file) throws Throwable {
		this.file = file;
		barSize = determineBarSize();
	}
	
	/**
	 * Returns the bar size in [0..1]
	 * 
	 * @return
	 */
	public float getBarSize() {
		return barSize;
	}
	
	/**
	 * Calculates the bar size in [0..1]
	 * 
	 * @return
	 */
	protected float determineBarSize() throws Throwable {
		if (file.getParentDirEntry() == null || !file.getParentDirEntry().isFullyLoaded()) return -1;
		float parent = file.getParentDirEntry().getLargestChild().getSize();
		float me = file.getSize();
		return me / parent;
	}

	/**
	 * Table sort comparator
	 * 
	 */
	@Override
	public int compareTo(Object o) {
		try {
			SizeBar s = (SizeBar)o;
			return (barSize > s.barSize) ? 1 : -1;
			
		} catch (Throwable t) {
			Main.handleThrowable(t);
			return 0;
		}
	}
}
