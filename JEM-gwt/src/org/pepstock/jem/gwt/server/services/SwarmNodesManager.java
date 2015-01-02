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
package org.pepstock.jem.gwt.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.swarm.SwarmNodeMessage;
import org.pepstock.jem.node.swarm.executors.Drain;
import org.pepstock.jem.node.swarm.executors.GetNodes;
import org.pepstock.jem.node.swarm.executors.GetNodesByFilter;
import org.pepstock.jem.node.swarm.executors.GetStatus;
import org.pepstock.jem.node.swarm.executors.Start;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

/**
 * Is the manager of all operations to the nodes of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SwarmNodesManager extends DefaultService {

	private NodesManager nodeManager = new NodesManager();

	private Member getMemberFormNodeList(Collection<NodeInfoBean> list) {
		Member selectedMember = null;
		// select a swarm member from the list
		for (NodeInfoBean node : list) {
			// gets the cluster to have member object of Hazelcast
			// to execute the future task
			Cluster cluster = getInstance().getCluster();
			// gets all members and scans them
			Set<Member> set = cluster.getMembers();
			for (Member member : set) {
				String memberKey = member.getUuid();
				// is the same member
				if (node.getKey().equalsIgnoreCase(memberKey)) {
					selectedMember = member;
					break;
				}
			}
		}
		return selectedMember;
	}

	/**
	 * Returns the list of all nodes joined the cluster.
	 * 
	 * @param nodesFilter
	 *            ipaddress or hostname filter
	 * @return collection of nodes
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodes(String nodesFilter) throws ServiceMessageException {
		Collection<NodeInfoBean> list = new ArrayList<NodeInfoBean>();
		// if there are not swarm nodes return empty list
		Collection<NodeInfoBean> swarmNodes = nodeManager.getSwarmNodes("*");
		if (swarmNodes == null || swarmNodes.isEmpty()) {
			return list;
		}
		// builds permission
		String permission = Permissions.SEARCH_NODES + nodesFilter.toLowerCase();
		// checks if the user is authorized to search nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));
		
		DistributedTaskExecutor<Collection<NodeInfoBean>> task = new DistributedTaskExecutor<Collection<NodeInfoBean>>(new GetNodes(nodesFilter), getMemberFormNodeList(swarmNodes));
		return task.getResult();

	}

	/**
	 * Returns the list of all nodes joined the cluster. UNKNOWN members are not
	 * returned
	 * 
	 * @param nodesFilter
	 *            a String that will be parsed as a {@link Filter}
	 * @return collection of nodes
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodesByFilter(String nodesFilter) throws ServiceMessageException{
		List<NodeInfoBean> list = new ArrayList<NodeInfoBean>();
		// if MainSwarm hazelcast instance is not running return empty list
		Collection<NodeInfoBean> swarmNodes = nodeManager.getSwarmNodes("*");
		if (swarmNodes == null || swarmNodes.isEmpty()) {
			return list;
		}
		Filter filter = Filter.parseOrDefault(nodesFilter, Filter.NODE_DEFAULT_FILTER);
		// extract the label or hostname, if it is.
		// necessary to check permission because it is based on
		// label or hostname
		String nodesPermString = filter.get(NodeFilterFields.NAME.getName());
		// if label is null, try with hostname
		if ((nodesPermString == null) || (nodesPermString.trim().length() == 0)) {
			nodesPermString = filter.get(NodeFilterFields.HOSTNAME.getName());
			// if hostname is null as well, then use *
			if ((nodesPermString == null) || (nodesPermString.trim().length() == 0)) {
				nodesPermString = "*";
			}
		}
		// creates the right permission by jlabel or hostname
		String permission = Permissions.SEARCH_NODES + nodesPermString.toLowerCase();
		// checks if the user is authorized to get nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));
		
		DistributedTaskExecutor<Collection<NodeInfoBean>> task = new DistributedTaskExecutor<Collection<NodeInfoBean>>(new GetNodesByFilter(filter), getMemberFormNodeList(swarmNodes));
		return task.getResult();
	}

	/**
	 * Starts swarm nodes, using a future task by executor service of Hazelcast.
	 * 
	 * @return always TRUE
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Boolean start() throws ServiceMessageException {
		// checks if the user is authorized to start nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.SWARM_NODES_START));
		IMap<String, SwarmConfiguration> routingConfigMap = SharedObjects.getInstance().getHazelcastClient().getMap(Queues.ROUTING_CONFIG_MAP);
		SwarmConfiguration conf = null;
		try {
			routingConfigMap.lock(SwarmConfiguration.DEFAULT_NAME);
			conf = routingConfigMap.get(SwarmConfiguration.DEFAULT_NAME);
			if (conf == null) {
				throw new ServiceMessageException(SwarmNodeMessage.JEMO021E);
			}
			if (!conf.isEnabled()) {
				throw new ServiceMessageException(SwarmNodeMessage.JEMO019E);
			}
			if (conf.getNetworks().isEmpty()) {
				throw new ServiceMessageException(SwarmNodeMessage.JEMO020E);
			}
		} finally {
			routingConfigMap.unlock(SwarmConfiguration.DEFAULT_NAME);
		}
		
		Boolean executed = Boolean.FALSE;
		Boolean result = Boolean.TRUE;
		Collection<NodeInfoBean> nodes = nodeManager.getNodes("*");
		// scans all nodes
		for (NodeInfoBean node : nodes) {
			// gets the cluster to have member object of Hazelcast
			// to execute the future task
			Cluster cluster = getInstance().getCluster();
			// gets all members and scans them
			Set<Member> set = cluster.getMembers();
			for (Member member : set) {
				String memberKey = member.getUuid();
				// is the same member
				if (node.getKey().equalsIgnoreCase(memberKey)) {
					executed = Boolean.TRUE;
					DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(new Start(), member);
					Boolean taskResult = task.getResult();
					if (!taskResult) {
						result = Boolean.FALSE;
					}
				}
			}
		}
		return result && executed;
	}

	/**
	 * Shuts down all the swarm nodes, using a future task by executor service
	 * of Hazelcast.
	 * 
	 * @return always true
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Boolean drain() throws ServiceMessageException {
		// checks if the user is authorized to stop nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.SWARM_NODES_DRAIN));
		Boolean executed = Boolean.FALSE;
		Boolean result = Boolean.TRUE;
		// get nodes that has active swarm instance
		Collection<NodeInfoBean> swarmNodes = nodeManager.getSwarmNodes("*");
		// scans all nodes
		for (NodeInfoBean node : swarmNodes) {
			Cluster cluster = getInstance().getCluster();
			// gets all members and scans them
			Set<Member> set = cluster.getMembers();
			for (Member member : set) {
				String memberKey = member.getUuid();
				// is the same member
				if (node.getKey().equalsIgnoreCase(memberKey)) {
					executed = Boolean.TRUE;
					DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(new Drain(), member);
					Boolean taskResult = task.getResult();
					if (!taskResult) {
						result = Boolean.FALSE;
					}
				}
			}
		}
		return result && executed;
	}

	/**
	 * Returns the status of swarm
	 * 
	 * @return status if swarm
	 * @throws ServiceMessageException if any exception occurs
	 */
	public String getStatus() throws ServiceMessageException {
		// checks if the user is authenticated
		checkAuthentication();
		Status currentStatus = null;
		Collection<NodeInfoBean> swarmNodes = nodeManager.getSwarmNodes("*");
		// if there are no swarm node active then return drained status
		if (swarmNodes == null || swarmNodes.isEmpty()) {
			return Status.DRAINED.getDescription();
		}
		// scans all nodes
		for (NodeInfoBean node : swarmNodes) {
			// gets the cluster to have member object of Hazelcast
			// to execute the future task
			Cluster cluster = getInstance().getCluster();
			// gets all members and scans them
			Set<Member> set = cluster.getMembers();
			for (Member member : set) {
				String memberKey = member.getUuid();
				// is the same member
				if (node.getKey().equalsIgnoreCase(memberKey)) {
					DistributedTaskExecutor<Status> task = new DistributedTaskExecutor<Status>(new GetStatus(), member);
					Status status = task.getResult();
					if (currentStatus == null) {
						currentStatus = status;
					} else {
						if (!currentStatus.equals(status)) {
							return Status.UNKNOWN.getDescription();
						}
					}
				}
			}
		}
		return (currentStatus == null) ? Status.UNKNOWN.getDescription() : currentStatus.getDescription();
	}

}