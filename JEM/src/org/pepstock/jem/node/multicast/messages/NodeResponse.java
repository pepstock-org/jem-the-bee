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

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;

import com.thoughtworks.xstream.XStream;

/**
 * Is the POJO rapresentation of the multicast message sent by the node
 * coordinator to the web client. It contains the list of members in the form
 * IpAddress:port
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class NodeResponse implements MulticastMessage {

	private List<String> nodesMembers;

	private String group;
	
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
	 * 
	 * @param member a string rapresentation in the form IpAddress:port
	 */
	public void addMember(String member) {
		if (nodesMembers == null) {
			nodesMembers = new ArrayList<String>();
			nodesMembers.add(member);
		} else {
			nodesMembers.add(member);
		}
	}

	/**
	 * 
	 * @param xmlMulticastMessage the xml multicast message
	 * @return the JemLoginRequest unmarshall from the xml representation
	 * @throws NodeMessageException 
	 * @throws Exception if any exception occurs during the unmarshall process
	 */
	public static NodeResponse unmarshall(String xmlMulticastMessage) throws NodeMessageException {
		XStream xStream = new XStream();
		xStream.alias("nodeResponse", NodeResponse.class);
		xStream.alias("node", String.class);
		Object multicastMessage;
		try {
			multicastMessage = xStream.fromXML(xmlMulticastMessage);
		} catch (Exception e) {
			throw new NodeMessageException(NodeMessage.JEMC109W, e, xmlMulticastMessage);
		}
		if (!(multicastMessage instanceof NodeResponse)) {
			throw new NodeMessageException(NodeMessage.JEMC109W, xmlMulticastMessage);
		}
		return (NodeResponse) multicastMessage;
	}

	/**
	 * 
	 * @param message
	 * @return the xml marshall from the NodeMulticastMessage
	 */
	public static String marshall(NodeResponse message) {
		XStream xStream = new XStream();
		xStream.alias("nodeResponse", NodeResponse.class);
		xStream.alias("node", String.class);
		return xStream.toXML(message);
	}

	/**
	 * @return the hazelcast group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the hazelcst group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

}
