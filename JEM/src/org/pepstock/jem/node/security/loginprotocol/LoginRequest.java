/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import org.pepstock.jem.node.NodeMessage;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Simone Businaro
 * @version 1.0
 * 
 *          When a member join a cluster, it asks the cluster to enter using a
 *          JemLoginRequest that indeed represent the client part of the
 *          communication during the join cluster operation.
 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
 * 
 */
public class LoginRequest {

	/**
	 * is the subject id in case the client is a JEM NODE and not a submit
	 * operation or the jem web application
	 */
	public static final String JEM_NODE_USER = "JEM_NODE";

	/**
	 * is the subject id in case the client is a JEM WEB APPLICATION and not a submit
	 * operation or a JEM NODE
	 */
	public static final String JEM_WEB_USER = "JEM_WEB_APPLICATION";

	private String address;

	private String subjectId;

	private String password;

	private RequestOperation operation;

	/**
	 * 
	 * @return the address of client asking to join the cluster. Is in the form
	 *         ip:port
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address is the ip:port of the client asking to join the cluster
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 
	 * @return the password of the request needed to be authenticated by the
	 *         cluster
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password of the request needed to be authenticated by the cluster
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return the operation of the request.
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 */
	public RequestOperation getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 */
	public void setOperation(RequestOperation operation) {
		this.operation = operation;
	}

	/**
	 * @return the subjectId of the request that represent the name of the user
	 *         that correspond in the keystore to the alias of the X.509
	 *         Certifcate of the user. If the request is relative to a JemNode
	 *         than the user will be {@value #JEM_NODE_USER}
	 */
	public String getSubjectId() {
		return subjectId;
	}

	/**
	 * @param subjectId the subjectId to set. the subjectId of the request that
	 *            represent the name of the user that correspond in the keystore
	 *            to the alias of the X.509 Certifcate of the user. If the
	 *            request is relative to a JemNode than the user will be
	 *            {@value #JEM_NODE_USER}
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	/**
	 * 
	 * @param xmlRequest
	 * @return the JemLoginRequest unmarshall from the xml representation
	 * @throws  LoginProtocolException if any exception occurs during the unmarshall process
	 */
	public static LoginRequest unmarshall(String xmlRequest) throws LoginProtocolException {
		XStream xStream = new XStream();
		xStream.alias("request", LoginRequest.class);
		xStream.alias("operation", RequestOperation.class);
		Object jemRequest = xStream.fromXML(xmlRequest);
		if (!(jemRequest instanceof LoginRequest)) {
			throw new  LoginProtocolException(NodeMessage.JEMC109W, xmlRequest);
		}
		return (LoginRequest) jemRequest;
	}

	/**
	 * 
	 * @param request
	 * @return the xml marshall from the JemLoginRequest
	 */
	public static String marshall(LoginRequest request) {
		XStream xStream = new XStream();
		xStream.alias("request", LoginRequest.class);
		xStream.alias("operation", RequestOperation.class);
		return xStream.toXML(request);
	}
}
