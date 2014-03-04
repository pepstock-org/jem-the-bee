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
 * Is a message used after shutting down the multicast service either web or
 * node so that the service execute another while loop and realise that the
 * Thread is benen interruped s it will exit
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class ShutDown implements MulticastMessage {

	private boolean shutDown=true;

	/**
	 * @return the shutDown
	 */
	public boolean isShutDown() {
		return shutDown;
	}

	/**
	 * @param shutDown the shutDown to set
	 */
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	/**
	 * 
	 * @param xmlMessage
	 * @return the NodeMulticastResponce unmarshall from the xml representation
	 * @throws NodeMessageException if any exception occurs during the unmarshall process
	 */
	public static ShutDown unmarshall(String xmlMessage) throws NodeMessageException {
		XStream xStream = new XStream();
		xStream.alias("shutDownMessage", ShutDown.class);
		Object multicastMessage;
		try {
			multicastMessage = xStream.fromXML(xmlMessage);
		} catch (Exception e) {
			throw new NodeMessageException(NodeMessage.JEMC109W, e, xmlMessage);
		}
		if (!(multicastMessage instanceof ShutDown)) {
			throw new NodeMessageException(NodeMessage.JEMC109W, xmlMessage);
		}
		return (ShutDown) multicastMessage;
	}

	/**
	 * 
	 * @param message
	 * @return the xml marshall from the ShutDown
	 */
	public static String marshall(ShutDown message) {
		XStream xStream = new XStream();
		xStream.alias("shutDownMessage", ShutDown.class);
		return xStream.toXML(message);
	}

}
