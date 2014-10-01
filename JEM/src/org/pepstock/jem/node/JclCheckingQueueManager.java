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
import org.pepstock.jem.DefaultJcl;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.Result;
import org.pepstock.jem.factories.JclFactoryException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.events.JobLifecycleEvent;
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

	private boolean isDown = false;

	/**
	 * Empty constructor
	 */
	public JclCheckingQueueManager() {
	}

	/**
	 * Access to queue and stay in wait for Prejob to check and validate.
	 * 
	 * @see org.pepstock.jem.node.Queues#JCL_CHECKING_QUEUE
	 */
	public void run() {

		IQueue<PreJob> jclCheckingQueue = Main.getHazelcast().getQueue(Queues.JCL_CHECKING_QUEUE);
		try {
			// reads from queue (waiting if necessary) and checks.. forever
			while (!Main.IS_SHUTTING_DOWN.get()) {
				if (Main.getNode().isOperational() && !Main.IS_ACCESS_MAINT.get()) {

					Transaction tran = Main.getHazelcast().getTransaction();
					tran.begin();
					PreJob prejob = null;
					try {
						// poll on queue
						prejob = jclCheckingQueue.poll(10L, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC163E);
					}
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
						Thread.sleep(2 * TimeUtils.SECOND);
					}
				} else {
					Thread.sleep(15 * TimeUtils.SECOND);
				}
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC068W, StringUtils.substringAfterLast(JclCheckingQueueManager.class.getName(), "."));
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC069I, StringUtils.substringAfterLast(JclCheckingQueueManager.class.getName(), "."));
		isDown = true;
	}

	/**
	 * Checks and load job from jcl checking queue to input queue.
	 * 
	 * @see org.pepstock.jem.node.Factory#loadJob(PreJob)
	 * @param prejob prejob instance, previously loaded into jcl checking queue
	 */
	public void checkAndLoadJcl(PreJob prejob) {
		Main.NUMBER_OF_JCL_CHECK.incrementAndGet();

		// extract JOB from prejob
		Job job = prejob.getJob();
		try {
			// using the factory, validates, checks and loads JCL into JOB
			Factory.loadJob(prejob);
			
			// check if user is grant for job submitting
			User user = new User(job.getUser());
			user.setOrgUnitId(job.getOrgUnit());
			// creates Hazelcast predicate to extract all roles and permissions
			// assigned to user
			RolesQueuePredicate predicate = new RolesQueuePredicate();
			predicate.setUser(user);

			IMap<String, Role> roles = Main.getHazelcast().getMap(Queues.ROLES_MAP);
			Lock lock = Main.getHazelcast().getLock(Queues.ROLES_MAP_LOCK);
			List<Role> myroles = null;
			boolean isLock = false;
			try {
				isLock = lock.tryLock(10, TimeUnit.SECONDS);
				if (isLock) {
					myroles = new ArrayList<Role>(roles.values(predicate));
				} else {
					throw new NodeMessageException(NodeMessage.JEMC119E, Queues.ROLES_MAP);
				}
			} catch (Exception e) {
				throw new NodeMessageException(NodeMessage.JEMC119E, e, Queues.ROLES_MAP);
			} finally {
				if (isLock){
					lock.unlock();
				}
			}
			// checks job submit permission
			StringPermission jobSubmitPermission = new StringPermission(Permissions.JOBS_SUBMIT);
			boolean allowedJobSubmit = false;
			for (Role role : myroles) {
				if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)) {
					allowedJobSubmit = true;
				} else {
					for (String permission : role.getPermissions()) {
						StringPermission perm = new StringPermission(permission);
						if (!allowedJobSubmit){
							allowedJobSubmit = perm.implies(new StringPermission(Permissions.JOBS_SUBMIT));
						}
					}
				}
				if (allowedJobSubmit){
					break;
				}
			}
			if (!allowedJobSubmit){
				throw new NodeMessageException(NodeMessage.JEMC144E, job.getUser(), jobSubmitPermission);
			}

			// checks if job hs different users between job and jcl.
			// checks if user is authorized to do it
			if (job.isUserSurrogated()) {
				StringPermission surrogatePermission = new StringPermission(Permissions.SURROGATE + Permissions.PERMISSION_SEPARATOR + job.getJcl().getUser());

				boolean allowed = false;
				for (Role role : myroles) {
					if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)) {
						allowed = true;
					} else {
						for (String permission : role.getPermissions()) {
							if (permission.equalsIgnoreCase(Permissions.SURROGATE_ALL)) {
								allowed = true;
							} else if (permission.startsWith(Permissions.SURROGATE) && !allowed) {
								Permission perm = new RegExpPermission(permission);
								allowed = perm.implies(surrogatePermission);
							}
						}
					}
					if (allowed){
						break;
					}
				}
				if (!allowed){
					throw new NodeMessageException(NodeMessage.JEMC144E, job.getUser(), surrogatePermission);
				}
			}

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
				inputQueue.unlock(job.getId());
			}
			
		} catch (JclFactoryException e) {
			Jcl jcl = e.getJcl();
			if (jcl != null){
				if (jcl.getType() == null){
					jcl.setType(DefaultJcl.UNKNOWN);
				}
			} else {
				// doesn't have any JCL so that sets a default jcl (to avoid further
				// Null point exception)
				jcl = new DefaultJcl();
				jcl.setContent(prejob.getJclContent());
				jcl.setType(prejob.getJclType());
			}
			performException(e, job, jcl);
		} catch (NodeMessageException e) {
			performException(e, job, job.getJcl());			
		} catch (Exception e) {
			Jcl jcl = new DefaultJcl();
			jcl.setContent(prejob.getJclContent());
			jcl.setType(prejob.getJclType());
			performException(e, job, jcl);
		}
	}
	
	/**
	 * Manages teh excetpion doing the same thing for all exceptions
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
		if (jcl.getJobName() != null && !DefaultJcl.UNKNOWN.equalsIgnoreCase(jcl.getJobName())){
			job.setName(jcl.getJobName());
		} else {
			if (job.getName() == null){
				job.setName(DefaultJcl.UNKNOWN);
			}
		}
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
		ITopic<Job> topic = Main.getHazelcast().getTopic(Queues.ENDED_JOB_TOPIC);
		topic.publish(job);		
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.ShutDownInterface#shutdown()
	 */
	@Override
	public void shutdown() throws NodeException, NodeMessageException {
		if (isAlive()){
			while (!isDown){
				try {
					Thread.sleep(1 * TimeUtils.SECOND);
				} catch (InterruptedException e) {
					throw new NodeException(e.getMessage(), e);
				}
			}
		}
	}
}
