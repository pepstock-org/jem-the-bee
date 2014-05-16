package org.pepstock.jem.gwt.client;

/**
 * Contains some HTML HEX color codes 
 * @author Marco "Fuzzo" Cuccato
 */
public enum ColorsHex {

	/**
	 * 
	 */
	LIGHT_RED("#FFA8A8"),
	/**
	 * 
	 */
	LIGHT_BLUE("#99C7FF"),
	/**
	 * 
	 */
	LIGHT_GREEN("#7CEB98"),
	/**
	 * 
	 */
	VIOLET("#8C8CFF"),
	/**
	 * 
	 */
	CYAN("#57BCD9"),
	/**
	 * 
	 */
	LIGHT_ORANGE("#FFAC62");
	
	private String hexCode;
	
	private ColorsHex(String hexCode) {
		this.hexCode = hexCode;
	}
	
	/**
	 * @return the color code
	 */
	public String getCode() {
		return hexCode;
	}
}
