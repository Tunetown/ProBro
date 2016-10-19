package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for one custom column counting files of a specific
 * type. Being instantiated for each <b>projectproperty</b> tag in the XML 
 * project definition file.
 * 
 * @author tweber
 *
 */
public class ProjectProperty {

	/**
	 * Counter definition, defining the extensions for example
	 */
	private ProjectPropertyDefinition def;
	
	/**
	 * Project folder
	 */
	private ProjectDirEntry projectFolder = null;
	
	/**
	 * If the tokens have been found, this holds the reference to the found target folder or file.
	 */
	private ProjectDirEntry target = null;
	
	/**
	 * If a file definition has been found, this definition is stored here (the file itself becomes the target)
	 */
	private ProjectPropertyTarget targetProperty = null;
	
	/**
	 * List of matching files. These are searched inside the folder if one is defined, or in the project
	 * root if no folder is specified.
	 * 
	 */
	private List<ProjectDirEntry> matchingFiles = null;
	
	public ProjectProperty(ProjectDirEntry folder, ProjectPropertyDefinition def) {
		this.projectFolder = folder;
		this.def = def;
	}
	
	/**
	 * Get the property definition for this property
	 * 
	 * @return
	 */
	public ProjectPropertyDefinition getDefinition() {
		return def;
	}
	/**
	 * Set the folder reference for this counter.
	 * 
	 * @param folder
	 */
	public void setFolder(ProjectDirEntry folder) {
		this.projectFolder = folder;
	}
	
	/**
	 * Evaluates the content of the given folder, as specified by the tokens of the instance
	 * 
	 * @throws Throwable 
	 */
	public void load() throws Throwable {
		target = searchForProjectProperty();
		matchingFiles = loadMatchingFiles();
	}
	
	/**
	 * Returns if the given tokens have been found inside the given project folder
	 * @throws Throwable 
	 */
	public boolean isFound() throws Throwable {
		if (def.getTargets().size() > 0) {
			return target != null;
		} else {
			if (matchingFiles.size() > 0) return true;
			return false;
		}
	}
	
	/**
	 * Returns the folder which has been found by the given tokens, or null if nothing has been found.
	 * 
	 * @return
	 */
	public ProjectDirEntry getTarget() {
		return target;
	}
	
	/**
	 * Return the table text, if defined
	 * 
	 * @return
	 */
	public String getTableText() {
		return targetProperty == null ? null : targetProperty.getTableText();
	}
	

	/**
	 * Evaluates the property inside the project folder of this instance.
	 * 
	 * @param tokens
	 * @return
	 */
	private ProjectDirEntry searchForProjectProperty() throws Throwable {
		if (projectFolder == null) throw new Exception("FileCounter folder not initialized yet!");
		if (!projectFolder.isDirectory()) return null;

		for (DirEntry d : projectFolder.getChildren()) {
			// Child is a file: See if it matches one of the file definitions of the property
			for (ProjectPropertyTarget token : def.getTargets()) {
				if (d.getName().toLowerCase().equals((token.getFileName()).toLowerCase())) {
					if ((!d.isDirectory() && token instanceof ProjectPropertyFileTarget) || (d.isDirectory() && token instanceof ProjectPropertyFolderTarget)) {
						targetProperty = token;
						return (ProjectDirEntry) d;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the property which triggered the target file/folder, if found, null if no target exists
	 * 
	 * @return
	 */
	public ProjectPropertyTarget getTargetProperty() {
		return targetProperty; 
	}

	/**
	 * Returns the amount of matching folders inside the target
	 * 
	 * @return
	 * @throws Throwable 
	 */
	private List<ProjectDirEntry> loadMatchingFiles() throws Throwable {
		if (def.getTargets().size() > 0) {
			// If any file or folders are defined, we have to search there
			if (target != null) {
				return getTarget().getMatchingFiles(def.getExtensions());
			} else return new ArrayList<ProjectDirEntry>();
		} else return projectFolder.getMatchingFiles(def.getExtensions());
	}

	/**
	 * Returns the extension which triggered the given file being counted
	 * 
	 * @param file
	 * @return
	 * @throws Throwable 
	 */
	public ProjectPropertyExtension getMatchingExtension(ProjectDirEntry file) throws Throwable {
		for (ProjectPropertyExtension ext : def.getExtensions()) {
			if (file.getExtension().toLowerCase().equals(ext.getExtension().toLowerCase())) return ext;
		}
		return null;
	}
	
	/**
	 * Returns if any of the files inside the target match a qualifying extension
	 * 
	 * @return
	 * @throws Throwable 
	 */
	public boolean hasQualifyingExtensions() throws Throwable {
		for(ProjectDirEntry file : matchingFiles) {
			if (getMatchingExtension(file).isQualifying()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns if the property, if found, is qualifying for the folder to be a project folder.
	 * 
	 * @return
	 * @throws Throwable 
	 */
	public boolean isQualifying() throws Throwable {
		if (targetProperty != null) {
			if (targetProperty.isQualifying()) return true;
			else if (hasQualifyingExtensions()) return true;
		}
		if (hasQualifyingExtensions()) return true;
		return false;
	}

	/**
	 * Returns the matching files (files inside the target if defined or the project folder) for the property´s extensions.
	 * 
	 * @return
	 */
	public List<ProjectDirEntry> getMatchingFiles() {
		return matchingFiles;
	}
}
