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
package org.pepstock.jem.gwt.server.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Calls to have the list of all active member of JEM group.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class GetClusterMembers extends JemDefaultServlet {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.servlet.DefaultServlet#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.execute(request, response);
		HazelcastInstance instance = SharedObjects.getInstance().getHazelcastClient();
		
//		List<Member> list = new ArrayList<Member>();
//		Cluster cluster = instance.getCluster();
//		Set<Member> members = cluster.getMembers();
//		for (Member member : members) {
//			list.add(member);
//		}
//		int count = list.size();
//		for (Member member : list) {
//			response.getWriter().print(member.getInetSocketAddress().getAddress().getHostAddress()+":"+member.getInetSocketAddress().getPort());
//			if (--count > 0){
//				response.getWriter().print(",");
//			}
//		}
//		response.getWriter().close();
		
	      IMap<String, NodeInfo> membersMap = instance.getMap(Queues.NODES_MAP);
	        try {
	            // check if node release version inside the cluster are different
	            Collection<NodeInfo> allNodes = membersMap.values();
	    		int count = allNodes.size();
	            // scans all node to check the version
	            for (NodeInfo currNodeInfo : allNodes) {
	            	
	    			response.getWriter().print(currNodeInfo.getDockerHostAddress());
	    			if (--count > 0){
	    				response.getWriter().print(",");
	    			}
	            }
	        } catch (Exception ex) {
	            LogAppl.getInstance().emit(NodeMessage.JEMC174E, ex);
	        } finally {
	        	response.getWriter().close();
	        }
	}

}