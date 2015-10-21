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
package org.pepstock.jem.commands;

import java.net.URL;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class SetURLFactory {
	
	private static boolean installed = false;

	/**
	 * to avoid any instantiation
	 */
	private SetURLFactory() {
	}
	
	/**
	 * Installs the JEM URL factory, checking if is already done
	 */
	public static synchronized void install(){
		if (!installed){
			// add JEM url handler factory if the user will use
			// JEM url to add JCL content from GFS of JEM
			URL.setURLStreamHandlerFactory(new JemURLStreamHandlerFactory());
			installed = true;
		}
	}

}
