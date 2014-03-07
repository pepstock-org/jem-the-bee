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
package org.pepstock.jem.node.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * THis resource object represents a FTP description to crate a FTP connection to manage 
 * file remotely.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
@SuppressWarnings("javadoc")
public class FtpResource extends Resource {

	private static final long serialVersionUID = 1L;

	public static final String URL = "url";
	public static final String BINARY = "binary";
	public static final String ACTION_MODE = "action";
	public static final String ACTION_READ = "read";
	public static final String ACTION_WRITE = "write";
	public static final String REMOTE_FILE = "remoteFile";

	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(USERID, PASSWORD, URL, BINARY, REMOTE_FILE, ACTION_MODE));

	/**
	 * Type for FTP data sources
	 */
	public static final String TYPE = "ftp";

	/**
	 * Constructor that creates resource
	 */
	public FtpResource() {
		setType(TYPE);
	}

}