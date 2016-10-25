package main;

import java.awt.Frame;
import java.awt.Point;
import java.io.File;

import view.MainFrame;
import view.details.DetailsPanel;

/**
 * Loads the application specific last used values from the temp file
 * 
 * @author tweber
 *
 */
public class ParamLoader {

	private MainFrame frame;
	
	public ParamLoader(MainFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * Adds and defines the shutdown hook, which stores the parameters
	 * 
	 */
	public void addShutdownHook() throws Throwable {
		final MainFrame frameWrapper = frame;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	try {
		    		System.out.print("Shutting down, saving last used values...");
	
			    	ParamFile var = new ParamFile(Main.paramFile);
			    	var.set("winWidth", frameWrapper.winW);
			    	var.set("winHeight", frameWrapper.winH);
			    	var.set("winX", frameWrapper.winX);
			    	var.set("winY", frameWrapper.winY);
			    	var.set("fullscreen", frameWrapper.fullscreen);
			    	var.set("selectedView", frameWrapper.mainPanel.details.getView());
			    	var.set("projectDefinition", Main.getProjectDefinition().getFile().getAbsolutePath());
					if (frameWrapper.mainPanel.details.getCurrentFile() != null) 
						var.set("selDir", frameWrapper.mainPanel.details.getCurrentFile().getAbsolutePath());
					var.store();
			    	
			    	System.out.println("finished.");
		    	} catch (Throwable e) {
		    		Main.handleThrowable(e);
		    	}
		    }
		});
	}
	
	/**
	 * Load Parameters: Load the last opened path etc, stored in a temporary file in the user�s home directory.
	 * After loading, this also applies the parameters (if existent) to the program.
	 * 
	 */
	public void loadParams() throws Throwable {
		ParamFile vars = new ParamFile(Main.paramFile);
	
		String loadedFileName = (String)(vars.get("selDir"));
		Integer winWidth = (Integer)(vars.get("winWidth"));
		Integer winHeight =(Integer)(vars.get("winHeight"));
		Integer winX = (Integer)(vars.get("winX"));
		Integer winY = (Integer)(vars.get("winY"));
		Integer fullscreen = (Integer)(vars.get("fullscreen"));
		Integer selectedView = (Integer)(vars.get("selectedView"));
		String projectDefinition = (String)(vars.get("projectDefinition"));

		// Use loaded values
		System.out.println("Loaded last used values from " + vars.getFile().getAbsolutePath());
		
		if (loadedFileName != null) {
			System.out.println("Opening recent path: " + loadedFileName);
			frame.mainPanel.tree.expandToPath(loadedFileName);
		}

		if (projectDefinition != null) {
			System.out.println("Setting last used project definition: " + projectDefinition);
			frame.setProjectDefinition(new File(projectDefinition), DetailsPanel.VIEW_FILEBROWSER);
		}
		
		if (fullscreen != null && fullscreen == 1) {
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		} else {
			if (winX != null && winY != null) {
				System.out.println("Setting last used window position: " + winX + "x" + winY);
				frame.setSize(winWidth, winHeight);
				frame.setLocation(new Point(winX, winY));
			}
			
			if (winWidth != null && winHeight != null) {
				System.out.println("Setting last used window size: " + winWidth + "x" + winHeight);
				frame.setSize(winWidth, winHeight);
			}
		}
		
		if (selectedView != null) {
			// Only set the stored view when there is a project definition. The file browser view has been set already, 
			// we only need this if any project related view is stored.
			if (Main.getProjectDefinition() != null && selectedView != DetailsPanel.VIEW_FILEBROWSER) {
				System.out.println("Setting last used view selection: " + selectedView);
				frame.mainPanel.details.setView(selectedView);
			} 
		}
	}

}
