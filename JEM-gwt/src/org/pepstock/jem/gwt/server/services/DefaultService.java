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
package org.pepstock.jem.gwt.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.Roles;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.query.SqlPredicate;

/**
 * Common services with authorization and authentication services.<br>
 * It has got the Hazelcast instance to access to JEM maps.<br>
 * Furthermore it provides the available node to use when an executor must be
 * called.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DefaultService {

	/**
	 * Constructor
	 */
	public DefaultService() {
	}

	/**
	 * Returns the instance of Hazelcast
	 * 
	 * @return the instance
	 */
	public HazelcastInstance getInstance() {
		return SharedObjects.getHazelcastInstance();
	}

	/**
	 * Checks if the requestor is an administrator
	 * @throws ServiceMessageException 
	 * 
	 * @throws Exception
	 *             if requestor is not authenticated
	 */
	public void checkAdministrator() throws ServiceMessageException {
		// checks user authentication
		// if not, this method throws an exception
		checkAuthentication();

		Subject currentUser = SecurityUtils.getSubject();
		// checks is the user is a member of administrator role
		// if yes, continue
		if (currentUser.hasRole(Roles.ADMINISTRATOR)){
			return;
		}
		// user is not an administrator
		// so throws an exception
		Session shiroSession = currentUser.getSession();
		LoggedUser user = (LoggedUser) shiroSession.getAttribute(LoginManager.USER_KEY);
		// gets userid from session
		String userid = (user != null) ? user.toString() : currentUser.toString();
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG008E, userid, Roles.ADMINISTRATOR);
		throw new ServiceMessageException(UserInterfaceMessage.JEMG008E, userid, Roles.ADMINISTRATOR);
	}

	/**
	 * Checks if the requestor is authenticate
	 * @throws ServiceMessageException 
	 *             if requestor is not authenticated
	 */
	public void checkAuthentication() throws ServiceMessageException {
		checkAuthorization(null);
	}

	/**
	 * Checks if the requestor is authorized to a permission
	 * 
	 * @param permission
	 *            permission to check. if null, checks only if authenticate
	 * @throws ServiceMessageException
	 *             if requestor is not authenticated or authorized
	 */
	public void checkAuthorization(Permission permission) throws ServiceMessageException {
		Subject currentUser = SecurityUtils.getSubject();
		// checks if is authenticate
		if (currentUser.isAuthenticated()) {
			// if a permission is an argument checks permission
			if (permission != null) {
				// checks if user has got permission
				// if yes, returns
				if (currentUser.isPermitted(permission)) {
					return;
				}
				// user is not an authorized
				// so throws an exception
				Session shiroSession = currentUser.getSession();
				LoggedUser user = (LoggedUser) shiroSession.getAttribute(LoginManager.USER_KEY);
				// gets userid from session
				String userid = (user != null) ? user.toString() : currentUser.toString();
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG008E, userid, permission);
				throw new ServiceMessageException(UserInterfaceMessage.JEMG008E, userid, permission);
			}
			return;
		}
		// user is not an authenticated
		// so throws an exception
		Session shiroSession = currentUser.getSession();
		LoggedUser user = (LoggedUser) shiroSession.getAttribute(LoginManager.USER_KEY);
		// gets userid from session
		String userid = (user != null) ? user.toString() : "N/A";
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG009E, userid);
		throw new ServiceMessageException(UserInterfaceMessage.JEMG009E, userid);
	}

	/**
	 * Searches inside of nodes map the available node.<br>
	 * The availabilty of a node is based on its status.
	 * 
	 * @return available node
	 * @throws ServiceMessageException 
	 *             if any exception occurs or if no nodes are available
	 */
	public NodeInfo searchAvailableNodeInfo() throws ServiceMessageException {
		IMap<String, NodeInfo> map = getInstance().getMap(Queues.NODES_MAP);

		// searches DRAINED nodes because they are not doing anything
		NodeInfo node = getNode(map, "status.index = 3");
		if (node != null) {
			return node;
		}

		// searches INACTIVE nodes because they are not doing anything
		node = getNode(map, "status.index = 1");
		if (node != null) {
			return node;
		}

		// searches all other status nodes
		node = getNode(map, "status.index IN (0, 2, 4)");
		if (node != null) {
			return node;
		}
		// node is not found, throws an exception
		throw new ServiceMessageException(UserInterfaceMessage.JEMG023E, "searchAvailableNodeInfo");
	}

	/**
	 * Scans all nodes, filtered by a passed filter.
	 * 
	 * @param map
	 *            nodes map instance
	 * @param predicateString
	 *            query to perform
	 * @return a node info instance or null if there's any available node
	 */
	private NodeInfo getNode(IMap<String, NodeInfo> map, String predicateString) {
		Collection<NodeInfo> nodes = new ArrayList<NodeInfo>();
		// before searches for DRAINED because they're not doing anything
		StringBuilder sb = new StringBuilder();
		// this is predicate SQL string
		sb.append(predicateString);
		// creates a predicate
		SqlPredicate predicate = new SqlPredicate(sb.toString());

		// locks all map to have a consistent collection
		// only for 10 seconds otherwise
		// throws an exception
		boolean isLock = false;
		Lock lock = getInstance().getLock(Queues.NODES_MAP_LOCK);
		try {
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				// gets nodes by predicate
				nodes = map.values(predicate);
			} else {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG022E, Queues.NODES_MAP);
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG022E, e, Queues.NODES_MAP, e.getMessage());
		} finally {
			// unlocks always the map
			if (isLock){
				lock.unlock();
			}
		}
		// if the result is not empty,
		// it returns the first nodeinfo
		if (!nodes.isEmpty()) {
			for (NodeInfo node : nodes) {
				return node;
			}
		}
		// if not found, return null
		return null;
	}

	/**
	 * Returns a Hazelcast member by available NodeInfo.
	 * 
	 * @return member of hazelcast cluster
	 * @throws ServiceMessageException 
	 *             if it's not able to have any member
	 */
	public Member getMember() throws ServiceMessageException  {
		// uses search method to have an available node
		return getMember(searchAvailableNodeInfo());
	}

	/**
	 * Returns a Hazelcast member by a NodeInfo, passed as argument.
	 * 
	 * @param node
	 *            node info to search
	 * @return member of hazelcast cluster
	 * @throws ServiceMessageException 
	 *             if it's not able to have any member
	 */
	public Member getMember(NodeInfo node) throws ServiceMessageException  {
		return getMember(node.getKey());
	}

	/**
	 * Returns a Hazelcast member by a Node Info ID, passed as argument.
	 * 
	 * @param nodeId
	 *            node info id to search
	 * @return member of hazelcast cluster
	 * @throws ServiceMessageException 
	 *             if it's not able to have any member
	 */
	public Member getMember(String nodeId) throws ServiceMessageException  {
		Cluster cluster = getInstance().getCluster();
		Set<Member> set = cluster.getMembers();
		// scans all Hazelcast member 
		// searching for the member of node info
		for (Member member : set) {
			// checks if node info is the same of Hazelcast member
			if (member.getUuid().equalsIgnoreCase(nodeId)) {
				return member;
			}
		}
		// if none Hazelcast member matches with NodeInfo
		// an exception occurs. If occurs, there is a
		// inconsistent data in JEM node maps
		throw new ServiceMessageException(UserInterfaceMessage.JEMG023E, "searchMember");
	}
}