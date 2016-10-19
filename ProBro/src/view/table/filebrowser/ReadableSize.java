package view.table.filebrowser;

import main.Main;
import model.DirEntry;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Class for representing readable sizes in table cells
 * 
 * @author tweber
 *
 */
@SuppressWarnings("rawtypes")
public class ReadableSize extends JLabel implements Comparable {
	private static final long serialVersionUID = 1L;

	/**
	 * File whose size shall be showed
	 */
	private DirEntry file;
	
	public ReadableSize(DirEntry file) throws Throwable {
		super("", SwingConstants.TRAILING);
		this.file = file;
	}

	/**
	 * Returns the file instance
	 * 
	 * @return
	 */
	public DirEntry getFile() {
		return file;
	}
	
	/**
	 * Comparison function
	 * 
	 */
	@Override
	public int compareTo(Object o) {
		try {
			ReadableSize r = (ReadableSize)o;
			return (file.getSize() > r.file.getSize()) ? 1 : -1;
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return 0;
		}
	}
}
