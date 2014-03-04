package org.pepstock.jem.log;

/**
 * Contains all message log codes
 * @author Marco "Cuc" Cuccato
 */
@SuppressWarnings("javadoc")
public enum MessageCode {

	ANT("A"),
	ANT_UTIL("Z"),
	CLIENT("E"),
	GDG("D"),
	GRS("V"),
	IO("I"),
	JPPF("J"),
	LICENSE("L"),
	NODE("C"),
	NOTIFY("N"),
	RESOURCE("R"),
	SPRING_BATCH("S"),
	SUBMIT("W"),
	SWARM_NODE("O"),
	USER_INTERFACE("G"),
	UTIL("B");
	
	
	
	private String code;
	
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
