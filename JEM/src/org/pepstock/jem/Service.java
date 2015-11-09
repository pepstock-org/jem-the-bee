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
 * Is the abstract class for a service
 * 
 * @author Simone "Busy" Businaro
 * @version 2.0
 * 
 */
public abstract class Service {

	private ServiceStatus status;

	/**
	 * Start the service
	 */
	public abstract void start();

	/**
	 * Shut down the service
	 */
	public abstract void shutdown();

	/**
	 * @return the status of the service
	 */
	public ServiceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status of the service to set
	 */
	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

}
