/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.util.rmi;

/**
 * This interface contains all keys and used to set environment or system
 * variables for RMI connection
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class RmiKeys {

	/**
	 * Key used inside of job, during execution, to communicate with JEM node,
	 * accessing to RMI objects.
	 */
	public static final String JEM_RMI_PORT = "jem.rmi.port";

	/**
	 * To avoid any instantiation
	 */
	private RmiKeys() {
	}

}