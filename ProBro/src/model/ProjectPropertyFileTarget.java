package model;

import java.awt.Color;

/**
 * Definition for a property target, which is a file
 * 
 * @author tweber
 *
 */
public class ProjectPropertyFileTarget extends ProjectPropertyTarget {
	public ProjectPropertyFileTarget(String name, String tableText, boolean isQualifying, Color bgcolor) {
		super(name, tableText, isQualifying, bgcolor);
	}
		
}
