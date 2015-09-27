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

import java.util.Arrays;

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

import org.pepstock.jem.Job;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.PreJcl;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.UpdateJob;
import org.pepstock.jem.gwt.server.services.JobsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.rest.entities.JobQueue;
import org.pepstock.jem.rest.paths.CommonPaths;
import org.pepstock.jem.rest.paths.JobsManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Rest service to manage jobs queues.<br>
 * Pay attention to URL (@path) annotation
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@Singleton
@Path(JobsManagerPaths.MAIN)
public class JobsManagerImpl extends DefaultServerResource  {

	private JobsManager jobsManager = null;
	
	/**
	 * REST service which returns jobs in passed queue, by job filters
	 * 
	 * @param jobNameFilter job name filter
	 * @return a jobs container
	 */
	@GET
	@Path(JobsManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobs(@PathParam(JobsManagerPaths.QUEUE) String queue, @DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING)String jobNameFilter) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				JobQueue jQueue = getJobQueue(queue);
				return (jQueue == null) ? ResponseBuilder.JSON.badRequest(JobsManagerPaths.QUEUE) : ResponseBuilder.JSON.ok(jobsManager.getJobsByQueue(jQueue.getName(), jobNameFilter));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * REST service which returns jobs status, by job name filter
	 * 
	 * @param jobNameFilter job name filter
	 * @return a jobs container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(JobsManagerPaths.JOB_STATUS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobStatus(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String jobNameFilter) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				return ResponseBuilder.JSON.ok(jobsManager.getJobStatus(jobNameFilter));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns job, by job id filter
	 * 
	 * @param jobsParm job id filter
	 * @return a jobs container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(JobsManagerPaths.JOB_BY_ID)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobById(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				System.err.println(jQueue.getName()+" "+id+" "+job);
				if (job != null){
					return ResponseBuilder.JSON.ok(job);
				} else {
					return ResponseBuilder.JSON.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Holds the jobs in a specific queue
	 * 
	 * @param jobs list and queue name of jobs to hold
	 * @return returns <code>true</code> if all jobs are changed, otherwise <code>false</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(JobsManagerPaths.HOLD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response hold(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				if (job != null){
					return ResponseBuilder.PLAIN.ok(jobsManager.hold(Arrays.asList(job), jQueue.getName()).toString());
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * Releases holded jobs in a specific queue
	 * 
	 * @param jobs list and queue name of jobs to release
	 * @return returns <code>true</code> if all jobs are changed, otherwise <code>false</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(JobsManagerPaths.RELEASE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response release(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				if (job != null){
					return ResponseBuilder.PLAIN.ok(jobsManager.release(Arrays.asList(job), jQueue.getName()).toString());
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * Cancels jobs in execution
	 * 
	 * @param jobs list and queue name of jobs to cancel
	 * @return returns <code>true</code> if all jobs are changed, otherwise <code>false</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(JobsManagerPaths.CANCEL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response cancel(@PathParam(JobsManagerPaths.JOBID) String id, @PathParam(JobsManagerPaths.FORCE) String force) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				boolean isCancelForce  = Boolean.parseBoolean(force);
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				Job job = jobsManager.getJobById(Queues.RUNNING_QUEUE, id);
				if (job != null){
					return ResponseBuilder.PLAIN.ok(jobsManager.cancel(Arrays.asList(job), isCancelForce).toString());
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Purges jobs from queues
	 * 
	 * @param jobs list and queue name of jobs to purge
	 * @return returns <code>true</code> if all jobs are changed, otherwise <code>false</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(JobsManagerPaths.PURGE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response purge(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				if (job != null){
					return ResponseBuilder.PLAIN.ok(jobsManager.purge(Arrays.asList(job), jQueue.getName()));
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Updates a job in queue
	 * 
	 * @param jobs list and queue name of jobs to update
	 * @return returns <code>true</code> if all jobs are changed, otherwise <code>false</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(JobsManagerPaths.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response update(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id, UpdateJob job) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				job.setId(id);
				return ResponseBuilder.PLAIN.ok(jobsManager.update(job, jQueue.getName()).toString());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Submits a job in JEM, returning the job id
	 * 
	 * @param preJcl job to submit 
	 * @return job ID calculated after submission
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(JobsManagerPaths.SUBMIT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response submit(PreJcl preJcl) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (preJcl.getContent() == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				// creates a pre job using the JCL
				PreJob preJob = new PreJob();
				preJob.setJclContent(preJcl.getContent());
				// sets JCL type
				preJob.setJclType(preJcl.getType());
				// creates a job
				Job job = new Job();
				preJob.setJob(job);
				return ResponseBuilder.PLAIN.ok(jobsManager.submit(preJob));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Returns the tree with all output files produced by job
	 * 
	 * @param jobs job container with queue name
	 * @return a tree with all references to output files
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(JobsManagerPaths.OUTPUT_TREE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOutputTree(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				if (job != null){
					return ResponseBuilder.JSON.ok(jobsManager.getOutputTree(job, jQueue.getName()));
				} else {
					return ResponseBuilder.JSON.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * Returns the content of requested output file for a specific job.
	 * 
	 * @param jobFileContent container with output file and job instances
	 * @return content file
	 * @throws JemException if JEM group is not available or not authorized  
	 */
	@POST
	@Path(JobsManagerPaths.OUTPUT_FILE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getOutputFileContent(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id, OutputListItem item) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				if (job != null){
					return ResponseBuilder.PLAIN.ok(jobsManager.getOutputFileContent(job, item));
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * Returns the content of requested jcl for a specific job.
	 * 
	 * @param jobs job container with queue name
	 * @return content JCL
	 * @throws JemException if JEM group is not available or not authorized  
	 */
	@GET
	@Path(JobsManagerPaths.JCL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getJcl(@PathParam(JobsManagerPaths.QUEUE) String queue, @PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.JOBID);
				}
				JobQueue jQueue = getJobQueue(queue);
				if (jQueue == null){
					return ResponseBuilder.PLAIN.badRequest(JobsManagerPaths.QUEUE);
				}
				Job job = jobsManager.getJobById(jQueue.getName(), id);
				if (job != null){
					return ResponseBuilder.PLAIN.ok(jobsManager.getJcl(job, jQueue.getName()));
				} else {
					return ResponseBuilder.PLAIN.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Returns the system information about the job in execution.
	 * 
	 * @param jobs job container with JOB IDS, process ids and members
	 * @return system information of job
	 * @throws JemException if JEM group is not available or not authorized  
	 */
	@GET
	@Path(JobsManagerPaths.JOB_SYSTEM_ACTIVITY)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobSystemActivity(@PathParam(JobsManagerPaths.JOBID) String id) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try {
				if (id == null){
					return ResponseBuilder.JSON.badRequest(JobsManagerPaths.JOBID);
				}
				Job job = jobsManager.getJobById(Queues.RUNNING_QUEUE, id);
				if (job != null){
					JobSystemActivity activity = jobsManager.getJobSystemActivity(job);
					if (activity != null){
						return ResponseBuilder.JSON.ok(activity);
					} else {
						return ResponseBuilder.JSON.notFound(id);
					}
				} else {
					return ResponseBuilder.JSON.notFound(id);
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}	
	
	
	private JobQueue getJobQueue(String queueName){
		return JobQueue.getQueueByPath(queueName);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#init()
	 */
    @Override
    boolean init() throws Exception {
		if (jobsManager == null){
			jobsManager = new JobsManager();
		}
		return true;
    }
}
