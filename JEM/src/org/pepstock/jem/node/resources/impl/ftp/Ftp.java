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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.io.CopyStreamListener;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@SuppressWarnings("javadoc")
public class Ftp{

	private FTPClient client = null;

	/**
	 * @param client
	 */
	public Ftp(FTPClient client) {
		super();
		this.client = client;
	}

	/**
	 * (non-Javadoc)
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#abort()
	 */
	
	public boolean abort() throws IOException {
		return client.abort();
	}

	/**
	 * (non-Javadoc)
	 * @param remote 
	 * @param local 
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#appendFile(java.lang.String,
	 *      java.io.InputStream)
	 */
	public boolean appendFile(String remote, InputStream local) throws IOException {
		return client.appendFile(remote, local);
	}

	/**
	 * (non-Javadoc)
	 * @param remote 
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#appendFileStream(java.lang.String)
	 */
	public OutputStream appendFileStream(String remote) throws IOException {
		return client.appendFileStream(remote);
	}

	/**
	 * (non-Javadoc)
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#changeToParentDirectory()
	 */
	public boolean changeToParentDirectory() throws IOException {
		return client.changeToParentDirectory();
	}

	/**
	 * (non-Javadoc)
	 * @param pathname 
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#changeWorkingDirectory(java.lang.String)
	 */
	public boolean changeWorkingDirectory(String pathname) throws IOException {
		return client.changeWorkingDirectory(pathname);
	}

	/**
	 * (non-Javadoc)
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#completePendingCommand()
	 */

	public boolean completePendingCommand() throws IOException {
		return client.completePendingCommand();
	}

	/**
	 * (non-Javadoc)
	 * @param pathname 
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#deleteFile(java.lang.String)
	 */

	public boolean deleteFile(String pathname) throws IOException {
		return client.deleteFile(pathname);
	}

	/**
	 * (non-Javadoc)
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#disconnect()
	 */

	public void disconnect() throws IOException {
		client.disconnect();
	}

	/**
	 * (non-Javadoc)
	 * @param command 
	 * @param params 
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#doCommand(java.lang.String,
	 *      java.lang.String)
	 */

	public boolean doCommand(String command, String params) throws IOException {
		return client.doCommand(command, params);
	}

	/**
	 * (non-Javadoc)
	 * @param command 
	 * @param params 
	 * @return 
	 * @throws IOException 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#doCommandAsStrings(java.lang.String,
	 *      java.lang.String)
	 */
	public String[] doCommandAsStrings(String command, String params) throws IOException {
		return client.doCommandAsStrings(command, params);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#enterLocalActiveMode()
	 */
	public void enterLocalActiveMode() {
		client.enterLocalActiveMode();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#enterLocalPassiveMode()
	 */
	public void enterLocalPassiveMode() {
		client.enterLocalPassiveMode();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#enterRemoteActiveMode(java.net.InetAddress,
	 *      int)
	 */
	public boolean enterRemoteActiveMode(InetAddress host, int port) throws IOException {
		return client.enterRemoteActiveMode(host, port);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#enterRemotePassiveMode()
	 */
	public boolean enterRemotePassiveMode() throws IOException {
		return client.enterRemotePassiveMode();
	}

	/**
	 * (non-Javadoc)
	 * @return 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getBufferSize()
	 */
	public int getBufferSize() {
		return client.getBufferSize();
	}

	/**
	 * (non-Javadoc)
	 * @return 
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getControlKeepAliveReplyTimeout()
	 */

	public int getControlKeepAliveReplyTimeout() {
		return client.getControlKeepAliveReplyTimeout();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getControlKeepAliveTimeout()
	 */

	public long getControlKeepAliveTimeout() {
		return client.getControlKeepAliveTimeout();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getCopyStreamListener()
	 */

	public CopyStreamListener getCopyStreamListener() {
		return client.getCopyStreamListener();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getDataConnectionMode()
	 */

	public int getDataConnectionMode() {
		return client.getDataConnectionMode();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getListHiddenFiles()
	 */

	public boolean getListHiddenFiles() {
		return client.getListHiddenFiles();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getModificationTime(java.lang.String)
	 */

	public String getModificationTime(String pathname) throws IOException {
		return client.getModificationTime(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getPassiveHost()
	 */

	public String getPassiveHost() {
		return client.getPassiveHost();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getPassiveLocalIPAddress()
	 */

	public InetAddress getPassiveLocalIPAddress() {
		return client.getPassiveLocalIPAddress();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getPassivePort()
	 */

	public int getPassivePort() {
		return client.getPassivePort();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getReceiveDataSocketBufferSize()
	 */

	public int getReceiveDataSocketBufferSize() {
		return client.getReceiveDataSocketBufferSize();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getRestartOffset()
	 */

	public long getRestartOffset() {
		return client.getRestartOffset();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getSendDataSocketBufferSize()
	 */

	public int getSendDataSocketBufferSize() {
		return client.getSendDataSocketBufferSize();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getStatus()
	 */

	public String getStatus() throws IOException {
		return client.getStatus();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getStatus(java.lang.String)
	 */

	public String getStatus(String pathname) throws IOException {
		return client.getStatus(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#getSystemType()
	 */

	public String getSystemType() throws IOException {
		return client.getSystemType();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#initiateListParsing()
	 */

	public FTPListParseEngine initiateListParsing() throws IOException {
		return client.initiateListParsing();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#initiateListParsing(java.lang.String,
	 *      java.lang.String)
	 */

	public FTPListParseEngine initiateListParsing(String arg0, String arg1) throws IOException {
		return client.initiateListParsing(arg0, arg1);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#initiateListParsing(java.lang.String)
	 */

	public FTPListParseEngine initiateListParsing(String pathname) throws IOException {
		return client.initiateListParsing(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#isRemoteVerificationEnabled()
	 */

	public boolean isRemoteVerificationEnabled() {
		return client.isRemoteVerificationEnabled();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listDirectories()
	 */

	public FTPFile[] listDirectories() throws IOException {
		return client.listDirectories();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listDirectories(java.lang.String)
	 */

	public FTPFile[] listDirectories(String parent) throws IOException {
		return client.listDirectories(parent);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listFiles()
	 */

	public FTPFile[] listFiles() throws IOException {
		return client.listFiles();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listFiles(java.lang.String,
	 *      org.apache.commons.net.ftp.FTPFileFilter)
	 */

	public FTPFile[] listFiles(String pathname, FTPFileFilter filter) throws IOException {
		return client.listFiles(pathname, filter);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listFiles(java.lang.String)
	 */

	public FTPFile[] listFiles(String pathname) throws IOException {
		return client.listFiles(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listHelp()
	 */

	public String listHelp() throws IOException {
		return client.listHelp();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listHelp(java.lang.String)
	 */

	public String listHelp(String command) throws IOException {
		return client.listHelp(command);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listNames()
	 */

	public String[] listNames() throws IOException {
		return client.listNames();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#listNames(java.lang.String)
	 */

	public String[] listNames(String arg0) throws IOException {
		return client.listNames(arg0);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#logout()
	 */

	public boolean logout() throws IOException {
		return client.logout();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#makeDirectory(java.lang.String)
	 */

	public boolean makeDirectory(String pathname) throws IOException {
		return client.makeDirectory(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#mlistDir()
	 */

	public FTPFile[] mlistDir() throws IOException {
		return client.mlistDir();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#mlistDir(java.lang.String,
	 *      org.apache.commons.net.ftp.FTPFileFilter)
	 */

	public FTPFile[] mlistDir(String pathname, FTPFileFilter filter) throws IOException {
		return client.mlistDir(pathname, filter);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#mlistDir(java.lang.String)
	 */

	public FTPFile[] mlistDir(String pathname) throws IOException {
		return client.mlistDir(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#mlistFile(java.lang.String)
	 */

	public FTPFile mlistFile(String arg0) throws IOException {
		return client.mlistFile(arg0);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#printWorkingDirectory()
	 */

	public String printWorkingDirectory() throws IOException {
		return client.printWorkingDirectory();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#remoteAppend(java.lang.String)
	 */
	public boolean remoteAppend(String filename) throws IOException {
		return client.remoteAppend(filename);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#remoteRetrieve(java.lang.String)
	 */

	public boolean remoteRetrieve(String filename) throws IOException {
		return client.remoteRetrieve(filename);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#remoteStore(java.lang.String)
	 */

	public boolean remoteStore(String filename) throws IOException {
		return client.remoteStore(filename);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#remoteStoreUnique()
	 */

	public boolean remoteStoreUnique() throws IOException {
		return client.remoteStoreUnique();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#remoteStoreUnique(java.lang.String)
	 */

	public boolean remoteStoreUnique(String filename) throws IOException {
		return client.remoteStoreUnique(filename);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#removeDirectory(java.lang.String)
	 */

	public boolean removeDirectory(String pathname) throws IOException {
		return client.removeDirectory(pathname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#rename(java.lang.String,
	 *      java.lang.String)
	 */

	public boolean rename(String from, String to) throws IOException {
		return client.rename(from, to);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#retrieveFile(java.lang.String,
	 *      java.io.OutputStream)
	 */

	public boolean retrieveFile(String remote, OutputStream local) throws IOException {
		return client.retrieveFile(remote, local);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#retrieveFileStream(java.lang.String)
	 */

	public InputStream retrieveFileStream(String remote) throws IOException {
		return client.retrieveFileStream(remote);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#sendNoOp()
	 */

	public boolean sendNoOp() throws IOException {
		return client.sendNoOp();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#sendSiteCommand(java.lang.String)
	 */

	public boolean sendSiteCommand(String arguments) throws IOException {
		return client.sendSiteCommand(arguments);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setCopyStreamListener(org.apache.commons.net.io.CopyStreamListener)
	 */

	public void setCopyStreamListener(CopyStreamListener listener) {
		client.setCopyStreamListener(listener);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setFileTransferMode(int)
	 */

	public boolean setFileTransferMode(int mode) throws IOException {
		return client.setFileTransferMode(mode);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setFileType(int, int)
	 */

	public boolean setFileType(int fileType, int formatOrByteSize) throws IOException {
		return client.setFileType(fileType, formatOrByteSize);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setFileType(int)
	 */

	public boolean setFileType(int fileType) throws IOException {
		return client.setFileType(fileType);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setListHiddenFiles(boolean)
	 */

	public void setListHiddenFiles(boolean listHiddenFiles) {
		client.setListHiddenFiles(listHiddenFiles);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setModificationTime(java.lang.String,
	 *      java.lang.String)
	 */

	public boolean setModificationTime(String pathname, String timeval) throws IOException {
		return client.setModificationTime(pathname, timeval);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#setRestartOffset(long)
	 */

	public void setRestartOffset(long offset) {
		client.setRestartOffset(offset);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#storeFile(java.lang.String,
	 *      java.io.InputStream)
	 */

	public boolean storeFile(String remote, InputStream local) throws IOException {
		return client.storeFile(remote, local);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTPClient#storeFileStream(java.lang.String)
	 */
	public OutputStream storeFileStream(String remote) throws IOException {
		return client.storeFileStream(remote);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @param local
	 * @return
	 * @throws IOException
	 * @see org.apache.commons.net.ftp.FTPClient#storeUniqueFile(java.io.InputStream)
	 */
	public boolean storeUniqueFile(InputStream local) throws IOException {
		return client.storeUniqueFile(local);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @param remote
	 * @param local
	 * @return
	 * @throws IOException
	 * @see org.apache.commons.net.ftp.FTPClient#storeUniqueFile(java.lang.String,
	 *      java.io.InputStream)
	 */
	public boolean storeUniqueFile(String remote, InputStream local) throws IOException {
		return client.storeUniqueFile(remote, local);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @return
	 * @throws IOException
	 * @see org.apache.commons.net.ftp.FTPClient#storeUniqueFileStream()
	 */
	public OutputStream storeUniqueFileStream() throws IOException {
		return client.storeUniqueFileStream();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @param remote
	 * @return
	 * @throws IOException
	 * @see org.apache.commons.net.ftp.FTPClient#storeUniqueFileStream(java.lang.String)
	 */
	public OutputStream storeUniqueFileStream(String remote) throws IOException {
		return client.storeUniqueFileStream(remote);
	}
	

	/** (non-Javadoc)
	 * @see org.apache.commons.net.ftp.FTP#getReply()
	 */
	
	public int getReply() throws IOException {
		return client.getReply();
	}

	/** (non-Javadoc)
	 * @see org.apache.commons.net.ftp.FTP#getReplyCode()
	 */
	
	public int getReplyCode() {
		return client.getReplyCode();
	}

	/** (non-Javadoc)
	 * @see org.apache.commons.net.ftp.FTP#getReplyString()
	 */
	
	public String getReplyString() {
		return client.getReplyString();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.net.ftp.FTP#getReplyStrings()
	 */
	
	public String[] getReplyStrings() {
		return client.getReplyStrings();
	}

}
