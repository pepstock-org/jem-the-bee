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
package org.pepstock.jem.node.multicast.messages;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;

import com.thoughtworks.xstream.XStream;

/**
 * Request from Hazelcast client to connect to JEM group.
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 * 
 */
public class ClientRequest extends GroupMulticastMessage {
	
	private static final String CLIENT_REQUEST_ELEMENT = "clientRequest";

	/**
	 * De-serializes the string message in a client request
	 * 
	 * @param xmlRequestMessage the xml multicast message
	 * @return the NodeMulticastResponce unmarshall from the xml representation
	 * @throws NodeMessageException if any exception occurs during the unmarshall process
	 */
	public static ClientRequest unmarshall(String xmlRequestMessage) throws NodeMessageException {
		// uses XStream
		XStream xStream = new XStream();
		// sets request alias
		xStream.alias(CLIENT_REQUEST_ELEMENT, ClientRequest.class);
		Object multicastMessage;
		try {
			// reads from string the object
			multicastMessage = xStream.fromXML(xmlRequestMessage);
		} catch (Exception e) {
			throw new NodeMessageException(NodeMessage.JEMC109W, e, xmlRequestMessage);
		}
		// if the object is not a client request, EXCEPTION
		if (!(multicastMessage instanceof ClientRequest)) {
			throw new NodeMessageException(NodeMessage.JEMC109W, xmlRequestMessage);
		}
		return (ClientRequest) multicastMessage;
	}

	/**
	 * Serializes the object in a xml string format.
	 * @param instance Client request instance to serialize
	 * @return the xml marshall from the ClientMulticastRequest
	 */
	public static String marshall(ClientRequest instance) {
		// uses XStream
		XStream xStream = new XStream();
		xStream.alias(CLIENT_REQUEST_ELEMENT, ClientRequest.class);
		return xStream.toXML(instance);
	}
}
