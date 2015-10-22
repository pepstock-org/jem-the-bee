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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;

import org.apache.shiro.codec.Base64;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.security.keystore.Factory;
import org.pepstock.jem.node.security.keystore.KeyStoresInfo;
import org.pepstock.jem.node.security.keystore.KeysUtil;
import org.pepstock.jem.node.security.loginprotocol.ClientLoginProtocol;
import org.pepstock.jem.node.security.loginprotocol.LoginRequest;
import org.pepstock.jem.node.security.loginprotocol.ServerLoginProtocol;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.nio.MemberSocketInterceptor;

/**
 * Implements the socket interceptor at MEMBER level of Hazelcast.
 * <br>
 * When configured, is able to prevent malicious access to the JEM and then to Hazelcast cluster.
 * <br>
 * Only with the KEY, is possible the nodes can communicate
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class NodeInterceptor implements MemberSocketInterceptor {
	
	private static final int DEFAULT_SOCKET_TIMEOUT = 30 * (int)TimeUtils.SECOND;

	private KeyStoresInfo keystoresInfo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hazelcast.nio.MemberSocketInterceptor#init(com.hazelcast.config.
	 * SocketInterceptorConfig)
	 */
	@Override
	public void init(SocketInterceptorConfig config) {
		// gets the key of socket interceptor
		keystoresInfo = Factory.createKeyStoresInfo(config.getProperties());
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
			out = new PrintWriter(new OutputStreamWriter(connectedSocket.getOutputStream(), CharSet.DEFAULT), true);
			// get the reader to use to read the response sent from the server
			in = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream(), CharSet.DEFAULT));
			// is the ip seen from the server
			String ip = connectedSocket.getLocalAddress().getHostAddress();
			// is the port seen from the server
			int port = connectedSocket.getLocalPort();
			address = ip + ":" + port;
			// instantiate the jemProtocol that give the right request (Base64
			// encoded) from the given response
			Key symmetricKey = KeysUtil.getSymmetricKey(keystoresInfo.getClusterKeystoreInfo());
			// creates the client login protocol
			ClientLoginProtocol jemClientProtocol = new ClientLoginProtocol(symmetricKey);
			// creates a request
			String request = jemClientProtocol.getRequestFromResponse(null, address, LoginRequest.JEM_NODE_USER);
			LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(request), CharSet.DEFAULT));
			// send the response
			out.println(request);
			out.flush();
			String inputResponse;
			String outputRequest;
			while ((inputResponse = in.readLine()) != null) {
				// if here
				// received a message
				outputRequest = jemClientProtocol.getRequestFromResponse(inputResponse, address, LoginRequest.JEM_NODE_USER);
				// checks 
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
		} catch (Exception e) {
			// If Exception occurs, terminate the client otherwise it will try
			// to login again and again if it's configured in multicast
			LogAppl.getInstance().emit(NodeMessage.JEMC106W, e);
			System.exit(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hazelcast.nio.MemberSocketInterceptor#onAccept(java.net.Socket)
	 */
	@Override
	public void onAccept(Socket acceptedSocket) throws IOException {
		LogAppl.getInstance().emit(NodeMessage.JEMC202I);
		String callingIp = acceptedSocket.getInetAddress().getHostAddress();
		int callingPort = acceptedSocket.getPort();
		String callingSubject = callingIp + ":" + callingPort;
		try {
			// set a timeout on the socket of 30 seconds otherwise if a member
			// (client) doesn't send any request the "server" will remain append
			// on the while cycle until the member (client) will be shut down
			// and no exception will be throw. In this way, after 30 second the
			// server will throw an exception that will be present in the log of
			// the jem node
			acceptedSocket.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
			// get the printer to use to send the response to the client
			PrintWriter out = new PrintWriter(new OutputStreamWriter(acceptedSocket.getOutputStream(), CharSet.DEFAULT), true);
			// get the reader to read the request from the client
			BufferedReader in = new BufferedReader(new InputStreamReader(acceptedSocket.getInputStream(), CharSet.DEFAULT));
			String inputRequest;
			String outputResponse;
			// instantiate the jemProtocol that give the right response (crypted
			// and Base64 encoded) from the given request
			ServerLoginProtocol jemProtocol = new ServerLoginProtocol(keystoresInfo);
			while ((inputRequest = in.readLine()) != null) {
				outputResponse = jemProtocol.getResponseFromRequest(inputRequest);
				LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(inputRequest), CharSet.DEFAULT));
				LogAppl.getInstance().emit(NodeMessage.JEMC203I, new String(Base64.decode(outputResponse), CharSet.DEFAULT));
				out.println(outputResponse);
				out.flush();
				if (jemProtocol.isServerException()) {
					Thread.sleep(TimeUtils.SECOND);
					throw new NodeMessageException(NodeMessage.JEMC107W, callingSubject);
				}
				if (jemProtocol.isConversationTerminated()){
					break;
				}
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
