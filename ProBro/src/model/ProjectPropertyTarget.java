package model;

import java.awt.Color;

/**
 * Property target. This can be a file or folder definition.
 * 
 * @author tweber
 *
 */
public class ProjectPropertyTarget {
	
	private String name;
	private String tableText;
	private boolean isQualifying;
	private Color bgColor;
	
	public ProjectPropertyTarget(String name, String tableText, boolean isQualifying, Color bgcolor) {
		this.name = name;
		this.tableText = tableText.length() == 0 ? null : tableText;
		this.isQualifying = isQualifying;
		this.bgColor = bgcolor;
	}
	
	public String getFileName() {
		return name;
	}
	
	public String getTableText() {
		return tableText;
	}

	public boolean isQualifying() {
		return isQualifying;
	}
	
	public Color getBgColor() {
		return bgColor;
	}
}
