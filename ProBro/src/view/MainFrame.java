package view;

import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import view.details.DetailsPanel;
import main.Main;
import main.Messages;
import main.ParamLoader;

/**
 * Main application frame
 * 
 * @author tweber
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

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
	
	/**
	 * Parameter loader
	 */
	private ParamLoader paramLoader = null;	
	
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
		setView(DetailsPanel.VIEW_FILEBROWSER); 

		// Do some size and location stuff
		pack();
		setLocationByPlatform(true);
		setMinimumSize(getSize());
		setVisible(true);
		
		// Load user parameters from file and use them
		paramLoader = new ParamLoader(this);
		paramLoader.loadParams();
		
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
		paramLoader.addShutdownHook();
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
	 * Open a new project definition
	 * 
	 */
	public void openProjectDefinition() throws Throwable {
		JFileChooser j = new JFileChooser();
		j.setFileFilter(new FileNameExtensionFilter(Messages.getString("ProjectDefinitionFileType"), "xml"));  
		
		int answer = j.showOpenDialog(this);
		
		if (answer == JFileChooser.APPROVE_OPTION) {
			File file = j.getSelectedFile();
			setProjectDefinition(file, DetailsPanel.VIEW_PROJECTS);
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