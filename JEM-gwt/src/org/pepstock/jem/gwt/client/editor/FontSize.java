/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.editor;


/**
 * List of font sizes, available in JEM editor.
 * For more details, see http://www.w3schools.com/cssref/pr_font_font-size.asp
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
@SuppressWarnings("javadoc")
public enum FontSize {
	
    SMALL("Small", "small"),
	MEDIUM("Medium", "medium"),
	LARGE("Large", "large"),
	EXTRA_LARGE("Extra-Large",  "x-large");
	
	private final String name;
	
	private final String cssValue;
	
	private FontSize(String name, String cssValue) {
		this.name = name;
		this.cssValue = cssValue;
	}
	
	/**
	 * @return name used in set mode
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the cssValue
	 */
	public String getCssValue() {
		return cssValue;
	}
	
	
}
