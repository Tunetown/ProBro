package main;

import java.util.ResourceBundle;

/**
 * Message handling. Messages and texts are all stored in a properties file.
 * 
 * @author tweber
 *
 */
public class Messages {
	
	/**
	 * Name of the resource file holding the messages and texts
	 */
	private static final String BUNDLE_NAME = "messages"; 

	/**
	 * Resource bundle for all texts and messages
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Prevents instantiation of this class
	 * 
	 */
	private Messages() {}

	/**
	 * Message getter (incl. generic variable replacement)
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) throws Throwable {
		return getString(key, null, null, null, null);
	}

	/**
	 * Message getter (incl. generic variable replacement)
	 * 
	 * @param key
	 * @param val1
	 * @return
	 */
	public static String getString(String key, Object val1) throws Throwable {
		return getString(key, val1, null, null, null);
	}
	
	/**
	 * Message getter (incl. generic variable replacement)
	 * 
	 * @param key
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static String getString(String key, Object val1, Object val2) throws Throwable {
		return getString(key, val1, val2, null, null);
	}
	
	/**
	 * Message getter (incl. generic variable replacement)
	 * 
	 * @param key
	 * @param val1
	 * @param val2
	 * @param val3
	 * @return
	 */
	public static String getString(String key, Object val1, Object val2, Object val3) throws Throwable {
		return getString(key, val1, val2, val3, null);
	}

	/**
	 * Message getter (incl. generic variable replacement)
	 * 
	 * @param key
	 * @param val1
	 * @param val2
	 * @param val3
	 * @param val4
	 * @return
	 */
	public static String getString(String key, Object val1, Object val2, Object val3, Object val4) throws Throwable {
		String ret = RESOURCE_BUNDLE.getString(key);
		
		// Replace variables
		if (val1 != null) ret = ret.replace("&1", val1.toString());
		if (val2 != null) ret = ret.replace("&2", val2.toString());
		if (val3 != null) ret = ret.replace("&3", val3.toString());
		if (val4 != null) ret = ret.replace("&4", val4.toString());
		
		return ret;		
	}
}
