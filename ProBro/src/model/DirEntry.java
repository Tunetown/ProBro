package model;

import main.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

/**
 * A directory entry (file or folder). This instance can be loaded fully, which means that the whole
 * recursive folder tree is loaded and linked, also all sizes are calculated, to provide accurate
 * folder sizes (which are -1 if not loaded).
 * Also, the instance can be loaded gradually. A later call to loadFully will then complete the data anyway.
 * 
 * @author tweber
 *
 */
public class DirEntry extends File {
	private static final long serialVersionUID = 1L;

	/**
	 * Type for directories (getType())
	 */
	private static final String TYPE_DIR = "DIR";
	
	/**
	 * Parent folder. Can be null if this is the root of the DirEntry tree.
	 */
	protected DirEntry parent = null;                    
	
	/**
	 * Children of the folder. Null if this is a file.
	 */
	protected List<DirEntry> children = null;           
	
	/**
	 * File size. For folders, this is set to -1 if not fully loaded (meaning that all exact sizes etc are calculated), 
	 * if fully loaded, this shows the exact, overall size of all contents inside the folder.
	 */
	private long size = -1;                           
	
	/**
	 * Number of files (ignoring folders) inside this path. Set to -1 if not fully loaded.
	 */
	private long numOfFiles = -1;                     
	
	/**
	 * Number of folders inside this path. Set to -1 if not fully loaded.
	 */
	private long numOfFolders = -1;                   
	
	/**
	 * Is the child tree fully loaded (meaning that all exact sizes etc are calculated)?
	 */
	private boolean loadedFully = false;              
	
	/**
	 * Load hidden files?
	 */
	private boolean loadHiddenFiles = false;          
	
	/**
	 * Escaper tool for shell operations
	 */
	private Escaper escaper;
	
	/**
	 * Buffer for largest child
	 */
	private DirEntry largestChild = null;
	
	/**
	 * Buffer for readable size
	 */
	private String readableSize = null;
	
	/**
	 * Depth buffer
	 */
	private int depth = -1;
	
	public DirEntry(String name) throws Throwable {
		super(name);
		if (!exists()) throw new FileNotFoundException(getAbsolutePath());
		
		// Get escaper for shell file names
		final Escapers.Builder builder = Escapers.builder();
        builder.addEscape('\'', "'\"'\"'"); //$NON-NLS-1$
        escaper = builder.build();
	}

	public DirEntry(String name, DirEntry parent) throws Throwable {
		this(name);
		this.parent = parent;
	}

	public DirEntry(String name, DirEntry parent, boolean loadHiddenFiles) throws Throwable {
		this(name, parent);
		this.loadHiddenFiles = loadHiddenFiles;
	}

	/**
	 * Load the children of this folder, if it is a folder. This does not load accurate 
	 * folder sizes or deep folder structures! 
	 * 
	 * @return
	 */
	public List<DirEntry> getChildren() throws Throwable {
		if (children != null) {
			return children;
		}
		
		if (!isDirectory()) {
			return null;
		}
		
		File[] childrenFiles = Utils.getFileSystemView().getFiles(this, !loadHiddenFiles);
		
		children = new ArrayList<DirEntry>();
		for(File file : childrenFiles) {
			children.add(createChild(file));
		}
		return children;
	}
	
	/**
	 * Create a child instance
	 * 
	 * @param file
	 * @return
	 */
	protected DirEntry createChild(File file) throws Throwable {
		return new DirEntry(file.getAbsolutePath(), this);
	}

	/**
	 * Load the whole recursive directory tree of this DirEntry,
	 * also determining the exact folder sizes. CAUTION: This may take a while
	 * for big folders!
	 * 
	 * @throws IOException 
	 * 
	 */
	public void loadFully() throws Throwable {
		if (isFullyLoaded()) return;
		
		if (isDirectory()) {
			List<DirEntry> ch = getChildren();
			
			// Recurse
			for (DirEntry child : ch) {
				child.loadFully();
			}
		}
			
		getSize();
		getNumOfFiles();
		
		loadedFully = true;
	}

	/**
	 * Is the instance fully loaded (recursively) including all deep 
	 * folder structure and exact folder sizes?
	 * 
	 * @return
	 */
	public boolean isFullyLoaded() throws Throwable {
		if (isDirectory()) {
			return loadedFully;
		} else {
			// Files are always fully loaded
			return true;
		}
	}
	
	/**
	 * Get total size of file/directory. If the instance is not fully loaded, 
	 * for folders -1 is returned as the size is not yet known.
	 * 
	 * @return
	 */
	public long getSize() throws Throwable {
		if (size > -1) {
			// Already calculated
			return size;
		}
		
		// Size determination...
		if (isDirectory()) {
			// Folder: Recursively add all children, or -1 if the instance is not fully loaded.
			if (!isFullyLoaded()) {
				return -1;
			}
			
			long s = 0;
			for(DirEntry e:children) {
				s += e.getSize();
			}
			
			size = s;
		} else {
			// File: Directly determine the file size
			size = length();
		}
		
		return size;
	}
	
	/**
	 * Returns the number of files inside this folder, or -1 if not fully loaded
	 *  
	 * @return
	 */
	public long getNumOfFiles() throws Throwable {
		if (numOfFiles > -1) {
			// Already calculated
			return numOfFiles;
		}
		
		if (isDirectory()) {
			// Folder: Recursively add all children, or -1 if the instance is not fully loaded.
			if (!isFullyLoaded()) {
				return -1;
			}
			
			long s = 0;
			for(DirEntry e:children) {
				s += e.getNumOfFiles();
			}
			
			numOfFiles = s;
		} else {
			numOfFiles = 1;
		}
		
		return numOfFiles;
	}
	
	/**
	 * Returns the number of files inside this folder, or -1 if not fully loaded
	 *  
	 * @return
	 */
	public long getNumOfFolders() throws Throwable {
		if (numOfFolders > -1) {
			// Already calculated
			return numOfFolders;
		}
		
		if (isDirectory()) {
			// Folder: Recursively add all children, or -1 if the instance is not fully loaded.
			if (!isFullyLoaded()) {
				return -1;
			}
			
			long s = 0;
			for(DirEntry e:children) {
				s += e.getNumOfFolders();
			}
			
			numOfFolders = s + 1;
		} else {
			numOfFolders = 0;
		}
		
		return numOfFolders;
	}
	
	/**
	 * Return the depth of this entry, relative to the root DirEntry
	 * 
	 * @return
	 */
	public int getDepth() throws Throwable {
		if (depth > -1) return depth;
		depth = getDepth(0); 
		return depth;
	}
	
	/**
	 * Private, internal helper for getDepth
	 * 
	 * @param dep
	 * @return
	 */
	private int getDepth(int dep) throws Throwable {
		if (isRoot()) {
			return dep;
		} else {
			return parent.getDepth(dep + 1);
		}
	}

	/**
	 * Returns if this is the root of the current DirEntry tree hierarchy
	 * 
	 * @return
	 */
	public boolean isRoot() {
		return (parent == null);
	}
	
	/**
	 * Returns if this file is a child of other.
	 * 
	 * @param other
	 * @return
	 */
	public boolean isChildOf(DirEntry other) throws Throwable {
		if (other == null) return false;
		if (other.isDirectory()) {
			for(DirEntry child : other.getChildren()) {
				if (isChildOf(child)) {
					return true;
				}
			}
			return false;
		} else {
			// File
			return this.equals(other);
		}
	}
	
	/**
	 * Open the file externally
	 * 
	 * @throws IOException
	 */
	public void open() throws Throwable {
		Utils.getDesktop().open(this);
	}
	
	/**
	 * Open the file's parent folder externally 
	 * 
	 * @throws IOException
	 */
	public void openFolder() throws Throwable {
		Utils.getDesktop().open(this.getParentFile());
	}
	
	/**
	 * Zip the file using the shell
	 * 
	 * TODO: Currently, the whole path is stored in the ZIP file from /Volumes up. 
	 * 		 Examine this...
	 * 
	 * @throws IOException 
	 * 
	 */
	public Process zip() throws Throwable {
		String file = this.getAbsolutePath();		
		String zipfile = file + ".zip"; //$NON-NLS-1$
		
        DirEntry zip = new DirEntry(zipfile);
        if (zip.exists()) zip.delete();
        
        // Build ZIP command 
		String[] cmd = new String[6];
		cmd[0] = "zip"; //$NON-NLS-1$
		cmd[1] = "-r"; //$NON-NLS-1$
		cmd[2] = "-o"; //$NON-NLS-1$
		cmd[3] = "-9"; //$NON-NLS-1$
		cmd[4] = escaper.escape(zipfile);
		cmd[5] = escaper.escape(file);
		
		// Return the process instance to be able to evaluate the results in the caller
		return Runtime.getRuntime().exec(cmd);
	}
	
	/**
	 * Delete file (also works recursively, by shell command rm -r)
	 * 
	 */
	@Override
	public boolean delete() {
		try {
	        // Build deletion command
			String[] cmd = new String[3];
			cmd[0] = "rm"; //$NON-NLS-1$
			cmd[1] = "-r"; //$NON-NLS-1$
			cmd[2] = escaper.escape(this.getAbsolutePath());
			
			// Return the process instance to be able to evaluate the results in the caller
			System.out.println("Execution of rm command: " + cmd); //$NON-NLS-1$
			
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
		}
			
		// TODO: PRIO LOW Evaluate the return (not so important now)
		return true;
	}
	
	/**
	 * Get readable size string
	 * 
	 * @return
	 */
	public String getReadableSize() throws Throwable {
		if (readableSize != null) return readableSize;
		
		long s = getSize();
	    if(s == 0) return "0 B"; //$NON-NLS-1$
	    if(s < 0) return Messages.getString("DirEntry.NotLoaded");  //$NON-NLS-1$
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" }; // used with 1000   //$NON-NLS-1$ 
	    int digitGroups = (int) (Math.log10(s)/Math.log10(1000));
	    readableSize = new DecimalFormat("#,##0.#").format(s/Math.pow(1000, digitGroups)) + " " + units[digitGroups]; //$NON-NLS-1$
	    
	    return readableSize;
	}
	
	/**
	 * Returns the parent in the DirEntry tree or null 
	 * if this is the root 
	 * 
	 * @return
	 */
	public DirEntry getParentDirEntry() {
		return parent;
	}
	
	/**
	 * If fully loaded this returns the largest child, if
	 * not or no children exist, null.
	 * 
	 * @return
	 */
	public DirEntry getLargestChild() throws Throwable {
		if (largestChild != null) return largestChild;
		
		if (!isFullyLoaded() || children == null || children.size() == 0) return null;
		
		long max = 0;
		DirEntry ret = null;
		for(DirEntry child : children) {
			if (child.getSize() >= max) {
				max = child.getSize();
				ret = child;
			}
		}
		largestChild = ret;
		return largestChild;
	}
	
	/**
	 * Returns the type of the file. This normally is the extension, or DIR if it is a folder.
	 * 
	 * @return 
	 */
	public String getType() throws Throwable {
		if (isDirectory()) return TYPE_DIR; 
		return com.google.common.io.Files.getFileExtension(getAbsolutePath());
	}
	
	/**
	 * Returns the creation date and time of the file or folder
	 * 
	 * @return
	 * @throws IOException
	 */
	public Date getCreationDate() throws Throwable {
		// TODO buffer
		//BasicFileAttributes attrs = Files.readAttributes(this.toPath(), BasicFileAttributes.class);
		//return new Date(attrs.creationTime().toMillis());
		return new Date();
	}
	
	/**
	 * Returns the last modification date and time of the file or folder
	 * 
	 * @return
	 * @throws IOException
	 */
	public Date getLastModificationDate() throws Throwable {
		// TODO buffer
		//BasicFileAttributes attrs = Files.readAttributes(this.toPath(), BasicFileAttributes.class);
		//return new Date(attrs.lastModifiedTime().toMillis());
		return new Date();
	}
}
