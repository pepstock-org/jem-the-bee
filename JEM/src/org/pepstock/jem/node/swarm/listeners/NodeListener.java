/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Busimato
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
package org.pepstock.jem.node.swarm.listeners;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.swarm.SwarmNodeMessage;
import org.pepstock.jem.node.swarm.SwarmQueues;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * Implements the listener interface of Hazelcast cluster to listen when a
 * member is removed from swarm cluster.<br>
 * It's necessary to check who is the first member in the cluster so to let do
 * to him all the work
 * 
 * @author Simone "Busy" Busimato
 * 
 */
public class NodeListener implements MembershipListener {

	/**
	 * Empty constructor.
	 */
	public NodeListener() {
	}

	/**
	 * Not implement because not necessary
	 * 
	 * @param event event of hazelcast
	 */
	@Override
	public void memberAdded(MembershipEvent event) {
		// do nothing
	}

	/**
	 * Checks who is the first member on the list will work, becoming the
	 * coordinator of group.
	 * 
	 * @param event event of Hazelcast
	 */

	@Override
	public void memberRemoved(MembershipEvent event) {

		// get hazelcast cluster and local node
		Cluster cluster = Main.SWARM.getHazelcastInstance().getCluster();
		Member local = cluster.getLocalMember();

		for (Member member : cluster.getMembers()) {
			// if the first in the list is not the local
			// one, return
			// because only the first must work (it could be already the
			// coordinator but
			// tried to avoid double check
			if (!local.equals(member)){
				return;
			} else {
				// this node is the coordinator so ONLY the coordinator must
				// work for all group
				actionsForCoordinator(event);
				// afterwards return, stopping the cycle on FOR
				return;
			}
		}
	}

	/**
	 * Performs all actions necessary when a member is removed from swarm
	 * cluster.<br>
	 * Current node will be a coordinator, setting the status UNKNOWN to removed
	 * node.
	 * 
	 * @param event event of Hazelcast
	 */
	private void actionsForCoordinator(MembershipEvent event) {

		// get member removed
		Member memberRemoved = event.getMember();

		// access to map using Uuid of member removed
		IMap<String, NodeInfo> membersMap = Main.SWARM.getHazelcastInstance().getMap(SwarmQueues.NODES_MAP);
		String key = memberRemoved.getUuid();
		// check is I have on nodes map
		if (membersMap.containsKey(key)) {
			try {
				membersMap.lock(key);
				// remove the node
				membersMap.remove(key);
			} catch (Exception ex) {
				LogAppl.getInstance().emit(SwarmNodeMessage.JEMO018E, ex);
			} finally {
				membersMap.unlock(key);
			}
		} else {
			// if not found, probably this is not correct!!!
			// it means that the list NODES_MAP is not well-maintained
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO017E, memberRemoved.toString(), SwarmQueues.NODES_MAP);
		}
	}
}