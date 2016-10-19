package model;

import java.util.List;

/**
 * Definition of a project property
 * 
 * @author tweber
 *
 */
public class ProjectPropertyDefinition {

	/**
	 * File extensions which are counted by this counter
	 */
	private List<ProjectPropertyExtension> extensions = null;
	
	/**
	 * Folders in which to count the files
	 */
	private List<ProjectPropertyTarget> targets = null;
	
	/**
	 * Column header
	 */
	private String header;
	
	public ProjectPropertyDefinition(List<ProjectPropertyTarget> targets, List<ProjectPropertyExtension> extensions, String header) {
		this.targets = targets;
		this.extensions = extensions;
		this.header = header;
	}
	
	/**
	 * Returns the defined targets for this property (files/folders)
	 * 
	 * @return
	 */
	public List<ProjectPropertyTarget> getTargets() {
		return targets;
	}
	
	/**
	 * Returns the defined extensions for this property
	 * 
	 * @return
	 */
	public List<ProjectPropertyExtension> getExtensions() {
		return extensions;
	}
	
	/**
	 * Returns the column header for this counter
	 * 
	 * @return
	 */
	public String getHeader() {
		return header;
	}
	
	/**
	 * Returns if the property, if found, is qualifying for the folder to be a project folder.
	 * 
	 * @return
	 */
	public boolean isQualifying() {
		for(ProjectPropertyTarget f : getTargets()) {
			if (f.isQualifying()) return true;
		}
		for(ProjectPropertyExtension e : getExtensions()) {
			if (e.isQualifying()) return true;
		}
		return false;
	}
}
