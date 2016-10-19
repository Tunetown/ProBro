package view.table.projects;

import main.Main;
import model.ProjectProperty;

/**
 * Class for representing audio file counts in projects view
 * 
 * @author tweber
 *
 */
@SuppressWarnings("rawtypes")
public class ProjectPropertyValue implements Comparable {
	
	/**
	 * File counter for which this instance is responsible to.
	 */
	private ProjectProperty projectProperty;
	
	public ProjectPropertyValue(ProjectProperty counter) throws Throwable {
		this.projectProperty = counter;
	}

	/**
	 * Returns the counter instance
	 * 
	 * @return
	 */
	public ProjectProperty getProjectProperty() throws Throwable {
		return projectProperty;
	}
	
	/**
	 * Table sort comparator
	 * 
	 */
	@Override
	public int compareTo(Object o) {
		try {
			ProjectPropertyValue s = (ProjectPropertyValue)o;
			Integer me = getNumericalOutput();
			Integer other = s.getNumericalOutput();
			return me.compareTo(other);
			
		} catch (Throwable e) {
			Main.handleThrowable(e);
			return 0;
		}
	}
	
	/**
	 * Delivers the visible output string
	 * 
	 * @return
	 */
	public String getOutput() throws Throwable {
		if (!projectProperty.isFound()) return "-";

		// Do we have a fixed text to show if the property has been found?
		if (projectProperty.getTableText() != null) return projectProperty.getTableText();
		
		// Show count of found audio files inside the property target
		return Integer.toString(projectProperty.getMatchingFiles().size()); 
	}
	
	/**
	 * Get a numerical quantization for this property value for sorting
	 * 
	 * @return
	 * @throws Throwable 
	 */
	private int getNumericalOutput() throws Throwable {
		if (!projectProperty.isFound()) return -1;

		// Do we have a fixed text to show if the property has been found?
		if (projectProperty.getTableText() != null) return 1;
				
		// Return count of found audio files inside the property target
		return projectProperty.getMatchingFiles().size();
	}
}
