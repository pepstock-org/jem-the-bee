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

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.pepstock.jem.log.LogAppl;

/**
 * Utility to act with FTP client
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public final class FtpUtil {

	/**
	 * To avoid any instantiation
	 */
	private FtpUtil() {
	}

	/**
	 * Close the FTP client connection
	 * @param ftp ftp client instance
	 * @throws IOException if any IO error occurs
	 */
	public static void close(FTPClient ftp) throws IOException{
		try {
			// close FTP connection
			ftp.logout();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
	
}
