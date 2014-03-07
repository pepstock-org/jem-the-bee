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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.security.Crypto;
import org.pepstock.jem.node.security.keystore.KeyStoresInfo;
import org.pepstock.jem.node.security.keystore.KeysUtil;
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
 * 
 */

/**
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class ServerLoginProtocol {

	/**
	 * The map the hold the client subject and password to check during login
	 * operation
	 */
	private Map<String, String> authorizedSubject = new HashMap<String, String>(1);

	private boolean conversationTerminated = false;

	private boolean serverException = false;

	// is the public key relative to the private key provided by the user in
	// case of a Submit operation or
	// the symmetric key in case the client/server communication is between Jem
	// Nodes.
	private Key cryptographicKey;

	private KeyStoresInfo keystoresInfo;

	/**
	 * 
	 * @param keystoresInfo is the bean containing information about the
	 *            keystores
	 */
	public ServerLoginProtocol(KeyStoresInfo keystoresInfo) {
		this.keystoresInfo = keystoresInfo;
	}

	/**
	 * 
	 * @param requestParm is the request from client encoded in Base64
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginRequest
	 * 
	 * @return the response that the server must send to the client after
	 *         receiving a specified request.
	 * @throws LoginProtocolException if any exception occur during the
	 *             generation of the Request and the parsing of the Response
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginRequest
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 * @see org.pepstock.jem.node.security.loginprotocol.RequestOperation
	 * @see org.pepstock.jem.node.security.loginprotocol.ResponseOperation
	 */
	public String getResponseFromRequest(String requestParm) throws LoginProtocolException {
		LoginResponse jemResponse = null;
		try {
			String request = new String(Base64.decodeBase64(requestParm), CharSet.DEFAULT);
			LoginRequest jemRequest = LoginRequest.unmarshall(request);
			String subjectId = jemRequest.getSubjectId();
			if (cryptographicKey == null) {
				if (subjectId.equals(LoginRequest.JEM_NODE_USER) || subjectId.equals(LoginRequest.JEM_WEB_USER)) {
					this.cryptographicKey = KeysUtil.getSymmetricKey(keystoresInfo.getClusterKeystoreInfo());
				} else {
					this.cryptographicKey = KeysUtil.getPublicKeyByAlias(keystoresInfo.getUserKeystoreInfo(), subjectId);
				}
			}
			// if the request operation is get password, generate a one time
			// password, memorized it and send it to the cilent
			if (jemRequest.getOperation().getName().equals(Operation.GETPASSWORD.toString())) {
				jemResponse = new LoginResponse();
				String randomPassword = UUID.randomUUID().toString();
				jemResponse.setAddress(jemRequest.getAddress());
				jemResponse.setSubjectId(jemRequest.getSubjectId());
				ResponseOperation op = new ResponseOperation();
				op.setName(Operation.GETPASSWORD.toString());
				op.setResult(randomPassword);
				jemResponse.setOperation(op);
				authorizedSubject.put(jemRequest.getAddress(), randomPassword);
				// if the request operation is login, verify user password to
				// allow or not the client to participate to the cluster and
				// send the response back
			} else if (jemRequest.getOperation().getName().equals(Operation.LOGIN.toString())) {
				jemResponse = new LoginResponse();
				String address = jemRequest.getAddress();
				String clearPassword = null;
				clearPassword = new String(Crypto.decrypt(Base64.decodeBase64(jemRequest.getPassword()), this.cryptographicKey), CharSet.DEFAULT);

				jemResponse.setAddress(address);
				jemResponse.setSubjectId(jemRequest.getSubjectId());
				ResponseOperation op = new ResponseOperation();
				op.setName(Operation.LOGIN.toString());
				if (authorizedSubject.containsKey(address) && authorizedSubject.get(address).equals(clearPassword)) {
					op.setResult(ResponseOperation.LOGIN_ACCEPTED);
					conversationTerminated = true;
				} else {
					LogAppl.getInstance().emit(NodeMessage.JEMC109W, "Wrong password");
					LogAppl.getInstance().emit(NodeMessage.JEMC106W);
					op.setResult(ResponseOperation.LOGIN_DENIED);
					serverException = true;
				}
				jemResponse.setOperation(op);
			} else {
				LogAppl.getInstance().emit(NodeMessage.JEMC109W, request);
				LogAppl.getInstance().emit(NodeMessage.JEMC106W);
				jemResponse = new LoginResponse();
				jemResponse.setException(LoginResponse.EXCEPTION_MESSAGE);
				serverException = true;
			}
		} catch (KeyException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			jemResponse = createResponseWithException(e);
		} catch (MessageException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			jemResponse = createResponseWithException(e);
		}
		String xmlResponse = LoginResponse.marshall(jemResponse);
		return Base64.encodeBase64String(xmlResponse.getBytes(CharSet.DEFAULT));
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
	 *         the MemberSocketInterceptor to exit abnormally.
	 * @see org.pepstock.jem.node.security.socketinterceptor.NodeInterceptor
	 * @see org.pepstock.jem.node.security.socketinterceptor.SubmitInterceptor
	 */
	public boolean isServerException() {
		return serverException;
	}

	/**
	 * Manages a response with exception.
	 * @param ex exception to print out
	 * @return login response object
	 */
	private LoginResponse createResponseWithException(Exception ex) {
		LoginResponse jemResponse = new LoginResponse();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		jemResponse.setException(sw.toString());
		serverException = true;
		return jemResponse;
	}

}
