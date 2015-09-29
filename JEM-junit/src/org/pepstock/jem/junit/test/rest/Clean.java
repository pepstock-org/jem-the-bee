package org.pepstock.jem.junit.test.rest;

import java.io.File;

import junit.framework.TestCase;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJcl;
import org.pepstock.jem.ant.AntFactory;
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
		File jcl= getJcl("clean/TEST_REST_CLEAN_JOBS.xml");
		PreJcl prejob=RestManager.getInstance().createJcl(jcl, "ant");
		// get jobid
		String jobId = RestManager.getInstance().getJobManager().submit(prejob);
		// verify output
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Job job = RestManager.getInstance().getJobManager().getEndedJobById(jobId);
			if(job != null){
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
		PreJcl prejob=RestManager.getInstance().createJcl(jcl, AntFactory.ANT_TYPE);
		// get jobid
		String jobId=RestManager.getInstance().getJobManager().submit(prejob);
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Job job = RestManager.getInstance().getJobManager().getEndedJobById(jobId);
			if(job != null){
				assertEquals(job.getResult().getReturnCode(), 0);
				break;
			}
		}
	}

	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
