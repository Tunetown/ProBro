package view.stats;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import main.Messages;
import model.ProjectDirEntry;
import model.ProjectPropertyDefinition;

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
	
	private List<StatsLine> data = null;
	
	public StatsTableModel(ProjectDirEntry file) throws Throwable {
		data = createData(file);
	}
	
	/**
	 * TODO
	 * 
	 * @param file
	 * @return
	 * @throws Throwable
	 */
	private List<StatsLine> createData(ProjectDirEntry file) throws Throwable {
		List<StatsLine> ret = new ArrayList<StatsLine>();
		
		// Overall count of projects and project leftovers TODO externalize
		ret.add(new StatsLine("Projects", ""+file.getProjectList().size()));
		ret.add(new StatsLine("Project Leftovers", ""+file.getProjectLeftoversList().size()));
		
		for (int i=0; i<Main.getProjectDefinition().getPropertyDefinitions().size(); i++) {
			ProjectPropertyDefinition p = Main.getProjectDefinition().getPropertyDefinitions().get(i);
			ret.add(new StatsLine(p.getHeader(), ""+ sumProperty(file, i)));
		}
		
		return ret;
	}

	/**
	 * TODO
	 * 
	 * @param p
	 * @return
	 * @throws Throwable
	 */
	private long sumProperty(ProjectDirEntry file, int def) throws Throwable {
		long ret = 0;
		List<ProjectDirEntry> projects = file.getProjectList();
		
		for (ProjectDirEntry p : projects) {
			ret += p.getProjectProperties().get(def).getMatchingFiles().size();
		}
		
		return ret;
	}

	/**
	 * Defines the values shown in the table
	 * 
	 */
	@Override
	public Object getValueAt(int row, int column) {
		StatsLine s = data.get(row);
		
		try {
			switch (column) {
			case 0:
				return s.getName();
			case 1:
				return s.getValue();
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
		return data.size();
	}

	/**
	 * Returns (defines) the column header texts
	 * 
	 * @return
	 */
	@Override
	public String getColumnName(int column) {
		try {
			return Messages.getString("StatsTable_ColumnHeader_" + column);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return "";
		} 
	}
}
