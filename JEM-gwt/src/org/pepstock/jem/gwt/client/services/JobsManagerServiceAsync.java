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
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async service.
 * 
 * @see JobsManagerService
 */
public interface JobsManagerServiceAsync {
	/**
	 * @see JobsManagerService#getInputQueue(String)
	 * @param jobNameFilter
	 * @param callback
	 */
	void getInputQueue(String jobNameFilter, AsyncCallback<Collection<Job>> callback);

	/**
	 * @see JobsManagerService#getRunningQueue(String)
	 * @param jobNameFilter
	 * @param callback
	 */
	void getRunningQueue(String jobNameFilter, AsyncCallback<Collection<Job>> callback);

	/**
	 * @see JobsManagerService#getOutputQueue(String)
	 * @param jobNameFilter
	 * @param callback
	 */
	void getOutputQueue(String jobNameFilter, AsyncCallback<Collection<Job>> callback);

	/**
	 * @see JobsManagerService#getRoutingQueue(String)
	 * @param jobNameFilter
	 * @param callback
	 */
	void getRoutingQueue(String jobNameFilter, AsyncCallback<Collection<Job>> callback);

	/**
	 * @see JobsManagerService#hold(Collection, String)
	 * @param jobs
	 * @param queueName
	 * @param callback
	 */
	void hold(Collection<Job> jobs, String queueName, AsyncCallback<Boolean> callback);

	/**
	 * @see JobsManagerService#cancel(Collection)
	 * @param jobs
	 * @param force
	 * @param callback
	 */
	void cancel(Collection<Job> jobs, boolean force, AsyncCallback<Boolean> callback);

	/**
	 * @see JobsManagerService#release(Collection, String)
	 * @param jobs
	 * @param queueName
	 * @param callback
	 */
	void release(Collection<Job> jobs, String queueName, AsyncCallback<Boolean> callback);

	/**
	 * @see JobsManagerService#purge(Collection, String)
	 * @param jobs
	 * @param queueName
	 * @param callback
	 */
	void purge(Collection<Job> jobs, String queueName, AsyncCallback<Boolean> callback);

	/**
	 * @see JobsManagerService#getJcl(Job, String)
	 * @param job
	 * @param queueName
	 * @param callback
	 */
	void getJcl(Job job, String queueName, AsyncCallback<String> callback);

	/**
	 * @see JobsManagerService#getOutputTree(Job, String)
	 * @param job
	 * @param queueName
	 * @param callback
	 */
	void getOutputTree(Job job, String queueName, AsyncCallback<OutputTree> callback);

	/**
	 * @see JobsManagerService#getOutputFileContent(Job, OutputListItem)
	 * @param job
	 * @param item
	 * @param callback
	 */
	void getOutputFileContent(Job job, OutputListItem item, AsyncCallback<OutputFileContent> callback);

	/**
	 * Some attributes are changed by UI and calls this method to store the new
	 * job.
	 * 
	 * @param job
	 *            job instance related to JCL
	 * @param queueName
	 *            queue name where the job is
	 * @param callback
	 */
	void update(Job job, String queueName, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @param filter
	 * @param callback
	 */
	void getJobStatus(String filter, AsyncCallback<JobStatus> callback);

	/**
	 * 
	 * 
	 * @param job
	 * @param callback
	 */
	void getJobSystemActivity(Job job, AsyncCallback<JobSystemActivity> callback);

	/**
	 * 
	 * @param jobName
	 * @param content
	 * @param type
	 * @param callback
	 */
	void submit(String jobName, String content, String type, AsyncCallback<String> callback);

	/**
	 * 
	 * @param content
	 * @param callback
	 */
	void indent(String content, AsyncCallback<String> callback);

	/**
	 * Returns the list of JCL type loaded on JEM and their descriptions
	 * 
	 * @param callback
	 */
	void getJclTypes(AsyncCallback<Map<String, String>> callback);
}