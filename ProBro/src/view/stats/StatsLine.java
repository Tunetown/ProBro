package view.stats;

/**
 * Name/value pair model for overall stats overview popup
 *  
 * @author tweber
 *
 */
public class StatsLine {

	private String name;
	private String value;
	
	public StatsLine(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the line value name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the line value
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}
}
