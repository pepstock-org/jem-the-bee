/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.persistence.mongo;

/**
 * Enumeration of MongoDB operators to use into queries
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public enum Operator {
	
	/**
	 * Equals 
	 */
	EQUALS("$eq"),
	/**
	 * Not equals
	 */
	NOT_EQUALS("$ne"),
	/**
	 * Greater or equals than
	 */
	GREATER_OR_EQUALS_THAN("$gte"),
	/**
	 * Greater than
	 */
	GREATER_THAN("$gt"),
	/**
	 * Less or equals than
	 */
	LESS_OR_EQUALS_THAN("$lte"),
	/**
	 * Less than
	 */
	LESS_THAN("$lt"),
	/**
	 * IN a list of values
	 */
	IN_LIST("$in"),
	/**
	 * Regular expression
	 */
	REG_EX("$regex"),
	/**
	 * If a field exists in the document
	 */
	EXISTS("$exists"),
	/**
	 * NOT logical operation
	 */
	NOT("$not"),
	/**
	 * OR logical operation
	 */
	OR("$or"),
	/**
	 * AND logical operation
	 */
	AND("$and");
	
	private String name = null;

	/**
	 * Creates the object with the MONGO keyword
	 * @param name MONGO keyword
	 */
	private Operator(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
