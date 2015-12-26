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
package org.pepstock.jem.node.hazelcast;



/**
 * Contains all constants, keys of Hazelcast collections
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class IdGenerators {

	/**
	 * Key for Hazelcast structure which are able to generate unique ID in the
	 * cluster. It is used to create a unique JOB id.
	 * 
	 * @see org.pepstock.jem.Job#setId(String)
	 */
	public static final String JOB = "org.pepstock.jem.job.id";

	/**
	 * To avoid any instantiation
	 */
	private IdGenerators() {
	}

}
