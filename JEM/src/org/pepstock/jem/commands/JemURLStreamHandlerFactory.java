/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * JEM url factory. Is able to manage "jem:" URL but following a specific syntax.
 * 
 * @see JemURLStreamHandler
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class JemURLStreamHandlerFactory implements URLStreamHandlerFactory {
	
	/**
	 * Protocol for JEM
	 */
	public static final String PROTOCOL = "jem";

	/* (non-Javadoc)
	 * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
	 */
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		// only if protocol is jem, the handler will manage the
		// URL parsing
		if (PROTOCOL.equalsIgnoreCase(protocol)){
			return new JemURLStreamHandler();		
		}
		return null;
	}
}
