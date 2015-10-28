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
package org.pepstock.jem.node;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.Permission;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.Result;
import org.pepstock.jem.commands.SetURLFactory;
import org.pepstock.jem.factories.JclFactoryException;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.events.JobLifecycleEvent;
import org.pepstock.jem.node.persistence.PreJobMapManager;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.RegExpPermission;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.node.security.RolesQueuePredicate;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.security.User;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Transaction;

/**
 * Manages all activities related to JCL checking queue, after job job
 * submitted. It's able to create JCL and JOB to move on input queue.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JclCheckingQueueManager extends Thread implements ShutDownInterface {
	
	private static final long INTERVAL_IF_QUEUE_IS_EMPTY = 2 * TimeUtils.SECOND;
			
	private static final long INTERVAL_IF_NODE_IS_NOT_OPERATIONAL =	15 * TimeUtils.SECOND;	
	
	private static final StringPermission JOB_SUBMIT_PERMISSION = new StringPermission(Permissions.JOBS_SUBMIT);

	private boolean isDown = false;

	/**
	 * Empty constructor
	 */
	public JclCheckingQueueManager() {
		SetURLFactory.install();
	}

	/**
	 * Access to queue and stay in wait for Prejob to check and validate.
	 * 
	 * @see org.pepstock.jem.node.Queues#JCL_CHECKING_QUEUE
	 */
	public void run() {
		// gets HC queue
		IQueue<PreJob> jclCheckingQueue = Main.getHazelcast().getQueue(Queues.JCL_CHECKING_QUEUE);
		try {
			// reads from queue (waiting if necessary) and checks.. forever
			// if shutdown is not in progress
			while (!Main.IS_SHUTTING_DOWN.get()) {
				// checks if the node is really working and not in access MAINT
				if (Main.getNode().isOperational() && !Main.IS_ACCESS_MAINT.get()) {
					
					// creates a transaction to rollback if there is any exception
					// done because it removes from queue and adds the job on input map
					// and an exception must rollback everything, re-putting the job in queue for check
					Transaction tran = Main.getHazelcast().getTransaction();
					tran.begin();
					// gets the job
					PreJob prejob = poll(jclCheckingQueue);

					// checks because when the system is shutting
					// down, hazecast gets a null object
					if (prejob != null) {
						// check JCL
						checkAndLoadJcl(prejob);
						// commit always! Rollback is never necessary
						// Rollback is called automatically when node crashed
						tran.commit();
					} else {
						// commit always! Rollback is never necessary
						// Rollback is called automatically when node crashed
						tran.commit();
						Thread.sleep(INTERVAL_IF_QUEUE_IS_EMPTY);
					}
				} else {
					// sleeps 15 second before checks if there is a new job on queue
					Thread.sleep(INTERVAL_IF_NODE_IS_NOT_OPERATIONAL);
				}
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC068W, StringUtils.substringAfterLast(JclCheckingQueueManager.class.getName(), "."));
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC069I, StringUtils.substringAfterLast(JclCheckingQueueManager.class.getName(), "."));
		// if here, the node is down
		isDown = true;
	}
	
	/**
	 * Polls on Hazelcast queue to get a job. It waits for 10 seconds and if there isn't any
	 * job, it leaves.
	 * @param jclCheckingQueue queue used to move the pre jobs before moving on all other maps.
	 * @return pre job instance
	 */
	private PreJob poll(IQueue<PreJob> jclCheckingQueue){
		PreJob prejob = null;
		try {
			// poll on queue for 10
			// seconds and then leaves
			prejob = jclCheckingQueue.poll(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
		} catch (Exception e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC163E, e);
		}
		return prejob;
	}

	/**
	 * Checks and load job from jcl checking queue to input queue.
	 * 
	 * @see org.pepstock.jem.node.Factory#loadJob(PreJob)
	 * @param prejob prejob instance, previously loaded into jcl checking queue
	 */
	public void checkAndLoadJcl(PreJob prejob) {
		// increments the number of JCL checked
		Main.NUMBER_OF_JCL_CHECK.incrementAndGet();

		// extract JOB from prejob
		Job job = prejob.getJob();
		try {
			// removes from queue
			PreJobMapManager.getInstance().delete(prejob);
			// using the factory, validates, checks and loads JCL into JOB
			Factory.loadJob(prejob);
			// check if user is grant for job submitting
			User user = new User(job.getUser());
			user.setOrgUnitId(job.getOrgUnit());
			// creates Hazelcast predicate to extract all roles and permissions
			// assigned to user
			List<Role> myroles = loadRoles(user);
			// checks job submit permission
			boolean allowedJobSubmit = false;
			// gets GFS tag
			String gfs = Factory.getGfsFromURL(prejob);
			// checks GFS tag permission
			boolean allowedGFSbyURL = gfs == null ? true : false;
			// scans all roles of the job user
			for (Role role : myroles) {
				// administrators can always submit
				if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)) {
					allowedJobSubmit = true;
					allowedGFSbyURL = true;
				} else {
					// scans the permissions of role
					for (String permission : role.getPermissions()) {
						StringPermission perm = new StringPermission(permission);
						// if not yet allowed, implies the permission
						if (!allowedJobSubmit){
							allowedJobSubmit = perm.implies(JOB_SUBMIT_PERMISSION);
						}
						if (!allowedGFSbyURL){
							String permissionGfs = GfsFileType.getPermission(gfs);
							if (permissionGfs != null){
								allowedGFSbyURL = perm.implies(new StringPermission(permissionGfs));
							}
						}
					}
				}
				// if allowed exit without continuing scanning all roles
				if (allowedJobSubmit && allowedGFSbyURL){
					break;
				}
			}
			// if not authorized, EXCEPTION
			if (!allowedGFSbyURL){
				// if not authorized to access to GFS,
				// remove JCl read before
				job.getJcl().setContent(null);
				throw new NodeMessageException(NodeMessage.JEMC144E, job.getUser(), GfsFileType.getPermission(gfs));
			}
			// if not authorized, EXCEPTION
			if (!allowedJobSubmit){
				throw new NodeMessageException(NodeMessage.JEMC144E, job.getUser(), Permissions.JOBS_SUBMIT);
			}

			// checks if job has different users between job and jcl.
			// checks if user is authorized to do it
			if (job.isUserSurrogated()) {
				// checks if the user is surrogated. Permission SURROGATE:pattern of user
				StringPermission surrogatePermission = new StringPermission(Permissions.SURROGATE + Permissions.PERMISSION_SEPARATOR + job.getJcl().getUser());
				
				boolean allowed = false;
				for (Role role : myroles) {
					// administrators can be surrogated
					if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)) {
						allowed = true;
					} else {
						// scans all permissions
						for (String permission : role.getPermissions()) {
							// if has got permission for all surrogates, is allowed
							if (permission.equalsIgnoreCase(Permissions.SURROGATE_ALL)) {
								allowed = true;
							} else if (permission.startsWith(Permissions.SURROGATE) && !allowed) {
								// uses the regex permission to check if has got the permission
								Permission perm = new RegExpPermission(permission);
								allowed = perm.implies(surrogatePermission);
							}
						}
					}
					// if allowed exit without continuing scanning all roles
					if (allowed){
						break;
					}
				}
				// if not authorized, EXCEPTION
				if (!allowed){
					throw new NodeMessageException(NodeMessage.JEMC144E, job.getUser(), surrogatePermission);
				}
			}
			// move job to INPUT queue
			moveToInputQueue(job);
			
		} catch (JclFactoryException e) {
			// if there a factory exception
			// means that is not able to parse completely the JCL
			Jcl jcl = e.getJcl();
			// if there is a JCL, set UNKNOW JCL TYPE
			if (jcl != null){
				if (jcl.getType() == null){
					jcl.setType(Jcl.UNKNOWN);
				}
			} else {
				// doesn't have any JCL so that sets a default jcl (to avoid further
				// Null point exception)
				jcl = Jcl.createUnknownJcl();
				jcl.setContent(prejob.getJclContent());
				jcl.setType(prejob.getJclType());
			}
			// go to exception method
			performException(e, job, jcl);
		} catch (NodeMessageException e) {
			// go to exception method
			performException(e, job, job.getJcl());			
		} catch (Exception e) {
			// creates an UNKNOW JCL 
			// because here is not able to create any JCL
			Jcl jcl = Jcl.createUnknownJcl();
			jcl.setContent(prejob.getJclContent());
			jcl.setType(prejob.getJclType());
			// go to exception method
			performException(e, job, jcl);
		}
	}
	
	/**
	 * Loads all roles assigned to the user of job
	 * @param user user of job 
	 * @return list of roles of user
	 * @throws NodeMessageException is any error occurs during getting the roles
	 */
	private List<Role> loadRoles(User user) throws NodeMessageException{
		// creates Hazelcast predicate to extract all roles and permissions
		// assigned to user
		RolesQueuePredicate predicate = new RolesQueuePredicate();
		predicate.setUser(user);

		// gets HC map for roles
		IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);
		// locks in HC to have a consistent status on roles
		Lock lock = Main.getHazelcast().getLock(Queues.ROLES_MAP_LOCK);
		List<Role> myroles = null;
		boolean isLock = false;
		try {
			// locks the map, if not EXCEPTION!!
			isLock = lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock) {
				// reads the roles of the job user
				myroles = new ArrayList<Role>(roles.values(predicate));
			} else {
				throw new NodeMessageException(NodeMessage.JEMC119E, Queues.ROLES_MAP);
			}
		} catch (Exception e) {
			throw new NodeMessageException(NodeMessage.JEMC119E, e, Queues.ROLES_MAP);
		} finally {
			// always unlock 
			if (isLock){
				lock.unlock();
			}
		}
		return myroles;
	}
	
	/**
	 * Moves the job into input queue
	 */
	private void moveToInputQueue(Job job){
		// move job to INPUT queue
		IMap<String, Job> inputQueue = Main.getHazelcast().getMap(Queues.INPUT_QUEUE);
		try {
			inputQueue.lock(job.getId());
			inputQueue.put(job.getId(), job);
			// fires event that the job is in queue
			Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addJobLifecycleEvent(new JobLifecycleEvent(Queues.INPUT_QUEUE, job));
		} catch (Exception ex){
			LogAppl.getInstance().emit(NodeMessage.JEMC170E, ex, job.getName());		
		} finally {
			// always unlock
			inputQueue.unlock(job.getId());
		}
	}
	
	/**
	 * Manages the exception doing the same thing for all exceptions
	 * @param e Exception to manage
	 * @param job job instance
	 * @param jcl read JCL
	 */
	private void performException(Exception e, Job job, Jcl jcl){
		// creates a writer to save all stack trace
		StringWriter sw = new StringWriter();
		// save the complete stack trace
		e.printStackTrace(new PrintWriter(sw));

		// Factory throws an exception creating the JCL
		// so taht creates result object with Exception and severe code, and
		// set it to the job
		Result result = new Result();
		result.setReturnCode(Result.SEVERE);
		result.setExceptionMessage(sw.getBuffer().toString());
		job.setResult(result);

		// set now as ended time
		job.setEndedTime(new Date());
		// usually the parser can extract the jbname, anyway.
		// if it is, it saves the jobname to job
		if (jcl.getJobName() != null && !Jcl.UNKNOWN.equalsIgnoreCase(jcl.getJobName())){
			job.setName(jcl.getJobName());
		} else {
			if (job.getName() == null){
				job.setName(Jcl.UNKNOWN);
			}
		}
		// sets JCL
		job.setJcl(jcl);
		
		// writes output for error
		try {
			Main.getOutputSystem().writeJcl(job);
			Main.getOutputSystem().writeMessageLog(job, sw.toString());
		} catch (FileNotFoundException e1) {
			LogAppl.getInstance().emit(NodeMessage.JEMC155W, e1, job.getName());
		} catch (IOException e1) {
			LogAppl.getInstance().emit(NodeMessage.JEMC155W, e1, job.getName());
		}
		
		// move job to OUTPUT queue
		IMap<String, Job> outputQueue = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);
		try{
			// put job on OUTPUT map
			outputQueue.lock(job.getId());
			outputQueue.put(job.getId(), job);
		} catch (Exception ex){
			LogAppl.getInstance().emit(NodeMessage.JEMC171E, ex, job.getName());				
		} finally {
			outputQueue.unlock(job.getId());
		}
		// fires event that the job is ended
		Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addJobLifecycleEvent(new JobLifecycleEvent(Queues.OUTPUT_QUEUE, job));

		// notify that the job is ended (on error). This is necessary
		// because the client, which submits jcl is still waiting for a
		// notification
		// client needs notification if "-nowait" parameter is not used
		if (!job.isNowait()){
			ITopic<Job> topic = Main.getHazelcast().getTopic(Queues.ENDED_JOB_TOPIC);
			topic.publish(job);
		}
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.ShutDownInterface#shutdown()
	 */
	@Override
	public void shutdown() throws NodeException, NodeMessageException {
		// if the thread is alive
		if (isAlive()){
			// if JCL queue manager is not down
			while (!isDown){
				try {
					// continues checking every second
					// before leaving this method
					Thread.sleep(TimeUtils.SECOND);
				} catch (InterruptedException e) {
					throw new NodeException(e.getMessage(), e);
				}
			}
		}
	}
}