package view.workers;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import main.Main;
import main.Messages;

/**
 * GUI model class for one worker panel, representing a worker thread on the GUI panel for workers.
 * 
 * @author tweber
 *
 */
public class WorkerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// Custom colors
	private Color killedColor = new Color(255, 200, 200);      // Background color for killed workers
	private Color finishedColor = new Color(200, 255, 200);  // Background color for finished workers
	private Color borderColor = Color.LIGHT_GRAY;            // Border color
	
	/**
	 * The worker instance to which this panel is linked
	 */
	@SuppressWarnings("rawtypes")
	private CustomSwingWorker parent;
	
	/**
	 * Reference to the progress bar
	 */
	public JProgressBar progressBar;
	
	/**
	 * Texts
	 */
	public JLabel[] texts;
	
	@SuppressWarnings("rawtypes")
	public WorkerPanel(CustomSwingWorker parent) throws Throwable {
		this.parent = parent;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		// Set borders
		Border b1 = BorderFactory.createEmptyBorder(1,0,1,0);
		Border b2 = BorderFactory.createLineBorder(borderColor, 1);
		Border b3 = BorderFactory.createEmptyBorder(2,2,2,2);
		this.setBorder(new CompoundBorder(new CompoundBorder(b1, b2), b3)); 
		
		
		// Add progress bar and add it to (this) main panel
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		// Set up the panel
		update();

		// Add double click listener (cancel)
		addMouseListener(new MouseAdapter() {
		    @Override
			public void mousePressed(MouseEvent me) {
		    	try {
			        if (me.getClickCount() == 2) {
			        	doubleclick();
			        }
		    	} catch (Throwable e) {
		    		Main.handleThrowable(e);
		    	}
		    }
		});
	}
	
	/**
	 * Double click handling
	 * 
	 */
	private void doubleclick() throws Throwable {
		if (parent.isDone()) {
			// Remove worker from panel
			parent.closeAFterFinish();
		} else {
			if (!parent.canBeKilled()) return;
			
			// User confirm
			int dialogResult = JOptionPane.showConfirmDialog(null, Messages.getString("WorkerPanel.ConfirmCancel"), Messages.getString("WorkerPanel.InfoTitle"), JOptionPane.YES_NO_OPTION);  //$NON-NLS-1$ //$NON-NLS-2$
			if (dialogResult == JOptionPane.NO_OPTION) {
				return;
			}
		
			parent.cancel();
		}
	}
	
	/**
	 * Update the text(s) from the worker etc.
	 * 
	 */
	public void update() throws Throwable {
		if (texts != null) {
			for(JLabel l: texts) {
				remove(l);
			}
		}
		if (progressBar != null) remove(progressBar);
		
		String[] out = parent.getOutputText();
		texts = new JLabel[out.length];
		for(int num=0; num<out.length; num++) {
			texts[num] = new JLabel(out[num], SwingConstants.TRAILING);
			add(texts[num]);
		}
		if (progressBar != null) add(progressBar);

		if (parent.isKilled()) this.setBackground(killedColor);
		if (parent.isDone()) this.setBackground(finishedColor);
		
		revalidate();
		repaint();
	}
}
