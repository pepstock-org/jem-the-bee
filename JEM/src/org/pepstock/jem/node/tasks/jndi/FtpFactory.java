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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.FtpResource;
import org.pepstock.jem.util.Parser;

/**
 * JNDI factory to create object for JAVA batches. It returns a FTPInputstream (if a file in SHR is requested) or
 * FTPOuptutstream.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class FtpFactory implements ObjectFactory {
	
	private static final String FTP_PROTOCOL = "ftp";

	private static final String FTPS_PROTOCOL = "ftps";
	
	
	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JNDIException {
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		Reference ref = (Reference) object;
		Properties properties = new Properties();
		for (int i = 0; i < FtpResource.PROPERTIES_ALL.size(); i++) {
			String propertyName = FtpResource.PROPERTIES_ALL.get(i);
			RefAddr ra = ref.get(propertyName);
			if (ra != null) {
				String propertyValue = ra.getContent().toString();
				properties.setProperty(propertyName, propertyValue);
			}
		}
		return createFtpClient(properties);
	}

	/**
	 * Creates and configures a FtpClient instance based on the
	 * given properties.
	 * 
	 * @param properties the ftp client configuration properties
	 * @return remote input steam
	 * @throws JNDIException if an error occurs creating the ftp client
	 */
	public static Object createFtpClient(Properties properties) throws JNDIException {
		String ftpUrlString = properties.getProperty(FtpResource.URL);
		if (ftpUrlString == null){
			throw new JNDIException(NodeMessage.JEMC136E, FtpResource.URL);
		}

		URL ftpUrl;
		try {
			ftpUrl = new URL(ftpUrlString);
		} catch (MalformedURLException e) {
			throw new JNDIException(NodeMessage.JEMC233E, e);
		}
		if (!ftpUrl.getProtocol().equalsIgnoreCase(FTP_PROTOCOL) && !ftpUrl.getProtocol().equalsIgnoreCase(FTPS_PROTOCOL)){
			throw new JNDIException(NodeMessage.JEMC137E, ftpUrl.getProtocol());
		}

		int port = ftpUrl.getPort();
		String server = ftpUrl.getHost();

		String username = properties.getProperty(FtpResource.USERID);
		if (username == null){
			throw new JNDIException(NodeMessage.JEMC136E, FtpResource.USERID);
		}

		String password = properties.getProperty(FtpResource.PASSWORD);
		if (password == null){
			throw new JNDIException(NodeMessage.JEMC136E, FtpResource.PASSWORD);
		}

		String remoteFile = properties.getProperty(FtpResource.REMOTE_FILE);
		if (remoteFile == null){
			throw new JNDIException(NodeMessage.JEMC136E, FtpResource.REMOTE_FILE);
		}

		String accessMode = properties.getProperty(FtpResource.ACTION_MODE, FtpResource.ACTION_READ);
		
		FTPClient ftp = ftpUrl.getProtocol().equalsIgnoreCase(FTP_PROTOCOL) ? new FTPClient() : new FTPSClient();

		boolean binaryTransfer = Parser.parseBoolean(properties.getProperty(FtpResource.BINARY, "false"), false);
		
		long restartOffset = Parser.parseLong(properties.getProperty(FtpResource.RESTART_OFFSET, "-1"), -1);
		
		int bufferSize = Parser.parseInt(properties.getProperty(FtpResource.BUFFER_SIZE, "-1"), -1);

		try {
			int reply;
			if (port > 0) {
				ftp.connect(server, port);
			} else {
				ftp.connect(server);
			}
			
			// After connection attempt, you should check the reply code to
			// verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new JNDIException(NodeMessage.JEMC138E, reply);
			}
			
			if (!ftp.login(username, password)) {
				ftp.logout();
			}
			
			if (binaryTransfer) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			}
			
			if (restartOffset >= 0){
				ftp.setRestartOffset(restartOffset);
			}
			
			if (bufferSize >= 0){
				ftp.setBufferSize(bufferSize);
			}
			
			if (accessMode.equalsIgnoreCase(FtpResource.ACTION_WRITE)){
				OutputStream os = ftp.storeFileStream(remoteFile);
				if (os == null){
					reply = ftp.getReplyCode();
					throw new JNDIException(NodeMessage.JEMC206E, remoteFile, reply);
				}
				return new FtpOutputStream(os, ftp);
			} else {
				InputStream is = ftp.retrieveFileStream(remoteFile);
				if (is == null){
					reply = ftp.getReplyCode();
					throw new JNDIException(NodeMessage.JEMC206E, remoteFile, reply);
				}
				return new FtpInputStream(is, ftp);
			}
		} catch (SocketException e) {
			throw new JNDIException(NodeMessage.JEMC234E, e);
		} catch (IOException e) {
			throw new JNDIException(NodeMessage.JEMC234E, e);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
					// debug
					LogAppl.getInstance().debug(e.getMessage(), e);
				}
			}
		}
	}
}