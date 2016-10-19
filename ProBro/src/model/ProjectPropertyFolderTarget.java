package model;

import java.awt.Color;

/**
 * Definition for a property target, which is a folder
 * 
 * @author tweber
 *
 */
public class ProjectPropertyFolderTarget extends ProjectPropertyTarget {
	public ProjectPropertyFolderTarget(String name, String tableText, boolean isQualifying, Color bgcolor) {
		super(name, tableText, isQualifying, bgcolor);
	}
}
