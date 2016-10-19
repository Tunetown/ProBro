package main;
import java.awt.Desktop;

import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

/**
 * Utilities
 * 
 * @author tweber
 *
 */
public class Utils {

	/**
	 * Used to open/edit/print files.
	 */
	private static Desktop desktop = null;                      
	
	/**
	 * Provides nice icons and names for files.
	 */
	private static FileSystemView fileSystemView = null;        

	/**
	 * Returns a singleton of the Desktop
	 * 
	 * @return
	 */
	public static Desktop getDesktop() throws Throwable {
		if (desktop == null) {
			desktop = Desktop.getDesktop();
		}
		return desktop;
	}

	/**
	 * Returns a singleton of the FileSystemView
	 * 
	 * @return
	 */
	public static FileSystemView getFileSystemView() throws Throwable {
		if (fileSystemView == null) {
			// Significantly improves the look of the output in
			// terms of the file names returned by FileSystemView!
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			fileSystemView = FileSystemView.getFileSystemView();
		}
		return fileSystemView;
	}
}
