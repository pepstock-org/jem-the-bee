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
package org.pepstock.jem;

/**
 * Is the class representing the status of a Service 
 * 
 * @author Simone "Busy" Businaro
 *
 */
public enum ServiceStatus {

	/**
	 * Starting
	 */
	STARTING,
	/**
	 * Started
	 */
	STARTED,
	/**
	 * Shutting down
	 */
	SHUTTING_DOWN,
	/**
	 * Shut down
	 */
	SHUT_DONW;
	
}
