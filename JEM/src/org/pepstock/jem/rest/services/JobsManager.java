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
package org.pepstock.jem.rest.services;

import javax.xml.bind.JAXBElement;

import org.pepstock.jem.Job;
import org.pepstock.jem.JobStatus;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.JclContent;
import org.pepstock.jem.rest.entities.JobId;
import org.pepstock.jem.rest.entities.JobOutputFileContent;
import org.pepstock.jem.rest.entities.JobOutputListArgument;
import org.pepstock.jem.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.rest.entities.JobStatusContent;
import org.pepstock.jem.rest.entities.JobSystemActivityContent;
import org.pepstock.jem.rest.entities.Jobs;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.paths.JobsManagerPaths;

import com.sun.jersey.api.client.GenericType;

/**
 * Client side of JOBS service.
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
		super(restClient);
	}

	/**
	 * Returns a list of jobs in input queue
	 * @param jobNameFilter job name filter
	 * @return list of jobs
	 * @throws JemException if any exception occurs
	 */
	public Jobs getInputQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerPaths.INPUT, jobNameFilter);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Jobs getRunningQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerPaths.RUNNING, jobNameFilter);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Jobs getOutputQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerPaths.OUTPUT, jobNameFilter);
	}

	/**
	 * 
	 * @param jobNameFilter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Jobs getRoutingQueue(String jobNameFilter) throws JemException {
		return getJobs(JobsManagerPaths.ROUTING, jobNameFilter);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException if any exception occurs
	 */
	public void hold(Jobs jobs) throws JemException {
		doAction(JobsManagerPaths.HOLD, jobs);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException if any exception occurs
	 */
	public void release(Jobs jobs) throws JemException {
		doAction(JobsManagerPaths.RELEASE, jobs);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException if any exception occurs
	 */
	public void cancel(Jobs jobs) throws JemException {
		doAction(JobsManagerPaths.CANCEL, jobs);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException if any exception occurs
	 */
	public void purge(Jobs jobs) throws JemException {
		doAction(JobsManagerPaths.PURGE, jobs);
	}

	/**
	 * 
	 * @param jobs
	 * @throws JemException if any exception occurs
	 */
	public void update(Jobs jobs) throws JemException {
		doAction(JobsManagerPaths.UPDATE, jobs);
	}

	/**
	 * 
	 * @param preJob
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public String submit(PreJob preJob) throws JemException {
		GenericType<JAXBElement<JobId>> generic = new GenericType<JAXBElement<JobId>>() {

		};
		JobsPostService<JobId, PreJob> service = new JobsPostService<JobId, PreJob>(JobsManagerPaths.SUBMIT);

		JobId jobid = service.execute(generic, preJob);
		if (jobid == null){
			return null;
		} else {
			return jobid.getId();
		}
	}

	/**
	 * @param jobs
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public JclContent getJcl(Jobs jobs) throws JemException {
		GenericType<JAXBElement<JclContent>> generic = new GenericType<JAXBElement<JclContent>>() {

		};
		JobsPostService<JclContent, Jobs> service = new JobsPostService<JclContent, Jobs>(JobsManagerPaths.JCL_CONTENT);
		return service.execute(generic, jobs);
	}

	/**
	 * @param jobs
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public JobOutputTreeContent getOutputTree(Jobs jobs) throws JemException {
		JobsPostService<JobOutputTreeContent, Jobs> service = new JobsPostService<JobOutputTreeContent, Jobs>(JobsManagerPaths.OUTPUT_TREE);
		GenericType<JAXBElement<JobOutputTreeContent>> generic = new GenericType<JAXBElement<JobOutputTreeContent>>() {

		};
		return service.execute(generic, jobs);
	}

	/**
	 * @param jobFileContent
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public OutputFileContent getOutputFileContent(JobOutputListArgument jobFileContent) throws JemException {
		GenericType<JAXBElement<JobOutputFileContent>> generic = new GenericType<JAXBElement<JobOutputFileContent>>() {

		};
		JobsPostService<JobOutputFileContent, JobOutputListArgument> service = new JobsPostService<JobOutputFileContent, JobOutputListArgument>(JobsManagerPaths.OUTPUT_FILE_CONTENT);
		JobOutputFileContent ofc = service.execute(generic, jobFileContent);
		
		if (ofc == null){
			return null;
		} else {
			return ofc.getOutputFileContent();
		}
	}

	/**
	 * @param jobFileContent
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public JobStatus getJobStatus(String filter) throws JemException {
		GenericType<JAXBElement<JobStatusContent>> generic = new GenericType<JAXBElement<JobStatusContent>>() {

		};
		JobsPostService<JobStatusContent, String> service = new JobsPostService<JobStatusContent, String>(JobsManagerPaths.JOB_STATUS);
		JobStatusContent sc = service.execute(generic, filter);
		
		if (sc == null){
			return null;
		} else {
			return sc.getJobStatus();
		}
	}
	
	/**
	 * 
	 * @param jobId
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Job getJobById(JobId jobId) throws JemException {
		JobsPostService<Jobs, JobId> service = new JobsPostService<Jobs, JobId>(JobsManagerPaths.JOB_BY_ID);
		GenericType<JAXBElement<Jobs>> generic = new GenericType<JAXBElement<Jobs>>() {

		};
		Jobs jobs = service.execute(generic, jobId);
		if (jobs == null || jobs.getJobs() == null || jobs.getJobs().isEmpty()){
			return null;
		} else {
			return jobs.getJobs().iterator().next();
		}		
	}
	
	/**
	 *  
	 * @param jobId
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Job getEndedJobById(String jobId) throws JemException {
		JobsPostService<Jobs, String> service = new JobsPostService<Jobs, String>(JobsManagerPaths.ENDED_JOB_BY_ID);
		GenericType<JAXBElement<Jobs>> generic = new GenericType<JAXBElement<Jobs>>() {

		};
		Jobs jobs = service.execute(generic, jobId);
		if (jobs == null || jobs.getJobs() == null || jobs.getJobs().isEmpty()){
			return null;
		} else {
			return jobs.getJobs().iterator().next();
		}
	}
	
	/**
	 * 
	 * @param jobs
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public JobSystemActivity getJobSystemActivity(Jobs jobs) throws JemException {
		GenericType<JAXBElement<JobSystemActivityContent>> generic = new GenericType<JAXBElement<JobSystemActivityContent>>() {

		};
		JobsPostService<JobSystemActivityContent, Jobs> service = new JobsPostService<JobSystemActivityContent, Jobs>(JobsManagerPaths.JOB_SYSTEM_ACTIVITY);
		JobSystemActivityContent sa = service.execute(generic, jobs);
		
		if (sa == null){
			return null;
		} else {
			return sa.getJobSystemActivity();
		}
	}
	/**
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	private Jobs getJobs(String method, String filter) throws JemException {
		JobsPostService<Jobs, String> service = new JobsPostService<Jobs, String>(method);
		GenericType<JAXBElement<Jobs>> generic = new GenericType<JAXBElement<Jobs>>() {

		};
		return service.execute(generic, filter);
	}

	/**
	 * 
	 * @param method
	 * @param jobs
	 * @throws JemException if any exception occurs
	 */
	private void doAction(String method, Jobs jobs) throws JemException {
		JobsPostService<ReturnedObject, Jobs> service = new JobsPostService<ReturnedObject, Jobs>(method);
		GenericType<JAXBElement<ReturnedObject>> generic = new GenericType<JAXBElement<ReturnedObject>>() {

		};
		service.execute(generic, jobs);
	}
	
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class JobsPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public JobsPostService(String subService) {
			super(JobsManager.this.getClient(), JobsManagerPaths.MAIN, subService);
		}

	}

}