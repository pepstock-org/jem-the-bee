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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.impl.AbstractObjectFactory;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.tasks.jndi.JNDIException;
import org.pepstock.jem.util.Parser;

/**
 * JNDI factory to create object for JAVA batches.
 * <br>
 * It returns a FTPInputstream (if a file in SHR is requested) or
 * FTPOuptutstreamif the data source is related to a data description.
 * <br>
 * It returns a FTP object (wrapper of FTPclient) when is not related to a data description 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class FtpFactory extends AbstractObjectFactory {
	
	private static final String FTP_PROTOCOL = "ftp";

	private static final String FTPS_PROTOCOL = "ftps";
	
	
	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JNDIException {
		// checks arguments. if null or not a reference return null
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		// creates and return a FTP client
		return createFtpClient(loadProperties(object, FtpResourceKeys.PROPERTIES_ALL));
	}

	/**
	 * Creates and configures a FtpClient instance based on the
	 * given properties.
	 * 
	 * @param properties the ftp client configuration properties
	 * @return remote input/output steam
	 * @throws JNDIException if an error occurs creating the ftp client
	 */
	private Object createFtpClient(Properties properties) throws JNDIException {
		// URL is mandatory
		String ftpUrlString = properties.getProperty(CommonKeys.URL);
		if (ftpUrlString == null){
			throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.URL);
		}
		
		// creates URL objects
		// from URL string
		URL ftpUrl;
		try {
			ftpUrl = new URL(ftpUrlString);
		} catch (MalformedURLException e) {
			throw new JNDIException(NodeMessage.JEMC233E, e, ftpUrlString);
		}
		// checks scheme
		// if SSL, activates a FTPS
		if (!ftpUrl.getProtocol().equalsIgnoreCase(FTP_PROTOCOL) && !ftpUrl.getProtocol().equalsIgnoreCase(FTPS_PROTOCOL)){
			throw new JNDIException(NodeMessage.JEMC137E, ftpUrl.getProtocol());
		}

		// gets port the host (from URL)
		int port = ftpUrl.getPort();
		String server = ftpUrl.getHost();

		// User id is mandatory
		String username = properties.getProperty(CommonKeys.USERID);
		if (username == null){
			throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.USERID);
		}

		// password is mandatory
		String password = properties.getProperty(CommonKeys.PASSWORD);
		if (password == null){
			throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.PASSWORD);
		}
		
		// checks if as input stream or not
		boolean asInputStream = Parser.parseBoolean(properties.getProperty(FtpResourceKeys.AS_INPUT_STREAM, "false"), false);

		String remoteFile = null;
		String accessMode = null;
		if (asInputStream){
			// remote file is mandatory
			// it must be set by a data description
		    remoteFile = properties.getProperty(FtpResourceKeys.REMOTE_FILE);
			if (remoteFile == null){
				throw new JNDIException(NodeMessage.JEMC136E, FtpResourceKeys.REMOTE_FILE);
			}
			// access mode is mandatory
			// it must be set by a data description
			accessMode = properties.getProperty(FtpResourceKeys.ACTION_MODE, FtpResourceKeys.ACTION_READ);
		}
		
		// creates a FTPclient 
		FTPClient ftp = ftpUrl.getProtocol().equalsIgnoreCase(FTP_PROTOCOL) ? new FTPClient() : new FTPSClient();
		
		// checks if binary
		boolean binaryTransfer = Parser.parseBoolean(properties.getProperty(FtpResourceKeys.BINARY, "false"), false);
		
		// checks if must be restarted
		long restartOffset = Parser.parseLong(properties.getProperty(FtpResourceKeys.RESTART_OFFSET, "-1"), -1);
		
		// checks and sets buffer size
		int bufferSize = Parser.parseInt(properties.getProperty(FtpResourceKeys.BUFFER_SIZE, "-1"), -1);

		try {
			// reply code instance
			int reply;
			// connect to server
			if (port > 0) {
				ftp.connect(server, port);
			} else {
				ftp.connect(server);
			}
			
			// After connection attempt, you should check the reply code to
			// verify
			// success.
			reply = ftp.getReplyCode();
			// if not connected for error, EXCEPTION
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new JNDIException(NodeMessage.JEMC138E, reply);
			}
			// login!!
			if (!ftp.login(username, password)) {
				ftp.logout();
			}
			// set all ftp properties
			if (binaryTransfer) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			}
			
			// sets restart offset if has been set
			if (restartOffset >= 0){
				ftp.setRestartOffset(restartOffset);
			}
			
			// sets buffer size
			if (bufferSize >= 0){
				ftp.setBufferSize(bufferSize);
			}
			
			// if is not related to a data descritpion,
			// returns a FTP object
			if (!asInputStream){
				return new Ftp(ftp);
			}
			
			// checks if is in WRITE mode
			if (accessMode.equalsIgnoreCase(FtpResourceKeys.ACTION_WRITE)){
				// gets outputstream
				// using the file name passed by data descritpion
				OutputStream os = ftp.storeFileStream(remoteFile);
				if (os == null){
					reply = ftp.getReplyCode();
					throw new JNDIException(NodeMessage.JEMC206E, remoteFile, reply);
				}
				// returns outputstream
				return new FtpOutputStream(os, ftp);
			} else {
				// gest inputstream
				// using the file name passed by data descritpion
				InputStream is = ftp.retrieveFileStream(remoteFile);
				if (is == null){
					reply = ftp.getReplyCode();
					throw new JNDIException(NodeMessage.JEMC206E, remoteFile, reply);
				}
				// returns inputstream 
				return new FtpInputStream(is, ftp);
			}
		} catch (SocketException e) {
			throw new JNDIException(NodeMessage.JEMC234E, e);
		} catch (IOException e) {
			throw new JNDIException(NodeMessage.JEMC234E, e);
		}
	}
}