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
package org.pepstock.jem.node.resources.impl.ftp;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ProxyOutputStream;
import org.apache.commons.net.ftp.FTPClient;

/**
 *  Output Stream created for FTP connection.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class FtpOutputStream extends ProxyOutputStream {

	private FTPClient ftp = null;

	/**
	 * Constructs object with proxy and ftp client
	 * @param proxy output stream
	 * @param client ftp client
	 */
	public FtpOutputStream(OutputStream proxy, FTPClient client) {
		super(proxy);
		this.ftp = client;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.output.ProxyOutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			FtpUtil.close(ftp);
		}
	}
}