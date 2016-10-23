package view;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Main;
import main.Messages;
import main.ParamFile;

/**
 * Main application frame
 * 
 * @author tweber
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final int VIEW_FILEBROWSER = 0;
	public static final int VIEW_PROJECTS = 1;
	public static final int VIEW_PROJECTLEFTOVERS = 2;
	
	/**
	 * Main panel reference
	 */
	public MainPanel mainPanel;                           
	
	/**
	 * Menu bar reference
	 */
	public Menu menuBar;
	
	/**
	 * Attributes for screen (user parameters)   
	 */
	public int winX, winY, winW, winH, fullscreen;                               
	
	public MainFrame(String title) throws Throwable {
		super(title);
	}
	
	/**
	 * Main setup of the application JFrame, including loading/saving user parameters
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * 
	 */
	public void init() throws Throwable {
		// Set up GUI elements (this results in a container holding all elements,
		// which will be embedded in a major JFrame in the following lines) by creating
		// the main GUI instance
		mainPanel = new MainPanel(this);
		
		// Add this main GUI instance to the main frame (this contains all elements)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(mainPanel);

		// Create menu
		createMenuBar();

		// Set default (file explorer) view
		setView(VIEW_FILEBROWSER); 

		// Do some size and location stuff
		pack();
		setLocationByPlatform(true);
		setMinimumSize(getSize());
		setVisible(true);
		
		// Load user parameters from file and use them
		loadParams();
		
		// Store user data before closing. For this, we need a shutdown hook, but also we need to store
		// some of the frame properties before this, because the frame might not be visible anymore when the hook
		// is running. Therefore, on every resizing / moving event, we store the values directly in attributes
		// here in this class, and store these later in the shutdown hook.
		final MainFrame frameWrapper = this;
		addComponentListener(new ComponentListener() {
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				try {
					frameWrapper.storeFrameProperties();
				} catch (Throwable e) {
					Main.handleThrowable(e);
				}
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				try {
					frameWrapper.storeFrameProperties();
				} catch (Throwable e) {
					Main.handleThrowable(e);
				}
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
			}
		});

		// Create the shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	try {
		    		System.out.print("Shutting down...");
	
			    	ParamFile var = new ParamFile(Main.paramFile);
			    	var.set("winWidth", frameWrapper.winW);
			    	var.set("winHeight", frameWrapper.winH);
			    	var.set("winX", frameWrapper.winX);
			    	var.set("winY", frameWrapper.winY);
			    	var.set("fullscreen", frameWrapper.fullscreen);
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
	 * Set the next view to show (no repaint!)
	 * 
	 * @param view
	 * @throws Throwable
	 */
	public void setView(int view) throws Throwable {
		menuBar.setView(view);
		mainPanel.details.setView(view);
	}
	
	/**
	 * Create the menu bar
	 * 
	 */
	private void createMenuBar() throws Throwable {
		menuBar = new Menu(this);
		menuBar.init();
	}
	
	/**
	 * Store frame properties to instance attributes (this way, we can store them even after
	 * the frame has been destroyed). Called on every resize/move event.
	 */
	public void storeFrameProperties() throws Throwable {
		winX = getLocationOnScreen().x;
		winY = getLocationOnScreen().x;
		winW = getWidth();
		winH = getHeight();
		fullscreen = ((getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) ? 1 : 0;
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

		// Use loaded values
		if (loadedFileName != null) {
			System.out.println("Opening recent path: " + loadedFileName);
			mainPanel.tree.expandToPath(loadedFileName);
		}
		
		if (fullscreen != null && fullscreen == 1) {
			setExtendedState(Frame.MAXIMIZED_BOTH);
		} else {
			if (winX != null && winY != null) {
				System.out.println("Setting last used window position: " + winX + "x" + winY);
				setSize(winWidth, winHeight);
				setLocation(new Point(winX, winY));
			}
			
			if (winWidth != null && winHeight != null) {
				System.out.println("Setting last used window size: " + winWidth + "x" + winHeight);
				setSize(winWidth, winHeight);
			}
		}
	}

	/**
	 * Open a new project definition
	 * 
	 */
	public void openProjectDefinition() throws Throwable {
		JFileChooser j = new JFileChooser();
		j.setFileFilter(new FileNameExtensionFilter(Messages.getString("ProjectDefinitionFileType"), "xml"));  
		
		int answer = j.showOpenDialog(this);
		
		if (answer == JFileChooser.APPROVE_OPTION) {
			File file = j.getSelectedFile();
			setProjectDefinition(file, MainFrame.VIEW_PROJECTS);
		}
	}
	
	/**
	 * Set a given project definition and switch to a given view
	 * 
	 * @param file
	 * @throws Throwable 
	 */
	public void setProjectDefinition(File file, int view) throws Throwable {
		if (this.mainPanel.workers.getWorkers().size() > 0) {
			String msg = Messages.getString("Msg_CloseWorkersBeforeOPD");
			JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			Main.setProjectDefinition(file);
			
			this.setView(view);
			mainPanel.refresh();
			menuBar.setProjectsOptionsState();
			
		} catch (Throwable t) {
			t.printStackTrace();
			JOptionPane.showMessageDialog(this, Messages.getString("Msg_ErrorOpeningPD", t.getMessage()), "Error", JOptionPane.ERROR_MESSAGE); 
		}		
	}
}