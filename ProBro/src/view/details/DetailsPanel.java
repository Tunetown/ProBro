package view.details;

import main.*;
import model.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import view.MainPanel;
import view.table.Table;
import view.table.TableModel;
import view.table.filebrowser.FileBrowserTableModel;
import view.table.projectleftovers.ProjectLeftoversTableModel;
import view.table.projects.ProjectsTableModel;
import view.workers.LoadFullyWorker;

/**
 * GUI model class for details panel
 * 
 * @author tweber
 *
 */
public class DetailsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int VIEW_FILEBROWSER = 0;
	public static final int VIEW_PROJECTS = 1;
	public static final int VIEW_PROJECTLEFTOVERS = 2;

	/**
	 * Main panel reference
	 */
	private MainPanel gui;
	
	/**
	 * JTable reference
	 */
	public Table table;                         
	
	/**
	 * Table scroll pane reference (this is actually added to the details panel)
	 */
	private JScrollPane tableScroll;
	
	/**
	 * This is the currently shown file instance
	 */
	private DirEntry currentFile;
	
	/**
	 * This is the currently selected view
	 */
	private int selectedView = VIEW_FILEBROWSER;

	// File details
	private JLabel fileName;                     
	private JTextField path;
	private JLabel date;
	private JLabel size;
	private JLabel type;
	private JLabel numFiles;
	private JLabel numFolders;
	private JCheckBox readable;
	private JCheckBox writable;
	private JCheckBox executable;
	private JCheckBox isDirectory;

	// Buttons
	private JButton loadFully;
	private JButton loadProjects;
	private JButton refresh;

	public DetailsPanel(MainPanel gui) throws Throwable {
		super(new BorderLayout(3, 3));
		this.gui = gui;

		// Create details pane and buttons toolbar
		JPanel fileMainDetails = createMainDetails();
		JToolBar buttons = createButtons();

		// Add these two together in a border layout, in a new JPanel, and add this
		// to the details panel (this)
		JPanel fileView = new JPanel(new BorderLayout(3, 3));
		fileView.add(buttons, BorderLayout.NORTH);
		fileView.add(fileMainDetails, BorderLayout.CENTER);
		add(fileView, BorderLayout.SOUTH);
	}

	/**
	 * Set up or replace the table instance 
	 * 
	 * @param model
	 */
	private void initTable(TableModel model) throws Throwable {
		// Initialize the file table and attach it to the details (this), embedded into a scroll pane of course
		if (tableScroll != null) {
			table.getSelectionModel().removeListSelectionListener(table.listSelectionListener);
			remove(tableScroll);
		}
		table = new Table(gui, model);
		tableScroll = new JScrollPane(table);
		Dimension d = tableScroll.getPreferredSize();
		tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
		add(tableScroll, BorderLayout.CENTER);

		table.revalidate();
		revalidate();
	}
	
	/**
	 * Creates the main details panel
	 * 
	 * @return
	 */
	private JPanel createMainDetails() throws Throwable {
		JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
		fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

		JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
		fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

		JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
		fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

		fileDetailsLabels.add(new JLabel(Messages.getString("Label_FileName"), SwingConstants.TRAILING)); //$NON-NLS-1$
		fileName = new JLabel();
		fileDetailsValues.add(fileName);
		
		fileDetailsLabels.add(new JLabel(Messages.getString("Label_FilePath"), SwingConstants.TRAILING)); //$NON-NLS-1$
		path = new JTextField(5);
		path.setEditable(false);
		fileDetailsValues.add(path);
		
		fileDetailsLabels.add(new JLabel(Messages.getString("Label_LastModified"), SwingConstants.TRAILING)); //$NON-NLS-1$
		date = new JLabel();
		fileDetailsValues.add(date);
		
		fileDetailsLabels.add(new JLabel(Messages.getString("Label_FileSize"), SwingConstants.TRAILING)); //$NON-NLS-1$
		size = new JLabel();
		fileDetailsValues.add(size);

		fileDetailsLabels.add(new JLabel(Messages.getString("Label_Filetype"), SwingConstants.TRAILING)); //$NON-NLS-1$
		type = new JLabel();
		fileDetailsValues.add(type);

		fileDetailsLabels.add(new JLabel(Messages.getString("Label_FileNum"), SwingConstants.TRAILING)); //$NON-NLS-1$  
		numFiles = new JLabel();
		fileDetailsValues.add(numFiles);

		fileDetailsLabels.add(new JLabel(Messages.getString("Label_FolderNum"), SwingConstants.TRAILING)); //$NON-NLS-1$
		numFolders = new JLabel();
		fileDetailsValues.add(numFolders);

		// Flags (file type, attributes)
		fileDetailsLabels.add(new JLabel(Messages.getString("Label_FileProperties"), SwingConstants.TRAILING)); //$NON-NLS-1$

		JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));

		isDirectory = new JCheckBox(Messages.getString("Label_FileDir")); //$NON-NLS-1$
		flags.add(isDirectory);

		fileDetailsValues.add(flags);
		
		flags.add(new JLabel(Messages.getString("Label_Flags"))); //$NON-NLS-1$
		readable = new JCheckBox(Messages.getString("Label_Radiobutton_Read")); //$NON-NLS-1$
		flags.add(readable);

		writable = new JCheckBox(Messages.getString("Label_Radiobutton_Write")); //$NON-NLS-1$
		flags.add(writable);

		executable = new JCheckBox(Messages.getString("Label_Radiobutton_Exec")); //$NON-NLS-1$
		flags.add(executable);

		int count = fileDetailsLabels.getComponentCount();
		for (int ii = 0; ii < count; ii++) {
			fileDetailsLabels.getComponent(ii).setEnabled(false);
		}

		count = flags.getComponentCount();
		for (int ii = 0; ii < count; ii++) {
			flags.getComponent(ii).setEnabled(false);
		}

		return fileMainDetails;
	}
	
	/**
	 * Initialize Buttons
	 * 
	 * @param toolBar
	 */
	private JToolBar createButtons() throws Throwable {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		final MainPanel wrapper = gui;
		
		refresh = new JButton(Messages.getString("DetailsPanel.Refresh"));   //$NON-NLS-1$
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					gui.refresh();					
				} catch (Throwable t) {
					Main.handleThrowable(t);
				}
			}
		});
		toolBar.add(refresh);

		loadFully = new JButton(Messages.getString("DetailsPanel.LoadDeepInfo"));   //$NON-NLS-1$
		loadFully.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					Commands com = new Commands(wrapper, loadFully);
					com.loadFully(ae);
				} catch (Throwable t) {
					Main.handleThrowable(t);
				}
			}
		});
		toolBar.add(loadFully);

		loadProjects = new JButton(Messages.getString("DetailsPanel.SearchProjects"));  //$NON-NLS-1$
		loadProjects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					Commands com = new Commands(wrapper, loadProjects);
					com.loadProjects(ae);
				} catch (Throwable t) {
					Main.handleThrowable(t);
				}
			}
		});
		toolBar.add(loadProjects);

		return toolBar;
	}
	
	/**
	 * Sets the view (file browser or projects list)
	 * 
	 * @param projectsView
	 */
	public void setView(int view) throws Throwable {
		if (view == 0) {
			initTable(new FileBrowserTableModel());
			selectedView = VIEW_FILEBROWSER;
			
			loadFully.setVisible(true);
			loadProjects.setVisible(false);
			
			if (getCurrentFile() != null) loadFully.setEnabled(!getCurrentFile().isFullyLoaded());
		} 
		if (view == 1) {
			initTable(new ProjectsTableModel());			
			selectedView = VIEW_PROJECTS;

			loadFully.setVisible(false);
			loadProjects.setVisible(true);
			
			if (getCurrentFile() != null) loadFully.setEnabled(!((ProjectDirEntry)getCurrentFile()).projectsLoaded());
		}
		if (view == 2) {
			initTable(new ProjectLeftoversTableModel());			
			selectedView = VIEW_PROJECTLEFTOVERS;

			loadFully.setVisible(false);
			loadProjects.setVisible(true);
			
			if (getCurrentFile() != null) loadFully.setEnabled(!((ProjectDirEntry)getCurrentFile()).projectsLoaded());
		}
		table.setTableData(getCurrentFile());
	}
	
	/**
	 * Returns the selected view (file browser or projects)
	 * 
	 * @return
	 */
	public int getView() {
		return selectedView;
	}

	
	/**
	 * Update the File details view with the details of this File.
	 * 
	 * @param file
	 */
	public void setFileDetails(DirEntry file) throws Throwable {
		currentFile = file;
		Icon icon = Utils.getFileSystemView().getSystemIcon(file);
		fileName.setIcon(icon);
		fileName.setText(Utils.getFileSystemView().getSystemDisplayName(file));
		path.setText(file.getPath());
		date.setText(new Date(file.lastModified()).toString());
		size.setText(file.getReadableSize()); 
		type.setText(file.getType());
		numFiles.setText(file.getNumOfFiles() >= 0 ? ""+file.getNumOfFiles() : Messages.getString("DirEntry.NotLoaded")); //$NON-NLS-1$ 
		numFolders.setText(file.getNumOfFolders() >= 0 ? ""+file.getNumOfFolders() :Messages.getString("DirEntry.NotLoaded")); //$NON-NLS-1$
		readable.setSelected(file.canRead());
		writable.setSelected(file.canWrite());
		executable.setSelected(file.canExecute());
		isDirectory.setSelected(file.isDirectory());
		
		// Set window title 
		JFrame f = (JFrame)gui.getTopLevelAncestor();
		if (f != null) {
			f.setTitle(Messages.getString("Main.ApplicationTitle") + " :: " + Utils.getFileSystemView().getSystemDisplayName(file)); //$NON-NLS-1$
		}
		
		// Set fully loaded button state
		try {
			if (getView() == VIEW_FILEBROWSER) { 
				if (currentFile.isFullyLoaded()) {
					gui.details.loadFully.setEnabled(false);
				} else {
					gui.details.loadFully.setEnabled(!(gui.workers.getWorkers(LoadFullyWorker.class).size() > 0));
				}
			} 
			if (getView() == VIEW_PROJECTS || getView() == VIEW_PROJECTLEFTOVERS){
				if (((ProjectDirEntry)currentFile).projectsLoaded()) {
					gui.details.loadProjects.setEnabled(false);
				} else {
					gui.details.loadProjects.setEnabled(!(gui.workers.getWorkers(LoadFullyWorker.class).size() > 0));
				}
			}
		} catch (Throwable t) {
			gui.details.loadFully.setEnabled(false);  
			gui.details.loadProjects.setEnabled(false);  
		}

		gui.repaint();
	}
	
	/**
	 * Getter for current file instance
	 * 
	 * @return
	 */
	public DirEntry getCurrentFile() {
		return currentFile;
	}

	/**
	 * Also update the detail data when refreshing the details panel
	 * 
	 */
	@Override
	public void repaint() {
		try {
			if (currentFile != null) {
				// Also update the details values before repainting
				setFileDetails(currentFile);
			}
			super.repaint();

		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}
}
