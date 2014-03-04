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
package org.pepstock.jem.node.multicast.messages;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class ClientRequest implements MulticastMessage {

	private String group;

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * 
	 * @param xmlRequestMessage the xml multicast message
	 * @return the NodeMulticastResponce unmarshall from the xml representation
	 * @throws NodeMessageException if any exception occurs during the unmarshall process
	 */
	public static ClientRequest unmarshall(String xmlRequestMessage) throws NodeMessageException {
		XStream xStream = new XStream();
		xStream.alias("clientRequest", ClientRequest.class);
		Object multicastMessage;
		try {
			multicastMessage = xStream.fromXML(xmlRequestMessage);
		} catch (Exception e) {
			throw new NodeMessageException(NodeMessage.JEMC109W, e, xmlRequestMessage);
		}
		if (!(multicastMessage instanceof ClientRequest)) {
			throw new NodeMessageException(NodeMessage.JEMC109W, xmlRequestMessage);
		}
		return (ClientRequest) multicastMessage;
	}

	/**
	 * 
	 * @param message
	 * @return the xml marshall from the ClientMulticastRequest
	 */
	public static String marshall(ClientRequest message) {
		XStream xStream = new XStream();
		xStream.alias("clientRequest", ClientRequest.class);
		return xStream.toXML(message);
	}

}
