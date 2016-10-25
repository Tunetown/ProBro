package main;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import view.MainPanel;
import view.details.DetailsPanel;
import view.table.projects.LoadProjectsWorker;
import view.workers.LoadFullyWorker;
import view.workers.ZipWorker;

/**
 * Implements all commands which can be triggered by the detail view buttons, menu etc.
 * 
 * @author tweber
 *
 */
public class Commands {
	
	/**
	 * Reference to main JPanel
	 */
	private MainPanel gui;
	
	/**
	 * Source Component which caused the command
	 */
	private JComponent source;
	
	public Commands(MainPanel gui, JComponent source) throws Throwable {
		this.gui = gui;
		this.source = source;
	}

	/**
	 * Open file´s parent folder
	 * 
	 * @param ae
	 * @throws IOException
	 */
	public void locateFile(ActionEvent ae) throws Throwable {
		gui.details.getCurrentFile().openFolder();
		gui.details.repaint();
	}
	
	/**
	 * Open file
	 * 
	 * @param ae
	 * @throws IOException
	 */
	public void openFile(ActionEvent ae) throws Throwable {
		gui.details.getCurrentFile().open();
		gui.details.repaint();
	}
	
	/**
	 * Load full size info and deep folder info (thread wrapper)
	 * 
	 * @param ae
	 */
	public void loadFully(ActionEvent ae) throws Throwable {
		source.setEnabled(false);

		LoadFullyWorker worker = new LoadFullyWorker(gui);
		worker.execute();
	}

	/**
	 * Loads all projects of the current file
	 * 
	 * @param ae
	 */
	public void loadProjects(ActionEvent ae) throws Throwable {
		source.setEnabled(false);

		LoadProjectsWorker worker = new LoadProjectsWorker(gui);
		worker.execute();
	}

	/**
	 * Zip a file or folder, using the shell
	 * 
	 * @param ae
	 * @throws Throwable
	 */
	public void zipFile(ActionEvent ae) throws Throwable {
		if (OS.isWindows()) {
			JOptionPane.showMessageDialog(null, Messages.getString("Commands.ZipNotAllowed"), Messages.getString("Commands.ErrorTitle"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$ 
			return;
		}
		
		// User confirm
		int dialogResult = JOptionPane.showConfirmDialog(null, Messages.getString("Commands.ConfirmZip", gui.details.getCurrentFile()), Messages.getString("Commands.InfoTitle"), JOptionPane.YES_NO_OPTION);   //$NON-NLS-2$
		if (dialogResult == JOptionPane.NO_OPTION) {
			return;
		}
		
		ZipWorker worker = new ZipWorker(gui);
		worker.execute();
    }
	
	/**
	 * Delete a file 
	 * 
	 * @param ae
	 * @throws Throwable
	 *
	public void deleteFile(ActionEvent ae) throws Throwable {
		if (OS.isWindows()) {
			JOptionPane.showMessageDialog(null, Messages.getString("Commands.DelNotAllowed"), Messages.getString("Commands.ErrorTitle"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$ 
			return;
		}

		// User confirm
		int dialogResult = JOptionPane.showConfirmDialog(null, Messages.getString("Commands.ConfirmDelete", gui.details.getCurrentFile()), Messages.getString("Commands.WarningTitle"), JOptionPane.YES_NO_OPTION);  //$NON-NLS-2$
		if (dialogResult == JOptionPane.NO_OPTION) {
			return;
		}

		gui.details.getCurrentFile().delete();
		gui.tree.expandToPath(gui.details.getCurrentFile().getParentDirEntry().getAbsolutePath());
		gui.refresh();
    }

	/**
	 * Show file manager
	 * 
	 */
	public void showFilebrowser() throws Throwable {
		gui.frame.setView(DetailsPanel.VIEW_FILEBROWSER);
	}

	/**
	 * Show projects view
	 * 
	 */
	public void showProjects() throws Throwable {
		gui.frame.setView(DetailsPanel.VIEW_PROJECTS);
	}

	/**
	 * Show project leftovers view
	 * 
	 */
	public void showProjectLeftovers() throws Throwable {
		gui.frame.setView(DetailsPanel.VIEW_PROJECTLEFTOVERS);
	}

	/**
	 * Open a project definition file
	 * 
	 * @throws Throwable 
	 */
	public void openDefinition() throws Throwable {
		gui.frame.openProjectDefinition();
	}

	/**
	 * Open the default project definition file
	 * 
	 * @throws Throwable 
	 */
	public void openDefaultDefinition() throws Throwable {
		gui.frame.setProjectDefinition(Main.getDefaultProjectDefinitionFile(), DetailsPanel.VIEW_PROJECTS);
	}
}
