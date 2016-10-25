package view.stats;

import main.Main;
import main.Messages;
import javax.swing.table.AbstractTableModel;
import view.table.Table;

/** 
 * A TableModel to hold a DirEntry[] table. Shown above the file details panel. 
 * 
 */
public class StatsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * Number of columns (also maintain the column headers!)
	 */
	private int columnCount = 2;                                   
	
	/**
	 * We only set cell sizes once at the first call!
	 */
	private boolean cellSizesSet = false;                          
	
	public StatsTableModel() {}
	
	/**
	 * Defines the values shown in the table
	 * 
	 */
	@Override
	public Object getValueAt(int row, int column) {
		try {
			switch (column) {
			case 0:
				return "d" ; // TODO
			case 1:
				return "g"; // TODO
			default:
				System.err.println(Messages.getString("Table_Errorstring", column)); 
			}

		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
		return ""; 
	}

	/**
	 * Defines the column formats
	 * 
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		return String.class;
	}
	
	/**
	 * Set cell widths / heights on the table instance (implemented here to keep
	 * everything defining the table together)
	 * 
	 * @param table
	 */
	public void setCellSizes(Table table) throws Throwable {
		if (!cellSizesSet) {
			// Set width for all fixed columns
			table.setColumnWidth(0);
			table.setColumnWidth(1);

			cellSizesSet = true;
		}
	}

	/**
	 * Get number of columns
	 * 
	 */
	@Override
	public int getColumnCount() {
		return columnCount;
	}
	
	/**
	 * Get number of rows
	 * 
	 */
	@Override
	public int getRowCount() {
		return 10; // TODO
		/*try {
			if (dirEntry == null) return 0;
			return dirEntry.getChildren().size();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return 0;
		}*/
	}

	/**
	 * Returns (defines) the column header texts
	 * 
	 * @return
	 */
	@Override
	public String getColumnName(int column) {
		// TODO
		try {
			return Messages.getString("Table_ColumnHeader_" + column);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return "";
		} 
	}
}
