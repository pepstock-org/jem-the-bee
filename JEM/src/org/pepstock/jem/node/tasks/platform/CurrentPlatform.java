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
package org.pepstock.jem.node.tasks.platform;

import org.apache.commons.lang3.SystemUtils;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;

/**
 * Singleton class to set the current platform where JEM node is running
 * 
 * @see AbstractPlatform
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class CurrentPlatform {
	
	private static Platform INSTANCE = null;

	/**
	 * Constructs the platform object by the OS where is running
	 * 
	 * @throws NodeMessageException if OS is not supported, an exception occurs
	 */
	private CurrentPlatform() throws NodeMessageException {
		// if UNIX, uses the unix platform
		if (SystemUtils.IS_OS_UNIX){
			INSTANCE = new UnixPlatform();
		} else if (SystemUtils.IS_OS_WINDOWS){
			// otherwise WINDOWS
			INSTANCE = new WindowsPlatform();
		} else {
			// if here OS is not supported
			throw new NodeMessageException(NodeMessage.JEMC245E, SystemUtils.OS_NAME);
		}
	}
	
	/**
	 * Singleton method to get the current platform
	 * 
	 * @return platform instance
	 * 
	 * @throws NodeMessageException if OS is not supported, an exception occurs
	 */
	public static final Platform getInstance() throws NodeMessageException{
		if (INSTANCE == null){
			new CurrentPlatform();
		}
		return INSTANCE;
	}
}