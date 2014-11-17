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
package org.pepstock.jem.node.resources.impl.ftp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.node.resources.impl.CommonKeys;

/**
 * This resource object represents a FTP description to crate a FTP connection to manage 
 * file remotely. List of properties
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
@SuppressWarnings("javadoc")
public final class FtpResourceKeys {

	public static final String BINARY = "binary";
	
	public static final String RESTART_OFFSET = "restartOffset";
	public static final String BUFFER_SIZE = "bufferSize";
	
	static final String AS_INPUT_STREAM = "asInputStream";
	static final String ACTION_MODE = "action";
	static final String ACTION_READ = "read";
	static final String ACTION_WRITE = "write";
	static final String REMOTE_FILE = "remoteFile";


	public static final List<String> PROPERTIES_MANDATORY = Collections.unmodifiableList(Arrays.asList(CommonKeys.USERID, CommonKeys.PASSWORD, CommonKeys.URL));
	
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(CommonKeys.USERID, CommonKeys.PASSWORD, CommonKeys.URL, 
			BINARY, RESTART_OFFSET, BUFFER_SIZE,
			AS_INPUT_STREAM, ACTION_MODE, REMOTE_FILE));

	/**
	 * To avoid any instantiation
	 */
	private FtpResourceKeys() {
	}
	
}