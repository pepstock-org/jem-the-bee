/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
	DARK_CYAN("#57BCD9"),
	/**
	 * 
	 */
	LIGHT_CYAN("#74FEF8"),
	/**
	 * 
	 */
	LIGHT_ORANGE("#FFAC62"),
	/**
	 * 
	 */
	PINK("#FE98F1"),
	/**
	 * 
	 */
	BLUE("#7BA7E1"),
	/**
	 * 
	 */
	YELLOW("#FFE920");	
	
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
	
	/**
	 * @return a random color :) 
	 */
	public static final ColorsHex randomColor() {
		ColorsHex[] allColors = ColorsHex.values();
		return allColors[Random.nextInt(allColors.length)];
	}

}
