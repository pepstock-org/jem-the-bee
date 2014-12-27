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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.NodeInfoBean;

/**
 * POJO container of nodes list.<br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
@XmlRootElement
public class Nodes extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;

	private Collection<NodeInfoBean> nodes = null;

	/**
	 * Empty constructor
	 */
	public Nodes() {
	}

	/**
	 * @return the nodes
	 */
	public Collection<NodeInfoBean> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(Collection<NodeInfoBean> nodes) {
		this.nodes = nodes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Nodes [nodes=" + nodes + "]";
	}
	
}