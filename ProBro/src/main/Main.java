package main;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.ProjectDefinition;
import view.MainFrame;

/**
* This is a file browser application which provides two views to the user:
* 
* - The default file browser view, which behaves like a standard explorer application,
*   and also provides some helpers to explore the disk space usage of folders (click on the
*   'load depp data' button)
*   
* - The projects viewer (select via Menu View -> Projects), which searches for specific
*   folders for master, mixes etc. and lists a deep search of projects for the selected folder.
*   In the tree, select the folder to search through, and click on "Search projects". The table
*   shows (after the data has been loaded) a list of all projects found in the selected folder,
*   also if they are deeper in the folder tree. 
*   
* - The properties like the names of masters, mixes etc. folders, as well as the file extension
*   tokens are loaded from a XML file, for more details no this see class ProjectDefinition.
*   
* Initial layout inspired by a file browser 
* by Andrew Thompson, see http://codereview.stackexchange.com/q/4446/7784
* 
* 
* 
* TODO:
* 
* IMPROVEMENTS --------------------------------------------
* - Sorting by size takes very long! Netbeans analyser!
* 
* - Documentation of views
* - Show overall count of masters, mixes etc somewhere
* - TODOs in DirENtry and ProjectDirEntry
* 
* - Test with real data (exact)!
* 
* - Bundle to APP
* 		- Set file browser to default view (MainFrame)
* 
* PRIO LOW ------------------------------------------------
* - BUG: Project view: Sorting by last modified date does not work (currently this does string comparison only)
* - ZIP: Currently, the whole path is stored in the ZIP. Check this out (PRIO LOW)
* - Tree: Select root does not work (PRIO LOW)
* - BUG: Table row heights bigger after first view change? (PRIO LOW)
* - All default values at least as attributes
* - osx stuff http://alvinalexander.com/apple/mac/java-mac-native-look/Putting_your_application_na.shtml
* 		-> build script not working, problem unsolved for now
*
* - DELETE: Evaluate console output of RM command (error if anything shown)
*
*
*
* @author Thomas Weber
* @version 2016-10-03
* @license LGPL
*/
public class Main {
	
	/**
	 * Parameter file, storing user parameters (last opened path, last used window properties etc)
	 */
	public static final File paramFile = new File(System.getProperty("user.home") + File.separator + "ProBroParameters.tmp");

	/**
	 * XML file to load the projects definitions from
	 */
	private static final File projectDefinitionFile = new File("ProBroProjectDef.xml");
	
	/**
	 * Project definitions (loaded from XML file in projectDefinitionFile)
	 */
	private static ProjectDefinition projectDefinition = null;
	
	/**
	 * Hide or show hidden files
	 */
	public static final boolean HIDE_FILES = true;       

	/**
	 * Main application method (just wraps the INIT method and runs it in the EDT)
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
			
		try {
			// Use the native menu bar on mac os x
			System.setProperty("apple.laf.useScreenMenuBar", "true"); //$NON-NLS-1$
			
			// Set the name of the application menu item
			//System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_TITLE);  TODO not working 

			// Set native look and feel 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
			// Load projects definitions
			try {
				projectDefinition = new ProjectDefinition(projectDefinitionFile);
				
			} catch (FileNotFoundException e) {
				// IF the project definition file is not there, we do not want to quit the program!
				JOptionPane.showMessageDialog(null, Messages.getString("Msg_WarnProjectDefinitionMissing", projectDefinitionFile.getAbsolutePath()), "Warning", JOptionPane.WARNING_MESSAGE); 
			}
			
			// Run application
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						MainFrame appl = new MainFrame(Messages.getString("Main.ApplicationTitle"));
						appl.init();
						
					} catch (Throwable t) {
						Main.handleThrowable(t);
					}
				}
			});
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
	}
	
	/**
	 * Returns the project definition loaded from the XML file
	 * 
	 * @return
	 */
	public static ProjectDefinition getProjectDefinition() {
		return projectDefinition;
	}
	
	/**
	 * Exception handler, used exclusively in this application
	 * 
	 * @param t
	 */
	public static void handleThrowable(Throwable t) {
		// Console output as usual
		t.printStackTrace();
		
		// Also show a popup to provide some info. Otherwise, a user not having a console open might
		// not even notice the error.
		String loc = t.getStackTrace()[0].getClassName() + "." + t.getStackTrace()[0].getMethodName();
		JOptionPane.showMessageDialog(null, t.toString() + " (" + loc + ")", "Exception", JOptionPane.ERROR_MESSAGE);
		
		// Leave the program
		System.exit(8);
	}

}
