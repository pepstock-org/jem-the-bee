/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
import org.pepstock.jem.node.swarm.SwarmException;

import com.hazelcast.query.SqlPredicate;

/**
 * Is the Callable responsible to retrieve the storm nodes that satisfies the
 * filter
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class GetNodes extends AbstractGetNodes {

	private static final long serialVersionUID = 1L;

	/**
	 * The filter to apply to the swarm nodes
	 */
	private String nodesFilter = null;

	/**
	 * 
	 * @param nodesFilter
	 */
	public GetNodes(String nodesFilter) {
		this.nodesFilter = nodesFilter;
	}

	/**
	 * Return the list of the swarm nodes that satisfies the sql filter
	 * 
	 * @throws exception
	 */
	@Override
	public Collection<NodeInfoBean> call() throws SwarmException {
		List<NodeInfoBean> list = new ArrayList<NodeInfoBean>();
		// prepares SQL query to extract the right nodes
		String sqlFilter = nodesFilter.replace('.', '_');
		sqlFilter = sqlFilter.replace('*', '%');
		// creates SQL
		StringBuilder sb = new StringBuilder();
		sb.append("(hostname like '").append(sqlFilter).append("'").append(" OR ");
		sb.append("label like '").append(sqlFilter).append("') ");
		SqlPredicate predicate = new SqlPredicate(sb.toString());

		// locks all map to have a consistent collection
		// only for 10 seconds otherwise
		// throws an exception
		Collection<NodeInfo> allNodes = getNodes(predicate);
		// must scan the result of all nodes
		// becuse it has to serialize NodeInfoBean
		if (allNodes != null) {
			// gets the nodes info bean and returns them
			for (NodeInfo node : allNodes) {
				list.add(node.getNodeInfoBean());
			}
		}
		return list;
	}

}