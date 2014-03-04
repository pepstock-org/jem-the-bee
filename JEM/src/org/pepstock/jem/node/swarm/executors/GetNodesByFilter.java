/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone "Busy" Businaro
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
package org.pepstock.jem.node.swarm.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.swarm.SwarmException;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.predicates.NodePredicate;

/**
 * Is the Callable responsible to retrieve the storm nodes that satisfies the
 * filter
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class GetNodesByFilter extends AbstractGetNodes {

	private static final long serialVersionUID = 1L;

	/**
	 * The filter to apply to the swarm nodes
	 */
	Filter filter = null;

	/**
	 * 
	 * @param filter
	 */
	public GetNodesByFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * Return the list of the swarm nodes that satisfies the sql filter
	 * 
	 * @throws SwarmException if any exception occurs
	 */
	@Override
	public Collection<NodeInfoBean> call() throws SwarmException  {
		List<NodeInfoBean> list = new ArrayList<NodeInfoBean>();
		// creates predicate
		NodePredicate predicate = new NodePredicate(filter);

		Collection<NodeInfo> allNodes = getNodes(predicate);
		if (allNodes != null) {
			// gets the nodes and returns them
			// removing the nodes in UNKNOW
			// to avoid misunderstanding on UI
			for (NodeInfo node : allNodes) {
				if (!node.getStatus().equals(Status.DRAINED)){
					list.add(node.getNodeInfoBean());
				}
			}
		}
		return list;
	}

}