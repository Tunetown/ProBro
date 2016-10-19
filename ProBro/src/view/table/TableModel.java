package view.table;

import javax.swing.table.AbstractTableModel;

import model.DirEntry;

/**
 * Abstract table model class. All table models in this application mus inherit from this.
 * 
 * @author xwebert
 *
 */
public abstract class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * Set the root file, whose children or whatever shall be shown on the table
	 * 
	 * @param file
	 */
	public abstract void setDirEntry(DirEntry file) throws Throwable;

	/**
	 * Set the cell sizes of all cells (only called once at initialization)
	 * 
	 * @param table
	 */
	public abstract void setCellSizes(Table table) throws Throwable;

	/**
	 * Returns the DirEntry representing the given row
	 * 
	 * @param row
	 * @return
	 */
	public abstract DirEntry getRowDirEntry(int row) throws Throwable;
	
	/**
	 * With this, the child classes can call their specific cell renderers
	 * 
	 */
	public abstract void setCellRenderers(Table table) throws Throwable;
}
