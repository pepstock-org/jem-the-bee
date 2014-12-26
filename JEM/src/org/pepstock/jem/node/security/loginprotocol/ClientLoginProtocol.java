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

package org.pepstock.jem.node.security.loginprotocol;

import java.security.Key;
import java.security.KeyException;

import org.apache.commons.codec.binary.Base64;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.security.Crypto;
import org.pepstock.jem.util.CharSet;


/**
 * @author Simone "Busi" Businaro
 * @version 1.0
 * 
 *          Is the Jem protocol, client side, through which a node/client login
 *          itself to the hazelcast cluster. Here an example of a complete
 *          communication client server: The client ask the server a password to
 *          connect: {@code
 *  <request>
 * 		<address>197.12.1.1:58824</address>
 * 		<subjectId>JEM_NODE or UserName</subjectId>
 * 		<operation>
 * 			<name>getPassword</name>
 * 		</operation>
 * 	</request>
 * }
 *          <p>
 *          before sending the request, the client encoded the message in
 *          Base64.
 *          <p>
 *          At this point the server receive the request, makes a Base64 decode
 *          operation, and if there is no errors produces the reponse and store
 *          the password and the address. Once again the responce will be Base64
 *          encoded.
 *          <p>
 *          {@code
 * <response>
 * 		<address>197.12.1.1:58824</address>
 * 		<subjectId>JEM_NODE or UserName</subjectId>
 * 		<operation>
 * 			<name>getPassword</name>
 * 			<result>randomGeneratedPassowrd</result>
 * 		</operation>
 * </response>
 * }
 *          <p>
 *          Now the client decodes (Base64), and if there is no error produces
 *          the request. The value of the tag password this time will be
 *          encrypted. All the request once again will be Base64 encoded.
 *          <p>
 *          For what concern encryption:
 *          <ul>
 *          <li>In case the request come from a Submit operation, than an
 *          asymmetric key will be used, where the private key will be provide
 *          by the client to encrypt the password while the public key will be
 *          used by the server to decrypt it. The public key will be store in
 *          the jem key store inside an X509 certificate</li>
 *          </ul>
 *          <li>In case the request come Jem Node (either web or not) than a
 *          symmetric key will be used. The key is store in the jem key store
 *          inside the persistence folder of the GFS (global file system)</li>
 *          </ul> {@code
 *  <request>
 * 		<address>197.12.1.1:58824</address>
 * 		<subjectId>JEM_NODE or UserName</subjectId>
 * 		<password>randomGeneratedPassowrd received from server encrypted and b64 encoded</password>
 * 		<operation>
 * 			<name>login</name>
 * 		</operation>
 * 	</request>
 * }
 *          <p>
 *          the server decodes the request, decrypts the password and if address
 *          and password match produce the response (encoded in base64), and let
 *          the client in
 *          <p>
 *          {@code
 * <response>
 * 		<address>197.12.1.1:58824</address>
 * 		<subjectId>JEM_NODE or UserName</subjectId>
 * 		<operation>
 * 			<name>login</name>
 * 			<result>accepted</result>
 * 		</operation>
 * </response>
 * }
 *          <p>
 *          If address and password doens't match produces the following
 *          response, end the communication and doesn't let the client in:
 *          <p>
 *          {@code
 * <response>
 * 		<address>197.12.1.1:58824</address>
 * 		<subjectId>JEM_NODE or UserName</subjectId>
 * 		<operation>
 * 			<name>login</name>
 * 			<result>denied</result>
 * 		</operation>
 * </response>
 * }
 *          <p>
 *          If the client receive a login denied or is not able to interpret the
 *          response will throw an exception and exit(1)
 *          <p>
 *          if the server receive a request that is not able to interpret will
 *          produce the following response, terminate the comunication and will
 *          not let the client in
 *          <p>
 *          {@code
 * <response>
 * 		<exception>bad request</exception>
 * </response>
 * }
 * 
 */
public class ClientLoginProtocol {

	private boolean conversationTerminated = false;

	private boolean serverException = false;

	// is the private key provided by the user in case of a Submit operation or
	// the symmetric key in case the client/server communication is between Jem
	// Nodes.
	private Key cryptographicKey;

	/**
	 * 
	 * @param cryptographicKey is the private key provided by the user in case
	 *            of a Submit operation or the symmetric key in case the
	 *            client/server communication is between Jem Nodes.
	 */
	public ClientLoginProtocol(Key cryptographicKey) {
		this.cryptographicKey = cryptographicKey;
	}

	/**
	 * 
	 * @param responseParm is the response from server encoded in Base64
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginResponse
	 * 
	 * @param address is the ip:port of the client get form the socket.
	 * 
	 * @param subjectId subjectId of the client that can be either JEM_USER or
	 *            the user
	 * 
	 * @return the request that the client must send to the server after
	 *         receiving a specified response.
	 * @throws LoginProtocolException if any exception occur during the generation of the
	 *             Request and the parsing of the Response
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginRequest
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 * @see org.pepstock.jem.node.security.loginprotocol.RequestOperation
	 * @see org.pepstock.jem.node.security.loginprotocol.ResponseOperation
	 */
	public String getRequestFromResponse(String responseParm, String address, String subjectId) throws LoginProtocolException {
		LoginRequest jemRequest = null;

		// response is null when the client for the first time start
		// conversation with the server
		if (responseParm == null) {
			jemRequest = new LoginRequest();
			jemRequest.setAddress(address);
			jemRequest.setSubjectId(subjectId);
			RequestOperation op = new RequestOperation();
			op.setName(Operation.GETPASSWORD.toString());
			jemRequest.setOperation(op);
		} else {
			try {
				// request and response always travel in Base64 encode
				String response = new String(Base64.decodeBase64(responseParm), CharSet.DEFAULT);
				LoginResponse jemResponce = LoginResponse.unmarshall(response);
				if (jemResponce.getException() != null) {
					serverException = true;
					return null;
				} else if (jemResponce.getOperation().getName().equals(Operation.GETPASSWORD.toString())) {
					jemRequest = new LoginRequest();
					jemRequest.setAddress(address);
					jemRequest.setSubjectId(subjectId);
					// get the password sent by server encrypt it and encode it in
					// Base64. the server will than verify the password after
					// decryption
					String cryptPassword = Base64.encodeBase64String(Crypto.encrypt(jemResponce.getOperation().getResult().getBytes(CharSet.DEFAULT), this.cryptographicKey));
					jemRequest.setPassword(cryptPassword);
					RequestOperation op = new RequestOperation();
					op.setName(Operation.LOGIN.toString());
					jemRequest.setOperation(op);
					// else if the operation is login
				} else if (jemResponce.getOperation().getName().equals(Operation.LOGIN.toString())) {
					if (jemResponce.getOperation().getResult().equals(ResponseOperation.LOGIN_DENIED)) {
						throw new LoginProtocolException(NodeMessage.JEMC106W);
					} else if (jemResponce.getOperation().getResult().equals(ResponseOperation.LOGIN_ACCEPTED)) {
						conversationTerminated = true;
						return null;
					} else {
						conversationTerminated = true;
						throw new LoginProtocolException(NodeMessage.JEMC106W);
					}
					// else the operation is not correct
				} else {
					conversationTerminated = true;
					throw new LoginProtocolException(NodeMessage.JEMC106W);
				}
			} catch (KeyException e) {
				throw new LoginProtocolException(NodeMessage.JEMC106W, e);				
			}
		}
		String xmlRequest = LoginRequest.marshall(jemRequest);
		return Base64.encodeBase64String(xmlRequest.getBytes(CharSet.DEFAULT));
	}

	/**
	 * 
	 * @return true when last message of the client server communication is set.
	 *         Is used by MemberSocketInterceptor.
	 * @see org.pepstock.jem.node.security.socketinterceptor.NodeInterceptor
	 * @see org.pepstock.jem.node.security.socketinterceptor.SubmitInterceptor
	 */
	public boolean isConversationTerminated() {
		return conversationTerminated;
	}

	/**
	 * 
	 * @return true if the parsed request is not valid or if any exception occur
	 *         during the parsing of the request or if login fail. Is used by
	 *         the SocketInterceptor to exit abnormally.
	 * @see org.pepstock.jem.node.security.socketinterceptor.NodeInterceptor
	 * @see org.pepstock.jem.node.security.socketinterceptor.SubmitInterceptor
	 */
	public boolean isServerException() {
		return serverException;
	}

}
