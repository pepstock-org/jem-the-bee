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
package org.pepstock.jem.grs;

import java.io.Serializable;

import org.pepstock.jem.node.ResourceLock;


/**
 * Represents the entity which really needs the resources and the requester for
 * locking.<br>
 * Usually is a entity which runs inside of a GRSnode (see JEM example: the
 * requestor is job which is executing by jem node, which is the GRSnode).<br>
 * Contains the info of GRSNode too, as key and label.<br>
 * Is strictly related to LatchInfo, container of RequestorInfo.
 * 
 * @see LatchInfo#getRequestors()
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class RequestorInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nodeKey = null;

	private String nodeLabel = null;

	private String name = null;

	private String id = null;

	private int mode = ResourceLock.READ_MODE;

	/**
	 * Empty constructor
	 */
	public RequestorInfo() {
	}

	/**
	 * Returns the key of node, where the requestor is running.<br>
	 * Key for locking, together with requestorID.
	 * 
	 * @see GrsNode#getKey()
	 * @return key of node
	 */
	public String getNodeKey() {
		return nodeKey;
	}

	/**
	 * Sets the key of node, where the requestor is running.<br>
	 * Key for locking, together with requestorID.
	 * 
	 * @see GrsNode#getKey()
	 * @param nodeKey key of node
	 */
	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	/**
	 * Returns the label of node, where the requestor is running.
	 * 
	 * @see GrsNode#getLabel()
	 * @return label of node
	 */
	public String getNodeLabel() {
		return nodeLabel;
	}

	/**
	 * Sets the label of node, where the requestor is running.
	 * 
	 * @see GrsNode#getLabel()
	 * @param nodeLabel label of node
	 */
	public void setNodeLabel(String nodeLabel) {
		this.nodeLabel = nodeLabel;
	}

	/**
	 * Returns the name of requestor. Usually this is a readable information,
	 * helpful for logging.<br>
	 * In JEM is job-name.
	 * 
	 * @return name of requestor
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of requestor. Usually this is a readable information,
	 * helpful for logging.<br>
	 * In JEM is job-name.
	 * 
	 * @param name name of requestor
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the id of requestor. Key for locking, together with nodeKey.<br>
	 * In JEM is job-id.
	 * 
	 * @return id of requestor
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of requestor. Key for locking, together with nodeKey.<br>
	 * In JEM is job-id.
	 * 
	 * @param id id of requestor
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the requested lock type
	 * 
	 * @see ResourceLock#getMode()
	 * @see ResourceLock#READ_MODE
	 * @see ResourceLock#WRITE_MODE
	 * @return lock type
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the requested lock type
	 * 
	 * @see ResourceLock#getMode()
	 * @see ResourceLock#READ_MODE
	 * @see ResourceLock#WRITE_MODE
	 * @param mode lock type
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Returns the string representation of requestor.
	 * 
	 * @return requestor string representation
	 */
	@Override
	public String toString() {
		return "Requestor [name=" + name + ", node=" + nodeLabel + ", mode=" + ((mode == ResourceLock.READ_MODE) ? "READ" : "WRITE") + "]";
	}

}