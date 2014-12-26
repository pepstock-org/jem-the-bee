package org.pepstock.jem.log;

/**
 * All JEM messages contains a char which identify the sub component of JEM.
 * <br>
 * In this enumeration we can have all characters used on messages, from different modules.
 *  
 * @author Marco "Cuc" Cuccato
 * @version 1.3
 */
public enum MessageCode {

	/**
	 * ANT module
	 */
	ANT("A"),
	/**
	 * ANT utilities (in the enterprise project) module
	 */
	ANT_UTIL("Z"),
	/**
	 * GDG management module
	 */
	GDG("D"),
	/**
	 * GRS node (in the enterprise project) module
	 */
	GRS("V"),
	/**
	 * IO utilities module
	 */
	IO("I"),
	/**
	 * JPPF module
	 */
	JPPF("J"),
	/**
	 * License utility (in the enterprise project) module
	 */	
	LICENSE("L"),
	/**
	 * Core JEM module
	 */	
	NODE("C"),
	/**
	 * Notification engine module
	 */	
	NOTIFY("N"),
	/**
	 * Common resources module
	 */
	RESOURCE("R"),
	/**
	 * Spring batch module
	 */
	SPRING_BATCH("S"),
	/**
	 * SUBMIT engines module
	 */
	SUBMIT("W"),
	/**
	 * SWARM module
	 */
	SWARM_NODE("O"),
	/**
	 * GWT and user interface module
	 */
	USER_INTERFACE("G"),
	/**
	 * Common utilities module
	 */
	UTIL("B");
	
	private String code;
	
	/**
	 * Creates the message code using the char as argument
	 * @param code char of sub component which will use thos message codes
	 */
	private MessageCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the associated Id
	 */
	public String getCode() {
		return code;
	}
}
