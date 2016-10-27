package model;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import main.Messages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Loads and represents the definitions for searching project properties (names of 
 * master, mix etc folders, audio file and session file extensions etc), loaded from XML.
 *  
 * @author xwebert
 *
 */
public class ProjectDefinition {

	private static final String TAG_PROJECT_DEFINITION = "projectdefinition";
	private static final String TAG_PROJECT_PROPERTY = "projectproperty";
	private static final String TAG_IGNORE = "ignore";
	private static final String TAG_TARGET_FOLDER = "folder";
	private static final String TAG_TARGET_FILE = "file";
	private static final String TAG_EXTENSION = "extension";
	private static final String ATTRIBUTE_HEADER = "header";
	private static final String ATTRIBUTE_TABLETEXT = "tabletext";
	private static final String ATTRIBUTE_QUALIFYING = "qualifying";
	private static final String ATTRIBUTE_BGCOLOR = "bgcolor";
	private static final String ATTRIBUTE_RECURSIVE = "recursive";
	
	private static final String DELIMINATOR_COLORCOMPONENTS = ";";
	
	/**
	 * List of counters derived by the filecounter tags
	 */
	private List<ProjectPropertyDefinition> propertyDefinitions = new ArrayList<ProjectPropertyDefinition>();

	/**
	 * List of ignore extensions (see class documentation)
	 * 
	 */
	private List<String> ignoreExtensions = new ArrayList<String>();
	
	/**
	 * File to load the definition from
	 */
	private File definitionFile;
	
	public ProjectDefinition(File file) throws Throwable {
		this.definitionFile = file;
		parseXmlFile();
	}
	
	/**
	 * Load and parse the XML definitions into the instance attributes
	 */
	private void parseXmlFile() throws Throwable {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(definitionFile);

		// Get project definition node
		NodeList definitions = dom.getElementsByTagName(TAG_PROJECT_DEFINITION);
		if(definitions == null || definitions.getLength() < 1) {
			throw new Exception(Messages.getString("XMLError_DefNotFOund", TAG_PROJECT_DEFINITION));
		}
		Element definition = (Element)definitions.item(0);

		// Get project properties and evaluate them
		NodeList countDefs = definition.getElementsByTagName(TAG_PROJECT_PROPERTY);
		if(countDefs == null || countDefs.getLength() < 1) {
			throw new Exception(Messages.getString("XMLError_DefNoProperties", TAG_PROJECT_PROPERTY));
		}
		
		for(int i=0; i<countDefs.getLength(); i++) {
			Element c = (Element)countDefs.item(i);

			evalProperty(c, i);
		}

		// Ignore folders
		NodeList ignoreHd = definition.getElementsByTagName(TAG_IGNORE);
		if(ignoreHd != null && ignoreHd.getLength() == 1) {
			Element ignore = (Element)ignoreHd.item(0);
			
			NodeList ie = ignore.getElementsByTagName(TAG_EXTENSION);
			if(ie != null && ie.getLength() > 0) {
				for(int i=0; i<ie.getLength(); i++) {
					Element c = (Element)ie.item(i);
					ignoreExtensions.add(c.getTextContent());
				}
			}
		}
	}
	
	/**
	 * Evaluate one property from XML and create/add a new definition instance
	 * 
	 * @param c
	 * @throws Throwable 
	 */
	private void evalProperty(Element c, int i) throws Throwable {
		// Get folders or files definitions
		List<ProjectPropertyTarget> targets = new ArrayList<ProjectPropertyTarget>();
		getTargets(c, TAG_TARGET_FOLDER, targets);
		getTargets(c, TAG_TARGET_FILE, targets);

		// Get extensions
		List<ProjectPropertyExtension> ext = new ArrayList<ProjectPropertyExtension>();
		getExtensions(c, TAG_EXTENSION, ext);

		// Get header
		String header = c.getAttribute(ATTRIBUTE_HEADER);
		if (header == null || header.length() == 0) 
			throw new Exception(Messages.getString("XMLError_DefPropertyNoHeader", i));

		ProjectPropertyDefinition counter = new ProjectPropertyDefinition(targets, ext, header);
		propertyDefinitions.add(counter);
	}
	
	/**
	 * Returns all file counter definitions
	 * 
	 * @return
	 */
	public List<ProjectPropertyDefinition> getPropertyDefinitions() {
		return propertyDefinitions;
	}

	/**
	 * Loads the contents of the given tag into the string array list
	 * 
	 * @param tagName
	 * @param list
	 */
	private void getTargets(Element def, String innerTag, List<ProjectPropertyTarget> list) throws Throwable {
		NodeList tags = def.getElementsByTagName(innerTag);
		
		if (tags == null || tags.getLength() < 1) return;
		
		for(int i = 0; i < tags.getLength(); i++) {
			Element targetName = (Element)tags.item(i);
			boolean q = targetName.getAttribute(ATTRIBUTE_QUALIFYING).length() == 0 ? false : true;
			Color c = parseColor(targetName.getAttribute(ATTRIBUTE_BGCOLOR));

			if (innerTag == TAG_TARGET_FILE) list.add(new ProjectPropertyFileTarget(targetName.getTextContent(), targetName.getAttribute(ATTRIBUTE_TABLETEXT), q, c)); 
			if (innerTag == TAG_TARGET_FOLDER) list.add(new ProjectPropertyFolderTarget(targetName.getTextContent(), targetName.getAttribute(ATTRIBUTE_TABLETEXT), q, c)); 
		}
	}

	/**
	 * Loads the contents of the given tag into the string array list
	 * 
	 * @param tagName
	 * @param list
	 */
	private void getExtensions(Element def, String innerTag, List<ProjectPropertyExtension> list) throws Throwable {
		NodeList tag = def.getElementsByTagName(innerTag);
		
		if (tag == null || tag.getLength() < 1) return;

		if(tag != null && tag.getLength() > 0) {
			for(int i = 0; i < tag.getLength(); i++) {
				Element extension = (Element)tag.item(i);
				Color bgColor = parseColor(extension.getAttribute(ATTRIBUTE_BGCOLOR));
				boolean q = extension.getAttribute(ATTRIBUTE_QUALIFYING).length() == 0 ? false : true;
				boolean r = extension.getAttribute(ATTRIBUTE_RECURSIVE).length() == 0 ? false : true;
				list.add(new ProjectPropertyExtension(extension.getTextContent(), bgColor, q, r));
			}
		}		
	}
	
	/**
	 * Returns the extensions to be ignored as folders in project search
	 * 
	 * @return
	 */
	public List<String> getIgnoreExtensions() {
		return ignoreExtensions;
	}
	
	/**
	 * Parse the color attribute string from the XML definition, to a Color object.
	 * 
	 * @param in
	 * @return
	 * @throws Exception 
	 */
	private Color parseColor(String in) throws Exception {
		String[] split = in.split(DELIMINATOR_COLORCOMPONENTS);
		if (split.length != 3) return null;
		int r = Integer.parseInt(split[0]);
		int g = Integer.parseInt(split[1]);
		int b = Integer.parseInt(split[2]);
		return new Color(r,g,b);
	}
	
	/**
	 * Returns the loaded definition file
	 * 
	 * @return
	 */
	public File getFile() {
		return this.definitionFile;
	}
}
