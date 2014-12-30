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

import org.pepstock.jem.node.NodeMessage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author Simone Businaro
 * @version 1.0
 * 
 *          It represent the server part of the communication during the join
 *          cluster operation.
 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
 * @see org.pepstock.jem.node.security.loginprotocol.LoginRequest
 */
public class LoginResponse {

	/**
	 * Exception message of the response
	 */
	public static final String EXCEPTION_MESSAGE = "bad request";
	
	private static final String RESPONSE_ELEMENT = "response";

	private static final String OPERATION_ELEMENT = "operation";
	
	private String address;

	private String subjectId;

	private ResponseOperation operation;

	private String exception;

	/**
	 * 
	 * @return the exception message of the response.
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
	 */
	public String getException() {
		return exception;
	}

	/**
	 * 
	 * @param exception
	 */
	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * 
	 * @return the address of the response that is the ip:port of the client
	 *         that is trying to connect to the cluster
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address of the response that is the
	 *            ip:port of the client that is trying to connect to the cluster
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 
	 * @return the operation of the response.
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 * 
	 */
	public ResponseOperation getOperation() {
		return operation;
	}

	/**
	 * 
	 * @param operation of the response
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 */
	public void setOperation(ResponseOperation operation) {
		this.operation = operation;
	}

	/**
	 * @return the subjectId
	 */
	public String getSubjectId() {
		return subjectId;
	}

	/**
	 * @param subjectId the subjectId to set
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	/**
	 * 
	 * @param xmlResponse
	 * @return the JemLoginResponse unmarshall from the xml representation
	 * @throws LoginProtocolException if any exception occurs during the unmarshall process
	 */
	public static LoginResponse unmarshall(String xmlResponse) throws LoginProtocolException {
		XStream xStream = new XStream(new DomDriver());
		xStream.alias(RESPONSE_ELEMENT, LoginResponse.class);
		xStream.alias(OPERATION_ELEMENT, ResponseOperation.class);
		Object jemResponce = xStream.fromXML(xmlResponse);
		if (!(jemResponce instanceof LoginResponse)) {
			throw new LoginProtocolException(NodeMessage.JEMC109W, xmlResponse);
		}
		return (LoginResponse) jemResponce;
	}

	/**
	 * 
	 * @param response
	 * @return the xml marshall from the JemLoginResponse
	 */
	public static String marshall(LoginResponse response) {
		XStream xStream = new XStream();
		xStream.alias(RESPONSE_ELEMENT, LoginResponse.class);
		xStream.alias(OPERATION_ELEMENT, ResponseOperation.class);
		return xStream.toXML(response);
	}
}