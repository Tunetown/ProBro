package view.table.filebrowser;

import main.Main;
import main.Messages;
import main.Utils;
import model.DirEntry;

import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import view.table.Table;
import view.table.TableModel;

/** 
 * A TableModel to hold a DirEntry[] table. Shown above the file details panel. 
 * 
 */
public class FileBrowserTableModel extends TableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * Number of columns (also maintain the column headers!)
	 */
	private int columnCount = 9;                                   
	
	/**
	 * Padding for icons
	 */
	private int rowIconPadding = 6;                                
	
	/**
	 * Fixed width of the file size column
	 */
	private int sizeColumnWidth = 90;                              
	
	/**
	 * Fixed width of the type column
	 */
	private int typeColumnWidth = 35;                              
	
	/**
	 * Fixed with of the file size column  
	 */
	private int fileColumnWidth = 200;                             
	
	/**
	 * Max. width of file size column
	 */
	private int fileColumnMaxWidth = 600;                          

	/**
	 * Current File list
	 */
	private DirEntry dirEntry = null;                              
	
	/**
	 * We only set cell sizes once at the first call!
	 */
	private boolean cellSizesSet = false;                          
	
	/**
	 * Utility for getting nice file icons
	 */
	private JFileChooser j = new JFileChooser();
	
	public FileBrowserTableModel() {}
	
	/**
	 * Defines the values shown in the table
	 * 
	 */
	@Override
	public Object getValueAt(int row, int column) {
		try {
			if (dirEntry == null) {
				return null;
			}
			
			DirEntry file = dirEntry.getChildren().get(row);
	
			switch (column) {
			case 0:
				return j.getUI().getFileView(j).getIcon(file);  
			case 1:
				return Utils.getFileSystemView().getSystemDisplayName(file);
			case 2:
				return new SizeBar(file); 
			case 3:
				return new ReadableSize(file);
			case 4:
				return file.getType(); 
			case 5:
				return file.lastModified();
			case 6:
				return file.canRead();
			case 7:
				return file.canWrite();
			case 8:
				return file.canExecute();
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
		switch (column) {
		case 0:
			return ImageIcon.class;
		case 2:
			return SizeBar.class;
		case 3:
			return ReadableSize.class;
		case 4:
			return String.class;
		case 5:
			return Date.class;
		case 6:
		case 7:
		case 8:
			return Boolean.class;
		}
		
		return String.class;
	}
	
	/**
	 * Set cell widths / heights on the table instance (implemented here to keep
	 * everything defining the table together)
	 * 
	 * @param table
	 */
	@Override
	public void setCellSizes(Table table) throws Throwable {
		if (!cellSizesSet) {
			Icon icon = getIcon(0);

			// size adjustment to better account for icons
			if (icon != null) table.setRowHeight(icon.getIconHeight() + rowIconPadding);

			// Set width for all fixed columns
			table.setColumnWidth(0);
			table.setColumnWidth(1, fileColumnWidth);
			table.getColumnModel().getColumn(1).setMaxWidth(fileColumnMaxWidth);
			table.setColumnWidth(3, sizeColumnWidth);
			table.setColumnWidth(4, typeColumnWidth);
			table.setColumnWidth(5);
			table.setColumnWidth(6);
			table.setColumnWidth(7);
			table.setColumnWidth(8);

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
		try {
			if (dirEntry == null) return 0;
			return dirEntry.getChildren().size();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return 0;
		}
	}

	/**
	 * Get the DirEntry instance representing a specific row
	 * 
	 * @param row
	 * @return
	 */
	@Override
	public DirEntry getRowDirEntry(int row) {
		try {
			if (dirEntry == null) return null;
			return dirEntry.getChildren().get(row);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return null;
		}
	}

	/**
	 * Set a new DirEntry whose children shall be shown in the table
	 * 
	 * @param d
	 */
	@Override
	public void setDirEntry(DirEntry d) {
		try {
			this.dirEntry = d;
			fireTableDataChanged();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}	
	}

	/**
	 * Returns the file icon of a specific row
	 * 
	 * @param row
	 * @return
	 */
	public Icon getIcon(int row) throws Throwable {
		if (dirEntry == null || dirEntry.getChildren().size() == 0) return null;
		return Utils.getFileSystemView().getSystemIcon(dirEntry.getChildren().get(row));
	}

	/**
	 * Returns (defines) the column header texts
	 * 
	 * @return
	 */
	@Override
	public String getColumnName(int column) {
		try {
			return Messages.getString("Table_ColumnHeader_" + column);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return "";
		} 
	}

	/**
	 * No renderers to set here (all renderers are class-specific, set in table instance)
	 * 
	 */
	@Override
	public void setCellRenderers(Table table) throws Throwable {}
}
