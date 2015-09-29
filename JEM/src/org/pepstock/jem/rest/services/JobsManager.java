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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.JobStatus;
import org.pepstock.jem.JobSystemActivity;
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
 * @version 2.3
 * 
 */
public class JobsManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient REST client instance
	 */
	public JobsManager(RestClient restClient) {
		super(restClient, JobsManagerPaths.MAIN);
	}

	/**
	 * This is common method to extract jobs from different queues by filter
	 * string
	 * 
	 * @param queue queue name to use to get the right map
	 * @param filter filter string
	 * @return collection of jobs
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<Job> getJobs(JobQueue queue, String filter) throws RestException {
		try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
			RequestBuilder builder = RequestBuilder.media(this);
			// replaces on the path the queue of the jobs to search
			String path = PathReplacer.path(JobsManagerPaths.LIST).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).build();
			// performs the request adding the filter query param
			ClientResponse response = builder.filter(filter).get(path);
			// if HTTP status code is OK,parses the result to list of jobs
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (List<Job>) JsonUtil.getInstance().deserializeList(response, Job.class);
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			// throw an exception of JSON parsing
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}

	/**
	 * Holds a job in INPUT, OUTPUT or ROUTING queue. To set HOLD means that it
	 * can't be executed or removed from queue.
	 * 
	 * @param id job id to hold
	 * @param queue map where the job is
	 * @return true is it holds them, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean hold(String id, JobQueue queue) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.HOLD).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call
		ClientResponse response = builder.put(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Releases jobs in INPUT, OUTPUT or ROUTING queue, which were previously
	 * hold.
	 * 
	 * @param id job id to release
	 * @param queue map where the job is
	 * @return true is it releases the job, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean release(String id, JobQueue queue) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.RELEASE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call
		ClientResponse response = builder.put(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Cancels a job currently running. If force is set to true, JEM uses force
	 * mode to cancel job.
	 * 
	 * @param id id of job to cancel
	 * @param force if true, uses force attribute to cancel job
	 * @return true is it cancels the job, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean cancel(String id, boolean force) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path job id and if force must be used
		String path = PathReplacer.path(JobsManagerPaths.CANCEL).replace(JobsManagerPaths.FORCE_PATH_PARAM, String.valueOf(force)).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call
		ClientResponse response = builder.put(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Purge (removing any output) a job from INPUT, OUTPUT or ROUTING queue.
	 * 
	 * @param id job id to purge
	 * @param queue map where the job is
	 * @return true is it purges the job, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean purge(String id, JobQueue queue) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.PURGE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call
		ClientResponse response = builder.put(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Updates some attributes of a job. Usually is used in input queue to
	 * change environment, domain, affinity, memory or priority.
	 * 
	 * @param id job id to update
	 * @param queue map where the job is
	 * @param updateJob attributes it must changed
	 * @return true is it purges the job, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean update(String id, JobQueue queue, UpdateJob updateJob) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.UPDATE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call, passing the update job attributes in JSON in the
		// HTTP body
		ClientResponse response = builder.put(path, updateJob);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return Boolean.parseBoolean(result);
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Submits a new job by its JCL into JEM.
	 * 
	 * @param preJcl an object which contains the JCL content and the JCL type
	 * @see PreJcl
	 * @return Job id after submission
	 * @throws RestException if any exception occurs
	 */
	public String submit(PreJcl preJcl) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// performs REST call, passing the preJcl object in JSON in the HTTP
		// body
		ClientResponse response = builder.put(JobsManagerPaths.SUBMIT, preJcl);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the job ID
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return result;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Retrieves job JCL from INPUT, RUNNING, OUTPUT or ROUTING queue.
	 * 
	 * @param id job id to get JCL
	 * @param queue map where the job is
	 * @return JCL content
	 * @throws RestException if any exception occurs
	 */
	public String getJcl(String id, JobQueue queue) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.JCL).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the JCL content
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return result;
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			return Jcl.CONTENT_NOT_AVAILABLE;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Returns all folder in tree representation of job output folder.<br>
	 * Retrieves folder structure only if job is RUNNING or OUTPUT queue.
	 * 
	 * @param id job id to get output tree
	 * @param queue map where the job is
	 * @return object with all folder structure
	 * @throws RestException if any exception occurs
	 */
	public OutputTree getOutputTree(String id, JobQueue queue) throws RestException {
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.OUTPUT_TREE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// if HTTP status code is ok, returns the output tree
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(OutputTree.class);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			LogAppl.getInstance().debug(getValue(response, String.class));
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			throw new RestException(response.getStatus(), getValue(response, String.class));
		}
	}

	/**
	 * Returns the content of specific file inside of job output folder.
	 * 
	 * @param id job id to get output
	 * @param queue map where the job is
	 * @param item file descriptor, created by a previous call to getOutputTree
	 * @return object with file content
	 * @throws RestException if any exception occurs
	 */
	public String getOutputFileContent(String id, JobQueue queue, OutputListItem item) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the queue of the jobs to search and job id
		String path = PathReplacer.path(JobsManagerPaths.OUTPUT_FILE).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
		// performs REST call, passing the item in the HTTP body in JSON format
		ClientResponse response = builder.post(path, item);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the output content
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return result;
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			// the job id passed as path parameters hasn't identified any job
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Returns a job status by filter.<br>
	 * Filter must be job name (no pattern with wild-cards) or job id
	 * 
	 * @param filter job name (no pattern with wild-cards) or job id
	 * @return job status
	 * @throws RestException if any exception occurs
	 */
	public JobStatus getJobStatus(String filter) throws RestException {
		try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
			RequestBuilder builder = RequestBuilder.media(this);
			// performs REST call, setting the filter as query parameter
			ClientResponse response = builder.filter(filter).get(JobsManagerPaths.STATUS);
			// if HTTP status code is ok, returns the status object
			// parsing from JSON
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (JobStatus) JsonUtil.getInstance().deserialize(response, JobStatus.class);
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			// throws an exception of JSON parsing
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}

	/**
	 * Returns a job by its job id.
	 * 
	 * @param id job id to search
	 * @param queue map where the job is
	 * @return job, if found, otherwise null
	 * @throws RestException if any exception occurs
	 */
	public Job getJobById(String id, JobQueue queue) throws RestException {
		try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
			RequestBuilder builder = RequestBuilder.media(this);
			// replaces on the path the queue of the jobs to search and job id
			String path = PathReplacer.path(JobsManagerPaths.GET_BY_ID).replace(JobsManagerPaths.QUEUE_PATH_PARAM, queue.getPath()).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
			// performs REST call
			ClientResponse response = builder.get(path);
			// if HTTP status code is ok, returns the job object
			// parsing from JSON
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (Job) JsonUtil.getInstance().deserialize(response, Job.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
				// the job id passed as path parameters hasn't identified any
				// job
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				LogAppl.getInstance().debug(getValue(response, String.class));
				return null;
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			// throws an exception of JSON parsing
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}

	/**
	 * Returns a job by its job id, searching it in OUTPUT and ROUTED maps.<br>
	 * If job is not in output, tries searching it in ROUTED
	 * 
	 * @param id job id to search
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
	 * @param id job id to get system information
	 * @return system activity information
	 * @throws RestException if any exception occurs
	 */
	public JobSystemActivity getJobSystemActivity(String id) throws RestException {
		try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
			RequestBuilder builder = RequestBuilder.media(this);
			// replaces on the path the job id
			String path = PathReplacer.path(JobsManagerPaths.SYSTEM_ACTIVITY).replace(JobsManagerPaths.JOBID_PATH_PARAM, id).build();
			// performs REST call
			ClientResponse response = builder.get(path);
			// if HTTP status code is ok, returns the system activity
			// parsing from JSON
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (JobSystemActivity) JsonUtil.getInstance().deserialize(response, JobSystemActivity.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
				// the job id passed as path parameters hasn't identified any
				// job
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				LogAppl.getInstance().debug(getValue(response, String.class));
				return null;
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			// throws an exception of JSON parsing
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}
}