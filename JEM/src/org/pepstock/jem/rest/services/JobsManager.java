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
package org.pepstock.jem.rest.services;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.JobStatus;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.PreJcl;
import org.pepstock.jem.UpdateJob;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.JobQueue;
import org.pepstock.jem.rest.paths.JobsManagerPaths;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST Client side of JOBS service.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobsManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient
	 *            REST client instance
	 */
	public JobsManager(RestClient restClient) {
		super(restClient, JobsManagerPaths.MAIN);
	}
	
	/**
	 * This is common method to extract jobs from different queues by filter
	 * string
	 * 
	 * @param queue
	 *            queue name to use to get the right map
	 * @param filter
	 *            filter string
	 * @return collection of jobs
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<Job> getJobs(JobQueue queue, String filter) throws RestException {
		String path = PathReplacer.path(JobsManagerPaths.LIST).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).build();
	    try {
			// creates the returned object
			ClientResponse response = get(path, filter);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<Job>)JsonUtil.getInstance().deserializeList(response, Job.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Holds jobs in INPUT, OUTPUT or ROUTING queue. To set HOLD means that it
	 * can't be executed or removed from queue.
	 * 
	 * @param id
	 *            collections of jobs to hold
	 * @param queue
	 *            map where jobs are
	 * @return true is it holds them, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean hold(String id, JobQueue queue) throws RestException {
    	String path = PathReplacer.path(JobsManagerPaths.HOLD).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		return putAndGetBoolean(path);
	}

	/**
	 * Releases jobs in INPUT, OUTPUT or ROUTING queue, which were previously
	 * hold.
	 * 
	 * @param queue
	 *            collections of jobs to hold
	 * @param queueName
	 *            map where jobs are
	 * @return true is it holds them, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean release(String id, JobQueue queue) throws RestException {
		String path = PathReplacer.path(JobsManagerPaths.RELEASE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
				replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		return putAndGetBoolean(path);
	}

	/**
	 * Cancel a set of jobs currently in running. If force is set to true, JEM
	 * uses force mode to cancel jobs.
	 * 
	 * @param id
	 *            id of job to cancel
	 * @param force
	 *            if true, uses force attribute to cancel jobs
	 * @return always true!
	 * @throws RestException if any exception occurs
	 */
	public Boolean cancel(String id, boolean force) throws RestException {
    	String path = PathReplacer.path(JobsManagerPaths.CANCEL).replace(JobsManagerPaths.FORCE_PATH_PARAM, String.valueOf(force)).
    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		return putAndGetBoolean(path);
	}	

	/**
	 * Purge (removing any output) jobs from INPUT, OUTPUT or ROUTING queue.
	 * 
	 * @param id
	 *            collections of jobs to purge
	 * @param queue
	 *            map where jobs are
	 * @return true is it holds them, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean purge(String id, JobQueue queue) throws RestException {
		String path = PathReplacer.path(JobsManagerPaths.PURGE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		return putAndGetBoolean(path);
	}
	
	/**
	 * Updates some attributes of job. Usually is used in input queue to change
	 * environment, domain, affinity, memory or priority.
	 * 
	 * @param job
	 *            job to update
	 * @param queueName
	 *            map where job is
	 * @return true if it updated, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean update(String id, JobQueue queue, UpdateJob updateJob) throws RestException {
		String path = PathReplacer.path(JobsManagerPaths.UPDATE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		return putAndGetBoolean(path, updateJob);
	}	

	/**
	 * Submits a new job (passed as argument) in JEM.
	 * 
	 * @param preJob
	 *            job to submit
	 * @return Job id after submission
	 * @throws RestException if any exception occurs
	 */
	public String submit(PreJcl preJcl) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response =  put(JobsManagerPaths.SUBMIT, preJcl);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(String.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Retrieves job JCL from INPUT, RUNNING, OUTPUT or ROUTING queue.
	 * 
	 * @param job
	 *            job used to extract JCl
	 * @param queueName
	 *            map where job is
	 * @return JCL content
	 * @throws RestException if any exception occurs
	 */
	public String getJcl(String id, JobQueue queue) throws RestException {
	    try {
	    	String path = PathReplacer.path(JobsManagerPaths.JCL_CONTENT).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
	    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
	    	// creates the returned object
			ClientResponse response =  get(path);
			
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(String.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return Jcl.CONTENT_NOT_AVAILABLE;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Returns all folder in tree representation of job output folder.<br>
	 * Retrieves folder structure only if job is RUNNING or OUTPUT queue.
	 * 
	 * @param job
	 *            job used to extract folder structure. Needs to have output
	 *            folder
	 * @param queueName
	 *            map where job is
	 * @return object with all folder structure
	 * @throws RestException if any exception occurs
	 */
	public OutputTree getOutputTree(String id, JobQueue queue) throws RestException {
	    try {
	    	String path = PathReplacer.path(JobsManagerPaths.OUTPUT_TREE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
	    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
	    	// creates the returned object
			ClientResponse response =  get(path);
			
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(OutputTree.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Returns the content of specific file inside of job output folder.
	 * 
	 * @param job
	 *            job used to extract file. Needs to have output folder
	 * @param item
	 *            file descriptor, created by a previous call to getOutputTree
	 * @return object with file content
	 * @throws RestException if any exception occurs
	 */
	public OutputFileContent getOutputFileContent(String id, JobQueue queue, OutputListItem item) throws RestException {
	    try {
	    	String path = PathReplacer.path(JobsManagerPaths.OUTPUT_FILE_CONTENT).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
	    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
	    	// creates the returned object
			ClientResponse response =  post(path, item);
			
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(OutputFileContent.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Returns a job status by filter.<br>
	 * Filter must be job name (no pattern with wild-cards) or job id
	 * 
	 * @param filter
	 *            job name (no pattern with wild-cards) or job id
	 * @return job status
	 * @throws RestException if any exception occurs
	 */
	public JobStatus getJobStatus(String filter) throws RestException {
	    try {
	    	// creates the returned object
			ClientResponse response =  get(JobsManagerPaths.JOB_STATUS, filter);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (JobStatus)JsonUtil.getInstance().deserialize(response, JobStatus.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Returns a job by its job id.
	 * 
	 * @param queueName
	 *            map name
	 * @param jobId
	 *            job id to search
	 * @return job, if found, otherwise null
	 * @throws RestException if any exception occurs
	 */
	public Job getJobById(String id, JobQueue queue) throws RestException {
	    try {
	    	String path = PathReplacer.path(JobsManagerPaths.JOB_BY_ID).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).
	    			replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
	    	// creates the returned object
			ClientResponse response =  get(path);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (Job)JsonUtil.getInstance().deserialize(response, Job.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Returns a job by its job id, searching it in OUTPUT and ROUTED maps.<br>
	 * If job is not in output, tries searching it in ROUTED
	 * 
	 * @param jobId
	 *            job id
	 * @return job instance
	 * @throws RestException if any exception occurs
	 */
	public Job getEndedJobById(String id) throws RestException {
		return getJobById(id, JobQueue.OUTPUT);
	}
	
	/**
	 * Returns system information about resource consumption of job in running
	 * phase.
	 * 
	 * @param job
	 *            job to use to gather system information
	 * @return system activity information
	 * @throws RestException if any exception occurs
	 */
	public JobSystemActivity getJobSystemActivity(String id) throws RestException {
	    try {
	    	String path = PathReplacer.path(JobsManagerPaths.JOB_SYSTEM_ACTIVITY).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
	    	// creates the returned object
			ClientResponse response =  get(path);
			
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (JobSystemActivity)JsonUtil.getInstance().deserialize(response, JobSystemActivity.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

}