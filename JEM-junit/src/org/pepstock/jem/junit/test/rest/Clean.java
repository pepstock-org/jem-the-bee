package org.pepstock.jem.junit.test.rest;

import java.io.File;

import junit.framework.TestCase;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.ant.AntFactory;
import org.pepstock.jem.rest.entities.Jobs;
/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Clean extends TestCase {

	/**
	 * Clean jobs
	 * 
	 * @throws Exception
	 */
	public void testCleanJobs() throws Exception {
		File jcl=getJcl("clean/TEST_REST_CLEAN_JOBS.xml");
		PreJob prejob=RestManager.getSharedInstance().createJob(jcl, "ant");
		// get jobid
		String jobId=RestManager.getSharedInstance().getJobManager().submit(prejob);
		// verify output
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Jobs jobs=RestManager.getSharedInstance().getJobManager().getOutputQueue(jobId);
			if(jobs!=null && jobs.getJobs()!=null && !jobs.getJobs().isEmpty()){
				Job job=jobs.getJobs().iterator().next();
				assertEquals(job.getResult().getReturnCode(), 0);
				break;
			}
		}
	}

	/**
	 * Clean data
	 * 
	 * @throws Exception
	 */
	public void testCleanData() throws Exception {
		File jcl=getJcl("clean/TEST_REST_DELETE_DATA.xml");
		PreJob prejob=RestManager.getSharedInstance().createJob(jcl, AntFactory.ANT_TYPE);
		// get jobid
		String jobId=RestManager.getSharedInstance().getJobManager().submit(prejob);
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Jobs jobs=RestManager.getSharedInstance().getJobManager().getOutputQueue(jobId);
			if(jobs!=null && jobs.getJobs()!=null && !jobs.getJobs().isEmpty()){
				Job job=jobs.getJobs().iterator().next();
				assertEquals(job.getResult().getReturnCode(), 0);
				break;
			}
		}
	}

	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
