package view.table.projects;

import main.Main;
import main.Messages;
import main.Utils;
import model.DirEntry;
import model.ProjectDirEntry;

import java.text.SimpleDateFormat;

import javax.swing.Icon;

import view.table.Table;
import view.table.TableModel;
import view.table.filebrowser.ReadableSize;

/** 
 * A TableModel to hold a DirEntry[] table. Shown above the file details panel. 
 * 
 */
public class ProjectsTableModel extends TableModel {
	private static final long serialVersionUID = 1L;

	/**
	 * Number of columns (also maintain the column headers!), without counting the dynamic file counter columns.
	 */
	private int columnCount = 6;                                   
	
	/**
	 * Padding for icons
	 */
	private int rowIconPadding = 6;                                
	
	/**
	 * Fixed width of the file size column
	 */
	private int sizeColumnWidth = 90;                              
	
	/**
	 * Fixed with of the file size column  
	 */
	private int fileColumnWidth = 150;                             
	
	/**
	 * Max. width of file size column
	 */
	private int fileColumnMaxWidth = 600;                          

	/**
	 * Current File list
	 */
	private ProjectDirEntry dirEntry = null;                      
	
	/**
	 *  We only set cell sizes once at the first call!
	 */
	private boolean cellSizesSet = false;                          
	
	public ProjectsTableModel() {}
	
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
		
			ProjectDirEntry file = dirEntry.getProjectList().get(row);
	
			switch (column) {
			case 0:
				return new ProjectYearCell(file); 
			case 1:
				return Utils.getFileSystemView().getSystemDisplayName(file);
			case 2:
				return Utils.getFileSystemView().getSystemDisplayName(file.getParentDirEntry());
			case 3:
				return new ProjectSizeBar(file, dirEntry); 
			case 4:
				return new ReadableSize(file);
			case 5:
				return (new SimpleDateFormat("dd.mm.yyyy")).format(file.getProjectLastModified());
			}
			
			// Dynamic columns
			if (Main.getProjectDefinition().getPropertyDefinitions().size() > 0) {
				for (int i = 0; i < Main.getProjectDefinition().getPropertyDefinitions().size(); i++) {
					if (i + columnCount == column) {
						return new ProjectPropertyValue(file.getProjectProperties().get(i));
					}
				}
			}

			System.err.println(Messages.getString("Table_Errorstring", column)); 

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
			return ProjectYearCell.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return ProjectSizeBar.class;
		case 4:
			return ReadableSize.class;
		case 5:
			return String.class;
		}
		
		// Dynamic columns
		if (Main.getProjectDefinition().getPropertyDefinitions().size() > 0) {
			for (int i = 0; i < Main.getProjectDefinition().getPropertyDefinitions().size(); i++) {
				if (i + columnCount == column) {
					return ProjectPropertyValue.class;
				}
			}
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
			table.setColumnWidth(2, fileColumnWidth);
			table.getColumnModel().getColumn(2).setMaxWidth(fileColumnMaxWidth);
			
			table.setColumnWidth(3);
			table.setColumnWidth(4, sizeColumnWidth);
			table.setColumnWidth(5);
			
			if (Main.getProjectDefinition().getPropertyDefinitions().size() > 0) {
				for (int i = 0; i < Main.getProjectDefinition().getPropertyDefinitions().size(); i++) {
					table.setColumnWidth(columnCount + i);
				}
			}

			cellSizesSet = true;
		}
	}

	/**
	 * Set the cell renderers specific to projects view
	 * 
	 */
	@Override
	public void setCellRenderers(Table table) throws Throwable {
		table.getColumnModel().getColumn(0).setCellRenderer(new ProjectYearCellRenderer());
		
		// Dynamic columns
		if (Main.getProjectDefinition().getPropertyDefinitions().size() > 0) {
			for (int i = 0; i < Main.getProjectDefinition().getPropertyDefinitions().size(); i++) {
				table.getColumnModel().getColumn(columnCount + i).setCellRenderer(new ProjectPropertyCellRenderer());	
			}
		}
	}

	/**
	 * Get number of columns
	 * 
	 */
	@Override
	public int getColumnCount() {
		return columnCount + Main.getProjectDefinition().getPropertyDefinitions().size();
	}
	
	/**
	 * Get number of rows
	 * 
	 */
	@Override
	public int getRowCount() {
		try {
			if (dirEntry == null) return 0;
			return dirEntry.getProjectList().size();
			
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
	public ProjectDirEntry getRowDirEntry(int row) {
		try {
			if (dirEntry == null) return null;
			return dirEntry.getProjectList().get(row);
			
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
	public void setDirEntry(DirEntry d) throws Throwable {
		this.dirEntry = (ProjectDirEntry)d;
		fireTableDataChanged();
	}

	/**
	 * Returns the file icon of a specific row
	 * 
	 * @param row
	 * @return
	 */
	public Icon getIcon(int row) throws Throwable {
		if (dirEntry == null || dirEntry.getProjectList().size() == 0) return null;
		return Utils.getFileSystemView().getSystemIcon(dirEntry.getProjectList().get(row));
	}

	/**
	 * Returns (defines) the column header texts
	 * 
	 * @return
	 */
	@Override
	public String getColumnName(int column) {
		try {
			if (column < columnCount) return Messages.getString("ProjectTable_ColumnHeader_" + column);

			return Main.getProjectDefinition().getPropertyDefinitions().get(column - columnCount).getHeader(); 
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return "";
		}
	}
}
