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
package org.pepstock.jem.node.rmi;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.CancelableTask;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.GenericCallBack;
import org.pepstock.jem.node.executors.jobs.Purge;
import org.pepstock.jem.node.executors.nodes.Drain;
import org.pepstock.jem.node.executors.nodes.Start;
import org.pepstock.jem.node.executors.resources.AddResource;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.node.persistence.EvictionHelper;
import org.pepstock.jem.node.persistence.OutputMapManager;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourcesUtil;
import org.pepstock.jem.node.resources.definition.ResourceDefinition;
import org.pepstock.jem.node.resources.definition.ResourceDefinitionException;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.node.security.keystore.CertificatesUtil;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterFactory;
import org.pepstock.jem.util.filters.FilterParseException;
import org.pepstock.jem.util.filters.predicates.JobPredicate;
import org.pepstock.jem.util.filters.predicates.NodePredicate;
import org.pepstock.jem.util.filters.predicates.ResourcePredicate;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.Transaction;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class InternalUtilitiesImpl extends CommonResourcerImpl implements InternalUtilities {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 * @throws RemoteException never created
	 */
	public InternalUtilitiesImpl() throws RemoteException {
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#drain(java.lang.String, java.lang.String)
	 */
	@Override
	public int drain(String jobId, String nodesPattern) throws RemoteException {
		checkAuthorization(jobId, Permissions.NODES_DRAIN);
		int count = 0;
		try {
			// gets nodes map instance 
			Collection<NodeInfo> nodes = loadNodes(nodesPattern);

			// scans all nodes
			for (NodeInfo node : nodes){
				// if is ACTIVE or INACTIVE than drains it 
				if (node.getStatus().equals(Status.ACTIVE) || node.getStatus().equals(Status.INACTIVE)){
					// gets the cluster to have member object of Hazelcast
					// to execute the future task
					Cluster cluster = Main.getHazelcast().getCluster();
					// gets all members and scans them
					Set<Member> set = cluster.getMembers();
					for (Member member : set){
						String memberKey = member.getUuid();
						// is the same member
						if (node.getKey().equalsIgnoreCase(memberKey)){
							// creates the future task
							DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Drain(), member);
							// gets executor service and executes!
							ExecutorService executorService = Main.getHazelcast().getExecutorService();
							task.setExecutionCallback(new GenericCallBack());
							executorService.execute(task);
							count++;
						} 
					}
				}
			}
		} catch (Exception ex){
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			// it wraps Exception to avoid serialization exception with
			// missing classes on job side
			throw new RemoteException(ex.getMessage());
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#start(java.lang.String, java.lang.String)
	 */
	@Override
	public int start(String jobId, String nodesPattern) throws RemoteException {
		checkAuthorization(jobId, Permissions.NODES_START);
		int count = 0;
		try {
			// gets nodes map instance 
			Collection<NodeInfo> nodes = loadNodes(nodesPattern);

			// scans all nodes
			for (NodeInfo node : nodes){
				// if is DRAINED or DRAINING than starts it 
				if (node.getStatus().equals(Status.DRAINED) || node.getStatus().equals(Status.DRAINING)){
					// gets the cluster to have member object of Hazelcast
					// to execute the future task
					Cluster cluster = Main.getHazelcast().getCluster();
					// gets all members and scans them
					Set<Member> set = cluster.getMembers();
					for (Member member : set){
						String memberKey = member.getUuid();
						// is the same member
						if (node.getKey().equalsIgnoreCase(memberKey)){
							// creates the future task
							DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Start(), member);
							// gets executor service and executes!
							ExecutorService executorService = Main.getHazelcast().getExecutorService();
							task.setExecutionCallback(new GenericCallBack());
							executorService.execute(task);
							count++;
						} 
					}
				}
			}
		} catch (Exception ex){
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			// it wraps Exception to avoid serialization exception with
			// missing classes on job side
			throw new RemoteException(ex.getMessage());
		}
		return count;
	}

	/**
	 * Loads nodes from a filter. It uses the same syntax of UI
	 * @param nodesFilter filter on nodes (see syntax on UI)
	 * @return collection of nodes, matched with filter
	 * @throws RemoteException if any error occurs
	 */
	private Collection<NodeInfo> loadNodes(String nodesFilter) throws RemoteException{
		// creates a filter object
		Filter filter = null;
		try {
			filter = FilterFactory.parse(nodesFilter);
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(e.getMessage());
		}

		NodePredicate predicate = new NodePredicate(filter);
		IMap<String, NodeInfo> nodes = Main.getHazelcast().getMap(Queues.NODES_MAP);

		Collection<NodeInfo> allNodes = null;
		// locks the whole map
		// if is not able to lock it in 10 seconds
		// throws an exception
		boolean isLock = false;
		Lock lock = Main.getHazelcast().getLock(Queues.NODES_MAP_LOCK);
		try {
			isLock=lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock){ 
				allNodes = nodes.values(predicate);
			} else {
				throw new RemoteException(NodeMessage.JEMC119E.toMessage().getFormattedMessage(Queues.NODES_MAP));
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(NodeMessage.JEMC119E.toMessage().getFormattedMessage(Queues.NODES_MAP));
		} finally {
			if (isLock){
				lock.unlock();
			}
		}
		return new ArrayList<NodeInfo>(allNodes);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#grant(java.lang.String, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void grant(String jobId, String[] permissions, String[] rolesToUpdate) throws RemoteException {
		if (rolesToUpdate == null || permissions == null){
			return;
		}

		checkAuthorization(jobId, Permissions.ROLES_UPDATE);

		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);

		/**
		 * Checks
		 */
		List<Role> all = new ArrayList<Role>();
		for (int i=0; i<rolesToUpdate.length; i++){
			if (rolesToUpdate[i].equalsIgnoreCase(Roles.ADMINISTRATOR)){
				throw new RemoteException(NodeMessage.JEMC123E.toMessage().getFormattedMessage(Roles.ADMINISTRATOR));
			}
			if (!roles.containsKey(rolesToUpdate[i])){
				throw new RemoteException(NodeMessage.JEMC124E.toMessage().getFormattedMessage(rolesToUpdate[i]));
			}
			try {
				roles.lock(rolesToUpdate[i]);
				Role role = roles.get(rolesToUpdate[i]);
				all.add(role);
			} catch (Exception ex){
				// ignore it
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
			} finally {
				roles.unlock(rolesToUpdate[i]);
			}
		}

		/**
		 * LOAD Roles checking permissions
		 */
		Transaction txn = Main.getHazelcast().getTransaction();
		txn.begin();
		try {
			for (Role role: all){
				boolean updated = false;
				for (int i=0; i<permissions.length; i++){
					if (!role.getPermissions().contains(permissions[i])){
						role.getPermissions().add(permissions[i]);
						updated = true;
					}
				}
				if (updated){
					CancelableTask task = getCurrentTask(jobId);
					if (task.getJobTask().getJob().isUserSurrogated()){
						role.setUser(task.getJobTask().getJob().getJcl().getUser());	
					} else {
						role.setUser(task.getJobTask().getJob().getUser());
					}
					role.setLastModified(new Date());
					roles.put(role.getName(), role);
				}
			}
			//do other things..
			txn.commit();
		}catch (Exception ex)  {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			txn.rollback();
			throw new RemoteException(ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#revoke(java.lang.String, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void revoke(String jobId, String[] permissions, String[] rolesToUpdate) throws RemoteException {
		if (rolesToUpdate == null || permissions == null){
			return;
		}
		checkAuthorization(jobId, Permissions.ROLES_UPDATE);

		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);

		/**
		 * Checks
		 */
		List<Role> all = new ArrayList<Role>();
		for (int i=0; i<rolesToUpdate.length; i++){
			if (rolesToUpdate[i].equalsIgnoreCase(Roles.ADMINISTRATOR)){
				throw new RemoteException(NodeMessage.JEMC123E.toMessage().getFormattedMessage(Roles.ADMINISTRATOR));
			}
			if (!roles.containsKey(rolesToUpdate[i])){
				throw new RemoteException(NodeMessage.JEMC124E.toMessage().getFormattedMessage(rolesToUpdate[i]));
			}
			try {
				roles.lock(rolesToUpdate[i]);
				Role role = roles.get(rolesToUpdate[i]);
				all.add(role);
			} catch (Exception ex){
				// ignore it
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
			} finally {
				roles.unlock(rolesToUpdate[i]);	
			}
		}

		/**
		 * LOAD Roles checking permissions
		 */
		Transaction txn = Main.getHazelcast().getTransaction();
		txn.begin();
		try {
			for (Role role: all){
				boolean updated = false;
				for (int i=0; i<permissions.length; i++){
					if (role.getPermissions().contains(permissions[i])){
						role.getPermissions().remove(permissions[i]);
						updated = true;
					}
				}
				if (updated){
					CancelableTask task = getCurrentTask(jobId);
					if (task.getJobTask().getJob().isUserSurrogated()){
						role.setUser(task.getJobTask().getJob().getJcl().getUser());	
					} else {
						role.setUser(task.getJobTask().getJob().getUser());
					}
					role.setLastModified(new Date());
					roles.put(role.getName(), role);
				}
			}
			//do other things..
			txn.commit();
		}catch (Exception ex)  {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			txn.rollback();
			throw new RemoteException(ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#add(java.lang.String, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void add(String jobId, String[] users, String[] rolesToUpdate) throws RemoteException {
		if (rolesToUpdate == null || users == null){
			return;
		}
		checkAuthorization(jobId, Permissions.ROLES_UPDATE);

		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);

		/**
		 * Checks
		 */
		List<Role> all = new ArrayList<Role>();
		for (int i=0; i<rolesToUpdate.length; i++){
			if (!roles.containsKey(rolesToUpdate[i])){
				throw new RemoteException(NodeMessage.JEMC124E.toMessage().getFormattedMessage(rolesToUpdate[i]));
			}
			try {
				roles.lock(rolesToUpdate[i]);
				Role role = roles.get(rolesToUpdate[i]);
				all.add(role);
			} catch (Exception ex){
				// ignore it
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
			} finally {
				roles.unlock(rolesToUpdate[i]);
			}
		}

		Transaction txn = Main.getHazelcast().getTransaction();
		txn.begin();
		try {
			/**
			 * LOAD Roles checking permissions
			 */
			for (Role role: all){
				boolean updated = false;
				for (int i=0; i<users.length; i++){
					if (!role.getUsers().contains(users[i])){
						role.getUsers().add(users[i]);
						updated = true;
					}
				}
				if (updated){
					CancelableTask task = getCurrentTask(jobId);
					if (task.getJobTask().getJob().isUserSurrogated()){
						role.setUser(task.getJobTask().getJob().getJcl().getUser());	
					} else {
						role.setUser(task.getJobTask().getJob().getUser());
					}
					role.setLastModified(new Date());
					roles.put(role.getName(), role);
				}
			}
			//do other things..
			txn.commit();
		}catch (Exception ex)  {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			txn.rollback();
			throw new RemoteException(ex.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#delete(java.lang.String, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void delete(String jobId, String[] users, String[] rolesToUpdate) throws RemoteException {
		if (rolesToUpdate == null || users == null){
			return;
		}

		checkAuthorization(jobId, Permissions.ROLES_UPDATE);

		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);

		/**
		 * Checks
		 */
		List<Role> all = new ArrayList<Role>();
		for (int i=0; i<rolesToUpdate.length; i++){
			if (!roles.containsKey(rolesToUpdate[i])){
				throw new RemoteException(NodeMessage.JEMC124E.toMessage().getFormattedMessage(rolesToUpdate[i]));
			}
			try {
				roles.lock(rolesToUpdate[i]);
				Role role = roles.get(rolesToUpdate[i]);
				all.add(role);
			} catch (Exception ex){
				// ignore it
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
			} finally {
				roles.unlock(rolesToUpdate[i]);
			}
		}

		/**
		 * LOAD Roles checking permissions
		 */
		Transaction txn = Main.getHazelcast().getTransaction();
		txn.begin();
		try {
			for (Role role: all){
				boolean updated = false;
				for (int i=0; i<users.length; i++){
					if (role.getUsers().contains(users[i])){
						role.getUsers().remove(users[i]);
						updated = true;
					}
				}
				if (updated){
					CancelableTask task = getCurrentTask(jobId);
					if (task.getJobTask().getJob().isUserSurrogated()){
						role.setUser(task.getJobTask().getJob().getJcl().getUser());	
					} else {
						role.setUser(task.getJobTask().getJob().getUser());
					}
					role.setLastModified(new Date());
					roles.put(role.getName(), role);
				}
			}
			//do other things..
			txn.commit();
		}catch (Exception ex)  {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			txn.rollback();
			throw new RemoteException(ex.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#remove(java.lang.String, java.lang.String[])
	 */
	@Override
	public void remove(String jobId, String[] oldRoles) throws RemoteException {
		if (oldRoles == null){
			return;
		}
		
		checkAuthorization(jobId, Permissions.ROLES_DELETE);

		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);

		/**
		 * Checks 
		 */
		for (int i=0; i<oldRoles.length; i++){
			if (!roles.containsKey(oldRoles[i])){
				throw new RemoteException(NodeMessage.JEMC124E.toMessage().getFormattedMessage(oldRoles[i]));
			}
			Role role = null;
			try {
				roles.lock(oldRoles[i]);
				role = roles.get(oldRoles[i]);
				if (!role.isRemovable()){
					throw new RemoteException(NodeMessage.JEMC125E.toMessage().getFormattedMessage(oldRoles[i]));
				}
			} catch (Exception ex){
				// ignore it
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
			} finally {
				roles.unlock(oldRoles[i]);
			}
		}

		/**
		 * Checks 
		 */
		Transaction txn = Main.getHazelcast().getTransaction();
		txn.begin();
		try {
			for (int i=0; i<oldRoles.length; i++){
				roles.remove(oldRoles[i]);
			}
			//do other things..
			txn.commit();
		}catch (Exception ex)  {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			txn.rollback();
			throw new RemoteException(ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#create(java.lang.String, java.lang.String[])
	 */
	@Override
	public void create(String jobId, String[] newRoles) throws RemoteException {
		if (newRoles == null){
			return;
		}
		
		checkAuthorization(jobId, Permissions.ROLES_CREATE);
		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);

		/**
		 * Checks
		 */
		List<Role> all = new ArrayList<Role>();
		for (int i=0; i<newRoles.length; i++){
			if (roles.containsKey(newRoles[i])){
				throw new RemoteException(NodeMessage.JEMC126E.toMessage().getFormattedMessage(newRoles[i]));
			}
			Role role = new Role();
			role.setName(newRoles[i]);
			all.add(role);
		}

		/**
		 * LOAD
		 */
		Transaction txn = Main.getHazelcast().getTransaction();
		txn.begin();
		try {
			for (Role role: all){
				CancelableTask task = getCurrentTask(jobId);
				if (task.getJobTask().getJob().isUserSurrogated()){
					role.setUser(task.getJobTask().getJob().getJcl().getUser());	
				} else {
					role.setUser(task.getJobTask().getJob().getUser());
				}
				role.setLastModified(new Date());
				roles.put(role.getName(), role);
			}
			//do other things..
			txn.commit();
		}catch (Exception ex)  {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			txn.rollback();
			throw new RemoteException(ex.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#getJobs(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Job> getJobs(String jobId, String searchString) throws RemoteException {
		try {
			Filter filter = FilterFactory.parse(searchString);
			if (EvictionHelper.isEvicted(Queues.OUTPUT_QUEUE)){
				return OutputMapManager.getInstance().loadAll(filter);
			} else {
				IMap<String, Job> jobs = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);
				JobPredicate predicate = new JobPredicate(filter);
				return new ArrayList<Job>(jobs.values(predicate));
			}
		} catch (FilterParseException e) {
			throw new RemoteException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.InternalUtilities#purge(java.lang.String, Job)
	 */
	@Override
	public void purge(String jobId, Job job) throws RemoteException {
		checkAuthorization(jobId, Permissions.JOBS_PURGE);

		IMap<String, Job> jobs = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);
		try{
			jobs.lock(job.getId());
			if (jobs.containsKey(job.getId())) {
				jobs.remove(job.getId());
			}
		} catch (Exception ex){	
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			throw new RemoteException(ex.getMessage());
		} finally {
			jobs.unlock(job.getId());
		}

		Cluster cluster = Main.getHazelcast().getCluster();
		// creates the future task
		DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Purge(job), cluster.getLocalMember());
		// gets executor service and executes!
		ExecutorService executorService = Main.getHazelcast().getExecutorService();
		task.setExecutionCallback(new GenericCallBack());
		executorService.execute(task);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.CommonResourcer#put(java.lang.String, org.pepstock.jem.node.resources.Resource)
	 */
	@Override
	public boolean put(String jobId, Resource resource) throws RemoteException {
		IMap<String, Resource> map = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		CancelableTask task = getCurrentTask(jobId);
		if (task.getJobTask().getJob().isUserSurrogated()){
			resource.setUser(task.getJobTask().getJob().getJcl().getUser());	
		} else {
			resource.setUser(task.getJobTask().getJob().getUser());
		}
		resource.setLastModified(new Date());
		
		try {
	        // get resource definition
	        ResourceDefinition definition = Main.RESOURCE_DEFINITION_MANAGER.getResourceDefinition(resource.getType());
	        // adds additional properties
	        definition.validateResource(resource);
        } catch (ResourceDefinitionException e) {
        	LogAppl.getInstance().ignore(e.getMessage(), e);
        	throw new RemoteException(e.getMessage(), e);	
        }


		if (map.containsKey(resource.getName())){
			checkAuthorization(jobId, Permissions.RESOURCES_UPDATE);
			try {
				map.lock(resource.getName());
				map.replace(resource.getName(), resource);
			} catch (Exception ex) {
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
				throw new RemoteException(ex.getMessage(), ex);
			} finally {
				map.unlock(resource.getName());
			}
			return false;
		} else {
			checkAuthorization(jobId, Permissions.RESOURCES_CREATE);
			try {
				DistributedTask<Boolean> addTask = new DistributedTask<Boolean>(new AddResource(resource), Main.getHazelcast().getCluster().getLocalMember());
				// gets executor service and executes!
				ExecutorService executorService = Main.getHazelcast().getExecutorService();
				executorService.execute(addTask);
				return addTask.get();
			} catch (Exception ex) {
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
				throw new RemoteException(ex.getMessage(), ex);				
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.CommonResourcer#remove(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean remove(String jobId, String resourceName) throws RemoteException {
		checkAuthorization(jobId, Permissions.RESOURCES_DELETE);
		IMap<String, Resource> map = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		if (map.containsKey(resourceName)){
			Resource resource = null;
			try {
				map.lock(resourceName);
				resource = map.remove(resourceName);
			} catch (Exception ex) {
				LogAppl.getInstance().ignore(ex.getMessage(), ex);
				throw new RemoteException(ex.getMessage(), ex);				
			} finally {
				map.unlock(resourceName);
			}
			return resource != null;
		}
		return false;		
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.CommonResourcer#values()
	 */
	@Override
	public Collection<Resource> values(String jobId, String filter) throws RemoteException {
		checkAuthorization(jobId, Permissions.RESOURCES_READ);

		// creates a Resource predicate 
		// using filter filled on UI 
		ResourcePredicate predicate;
		try {
			 predicate = new ResourcePredicate(FilterFactory.parse(filter));
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(e.getMessage());
		}

		IMap<String, Resource> map = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		Collection<Resource> result = null;		
		boolean isLock=false;
		Lock lock = Main.getHazelcast().getLock(Queues.COMMON_RESOURCES_MAP_LOCK);
		try {
			isLock=lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock){ 
					result = map.values(predicate);
			} else {
				throw new RemoteException(NodeMessage.JEMC119E.toMessage().getFormattedMessage(Queues.COMMON_RESOURCES_MAP));
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(NodeMessage.JEMC119E.toMessage().getFormattedMessage(Queues.COMMON_RESOURCES_MAP));
		} finally {
			if(isLock){
				lock.unlock();
			}
		}
		return new ArrayList<Resource>(result);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.CommonResourcer#getSecret(org.pepstock.jem.node.resources.CryptedValueAndHash)
	 */
	@Override
	public String decrypt(String jobId, CryptedValueAndHash value) throws RemoteException {
		checkAuthorization(jobId, Permissions.RESOURCES_CREATE);
		try {
			return ResourcesUtil.getInstance().decrypt(value);
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.CommonResourcer#encrypt(java.lang.String)
	 */
	@Override
	public CryptedValueAndHash encrypt(String jobId, String value) throws RemoteException {
		checkAuthorization(jobId, Permissions.RESOURCES_CREATE);
		try {
			return ResourcesUtil.getInstance().encrypt(value);
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(e.getMessage(), e);
		}
	}

	@Override
	public void importCertificate(String jobId, byte[] certificate, String alias)
			throws RemoteException {
		checkAuthorization(jobId, Permissions.ADMINISTRATION_SECURITY_CERTIFICATE);
		try {
			CertificatesUtil.addCertificate(certificate, alias);
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(e.getMessage(), e);
		}		
	}

	@Override
	public void deleteCertificate(String jobId, String alias) throws RemoteException {
		checkAuthorization(jobId, Permissions.ADMINISTRATION_SECURITY_CERTIFICATE);
		try {
			CertificatesUtil.removeCertificate(alias);
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new RemoteException(e.getMessage(), e);
		}				
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.rmi.InternalUtilities#getStatisticsFolder()
	 */
    @Override
    public File getStatisticsFolder() throws RemoteException {
	    if (Main.getStatisticsManager().isEnable()){
	    	return Main.getStatisticsManager().getFolderStatsLog();
	    }
	    return null;
    }
}