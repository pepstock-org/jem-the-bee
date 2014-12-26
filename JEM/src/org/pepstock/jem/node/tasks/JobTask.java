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
package org.pepstock.jem.node.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.Result;
import org.pepstock.jem.factories.AbstractFactory;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.JobLogManager;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.RolesQueuePredicate;
import org.pepstock.jem.node.security.User;
import org.pepstock.jem.node.tasks.platform.CurrentPlatform;
import org.pepstock.jem.node.tasks.shell.Shell;
import org.pepstock.jem.util.Parser;

import com.hazelcast.core.IMap;

/**
 * Default class to extend if a new JCL must be execute.<br>
 * Has to take care the execution of JCL preparing the right command line<br>
 * Is also a listener of standard output and standard error and writes them on
 * message log file.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class JobTask extends CommandLineTask {

	private static final long serialVersionUID = 1L;

	private Job job = null;

	private boolean isCancelled = false;

	private Result result = new Result();

	private JemFactory factory = null;

	private List<Role> roles = null;

	/**
	 * Constructor with job and factory parameters
	 * 
	 * @param job job instance to execute
	 * @param factory JEM factory which creates this job task
	 */
	public JobTask(Job job, JemFactory factory) {
		this.job = job;
		this.factory = factory;
	}

	/**
	 * Sets job to execute
	 * 
	 * @param job job instance to execute
	 */
	public void setJob(Job job) {
		this.job = job;
	}

	/**
	 * Gets the job to execute
	 * 
	 * @return job instance to execute
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * Returns the factory which created a job task, otherwise null;
	 * 
	 * @return the factory
	 */
	public JemFactory getFactory() {
		return factory;
	}

	/**
	 * Sets the factory which created a job task
	 * 
	 * @param factory the factory to set
	 */
	public void setFactory(JemFactory factory) {
		this.factory = factory;
	}

	/**
	 * Returns the result of job execution
	 * 
	 * @return result instance
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * Sets the result of job execution
	 * 
	 * @param result result instance
	 */
	public void setResult(Result result) {
		this.result = result;
	}

	/**
	 * Returns the roles of the job user
	 * 
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * Sets the roles of the job user
	 * 
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Methods of Callable interface. This don't must be overrided.<br>
	 * Prepares all common actions for all job task.<br>
	 * Writes JCl on jcl file, sets all common environment variables.
	 * 
	 * @return result instance
	 * @throws Exception occurs if there is any error
	 */
	@Override
	public final Result call() throws JemException {
		try {
			// writes JCL
			Main.getOutputSystem().writeJcl(job);

			// sets the current path as starting directory
			setStartDir(Main.getOutputSystem().getCurrentPath().getAbsolutePath());

			// calls abstract method for process configuration
			configure();
		} catch (IOException e) {
			throw new JemException(e.getMessage(), e);
		}

		// gets job user and calculate the roles
		User user;
		if (job.isUserSurrogated()) {
			// if surrogates, use ONLY user in JCL definition
			user = new User(job.getJcl().getUser());
		} else {
			user = new User(job.getUser());
			user.setOrgUnitId(job.getOrgUnit());
		}

		// if classpath is empty, set a default classpath
		if (!getEnv().containsKey("CLASSPATH")) {
			// sets class path variable for the process with the classpath of
			// node
			getEnv().put("CLASSPATH", JavaUtils.getClassPath());
		}

		// sets the roles of the user
		try {
			setRoles(loadRoles(user));
		} catch (NodeMessageException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC103E, user, e);
			throw e;
		}

		// launch process and sets return code on return object
		try {
			int returnCode = launchProcess();
			if ((returnCode != 0) && (returnCode > result.getReturnCode())){
				result.setReturnCode(returnCode);
			}
			return result;
		} catch (Exception e) {
			throw new JemException(e.getMessage(), e);
		}
	}
	
	/**
	 * Loads all roles defined for a specific user.
	 * @param user user to extract its roles
	 * @return a list of roles assigned to the user
	 * @throws NodeMessageException if any error occurs retrievin gthe roles
	 */
	private List<Role> loadRoles(User user) throws NodeMessageException{
		// creates Hazelcast predicate to extract all roles and permissions
		// assigned to user
		RolesQueuePredicate predicate = new RolesQueuePredicate();
		predicate.setUser(user);

		List<Role> myroles = null;
		// gets map and performs predicate!
		IMap<String, Role> rolesMap = Main.getHazelcast().getMap(Queues.ROLES_MAP);
		Lock lock = Main.getHazelcast().getLock(Queues.ROLES_MAP_LOCK);
		boolean isLock = false;
		try {
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				myroles = new ArrayList<Role>(rolesMap.values(predicate));
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
		return myroles;
	}

	/**
	 * All job task implementation MUST implement this method where is possible
	 * to configure the execution of job.
	 * 
	 * @throws IOException occurs if there is any error
	 */
	public abstract void configure() throws IOException;

	/**
	 * Internal method which creates the process, preparing environment
	 * variables, creating directories, setting listener of output and error
	 * log, and wait for end of job execution.
	 * 
	 * @return return code of execution
	 * @throws NodeMessageException 
	 * @throws InterruptedException 
	 * @throws Exception occurs if there is any error
	 */

	private int launchProcess() throws IOException, NodeMessageException, InterruptedException {
		int returnCode = 0;
		Process process = null;
		try {
			String user = job.isUserSurrogated() ? job.getJcl().getUser() : job.getUser();
			AbstractFactory currFactory = (AbstractFactory) getFactory();
			boolean useSudo = currFactory.isUseSudo() && !user.equalsIgnoreCase(Main.getNode().getUser());

			// create a process builder
			ProcessBuilder builder = new ProcessBuilder();
			Shell shell = CurrentPlatform.getInstance().getShell();
			String command = CurrentPlatform.getInstance().getCommand(job, getCommand(), useSudo);
			builder.command(shell.getName(), shell.getParameters(), command);

			// set directory where execute process
			if (getStartDir() != null){
				builder.directory(new File(getStartDir()));
			}

			// load variable environment from a temporary maps that you can use
			// inside of configure method.
			Map<String, String> env = getEnv();
			Map<String, String> map = builder.environment();
			for (Map.Entry<String, String> e : env.entrySet()) {
				map.put(e.getKey(), e.getValue());
			}

			// writes JEM log with headers
			JobLogManager.printHeader(job);

			// start process and save instance
			process = builder.start();
			// wait for end of job execution
			returnCode = process.waitFor();
			// check if cancelled, setting the return code 222
			if (isCancelled){
				returnCode = Result.CANCELED;
			}
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return returnCode;
	}

	/**
	 * Cancels the execution of job, canceling the created process.<br>
	 * Uses the command of operating system to do it and the process id passed
	 * by RMI from executed job.
	 * 
	 * @see org.pepstock.jem.node.rmi.TasksDoorImpl#setJobStarted(String)
	 * @param pid process id to cancel
	 * @param force if true, use force attribute
	 * @return true if it was able to cancel the job in execution otherwise
	 *         false
	 */
	public final boolean cancel(String pid, boolean force) {
		// PID is usually pass in the format [pid]@[hostname] by JMX
		// implementation of JDK.

		String id = StringUtils.substringBefore(getJob().getProcessId(), "@");
		long intId = Parser.parseLong(id, -1L);
		if (intId == -1L){
			return false;
		}

		try {
			String user = job.isUserSurrogated() ? job.getJcl().getUser() : job.getUser();
			AbstractFactory currFactory = (AbstractFactory) getFactory();
			boolean useSudo = currFactory.isUseSudo() && !user.equalsIgnoreCase(Main.getNode().getUser());
			isCancelled = true;
			isCancelled = CurrentPlatform.getInstance().kill(intId, user, force, useSudo);
		} catch (Exception e) {
			isCancelled = false;
			LogAppl.getInstance().emit(NodeMessage.JEMC017E, e);
		}
		return isCancelled;
	}
}
