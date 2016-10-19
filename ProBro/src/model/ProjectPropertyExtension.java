package model;

import java.awt.Color;

/**
 * Property extension definition
 * 
 * @author tweber
 *
 */
public class ProjectPropertyExtension {
	
	private String extension;
	private Color bgColor;
	private boolean isQualifying; 
	private boolean recursiveSearch; 
	
	public ProjectPropertyExtension(String extension, Color bgColor, boolean isQualifying, boolean recursiveSearch) {
		this.extension = extension;
		this.bgColor = bgColor;
		this.isQualifying = isQualifying;
		this.recursiveSearch = recursiveSearch;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public Color getBgColor() {
		return bgColor;
	}

	public boolean isQualifying() {
		return isQualifying;
	}
	
	public boolean isRecursive() {
		return recursiveSearch;
	}
}
