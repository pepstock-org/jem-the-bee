package org.pepstock.jem.junit.test.rest;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;
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
		String jobId = RestManager.getInstance().getJobManager().submit(FileUtils.readFileToString(jcl), AntFactory.ANT_TYPE);
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
		// get jobid
		String jobId=RestManager.getInstance().getJobManager().submit(FileUtils.readFileToString(jcl), AntFactory.ANT_TYPE);
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
