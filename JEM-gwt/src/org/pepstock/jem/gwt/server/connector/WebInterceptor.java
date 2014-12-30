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

package org.pepstock.jem.gwt.server.connector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.codec.Base64;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.security.keystore.Factory;
import org.pepstock.jem.node.security.keystore.KeyStoreInfo;
import org.pepstock.jem.node.security.keystore.KeysUtil;
import org.pepstock.jem.node.security.loginprotocol.ClientLoginProtocol;
import org.pepstock.jem.node.security.loginprotocol.LoginProtocolException;
import org.pepstock.jem.node.security.loginprotocol.LoginRequest;
import org.pepstock.jem.util.CharSet;

import com.hazelcast.nio.SocketInterceptor;

/**
 * @author Simone Businaro
 * @version 1.0
 * 
 */
public class WebInterceptor implements SocketInterceptor {
	
	private static final String KEYSTORE_FILE_KEY = "jem.keystore.file";

	private KeyStoreInfo clusterKeystoreInfo;
	
	private String keyStoreFileName = null;
	
	private boolean readKeyStore = false;

	/**
	 * Build the web connector to JEM
	 * @param configProperties
	 * @throws IOException 
	 */
	public WebInterceptor(Properties configProperties) throws IOException {
		File clusterKeystoreFile = null;
		// before reading the hazelcast path for keystore file
		// checks on System properties
		keyStoreFileName = System.getProperty(KEYSTORE_FILE_KEY);
		// if doesn't exist
		// reads HC config
		if (keyStoreFileName == null){
			keyStoreFileName = configProperties.getProperty(Factory.JEM_KEYSTORE_PATH_PROP);
			clusterKeystoreFile = new File(keyStoreFileName);
			// if the file is not absolute
			// uses the ServletContext reading the resource as stream
			// setting true 
			readKeyStore = !clusterKeystoreFile.isAbsolute();
			
			if (clusterKeystoreFile.isAbsolute()){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG069E, clusterKeystoreFile.getAbsolutePath());
			} else {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG070E, keyStoreFileName);
			}
		} else {
			// it creates always a file (only if not null)
			// readKeyStore must be left to false
			// as the default
			clusterKeystoreFile = new File(keyStoreFileName);
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG068E, clusterKeystoreFile.getAbsolutePath(), KEYSTORE_FILE_KEY);
		}
		String keystorePasswd = configProperties.getProperty(Factory.JEM_KEYSTORE_PWD_PROP);
		String keyPasswd = configProperties.getProperty(Factory.JEM_CRYPT_KEY_PWD_PROP);
		String keyAlias = configProperties.getProperty(Factory.JEM_CRYPT_KEY_ALIAS_PROP);
		// Info relative to the keystore containing the symmetric key
		clusterKeystoreInfo = new KeyStoreInfo(KeyStoreInfo.JCEKS_KEYSTORE_TYPE);
		clusterKeystoreInfo.setFile(clusterKeystoreFile);
		clusterKeystoreInfo.setPassword(keystorePasswd);
		clusterKeystoreInfo.setSymmetricKeyAlias(keyAlias);
		clusterKeystoreInfo.setSymmetricKeyPwd(keyPasswd);
		
		// if it must read the keystore 
		// form servlet context
		if (readKeyStore){
			readKeyStore();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hazelcast.nio.SocketInterceptor#onConnect(java.net.Socket)
	 */
	@Override
	public void onConnect(Socket connectedSocket) throws IOException {
		LogAppl.getInstance().emit(NodeMessage.JEMC202I);
		PrintWriter out = null;
		BufferedReader in = null;
		String address = null;
		try {
			// get the printer to use to send the request to the server
			out = new PrintWriter(new OutputStreamWriter(connectedSocket.getOutputStream(), CharSet.DEFAULT));
			// get the reader to use to read the response sent from the server
			in = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream(), CharSet.DEFAULT));
			// is the ip seen from the server
			String ip = connectedSocket.getLocalAddress().getHostAddress();
			// is the port seen from the server
			int port = connectedSocket.getLocalPort();
			address = ip + ":" + port;
			// if it must read the keystore 
			// form servlet context
			if (readKeyStore){
				readKeyStore();
			}
			// instantiate the jemProtocol that give the right request (Base64
			// encoded) from the given response
			Key symmetricKey = KeysUtil.getSymmetricKey(clusterKeystoreInfo);
			ClientLoginProtocol jemClientProtocol = new ClientLoginProtocol(symmetricKey);
			String request = jemClientProtocol.getRequestFromResponse(null, address, LoginRequest.JEM_WEB_USER);
			LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(request), CharSet.DEFAULT));
			out.println(request);
			out.flush();
			String inputResponse;
			String outputRequest;
			while ((inputResponse = in.readLine()) != null) {
				outputRequest = jemClientProtocol.getRequestFromResponse(inputResponse, address, LoginRequest.JEM_WEB_USER);
				LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(inputResponse), CharSet.DEFAULT));
				if (outputRequest != null){
					LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(outputRequest), CharSet.DEFAULT));
				}
				if (jemClientProtocol.isConversationTerminated()) {
					break;
				}
				out.println(outputRequest);
				out.flush();
			}
		} catch (LoginProtocolException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC106W, e);
			throw new IOException(e.getMessage(), e);
		} catch (KeyException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC106W, e);
			throw new IOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Reads the content of key store in a bytes array
	 * @throws IOException if any IO exception occurs during the reading of key store
	 */
	private void readKeyStore() throws IOException{
		InputStream is= SharedObjects.getInstance().getContext().getResourceAsStream(keyStoreFileName);
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		clusterKeystoreInfo.setBytes(baos);
	}
}