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
package org.pepstock.jem.gwt.server.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.JobStatus;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.Result;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.gwt.server.commons.GenericDistributedTaskExecutor;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.jobs.Cancel;
import org.pepstock.jem.node.executors.jobs.GetJclTypes;
import org.pepstock.jem.node.executors.jobs.GetJobSystemActivity;
import org.pepstock.jem.node.executors.jobs.GetOutputFileContent;
import org.pepstock.jem.node.executors.jobs.GetOutputTree;
import org.pepstock.jem.node.executors.jobs.Purge;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.security.User;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.JobFilterFields;
import org.pepstock.jem.util.filters.predicates.JobPredicate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.query.SqlPredicate;

/**
 * This service provides all methods to access to jobs information.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
@SuppressWarnings("deprecation")
public class JobsManager extends DefaultService {

	/**
	 * Returns the list of jobs in INPUT, a filter string composed by UI filters
	 * 
	 * @param filter
	 *            filter string
	 * @return collection of jobs
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Collection<Job> getInputQueue(String filter) throws ServiceMessageException {
		return getJobsByQueue(Queues.INPUT_QUEUE, filter);
	}

	/**
	 * Returns the list of jobs in RUNNING, a filter string composed by UI
	 * filters
	 * 
	 * @param filter
	 *            filter string
	 * @return collection of jobs
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Collection<Job> getRunningQueue(String filter) throws ServiceMessageException {
		return getJobsByQueue(Queues.RUNNING_QUEUE, filter);
	}

	/**
	 * Returns the list of jobs in OUTPUT, a filter string composed by UI
	 * filters
	 * 
	 * @param filter
	 *            filter string
	 * @return collection of jobs
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Collection<Job> getOutputQueue(String filter) throws ServiceMessageException {
		return getJobsByQueue(Queues.OUTPUT_QUEUE, filter);
	}

	/**
	 * Returns the list of jobs in ROUTING, a filter string composed by UI
	 * filters
	 * 
	 * @param filter
	 *            filter string
	 * @return collection of jobs
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Collection<Job> getRoutingQueue(String filter) throws ServiceMessageException{
		return getJobsByQueue(Queues.ROUTING_QUEUE, filter);
	}

	/**
	 * This is common method to extract jobs from different queues by filter
	 * string
	 * 
	 * @param queueName
	 *            queue name to use to get the right map
	 * @param filterString
	 *            filter string
	 * @return collection of jobs
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	private Collection<Job> getJobsByQueue(String queueName, String filterString) throws ServiceMessageException {
		// creates a filter object
		Filter filter = null;
		try {
			filter = Filter.parse(filterString);
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// default case, all jobs
			filter = new Filter();
			filter.add(new FilterToken(JobFilterFields.NAME.getName(), StringUtils.EMPTY));
		}
		// extract the jobname, if it is.
		// necessary to check permission because it is based on
		// job name
		String jobName = filter.get(JobFilterFields.NAME.getName());
		// if job name is null, means all, then "*"
		if (jobName == null || jobName.trim().length() == 0) {
			jobName = "*";
		}
		// creates the right permission by job name
		String permission = Permissions.SEARCH_JOBS + jobName;
		// checks if the user is authorized to get jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));

		IMap<String, Job> jobs = getInstance().getMap(queueName);
		// creates predicate
		JobPredicate predicate = new JobPredicate(filter);
		return new ArrayList<Job>(jobs.values(predicate));
	}

	/**
	 * Returns a job status by filter.<br>
	 * Filter must be job name (no pattern with wild-cards) or job id
	 * 
	 * @param filter
	 *            job name (no pattern with wild-cards) or job id
	 * @return job status
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public JobStatus getJobStatus(String filter) throws ServiceMessageException {
		// checks user authentication
		// if not, this method throws an exception
		checkAuthentication();

		// creates job status
		JobStatus status = new JobStatus();
		StringBuilder sb = new StringBuilder();

		// parses filter to understand if the request is done by
		// job name or job id
		try {
			MessageFormat jobIdFormat = new MessageFormat(Factory.JOBID_FORMAT);
			// checks if is by job id
			jobIdFormat.parse(filter);
			sb.append("id = '").append(filter).append("'");
		} catch (ParseException pe) {
			// otherwise is by job name
			// creates the right permission by job name
			String permission = Permissions.SEARCH_JOBS + filter;
			// checks if the user is authorized to get jobs
			// if not, this method throws an exception
			checkAuthorization(new StringPermission(permission));
			// creates SQL predicate string
			sb.append("name = '").append(filter).append("'");
		}

		// performs predicate on all maps to search job
		SqlPredicate predicate = new SqlPredicate(sb.toString());
		status.setJobsInput(loadQueuesJobs(Queues.INPUT_QUEUE, predicate));
		status.setJobsRunning(loadQueuesJobs(Queues.RUNNING_QUEUE, predicate));
		status.setJobsOutput(loadQueuesJobs(Queues.OUTPUT_QUEUE, predicate));
		status.setJobsRouting(loadQueuesJobs(Queues.ROUTING_QUEUE, predicate));
		return status;
	}

	/**
	 * Performs the predicate pased as argument on a specific queue, identified
	 * by name.
	 * 
	 * @param queueName
	 *            map name
	 * @param sql
	 *            SQL predicate to apply on map
	 * @return collection of jobs
	 * @throws Exception
	 *             if any exception occurs or a lock timeout on map occurs
	 */
	private Collection<Job> loadQueuesJobs(String queueName, SqlPredicate sql) {
		IMap<String, Job> jobs = getInstance().getMap(queueName);
		// performs predicate to have the collection
		return new ArrayList<Job>(jobs.values(sql));
	}

	/**
	 * Returns a job by its job id.
	 * 
	 * @param queueName
	 *            map name
	 * @param jobId
	 *            job id to search
	 * @return job, if found, otherwise null
	 * @throws ServiceMessageException 
	 *             if any exception occurs or a lock timeout on map occurs
	 */
	public Job getJobById(String queueName, String jobId) throws ServiceMessageException {
		// checks user authentication
		// if not, this method throws an exception
		checkAuthentication();

		Job job = null;
		IMap<String, Job> queue = getInstance().getMap(queueName);
		try {
			// locks the key (job id)
			queue.lock(jobId);
			// checks if exist
			// if yes, saved it in local reference
			if (queue.containsKey(jobId)) {
				job = queue.get(jobId);
			}
		} finally {
			// unlocks always the key
			queue.unlock(jobId);
		}
		if (job != null) {
			// creates the right permission by job name
			String permission = Permissions.SEARCH_JOBS + job.getName();
			// checks if the user is authorized to get jobs
			// if not, this method throws an exception
			checkAuthorization(new StringPermission(permission));
		}
		// returns job
		return job;
	}

	/**
	 * Returns a job by its job id, searching it in OUTPUT and ROUTED maps.<br>
	 * If job is not in output, tries searching it in ROUTED
	 * 
	 * @param jobId
	 *            job id
	 * @return job instance
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Job getEndedJobById(String jobId) throws ServiceMessageException {
		// checks user authentication
		// if not, this method throws an exception
		checkAuthentication();

		// gets job from output.
		Job job = getJobById(Queues.OUTPUT_QUEUE, jobId);
		// if does not exist, checks on routed map
		if (job == null) {
			// Checks ROUTED QUEUE
			IMap<String, Job> routedQueue = getInstance().getMap(Queues.ROUTED_QUEUE);
			try {
				// locks the key (job id)
				routedQueue.lock(jobId);
				// checks if exist
				// if true, removes job
				if (routedQueue.containsKey(jobId)) {
					job = routedQueue.remove(jobId);
				}
			} finally {
				// unlocks always the map
				routedQueue.unlock(jobId);
			}
			if (job != null) {
				// creates the right permission by job name
				String permission = Permissions.SEARCH_JOBS + job.getName();
				// checks if the user is authorized to get jobs
				// if not, this method throws an exception
				checkAuthorization(new StringPermission(permission));
			}
		}
		// returns job
		return job;
	}

	/**
	 * Holds jobs in INPUT, OUTPUT or ROUTING queue. To set HOLD means that it
	 * can't be executed or removed from queue.
	 * 
	 * @param jobs
	 *            collections of jobs to hold
	 * @param queueName
	 *            map where jobs are
	 * @return true is it holds them, otherwise false
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Boolean hold(Collection<Job> jobs, String queueName) throws ServiceMessageException {
		// holds ONLY jobs in input, output and routing
		if (!queueName.equalsIgnoreCase(Queues.INPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.OUTPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.ROUTING_QUEUE)){
			return Boolean.FALSE;
		}
		// checks if the user is authorized to hold jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.JOBS_HOLD));

		IMap<String, Job> queue = getInstance().getMap(queueName);
		// scans all jobs to hold
		for (Job job : jobs) {
			try {
				// lock the key (job id)
				queue.lock(job.getId());
				// checks if exist
				if (queue.containsKey(job.getId())) {
					// gets job
					// set hold to true if it was already in hold
					Job storedJob = queue.get(job.getId());
					Jcl storedJcl = storedJob.getJcl();
					if (!storedJcl.isHold()) {
						storedJcl.setHold(true);
						queue.replace(storedJob.getId(), storedJob);
					}
				}
			} finally {
				// unlocks always the key
				queue.unlock(job.getId());
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Cancel a set of jobs currently in running. If force is set to true, JEM
	 * uses force mode to cancel jobs.
	 * 
	 * @param jobs
	 *            list of jobs to cancel
	 * @param force
	 *            if true, uses force attribute to cancel jobs
	 * @return always true!
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Boolean cancel(Collection<Job> jobs, boolean force) throws ServiceMessageException {
		// checks if the user is authorized to cancel or kill jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission((force) ? Permissions.JOBS_KILL : Permissions.JOBS_CANCEL));
		// gets user
		// that's necessary to print on log who cancel job
		Subject currentUser = SecurityUtils.getSubject();
		User user = (User) currentUser.getPrincipal();

		// scans jobs
		for (Job job : jobs) {
			Job jobSearched = job;
			String nodeKey = job.getMemberId();
			if ((nodeKey == null || job.getProcessId() == null) && job.getId() != null){
				jobSearched = getJobById(Queues.RUNNING_QUEUE, job.getId());
				nodeKey = jobSearched.getMemberId();
			} 
			// gets Hazelcast member
			// if is not able, an exception occurs
			GenericDistributedTaskExecutor task;
            try {
	            task = new GenericDistributedTaskExecutor(new Cancel(jobSearched, user.getId(), force), getMember(nodeKey));
	            task.execute();
            } catch (Exception e) {
	            LogAppl.getInstance().ignore(e.getMessage(), e);
            }
		}
		return Boolean.TRUE;
	}

	/**
	 * Releases jobs in INPUT, OUTPUT or ROUTING queue, which were previously
	 * hold.
	 * 
	 * @param jobs
	 *            collections of jobs to hold
	 * @param queueName
	 *            map where jobs are
	 * @return true is it holds them, otherwise false
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Boolean release(Collection<Job> jobs, String queueName) throws ServiceMessageException {
		// release ONLY jobs in input, output and routing
		if (!queueName.equalsIgnoreCase(Queues.INPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.OUTPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.ROUTING_QUEUE)){
			return Boolean.FALSE;
		}
		// checks if the user is authorized to release jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.JOBS_RELEASE));

		IMap<String, Job> queue = getInstance().getMap(queueName);
		// scans all jobs to hold
		for (Job job : jobs) {
			try {
				// lock the key (job id)
				queue.lock(job.getId());
				// checks if exist
				if (queue.containsKey(job.getId())) {
					// gets job
					// set hold to false if it was not already in hold
					Job storedJob = queue.get(job.getId());
					Jcl storedJcl = storedJob.getJcl();
					if (storedJcl.isHold()) {
						storedJcl.setHold(false);
						queue.replace(storedJob.getId(), storedJob);
					}
				}
			} finally {
				// unlocks always the key
				queue.unlock(job.getId());
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Purge (removing any output) jobs from INPUT, OUTPUT or ROUTING queue.
	 * 
	 * @param jobs
	 *            collections of jobs to purge
	 * @param queueName
	 *            map where jobs are
	 * @return true is it holds them, otherwise false
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Boolean purge(Collection<Job> jobs, String queueName) throws ServiceMessageException {
		// release ONLY jobs in input, output and routing
		if (!queueName.equalsIgnoreCase(Queues.INPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.OUTPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.ROUTING_QUEUE)){
			return Boolean.FALSE;
		}
		// checks if the user is authorized to purge jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.JOBS_PURGE));

		// gets user
		// that's necessary to print on log who cancel job
		Subject currentUser = SecurityUtils.getSubject();
		User user = (User) currentUser.getPrincipal();

		IMap<String, Job> queue = getInstance().getMap(queueName);
		// scans jobs
		for (Job job : jobs) {
			try {
				// locks the key(job id)
				queue.lock(job.getId());
				// checks if exist
				if (queue.containsKey(job.getId())) {
					// gets job
					Job storedJob = queue.get(job.getId());
					// sets the result to Cancelled
					Result result = new Result();
					result.setReturnCode(Result.CANCELED);
					// sets exception, puttinh the user id
					result.setExceptionMessage("Purge by user(" + user.getId() + ") interface");
					storedJob.setEndedTime(new Date());
					// store result into job
					storedJob.setResult(result);
					// removes from queue
					queue.remove(storedJob.getId());

					// if it was in OUTPUT,
					// removes output files from file system
					// calling a exectuor
					if (queueName.equalsIgnoreCase(Queues.OUTPUT_QUEUE)) {
						callPurge(storedJob);
					}

					// sends a topic to all subscribers
					// telling them that jobs is ended
					ITopic<Job> topic = getInstance().getTopic(Queues.ENDED_JOB_TOPIC);
					topic.publish(storedJob);
				}
			} finally {
				// always unlocks the key
				queue.unlock(job.getId());
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Calls a executor to remove the joboutput directory from file system
	 * 
	 * @param storedJob
	 *            removed job
	 */
	private void callPurge(Job storedJob) {
		try {
			GenericDistributedTaskExecutor task = new GenericDistributedTaskExecutor(new Purge(storedJob), getMember());
			task.execute();
		} catch (Exception e) {
			// catches to avoid any useless message of failure but prints on log
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG054E, e, storedJob.toString());
		}
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
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public Boolean update(Job job, String queueName) throws ServiceMessageException {
		// builds permission
		String permission = Permissions.JOBS_UPDATE;
		// checks if the user is authorized to update job
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));

		IMap<String, Job> queue = getInstance().getMap(queueName);
		try {
			// locks the key(job id)
			queue.lock(job.getId());
			// checks if exist
			if (queue.containsKey(job.getId())) {
				// gets job and jcl, sets to new job
				// and stores new job
				Job storedJob = queue.get(job.getId());
				Jcl storedJcl = storedJob.getJcl();
				// sets JCL because JCL is not
				// serialized to GWT (too big)
				job.getJcl().setContent(storedJcl.getContent());
				queue.replace(job.getId(), job);
			}
		} finally {
			// unlocks always the key
			queue.unlock(job.getId());
		}
		return Boolean.TRUE;
	}

	/**
	 * Retrieves job JCL from INPUT, RUNNING, OUTPUT or ROUTING queue.
	 * 
	 * @param job
	 *            job used to extract JCl
	 * @param queueName
	 *            map where job is
	 * @return JCL content
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public String getJcl(Job job, String queueName) throws ServiceMessageException {
		// release ONLY jobs in input, output, running and routing
		if (!queueName.equalsIgnoreCase(Queues.INPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.OUTPUT_QUEUE) && !queueName.equalsIgnoreCase(Queues.RUNNING_QUEUE) && !queueName.equalsIgnoreCase(Queues.ROUTING_QUEUE)) {
			throw new ServiceMessageException(UserInterfaceMessage.JEMG025E, queueName);
		}
		// builds permission
		String permission = Permissions.SEARCH_JOBS + job.getName();
		// checks if the user is authorized to get JCL
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));

		IMap<String, Job> queue = getInstance().getMap(queueName);
		Job storedJob = null;
		try {
			// locks the key (job id)
			queue.lock(job.getId());
			// gets JOB
			// for JCL because JCL is not
			// serialized to GWT (too big)
			storedJob = queue.get(job.getId());
		} finally {
			// unlocks always the key
			queue.unlock(job.getId());
		}
		// if stored job is null
		// means the probably the view on UI is old,
		// and the job is no longer on map
		// Constant string is NOT AVAILABLE
		if (storedJob == null) {
			return Jcl.CONTENT_NOT_AVAILABLE;
		}
		// return the JCL
		return storedJob.getJcl().getContent();
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
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public OutputTree getOutputTree(Job job, String queueName) throws ServiceMessageException {
		// builds permission
		String permission = Permissions.SEARCH_JOBS + job.getName();
		// checks if the user is authorized to get folder
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));

		OutputTree tree;
		// release ONLY jobs in output and running
		if (queueName.equalsIgnoreCase(Queues.OUTPUT_QUEUE) || queueName.equalsIgnoreCase(Queues.RUNNING_QUEUE)){
			DistributedTaskExecutor<OutputTree> task = new DistributedTaskExecutor<OutputTree>(new GetOutputTree(job), getMember());
			tree =  task.getResult();
		} else {
			tree = new OutputTree();
		}

		// now try to get JCL
		// because this method is called when you go
		// in inspect in a job
		IMap<String, Job> queue = getInstance().getMap(queueName);
		Job storedJob = null;
		try {
			// locks the key (job id)
			queue.lock(job.getId());
			// gets job
			storedJob = queue.get(job.getId());
		} finally {
			// unlocks always the key
			queue.unlock(job.getId());
		}
		// if there is the job
		// gets JCL content
		if (storedJob != null) {
			tree.setJclContent(storedJob.getJcl().getContent());
		} else {
			// otherwise NOT available
			tree.setJclContent(Jcl.CONTENT_NOT_AVAILABLE);
		}
		return tree;
	}

	/**
	 * Returns the content of specific file inside of job output folder.
	 * 
	 * @param job
	 *            job used to extract file. Needs to have output folder
	 * @param item
	 *            file descriptor, created by a previous call to getOutputTree
	 * @return object with file content
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public OutputFileContent getOutputFileContent(Job job, OutputListItem item) throws ServiceMessageException  {
		// builds permission
		String permission = Permissions.SEARCH_JOBS + job.getName();
		// checks if the user is authorized to get file
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));
	
		DistributedTaskExecutor<OutputFileContent> task = new DistributedTaskExecutor<OutputFileContent>(new GetOutputFileContent(item), getMember());
		return task.getResult();
	}

	/**
	 * Submits a new job (passed as argument) in JEM.
	 * 
	 * @param preJob
	 *            job to submit
	 * @return Job id after submission
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public String submit(PreJob preJob) throws ServiceMessageException {
		// checks if the user is authorized to submit jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.JOBS_SUBMIT));

		// gets job
		Job job = preJob.getJob();

		// gets userid and org unit from
		// current session, ovverriding JOB values
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getPrincipal();
		if (obj instanceof User) {
			User user = (User) obj;
			job.setUser(user.getId());
			job.setOrgUnit(user.getOrgUnitId());
		}

		// gets a new ID to create a unique job id
		IdGenerator generator = getInstance().getIdGenerator(Queues.JOB_ID_GENERATOR);
		long id = generator.newId();

		// creates job id and sets it
		String jobId = Factory.createJobId(job, id);
		job.setId(jobId);

		// puts the pre job in a queue for validating
		IQueue<PreJob> jclCheckingQueue = getInstance().getQueue(Queues.JCL_CHECKING_QUEUE);
		try {
			jclCheckingQueue.put(preJob);
		} catch (InterruptedException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.JCL_CHECKING_QUEUE);
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.JCL_CHECKING_QUEUE);
		}
		return jobId;
	}
	
	/**
	 * Indents JCL content, for editing
	 * @param content JCL
	 * @return indented JCL
	 * @throws ServiceMessageException 
	 * @throws Exception if any XML exception occurs
	 */
    public String indent(String content) throws ServiceMessageException{
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(new ReaderInputStream(new StringReader(content)));
	        OutputFormat format = new OutputFormat(document);
	        format.setLineWidth(65);
	        format.setIndenting(true);
	        format.setIndent(4);

	        StringWriter writer = new StringWriter();
	        XMLSerializer serializer = new XMLSerializer(writer, format);
	        serializer.serialize(document);
	        return writer.toString();
        } catch (ParserConfigurationException e) {
        	throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, e.getMessage());
        } catch (SAXException e) {
        	throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, e.getMessage());
        } catch (IOException e) {
        	throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, e.getMessage());
        }
	}

	/**
	 * Returns system information about resource consumption of job in running
	 * phase.
	 * 
	 * @param job
	 *            job to use to gather system information
	 * @return system activity information
	 * @throws ServiceMessageException 
	 *             if any exception occurs
	 */
	public JobSystemActivity getJobSystemActivity(Job job) throws ServiceMessageException{
		// checks if the user is authorized to search job
		// if not, this method throws an exception
		String permission = Permissions.SEARCH_JOBS + job.getName();
		checkAuthorization(new StringPermission(permission));
		
		Job jobToSearch = job;
		String nodeKey = job.getMemberId();
		if (nodeKey == null || job.getProcessId() == null){
			if (job.getId() == null){
				return null;
			} else {
				jobToSearch = getJobById(Queues.RUNNING_QUEUE, job.getId());
				if (jobToSearch == null){
					return null;
				}
				nodeKey = jobToSearch.getMemberId();
			}
		}
		DistributedTaskExecutor<JobSystemActivity> task = new DistributedTaskExecutor<JobSystemActivity>(new GetJobSystemActivity(jobToSearch), getMember(nodeKey));
		return task.getResult();

	}
	
	/**
	 * Returns the list of JCL types and their description
	 * 
	 * @return map with all jcl types
	 * @throws ServiceMessageException 
	 * @throws Exception
	 *             if any exception occurs
	 */
	public Map<String, String> getJclTypes() throws ServiceMessageException {
		// checks if the user is authorized to submit jobs
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.JOBS_SUBMIT));
		DistributedTaskExecutor<Map<String, String>> task = new DistributedTaskExecutor<Map<String, String>>(new GetJclTypes(), getMember());
		return task.getResult();
	}

}