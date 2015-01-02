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

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;

import com.thoughtworks.xstream.XStream;

/**
 * Is the POJO representation of the multicast message sent by the node
 * coordinator to the web client. 
 * <br>
 * It contains the list of members in the form <code>IpAddress:port</code> 
 * 
 * @author Simone "Busy" Businaro
 * @version 2.0
 * 
 */
public class NodeResponse extends GroupMulticastMessage {
	
	private static final String NODE_RESPONSE_ELEMENT = "nodeResponse";

	private static final String NODE_ELEMENT = "node";
	
	private List<String> nodesMembers;
	
	/**
	 * @return the nodesMembers
	 */
	public List<String> getNodesMembers() {
		return nodesMembers;
	}

	/**
	 * @param nodesMembers the nodesMembers to set
	 */
	public void setNodesMembers(List<String> nodesMembers) {
		this.nodesMembers = nodesMembers;
	}

	/**
	 * @param member a string representation in the form IpAddress:port
	 */
	public void addMember(String member) {
		// if collection is null
		// creates the collection.
		if (nodesMembers == null) {
			nodesMembers = new ArrayList<String>();
		}
		nodesMembers.add(member);
	}

	/**
	 * De-serialize a string message in a java object.
	 * 
	 * @param xmlMulticastMessage the xml multicast message
	 * @return the JemLoginRequest unmarshall from the xml representation
	 * @throws NodeMessageException if any exception occurs during the unmarshall process
	 */
	public static NodeResponse unmarshall(String xmlMulticastMessage) throws NodeMessageException {
		// uses XStream
		XStream xStream = new XStream();
		// sets nodeResponse and node as alias
		xStream.alias(NODE_RESPONSE_ELEMENT, NodeResponse.class);
		xStream.alias(NODE_ELEMENT, String.class);
		Object multicastMessage;
		try {
			// deserializes from XML
			multicastMessage = xStream.fromXML(xmlMulticastMessage);
		} catch (Exception e) {
			throw new NodeMessageException(NodeMessage.JEMC109W, e, xmlMulticastMessage);
		}
		// if the object is not a NodeResponse, EXCEPTION 
		if (!(multicastMessage instanceof NodeResponse)) {
			throw new NodeMessageException(NodeMessage.JEMC109W, xmlMulticastMessage);
		}
		return (NodeResponse) multicastMessage;
	}

	/**
	 * Serializes the object in XML
	 * @param instance node response instance to serialize
	 * @return the xml marshall from the NodeMulticastMessage
	 */
	public static String marshall(NodeResponse instance) {
		XStream xStream = new XStream();
		xStream.alias(NODE_RESPONSE_ELEMENT, NodeResponse.class);
		xStream.alias(NODE_ELEMENT, String.class);
		return xStream.toXML(instance);
	}
}