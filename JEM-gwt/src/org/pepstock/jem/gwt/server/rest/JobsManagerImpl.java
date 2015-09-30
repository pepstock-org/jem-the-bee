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
package org.pepstock.jem.gwt.server.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.JobStatus;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.UpdateJob;
import org.pepstock.jem.commands.JemURLStreamHandlerFactory;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.JobsManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.rest.entities.JobQueue;
import org.pepstock.jem.rest.paths.CommonPaths;
import org.pepstock.jem.rest.paths.JobsManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Rest service to manage jobs.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
@Singleton
@Path(JobsManagerPaths.MAIN)
public class JobsManagerImpl extends DefaultServerResource {

	private JobsManager jobsManager = null;

	/**
	 * REST service which returns jobs in passed queue, by job filters
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param jobNameFilter
	 *            job name filter (default *)
	 * @return a list of jobs
	 */
	@GET
	@Path(JobsManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobs(@PathParam(JobsManagerPaths.QUEUE) String queue, @DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String jobNameFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// gets the job queue by name
				JobQueue jQueue = getJobQueue(queue);
				// if queue is null, bad request otherwise it performs the query
				return (jQueue == null) ? ResponseBuilder.JSON.badRequest(JobsManagerPaths.QUEUE) : ResponseBuilder.JSON.ok(jobsManager.getJobsByQueue(jQueue.getName(), jobNameFilter));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which returns jobs status, by job name filter
	 * 
	 * @param jobNameFilter
	 *            job name filter (default *)
	 * @return a job status object
	 * @see JobStatus
	 */
	@GET
	@Path(JobsManagerPaths.STATUS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobStatus(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String jobNameFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns the job status
				return ResponseBuilder.JSON.ok(jobsManager.getJobStatus(jobNameFilter));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which returns job, by job id filter
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @return the job instance
	 */
	@GET
	@Path(JobsManagerPaths.GET_BY_ID)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobById(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// checks if job id is missing
				if (id == null) {
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks the queue if correct
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.QUEUE);
				}
				// searches the job by ID
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if we have the job instance, ok!
				if (job != null) {
					return ResponseBuilder.JSON.ok(job);
				} else {
					// otherwise return not found
					return ResponseBuilder.JSON.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}
	
	/**
	 * REST service which returns jobs status, by job name filter
	 * 
	 * @param jobNameFilter
	 *            job name filter (default *)
	 * @return a job status object
	 * @see JobStatus
	 */
	@GET
	@Path(JobsManagerPaths.GET_JCL_TYPES)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobStatus() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// translates a set to list
				List<String> list = new ArrayList<String>(jobsManager.getJclTypes().keySet());
				// returns the jcl types
				return ResponseBuilder.JSON.ok(list);
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Holds the jobs in a specific queue
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @return returns <code>true</code> if all jobs are changed, otherwise
	 *         <code>false</code>
	 */
	@PUT
	@Path(JobsManagerPaths.HOLD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response hold(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// checks if job id is missing
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks the queue if correct
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				// searches the job by ID
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if we have the job instance, try to hold
				if (job != null) {
					// returns true if OK
					return ResponseBuilder.PLAIN.ok(jobsManager.hold(Arrays.asList(job), jQueue.getName()).toString());
				} else {
					// otherwise return not found
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Releases holded jobs in a specific queue
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @return returns <code>true</code> if all jobs are changed, otherwise
	 *         <code>false</code>
	 */
	@PUT
	@Path(JobsManagerPaths.RELEASE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response release(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// checks if job id is missing
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks the queue if correct
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				// searches the job by ID
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if we have the job instance, try to release
				if (job != null) {
					// returns true if OK
					return ResponseBuilder.PLAIN.ok(jobsManager.release(Arrays.asList(job), jQueue.getName()).toString());
				} else {
					// otherwise not found
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Cancels jobs in execution
	 * 
	 * @param id
	 *            job id
	 * @param force
	 *            if set, performs a cancel with force attribute
	 * @return returns <code>true</code> if all jobs are changed, otherwise
	 *         <code>false</code>
	 */
	@PUT
	@Path(JobsManagerPaths.CANCEL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response cancel(@PathParam(JobsManagerPaths.JOBID) String id, @PathParam(JobsManagerPaths.FORCE) String force) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// gets the force parameter
				boolean isCancelForce = Boolean.parseBoolean(force);
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets the job by id
				Job job = jobsManager.getJobById(Queues.RUNNING_QUEUE, id);
				// if job is missing, not found
				if (job != null) {
					// performs cancel returning true if OK
					return ResponseBuilder.PLAIN.ok(jobsManager.cancel(Arrays.asList(job), isCancelForce).toString());
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Purges jobs from queues
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @return returns <code>true</code> if all jobs are changed, otherwise
	 *         <code>false</code>
	 */
	@PUT
	@Path(JobsManagerPaths.PURGE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response purge(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks queue. If missing, bad request
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				// gets job by id
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if job is missing, not found
				if (job != null) {
					// performs the purge and return true if OK
					return ResponseBuilder.PLAIN.ok(jobsManager.purge(Arrays.asList(job), jQueue.getName()).toString());
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Updates a job in queue
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @param job
	 *            list of attributes of the job to be changed
	 * @return <code>true</code> if ended correctly
	 */
	@PUT
	@Path(JobsManagerPaths.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response update(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id, UpdateJob job) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks queue. If missing, bad request
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				// sets to update job object the job id
				job.setId(id);
				// returns true if OK
				return ResponseBuilder.PLAIN.ok(jobsManager.update(job, jQueue.getName()).toString());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Submits a job in JEM, returning the job id
	 * 
	 * @param jclType JCL type
	 * @param jclURL JEM URL (ONLY JEM URL is allowed) to read the JCL from GFS
	 * @param jclProperties optional properties to pass to JOB
	 * @param content JCL content
	 * @return job ID calculated after submission
	 */
	@PUT
	@Path(JobsManagerPaths.SUBMIT)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response submit(@QueryParam(JobsManagerPaths.JCL_TYPE_QUERY_STRING) String jclType, 
			@QueryParam(JobsManagerPaths.JCL_URL_QUERY_STRING) String jclURL,
			@QueryParam(JobsManagerPaths.JCL_PROPERTIES_QUERY_STRING) String jclProperties,
			String content) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// checks if there is a valid content
				boolean hasContent = content != null && content.trim().length() > 0;
				// if the JCL is missing, bad request
				if (!hasContent && jclURL == null) {
					throw new MessageException(UserInterfaceMessage.JEMG073E, JobsManagerPaths.JCL_URL_QUERY_STRING);
				}
				if (jclURL != null && !jclURL.startsWith(JemURLStreamHandlerFactory.PROTOCOL)){
					throw new MessageException(UserInterfaceMessage.JEMG074E);
				}
				if (hasContent && jclURL != null) {
					throw new MessageException(UserInterfaceMessage.JEMG072E, JobsManagerPaths.JCL_URL_QUERY_STRING);
				}
				// creates a pre job using the JCL
				PreJob preJob = new PreJob();
				if (hasContent){
					preJob.setJclContent(content);
				}
				// sets JCL type
				preJob.setJclType(jclType);
				// creates a job
				Job job = new Job();
				if (jclProperties != null && jclProperties.length() > 0){
					String[] props = StringUtils.split(jclProperties, JobsManagerPaths.JCL_PROPERTY_SEPARATOR);
					job.setInputArguments(Arrays.asList(props));
				}
				preJob.setJob(job);
				// set URL if not null
				if (jclURL != null){
					preJob.setUrl(jclURL);
				}
				// submits and return JOBid
				return ResponseBuilder.PLAIN.ok(jobsManager.submit(preJob));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Returns the tree with all output files produced by job
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @return a tree with all references to output files
	 */
	@GET
	@Path(JobsManagerPaths.OUTPUT_TREE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOutputTree(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks queue. If missing, bad request
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.QUEUE);
				}
				// gets job by id
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if job is missing, not found
				if (job != null) {
					// returns the output tre object
					return ResponseBuilder.JSON.ok(jobsManager.getOutputTree(job, jQueue.getName()));
				} else {
					return ResponseBuilder.JSON.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Returns the content of requested output file for a specific job.
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @param item
	 *            type of output content is requested
	 * @return content file
	 */
	@POST
	@Path(JobsManagerPaths.OUTPUT_FILE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getOutputFileContent(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id, OutputListItem item) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks queue. If missing, bad request
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				// gets job by id
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if job is missing, not found
				if (job != null) {
					// returns the content of output
					return ResponseBuilder.PLAIN.ok(jobsManager.getOutputFileContent(job, item));
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Returns the content of requested jcl for a specific job.
	 * 
	 * @param queue
	 *            queue of JEM where perform query
	 * @param id
	 *            job id
	 * @return content JCL
	 */
	@GET
	@Path(JobsManagerPaths.JCL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getJcl(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// gets and checks queue. If missing, bad request
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null) {
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				// gets job by id
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				// if job is missing, not found
				if (job != null) {
					// returns JCL
					return ResponseBuilder.PLAIN.ok(jobsManager.getJcl(job, jQueue.getName()));
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Returns the system information about the job in execution.
	 * 
	 * @param id
	 *            job id
	 * @return system information of job
	 */
	@GET
	@Path(JobsManagerPaths.SYSTEM_ACTIVITY)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobSystemActivity(@PathParam(JobsManagerPaths.JOBID) String id) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if id is missing, bad request
				if (id == null) {
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.JOBID);
				}
				// gets job by id
				Job job = jobsManager.getJobById(Queues.RUNNING_QUEUE, id);
				// if job is missing, not found
				if (job != null) {
					// gets system activity
					JobSystemActivity activity = jobsManager.getJobSystemActivity(job);
					// if missing, not found
					if (activity != null) {
						// returns the system activity o f the job
						return ResponseBuilder.JSON.ok(activity);
					} else {
						return ResponseBuilder.JSON.notFound(id);
					}
				} else {
					return ResponseBuilder.JSON.notFound(id);
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Returns the JOBQUEUE by a string
	 * 
	 * @see JobQueue
	 * @param queueName
	 *            queue name to search
	 * @return a job queue instance or null if not found
	 */
	private JobQueue getJobQueue(String queueName) {
		return JobQueue.getQueueByPath(queueName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#init()
	 */
	@Override
	boolean init() throws Exception {
		if (jobsManager == null) {
			jobsManager = new JobsManager();
		}
		return true;
	}
}
