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
 * Contains the constants necessary for MONGO interface
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class MongoFactory {
	
	/**
	 * Scheme used for MongoClientURI
	 */
	public static final String DATABASE_TYPE = "mongodb";

	/**
	 * To avoid any instantiation
	 */
	private MongoFactory() {
	}

}
