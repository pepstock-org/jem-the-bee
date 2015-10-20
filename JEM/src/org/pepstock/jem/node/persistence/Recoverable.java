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
package org.pepstock.jem.node.persistence;

import org.pepstock.jem.log.JemException;

/**
 * Interface to recover the red statements
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public interface Recoverable {
	
	/**
	 * Performs the recover of redo statement previously store in HZ map, waiting form
	 * the DB availability
	 * @param statement redo statement to apply
	 * @throws JemException if any error occurs on DB
	 */
	void recover(RedoStatement statement) throws JemException;
	
	/**
	 * Checks if the database is available
	 * @throws JemException if DB is not available
	 */
	void check() throws JemException;

}
