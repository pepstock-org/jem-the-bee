/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;
import java.util.Map;

import org.pepstock.jem.Job;
import org.pepstock.jem.JobStatus;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.log.JemException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. The service provides all methods to
 * load the jobs info to display, from different queues and all actions to
 * perform on jobs.
 * 
 */
@RemoteServiceRelativePath(Services.QUEUES)
public interface JobsManagerService extends RemoteService {
	/**
	 * Returns all jobs in input queue, using a job filter name.
	 * 
	 * @param jobNameFilter
	 *            filter name
	 * @return a collection of jobs in input
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Collection<Job> getInputQueue(String jobNameFilter) throws JemException;

	/**
	 * Returns all jobs in running queue, using a job filter name.
	 * 
	 * @param jobNameFilter
	 *            filter name
	 * @return a collection of jobs in running
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Collection<Job> getRunningQueue(String jobNameFilter) throws JemException;

	/**
	 * Returns all jobs in output queue, using a job filter name.
	 * 
	 * @param jobNameFilter
	 *            filter name
	 * @return a collection of jobs in output
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Collection<Job> getOutputQueue(String jobNameFilter) throws JemException;

	/**
	 * Returns all jobs in routing queue, using a job filter name.
	 * 
	 * @param jobNameFilter
	 *            filter name
	 * @return a collection of jobs in routing
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Collection<Job> getRoutingQueue(String jobNameFilter) throws JemException;

	/**
	 * Puts jobs in hold
	 * 
	 * @param jobs
	 *            list of jobs
	 * @param queueName
	 *            queue name where the jobs are
	 * @return always true
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Boolean hold(Collection<Job> jobs, String queueName) throws JemException;

	/**
	 * Cancels the current execution of job.
	 * 
	 * @param jobs
	 *            list of jobs
	 * @param force
	 *            if force cancel is set
	 * @return always true
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Boolean cancel(Collection<Job> jobs, boolean force) throws JemException;

	/**
	 * Release jobs previously put in HOLD
	 * 
	 * @param jobs
	 *            list of jobs
	 * @param queueName
	 *            queue name where the jobs are
	 * @return always true
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Boolean release(Collection<Job> jobs, String queueName) throws JemException;

	/**
	 * Submit a new job from inspector of a job
	 * @param jobName Job name
	 * 
	 * @param content
	 *            jcl content
	 * @param type
	 *            JCL type
	 * @return job id
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	String submit(String jobName, String content, String type) throws JemException;

	/**
	 * Indent JCL during editing
	 * 
	 * @param content
	 *            jcl content
	 * @return JCL indented
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	String indent(String content) throws JemException;

	/**
	 * Remove jobs from queue
	 * 
	 * @param jobs
	 *            list of jobs
	 * @param queueName
	 *            queue name where the jobs are
	 * @return always true
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Boolean purge(Collection<Job> jobs, String queueName) throws JemException;

	/**
	 * Some attributes are changed by UI and calls this method to store the new
	 * job.
	 * 
	 * @param job
	 *            job instance related to JCL
	 * @param queueName
	 *            queue name where the job is
	 * @return always true
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Boolean update(Job job, String queueName) throws JemException;

	/**
	 * Returns the JCL content
	 * 
	 * @param job
	 *            job instance related to JCL
	 * @param queueName
	 *            queue name where the job is
	 * @return jcl content
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	String getJcl(Job job, String queueName) throws JemException;

	/**
	 * Returns the tree of the folder where is stored the ouput job
	 * 
	 * @param job
	 *            job instance
	 * @param queueName
	 *            queue name where the job is
	 * @return output job content folder
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	OutputTree getOutputTree(Job job, String queueName) throws JemException;

	/**
	 * Returns the file content
	 * 
	 * @param job
	 *            job instance
	 * @param item
	 *            file representation of output
	 * @return file content
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	String getOutputFileContent(Job job, OutputListItem item) throws JemException;

	/**
	 * Returns a status with a set of collections
	 * 
	 * @param filter
	 *            job name filter or job id
	 * @return JobStatus
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	JobStatus getJobStatus(String filter) throws JemException;

	/**
	 * Returns CPU and Memory utilized by job during execution
	 * 
	 * @param job
	 *            job instance
	 * @return system activity
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	JobSystemActivity getJobSystemActivity(Job job) throws JemException;

	/**
	 * Returns the list of JCL type loaded on JEM and their descriptions
	 * 
	 * @return map with all types
	 * @throws JemException
	 *             if cluster is not available or not authorized
	 */
	Map<String, String> getJclTypes() throws JemException;

}