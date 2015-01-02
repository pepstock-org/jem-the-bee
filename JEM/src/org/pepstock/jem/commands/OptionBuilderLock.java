/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.commands;

/**
 *  This class is used to synchronized the access to OptionBuilder
 *  inside the commands. This is needed because OptionBuilder is not
 *  thread safe.
 *  
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public final class OptionBuilderLock {

	private static final Object LOCK = new Object();
	
	/**
	 * To avoid any instantiation
	 */
	private OptionBuilderLock() {
		
	}

	/**
	 * Static method to get a synchronized object
	 * @return the semaphore
	 */
	public static Object getLock(){
		return LOCK;
	}
}