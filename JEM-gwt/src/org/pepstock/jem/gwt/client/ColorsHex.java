package org.pepstock.jem.gwt.client;

import com.google.gwt.user.client.Random;

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
	
	/**
	 * @return a random color :) 
	 */
	public static final ColorsHex randomColor() {
		ColorsHex[] allColors = ColorsHex.values();
		return allColors[Random.nextInt(allColors.length)];
	}
	
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
