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
package org.pepstock.jem.node.tasks.jndi;

import java.io.InputStream;

import javax.naming.Reference;

/**
 * Sets constants for JNDI for FTPClient oject. It uses Apache common net classes.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class FtpReference extends Reference {

	private static final long serialVersionUID = 1L;

	/**
	 * Custom FTP factory
	 */
	public static final String FTP_FACTORY = FtpFactory.class.getName();
	
	/**
	 * Always returns a InoutStream
	 */
	public static final String FTP_OBJECT = InputStream.class.getName();
	
	/**
	 * Creates a JNDI reference for FTP purposes
	 */
	public FtpReference() {
		super(FTP_OBJECT, FTP_FACTORY, null);
	}

}