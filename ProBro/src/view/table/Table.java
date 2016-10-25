package view.table;

import main.Main;
import model.*;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import view.MainPanel;
import view.details.DetailsPanel;
import view.table.filebrowser.ReadableSize;
import view.table.filebrowser.ReadableSizeCellRenderer;
import view.table.filebrowser.SizeBar;
import view.table.filebrowser.SizeBarCellRenderer;
import view.table.projectleftovers.ProjectLeftoversSizeBar;
import view.table.projects.ProjectSizeBar;

/**
 * GUI model class for the file table. This just models the Swing part,
 * the column definitions etc are defined in ProBroTableModel.
 * 
 * @author tweber
 *
 */
public class Table extends JTable {
	private static final long serialVersionUID = 1L;

	/**
	 * Main panel reference
	 */
	private MainPanel gui;
	
	/**
	 * Table model for File[].
	 */
	private TableModel tableModel;                           
	
	/**
	 * Event listener: Selection of tree nodes
	 */
	public ListSelectionListener listSelectionListener;      

	public Table(MainPanel gui, TableModel tableModel) throws Throwable {
		this.gui = gui;
		this.tableModel = tableModel;
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateRowSorter(true);
		setShowVerticalLines(false);

		final MainPanel guiWrapper = gui;
		listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				try {
					int row = getSelectionModel().getAnchorSelectionIndex(); 
					DirEntry d = ((TableModel) getModel()).getRowDirEntry(convertRowIndexToModel(row));
					guiWrapper.details.setFileDetails(d);
					
				} catch (Throwable e) {
					Main.handleThrowable(e);
				}
			}
		};
		getSelectionModel().addListSelectionListener(listSelectionListener);
		
		// Create and activate the file table model
		setModel(tableModel);
		
		// Add renderer for some cells which have custom appearance
		setDefaultRenderer(SizeBar.class, new SizeBarCellRenderer());
		setDefaultRenderer(ProjectSizeBar.class, new SizeBarCellRenderer());
		setDefaultRenderer(ProjectLeftoversSizeBar.class, new SizeBarCellRenderer());
		setDefaultRenderer(ReadableSize.class, new ReadableSizeCellRenderer());

		// Add renderers for columns directly in the table model
		tableModel.setCellRenderers(this);
		
		// Add mouse listener
		addMouseListener(new MouseAdapter() {
		    @Override
			public void mousePressed(MouseEvent me) {
		    	try {
			        JTable table =(JTable) me.getSource();
			        int row = table.rowAtPoint(me.getPoint());
			        if (me.getClickCount() == 2) {
			        	DirEntry d = ((TableModel) getModel()).getRowDirEntry(convertRowIndexToModel(row));
			        	doubleClick(d);
			        }
			        
		    	} catch (Throwable e) {
		    		Main.handleThrowable(e);
		    	}
		    }
		});
	}

	/**
	 * Double click functionality
	 * 
	 * @param d
	 */
	private void doubleClick(DirEntry d) throws Throwable {
    	if (gui.details.getView() == DetailsPanel.VIEW_PROJECTS || gui.details.getView() == DetailsPanel.VIEW_PROJECTLEFTOVERS) {
    		// Projects view: open folder in OS
    		d.open();
    	} else {
    		// File browser: Open files, select folders in tree
        	if (d.isDirectory()) {
        		gui.tree.expandToPath(d.getAbsolutePath());
        	} else {
				d.open();
        	}
    	}
	}
	
	/**
	 * Update the table on the EDT with a new set of files
	 * 
	 * @param files
	 */
	public void setTableData(final DirEntry file) throws Throwable {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					setTableData(file, true);
					
				} catch (Throwable e) {
					Main.handleThrowable(e);
				}
			}
		});
	}

	/**
	 * Update the table on the EDT (real processing, invoked later)
	 * 
	 * @param files
	 * @param later dummy parameter to distinguish implementations of the method
	 */
	private void setTableData(final DirEntry file, boolean later) throws Throwable {
		getSelectionModel().removeListSelectionListener(listSelectionListener);
		tableModel.setDirEntry(file);
		getSelectionModel().addListSelectionListener(listSelectionListener);
		
		tableModel.setCellSizes(this);
	}
	
	/**
	 * Set a column width in the table
	 * 
	 * @param column (column number)
	 * @param width (-1 for automatic width)
	 */
	public void setColumnWidth(int column) throws Throwable {
		setColumnWidth(column, -1);
	}
	
	/**
	 * Set a column width in the table
	 * 
	 * @param column (column number)
	 * @param width (-1 for automatic width)
	 */
	public void setColumnWidth(int column, int width) throws Throwable {
		TableColumn tableColumn = getColumnModel().getColumn(column);
		
		if (width < 0) {
			// use the preferred width of the header.
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			width = (int) preferred.getWidth() + 14;
		}
		
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}
}
