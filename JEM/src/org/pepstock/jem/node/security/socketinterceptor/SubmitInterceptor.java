/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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

package org.pepstock.jem.node.security.socketinterceptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.util.Properties;

import org.apache.shiro.codec.Base64;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.security.Crypto;
import org.pepstock.jem.node.security.loginprotocol.ClientLoginProtocol;
import org.pepstock.jem.util.CharSet;

import com.hazelcast.nio.SocketInterceptor;

/**
 * When a Submit or a LocalHost submit command is launched, if the JEM is
 * configured to use SocketInterceptor, than the submit user need to give the
 * path relative to his private key. The corresponding public key is in a X.509
 * certificate stored in the jem keystore and with alias the user name. This
 * indeed permit to the JEM to authenticate each submit user trough the use of a
 * X.509 certificate.
 * <p>
 * The private key will always remain in the client to prevent from been stolen
 * <p>
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class SubmitInterceptor implements SocketInterceptor {

	private Key privateKey;

	private String subjectId;
	
	/**
	 * the private key for secure communication between client and cluster
	 */
	public static final String PRIVATE_KEY_FILE_PATH = "privateKeyFilePath";
	
	/**
	 * the password for the private key for secure communication between client and cluster
	 */
	public static final String KEY_PASSWORD = "keyPassword";

	/**
	 * the subject id relative to the private key so to be able to search it in the keystore
	 */
	public static final String SUBJECT_ID = "subjectId";
	
	/**
	 * 
	 * @param privateKeFilePath the path relative to the private key of the user
	 * @param keyPassword the password of the private key
	 * @param subjectId is the user calling the submit interceptor and
	 *            correspond to the alias of the public certificate in the jem
	 *            keystore
	 * @throws MessageException if any exception occurs
	 * @throws KeyException if any exception occurs
	 */
	public SubmitInterceptor(String privateKeFilePath, String keyPassword, String subjectId) throws KeyException, MessageException  {
		privateKey = Crypto.loadPrivateKeyFromFile(new File(privateKeFilePath), keyPassword);
		this.subjectId = subjectId;
	}
	
	/* (non-Javadoc)
	 * @see com.hazelcast.nio.SocketInterceptor#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
		subjectId = properties.getProperty(SUBJECT_ID);
		try {
			privateKey = Crypto.loadPrivateKeyFromFile(new File(properties.getProperty(PRIVATE_KEY_FILE_PATH)), properties.getProperty(KEY_PASSWORD));
		} catch (Exception e) {
			throw new RuntimeException(e);
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
			out = new PrintWriter(new OutputStreamWriter(connectedSocket.getOutputStream(), CharSet.DEFAULT_CHARSET_NAME));
			// get the reader to use to read the response sent from the server
			in = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream(), CharSet.DEFAULT));
			// is the ip seen from the server
			String ip = connectedSocket.getLocalAddress().getHostAddress();
			// is the port seen from the server
			int port = connectedSocket.getLocalPort();
			address = ip + ":" + port;
			// instantiate the jemProtocol that give the right request (Base64
			// encoded) from the given response
			ClientLoginProtocol jemClientProtocol = new ClientLoginProtocol(privateKey);
			String request = jemClientProtocol.getRequestFromResponse(null, address, subjectId);
			LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(request), CharSet.DEFAULT));
			out.println(request);
			out.flush();
			String inputResponse;
			String outputRequest;
			while ((inputResponse = in.readLine()) != null) {
				outputRequest = jemClientProtocol.getRequestFromResponse(inputResponse, address, subjectId);
				LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(inputResponse), CharSet.DEFAULT));
				if (outputRequest != null){
					LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(outputRequest), CharSet.DEFAULT));
				}
				if (jemClientProtocol.isConversationTerminated()) {
					break;
				}
				if (jemClientProtocol.isServerException()) {
					System.exit(1);
				}
				out.println(outputRequest);
				out.flush();
			}
		} catch (Exception e) {
			// If Exception occurs, terminate the client otherwise it will try
			// to login again and again if it's configured in multicast
			LogAppl.getInstance().emit(NodeMessage.JEMC106W, e);
			System.exit(1);
		}
	}

}
