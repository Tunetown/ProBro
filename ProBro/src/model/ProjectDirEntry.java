package model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Main;

/**
 * A directory entry (file or folder). This instance can be loaded fully, which
 * means that the whole recursive folder tree is loaded and linked, also all
 * sizes are calculated, to provide accurate folder sizes (which are -1 if not
 * loaded). Also, the instance can be loaded gradually. A later call to
 * loadFully will then complete the data anyway.
 * 
 * @author tweber
 *
 */
public class ProjectDirEntry extends DirEntry {
	private static final long serialVersionUID = 1L;

	/**
	 * File counter column models. 
	 */
	private List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
	
	/**
	 * Year of project (determined by different things, see usage)
	 */
	private String projectYear = null;
	
	/**
	 * This is true if the year of project is estimated by the earliest creation
	 * date of any file or folder inside the project
	 */
	private boolean yearIsEstimated = false;

	/**
	 * Date/time of the last modification of the last modification of any file
	 * or folder inside the project
	 */
	private Date projectLastModified = null;
	
	/**
	 * Are the project properties (num of mixes/masters etc, NOT the recursive
	 * search!) loaded
	 */
	private boolean projectPropertiesLoaded = false;

	/**
	 * Recursive project list. Contains all projects inside this folder. If
	 * null, the projects have not been searched for yet.
	 */
	private List<ProjectDirEntry> projectList = null;

	/**
	 * Recursive list of all folders not identified as projects. Contains all folder which 
	 * are not contained in projectsList.
	 */
	private List<ProjectDirEntry> projectLeftoversList = null;
	
	/**
	 * Buffer for isContainedInProject
	 */
	private boolean isContainedInProject;
	
	/**
	 * Buffer for isProject
	 */
	private int isProjectBuffer = -1;
	
	/**
	 * Buffer for getExtension
	 */
	private String extension = null;
	
	/**
	 * Earliest matching file/folder (buffer)
	 */
	private ProjectDirEntry earliestMatching;
	
	public ProjectDirEntry(String name) throws Throwable {
		super(name);
	}

	public ProjectDirEntry(String name, DirEntry parent) throws Throwable {
		super(name, parent);
	}

	public ProjectDirEntry(String name, DirEntry parent, boolean loadHiddenFiles) throws Throwable {
		super(name, parent, loadHiddenFiles);
	}

	/**
	 * Create a child instance
	 * 
	 */
	@Override
	protected DirEntry createChild(File file) throws Throwable {
		return new ProjectDirEntry(file.getAbsolutePath(), this);
	}

	/**
	 * Returns the project list. This does a deep search in the current folder,
	 * and searches for a specific folder structure
	 * 
	 * @return
	 */
	public List<ProjectDirEntry> getProjectList() throws Throwable {
		if (!isDirectory()) return null;
		if (!projectsLoaded()) return new ArrayList<ProjectDirEntry>();
		return projectList;
	}

	/**
	 * Returns the list of all folders inside this folder which are not identified as projects.
	 * 
	 * @return
	 * @throws Throwable
	 */
	public List<ProjectDirEntry> getProjectLeftoversList() throws Throwable {
		if (!isDirectory()) return null;
		if (!projectsLoaded()) return new ArrayList<ProjectDirEntry>();
		return projectLeftoversList;
	}
	
	/**
	 * Has the search for projects been taken already?
	 * 
	 * @return
	 */
	public boolean projectsLoaded() {
		return projectList != null;
	}

	/**
	 * Load project data (must be called before calling getProjectList()).
	 * 
	 * @param list
	 * @param current
	 * @throws IOException
	 */
	public void loadProjects() throws Throwable {
		loadProjects(this);
	}
		
	/**
	 * Recursive main algorithm for loadProjects()
	 * 
	 * @param root
	 * @throws Throwable
	 */
	private void loadProjects(ProjectDirEntry root) throws Throwable {
		if (projectsLoaded())
			return;

		if (!isDirectory())
			return;

		projectList = new ArrayList<ProjectDirEntry>();
		projectLeftoversList = new ArrayList<ProjectDirEntry>();

		for(String ext : Main.getProjectDefinition().getIgnoreExtensions()) {
			if (getExtension().toLowerCase().equals(ext.toLowerCase())) 
				return;			
		}

		loadProjectProperties();

		if (isProject()) {
			projectList.add(this);
			loadFully();
		} else {
			if (!isContainedInProject && root != this) 
				projectLeftoversList.add(this);
		}

		// Recurse search to all children
		for (DirEntry d : getChildren()) {
			if (d.isDirectory()) {
				ProjectDirEntry p = (ProjectDirEntry)d;
				if (isProject() || isContainedInProject) p.isContainedInProject = true;
				
				p.loadProjects(root);
				
				projectList.addAll(p.projectList);
				projectLeftoversList.addAll(p.projectLeftoversList);
			}
		}
	}

	/**
	 * Look for project properties in the current folder
	 */
	private void loadProjectProperties() throws Throwable {
		if (projectPropertiesLoaded()) return;

		// Set up and execute the file counters
		properties = new ArrayList<ProjectProperty>();
		
		for(ProjectPropertyDefinition d : Main.getProjectDefinition().getPropertyDefinitions()) {
			ProjectProperty cnt = new ProjectProperty(this, d);
			cnt.load();
			properties.add(cnt);
		}

		// If some have been found, also determine other relevant information
		if (isProject(true)) {
			// Year of project
			projectYear = determineProjectYear();
			// Last modified date (latest of all inside the project)
			projectLastModified = determineProjectLastModified();
		}

		projectPropertiesLoaded = true;
	}

	/**
	 * Is all projects info already gathered?
	 * 
	 * @return
	 */
	public boolean projectPropertiesLoaded() {
		return projectPropertiesLoaded;
	}

	/**
	 * Is this a project?
	 * 
	 * @return
	 */
	public boolean isProject() throws Throwable {
		if (isProjectBuffer > -1) return (isProjectBuffer == 0) ?  false : true;
		isProjectBuffer = isProject(false) ? 1 : 0;
		return (isProjectBuffer == 0) ?  false : true;
	}

	/**
	 * Does this folder contain a project? 
	 * 
	 * @return
	 * @throws Throwable 
	 */
	public boolean containsProject() throws Throwable {
		if (!this.projectPropertiesLoaded()) return false;
		
		if (isProject()) return true;
		
		for (DirEntry d: getChildren()) {
			ProjectDirEntry p = (ProjectDirEntry)d;
			if (p.containsProject()) return true;
		}
		
		return false;
	}
	
	/**
	 * Returns if this file or folder is contained in a project
	 * 
	 * @return
	 * @throws Throwable 
	 */
	public boolean isContainedInProject() throws Throwable {
		return isContainedInProject;
	}
	
	/**
	 * Is this a project?
	 * 
	 * @return
	 */
	private boolean isProject(boolean noLoad) throws Throwable {
		if (!isDirectory()) return false;
		if (!noLoad) loadProjectProperties();

		// If any of the loaded counters has found something, this returns true 
		for(ProjectProperty c : properties) {
			if (c.isFound() && c.isQualifying()) return true;
		}

		return false;
	}

	/**
	 * Determine the project year. If there is some year in the folder name,
	 * this is taken. If not, the earliest modification date among the files in
	 * the project is taken instead.
	 * 
	 * @return
	 * @throws IOException
	 */
	private String determineProjectYear() throws Throwable {
		// Search for 19XX
		Pattern p = Pattern.compile("19[\\d][\\d]");
		Matcher m = p.matcher(getName());
		if (m.find()) return m.group();

		// Search for 20XX
		Pattern p2 = Pattern.compile("20[\\d][\\d]");
		Matcher m2 = p2.matcher(getName());
		if (m2.find()) return m2.group();

		yearIsEstimated = true;

		// Nothing found: Creation date of the earliest file inside all counters
		ProjectDirEntry earliest = getEarliestMatchingFile();
		if (earliest != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			return df.format(earliest.getCreationDate());
		}

		// Nothing found finally
		return "-";
	}
	
	/**
	 * Returns the earliest created audio file in a folder
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public ProjectDirEntry getEarliestMatchingFile() throws Throwable {
		if (earliestMatching == null) {
			for (ProjectProperty p : properties) {
				for (ProjectDirEntry d : p.getMatchingFiles()) {
					if (earliestMatching == null || earliestMatching.getCreationDate().compareTo(d.getCreationDate()) < 0) {
						earliestMatching = d;
					}
				}
			}
		}
		return earliestMatching;
	}

	/**
	 * Determine last modification of the project, which is the latest
	 * modification date of all files inside the project.
	 * 
	 * @return
	 */
	private Date determineProjectLastModified() throws Throwable {
		Date ft = getLastModificationDate();
		
		if (isDirectory()) {
			for(DirEntry c : getChildren()) {
				ProjectDirEntry p = (ProjectDirEntry)c;
				Date childTime = p.determineProjectLastModified();
				
				if (ft.compareTo(childTime) < 0) {
					ft = childTime;
				}
			}
		}
		
		return ft;
	}

	/**
	 * Returns the project year (buffered). If there is some year in the folder
	 * name, this is taken. If not, the earliest modification date among the
	 * files in the project is taken instead.
	 * 
	 * @return
	 */
	public String getProjectYear() throws Throwable {
		loadProjectProperties();
		return projectYear;
	}
	
	/**
	 * Returns true if the projects year is estimated by audio files. If false,
	 * the year has been determined from the folder name of the project.
	 * 
	 * @return
	 */
	public boolean isProjectYearEstimated() throws Throwable {
		loadProjectProperties();
		return yearIsEstimated;
	}

	/**
	 * Returns the project�s last modification (buffered). This is the latest
	 * modification date of all files inside the project.
	 * 
	 * @return
	 */
	public Date getProjectLastModified() throws Throwable {
		loadProjectProperties();
		return projectLastModified;
	}

	/**
	 * Return true if the file extension matches one of the given extensions
	 * 
	 * @param tokens
	 * @return
	 */
	private boolean matchesExtension(ProjectPropertyExtension extension) throws Throwable {
		if (getExtension().toLowerCase().equals(extension.getExtension().toLowerCase())) return true;
		return false;
	}
	
	/**
	 * Returns the file extension
	 * 
	 * @return
	 */
	public String getExtension() throws Throwable {
		if (extension != null) return extension;
		extension = com.google.common.io.Files.getFileExtension(getAbsolutePath());
		return extension;
	}

	/**
	 * If fully loaded this returns the largest project, if
	 * not or no projects exist, null.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public ProjectDirEntry getLargestProject() throws Throwable {
		return getLargestOf(getProjectList());
	}

	/**
	 * If fully loaded this returns the largest non-project folder.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public ProjectDirEntry getLargestProjectLeftover() throws Throwable {
		return getLargestOf(getProjectLeftoversList());
	}

	/**
	 * Get the largest file of a set of files
	 * 
	 * @param list
	 * @return
	 * @throws Throwable
	 */
	private ProjectDirEntry getLargestOf(List<ProjectDirEntry> list) throws Throwable {
		if (!projectsLoaded()) return null;
		if (list == null) return null;
		
		long max = 0;
		ProjectDirEntry ret = null;
		for(ProjectDirEntry child : list) {
			if (!child.isFullyLoaded()) child.loadFully();
			if (child.getSize() >= max) {
				max = child.getSize();
				ret = child;
			}
		}
		
		return ret;
	}

	/**
	 * Returns the list of available file counters
	 * 
	 * @return
	 */
	public List<ProjectProperty> getProjectProperties() {
		return properties;
	}

	/**
	 * Returns all files or folders inside this folder which match the given list of extensions
	 * 
	 * @param size
	 * @return
	 * @throws Throwable 
	 */
	public List<ProjectDirEntry> getMatchingFiles(List<ProjectPropertyExtension> extensions) throws Throwable {
		List<ProjectDirEntry> ret = new ArrayList<ProjectDirEntry>();
		for(ProjectPropertyExtension e : extensions) {
			ret.addAll(getMatchingFiles(e, this));
		}
		return ret;
	}
	
	/**
	 * Internal wrapped method (see getMatchingFiles(extensions)). Returns all files matching the given extension inside this folder,
	 * and also takes care that non-recursive extensions will only be searched inside the root. 
	 * 
	 * @param extension
	 * @param root
	 * @return
	 * @throws Throwable
	 */
	private List<ProjectDirEntry> getMatchingFiles(ProjectPropertyExtension extension, ProjectDirEntry root) throws Throwable {
		List<ProjectDirEntry> ret = new ArrayList<ProjectDirEntry>();
		
		if (matchesExtension(extension)) {
			ret.add(this);
		}
		
		if (extension.isRecursive() || root == this) {
			if (isDirectory()) {
				for(DirEntry c : getChildren()) {
					ProjectDirEntry p  = (ProjectDirEntry)c;
					ret.addAll(p.getMatchingFiles(extension, root));
				}
			}
		}
		
		return ret;
	}
}
