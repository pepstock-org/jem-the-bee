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
package org.pepstock.jem.gwt.server;

import java.util.Collection;
import java.util.Map;

import org.pepstock.jem.Job;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.gwt.client.commons.JobStatus;
import org.pepstock.jem.gwt.client.services.JobsManagerService;
import org.pepstock.jem.gwt.server.services.JobsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Is GWT server service which can provide all methods to manage all jobs on all
 * queues.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobsManagerServiceImpl extends DefaultManager implements JobsManagerService {

	private static final long serialVersionUID = 1L;

	private transient JobsManager jobsManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getInputQueue
	 * (String jobNameFilter)
	 */
	@Override
	public Collection<Job> getInputQueue(String jobNameFilter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getInputQueue(jobNameFilter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getRunningQueue
	 * (String jobNameFilter)
	 */
	@Override
	public Collection<Job> getRunningQueue(String jobNameFilter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getRunningQueue(jobNameFilter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getOutputQueue
	 * (String jobNameFilter)
	 */
	@Override
	public Collection<Job> getOutputQueue(String jobNameFilter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getOutputQueue(jobNameFilter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getRoutingQueue
	 * (String jobNameFilter)
	 */
	@Override
	public Collection<Job> getRoutingQueue(String jobNameFilter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getRoutingQueue(jobNameFilter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#hold(Collection
	 * <Job> jobs, String queueName)
	 */
	@Override
	public Boolean hold(Collection<Job> jobs, String queueName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.hold(jobs, queueName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#cancel(Collection
	 * <Job> jobs, boolean force)
	 */
	@Override
	public Boolean cancel(Collection<Job> jobs, boolean force) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.cancel(jobs, force);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#release(Collection
	 * <Job> jobs, String queueName)
	 */
	@Override
	public Boolean release(Collection<Job> jobs, String queueName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.release(jobs, queueName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#submit(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public String submit(String content, String type) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			PreJob preJob = new PreJob();
			preJob.setJclContent(content);

			// creates a empty job
			Job job = new Job();
			// loads prejob with job
			preJob.setJob(job);
			// sets JCL type which was an argument
			preJob.setJclType(type);
			String id = jobsManager.submit(preJob);

			return UserInterfaceMessage.JEMG053I.toMessage().getFormattedMessage(id);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#indent(java.lang
	 * .String)
	 */
	@Override
	public String indent(String content) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.indent(content);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#purge(Collection
	 * <Job> jobs, String queueName)
	 */
	@Override
	public Boolean purge(Collection<Job> jobs, String queueName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.purge(jobs, queueName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.JobsManagerService#getJcl(Job
	 * job, String queueName)
	 */
	@Override
	public String getJcl(Job job, String queueName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getJcl(job, queueName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getOutputTree(Job
	 * job, String queueName)
	 */
	@Override
	public OutputTree getOutputTree(Job job, String queueName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getOutputTree(job, queueName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getOutputFileContent
	 * (Job job, OutputListItem item)
	 */
	@Override
	public OutputFileContent getOutputFileContent(Job job, OutputListItem item) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getOutputFileContent(job, item);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.JobsManagerService#update(Job
	 * job, String queueName)
	 */
	@Override
	public Boolean update(Job job, String queueName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.update(job, queueName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getJobStatus(
	 * String filter)
	 */
	@Override
	public JobStatus getJobStatus(String filter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getJobStatus(filter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.JobsManagerService#getJobSystemActivity
	 * (org.pepstock.jem.Job)
	 */
	@Override
	public JobSystemActivity getJobSystemActivity(Job job) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getJobSystemActivity(job);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.JobsManagerService#getJCLTpes()
	 */
	@Override
	public Map<String, String> getJclTypes() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (jobsManager == null) {
			initManager();
		}
		try {
			return jobsManager.getJclTypes();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/**
	 * Initializes a manager
	 * 
	 * @throws JemException
	 *             if any exception occurs
	 */
	private synchronized void initManager() throws JemException {
		if (jobsManager == null) {
			try {
				jobsManager = new JobsManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

}