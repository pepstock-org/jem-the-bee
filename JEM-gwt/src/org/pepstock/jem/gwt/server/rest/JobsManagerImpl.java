/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.util.Collection;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.rest.entities.JclContent;
import org.pepstock.jem.gwt.server.rest.entities.JobId;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputFileContent;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputListArgument;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.gwt.server.rest.entities.Jobs;
import org.pepstock.jem.gwt.server.rest.entities.ReturnedObject;
import org.pepstock.jem.gwt.server.services.JobsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;

/**
 * Rest service to manage jobs queues.<br>
 * Pay attention to URL (@path) annotation
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@Path("/"+JobsManagerImpl.JOBS_MANAGER_PATH)
public class JobsManagerImpl extends DefaultServerResource  {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String JOBS_MANAGER_PATH = "jobs";

	/**
	 * Key to define the path to bind input management method
	 */
	public static final String JOBS_MANAGER_INPUT_PATH = "input";

	/**
	 * Key to define the path to bind running management method
	 */
	public static final String JOBS_MANAGER_RUNNING_PATH = "running";

	/**
	 * Key to define the path to bind output management method
	 */
	public static final String JOBS_MANAGER_OUTPUT_PATH = "output";

	/**
	 * Key to define the path to bind routing management method
	 */
	public static final String JOBS_MANAGER_ROUTING_PATH = "routing";

	/**
	 * Key to define the path to bind hold action method
	 */
	public static final String JOBS_MANAGER_HOLD_PATH = "hold";

	/**
	 * Key to define the path to bind release action method
	 */
	public static final String JOBS_MANAGER_RELEASE_PATH = "release";

	/**
	 * Key to define the path to bind submit action method
	 */
	public static final String JOBS_MANAGER_SUBMIT_PATH = "submit";

	/**
	 * Key to define the path to bind get job output tree method
	 */
	public static final String JOBS_MANAGER_OUTPUT_TREE_PATH = "outputTree";

	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String JOBS_MANAGER_OUTPUT_FILE_CONTENT_PATH = "outputFileContent";

	/**
	 * Key to define the path to bind get job jcl content method
	 */
	public static final String JOBS_MANAGER_JCL_CONTENT_PATH = "jclContent";

	private JobsManager jobsManager = null;
	
	/**
	 * REST service which returns jobs in input queue, by job name filter
	 * 
	 * @param jobNameFilter job name filter
	 * @return a jobs container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_INPUT_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Jobs getInputQueue(String jobNameFilter) throws JemException {
		Jobs jobsContainer = new Jobs();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			Collection<Job> jobs;
            try {
	            jobs = jobsManager.getInputQueue(jobNameFilter);
				jobsContainer.setJobs(jobs);
				jobsContainer.setQueueName(Queues.INPUT_QUEUE);	            
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
            	jobsContainer.setExceptionMessage(e.getMessage());
            }				
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			jobsContainer.setExceptionMessage(msg);
		}
		return jobsContainer;
	}

	/**
	 * REST service which returns jobs in running queue, by job name filter
	 * 
	 * @param jobNameFilter job name filter
	 * @return a jobs container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_RUNNING_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Jobs getRunningQueue(String jobNameFilter) throws JemException {
		Jobs jobsContainer = new Jobs();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			Collection<Job> jobs;
            try {
	            jobs = jobsManager.getRunningQueue(jobNameFilter);
				jobsContainer.setJobs(jobs);
				jobsContainer.setQueueName(Queues.RUNNING_QUEUE);	            
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
            	jobsContainer.setExceptionMessage(e.getMessage());
            }				
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			jobsContainer.setExceptionMessage(msg);
		}
		return jobsContainer;
	}

	/**
	 * REST service which returns jobs in output queue, by job name filter
	 * 
	 * @param jobNameFilter job name filter   
	 * @return a jobs container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_OUTPUT_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Jobs getOutputQueue(String jobNameFilter) throws JemException {
		Jobs jobsContainer = new Jobs();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			Collection<Job> jobs;
            try {
	            jobs = jobsManager.getOutputQueue(jobNameFilter);
				jobsContainer.setJobs(jobs);
				jobsContainer.setQueueName(Queues.OUTPUT_QUEUE);	            
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
            	jobsContainer.setExceptionMessage(e.getMessage());
            }				
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			jobsContainer.setExceptionMessage(msg);
		}
		return jobsContainer;
	}

	/**
	 * REST service which returns jobs in routing queue, by job name filter
	 * 
	 * @param jobNameFilter job name filter
	 * @return a jobs container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_ROUTING_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Jobs getRoutingQueue(String jobNameFilter) throws JemException {
		Jobs jobsContainer = new Jobs();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			Collection<Job> jobs;
            try {
	            jobs = jobsManager.getRoutingQueue(jobNameFilter);
				jobsContainer.setJobs(jobs);
				jobsContainer.setQueueName(Queues.ROUTING_QUEUE);	            
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
            	jobsContainer.setExceptionMessage(e.getMessage());
            }				
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			jobsContainer.setExceptionMessage(msg);
		}
		return jobsContainer;
	}

	/**
	 * Holds the jobs in a specific queue
	 * 
	 * @param jobs list and queue name of jobs to hold
	 * @return returned object
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_HOLD_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ReturnedObject hold(Jobs jobs) throws JemException {
		ReturnedObject ro = new ReturnedObject();
		if (isEnable()){
			if (jobs.getQueueName() != null && !jobs.getJobs().isEmpty()){
				if (jobsManager == null){
					initManager();
				}
				try {
	                jobsManager.hold(jobs.getJobs(), jobs.getQueueName());
                } catch (Exception e) {
                	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
                	ro.setExceptionMessage(e.getMessage());
                }
			}
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			ro.setExceptionMessage(msg);
		}
		return ro;
	}

	/**
	 * Releases holded jobs in a specific queue
	 * 
	 * @param jobs list and queue name of jobs to release
	 * @return 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_RELEASE_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ReturnedObject release(Jobs jobs) throws JemException {
		ReturnedObject ro = new ReturnedObject();
		if (isEnable() && jobs.getQueueName() != null && !jobs.getJobs().isEmpty()){
			if (jobsManager == null){
				initManager();
			}
			try {
				jobsManager.release(jobs.getJobs(), jobs.getQueueName());
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
				ro.setExceptionMessage(e.getMessage());
			}
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			ro.setExceptionMessage(msg);
		}
		return ro;
	}
	
	/**
	 * Submits a job in JEM, returning the job id
	 * 
	 * @param preJob job to submit 
	 * @return job ID calculated after submission
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_SUBMIT_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public JobId submit(PreJob preJob) throws JemException {
		JobId jobid = new JobId();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			try {
				String id = jobsManager.submit(preJob);
				jobid.setId(id);
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
				jobid.setExceptionMessage(e.getMessage());
			}			
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			jobid.setExceptionMessage(msg);
		}
		return jobid;
	}
	
	/**
	 * Returns the tree with all output files produced by job
	 * 
	 * @param jobs job container with queue name
	 * @return a tree with all references to output files
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path("/"+JOBS_MANAGER_OUTPUT_TREE_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public JobOutputTreeContent getOutputTree(Jobs jobs) throws JemException {
		JobOutputTreeContent content = new JobOutputTreeContent();
		
		if (isEnable() && jobs.getQueueName() != null && jobs.getJobs().size() == 1){
			if (jobsManager == null){
				initManager();
			}
			try {
				Job job = jobs.getJobs().iterator().next();
				OutputTree tree = jobsManager.getOutputTree(job, jobs.getQueueName());
				content.setJclContent(tree.getJclContent());
				content.setFirstLevelItems(tree.getFirstLevelItems());
				content.setJob(job);
				for (List<OutputListItem> items : tree.getSecondLevelItems()){
					if (!items.isEmpty()){
						String key = items.get(0).getParent();
						content.getSecondLevelItems().put(key, items);
					}
				}
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
				content.setExceptionMessage(e.getMessage());
			}
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			content.setExceptionMessage(msg);
		}
		return content;
	}

	/**
	 * Returns the content of requested output file for a specific job.
	 * 
	 * @param jobFileContent container with output file and job instances
	 * @return content file
	 * @throws JemException if JEM group is not available or not authorized  
	 */
	@POST
	@Path("/"+JOBS_MANAGER_OUTPUT_FILE_CONTENT_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public JobOutputFileContent getOutputFileContent(JobOutputListArgument jobFileContent) throws JemException {
		JobOutputFileContent content = new JobOutputFileContent();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			try {
	            OutputFileContent fileContent = jobsManager.getOutputFileContent(jobFileContent.getJob(), jobFileContent.getItem());
	            content.setJob(jobFileContent.getJob());
	            content.setOutputFileContent(fileContent);
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
				content.setExceptionMessage(e.getMessage());
            }
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			content.setExceptionMessage(msg);
		}
		return content;
	}

	/**
	 * Returns the content of requested jcl for a specific job.
	 * 
	 * @param jobs job container with queue name
	 * @return content JCL
	 * @throws JemException if JEM group is not available or not authorized  
	 */
	@POST
	@Path("/"+JOBS_MANAGER_JCL_CONTENT_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public JclContent getJcl(Jobs jobs) throws JemException {
		JclContent content = new JclContent();
		if (isEnable()){
			if (jobsManager == null){
				initManager();
			}
			try {
	            Job job = jobs.getJobs().iterator().next();
	            String jclContent = jobsManager.getJcl(job, jobs.getQueueName());
	            content.setContent(jclContent);
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG038E, e, e.getMessage());
				content.setExceptionMessage(e.getMessage());
            }
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			content.setExceptionMessage(msg);
		}
		return content;
	}
	
	/**
	 * Initializes a jobs manager
	 */
	private synchronized void initManager(){
		if (jobsManager == null){
			jobsManager = new JobsManager();
		}
	}
}
