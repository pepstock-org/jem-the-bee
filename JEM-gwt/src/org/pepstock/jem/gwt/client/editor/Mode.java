/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Andrea "Stock" Stocchero
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
 * List of modes, available in JEM.
 * For more details, see ACE documentation, about MODE
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
@SuppressWarnings("javadoc")
public enum Mode {
	
	SH("sh"),
	BATCHFILE("batchfile"),
    GROOVY("groovy"),
	JAVA("java"),
	JAVASCRIPT("javascript"),
	PROPERTIES("properties"),
	TEXT("text"),
	TEXTILE("textile"),
	XML("xml");
	
	private final String name;
	
	private Mode(String name) {
		this.name = name;
	}
	
	/**
	 * @return name used in set mode
	 */
	public String getName() {
		return name;
	}
}
