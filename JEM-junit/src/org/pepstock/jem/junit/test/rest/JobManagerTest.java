package org.pepstock.jem.junit.test.rest;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.ant.AntFactory;
import org.pepstock.jem.rest.entities.JobQueue;
import org.pepstock.jem.rest.services.JobsManager;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JobManagerTest extends TestCase {

	/**
	 * This class will test the following:
	 * <ul>
	 * <li>{@link JobsManager#submit(PreJob)}</li>
	 * <li>{@link JobsManager#release(Jobs)}</li>
	 * <li>{@link JobsManager#getJcl(Jobs)}</li>
	 * <li>{@link JobsManager#getOutputTree(Jobs)}</li>
	 * <li>{@link JobsManager#getInputQueue(String)}</li>
	 * <li>{@link JobsManager#getOutputQueue(String)}</li>
	 * <li>{@link JobsManager#getRunningQueue(String)}</li>
	 * </ul>
	 * 
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		File jcl = getJcl("TEST_REST_WAIT.xml");
		// get jobid
		String jobId = RestManager.getInstance().getJobManager().submit(FileUtils.readFileToString(jcl), AntFactory.ANT_TYPE);
		Job job = verifyInputQueue(jobId);
		// release job
		RestManager.getInstance().getJobManager().release(job.getId(), JobQueue.INPUT);
		verifyRunningQueue(jobId);
		job = verifyOutputQueue(jobId);

		// verify jcl content
		String jclContent = RestManager.getInstance().getJobManager().getJcl(job.getId(), JobQueue.OUTPUT);
		System.out.println(jclContent);
		assertTrue(jclContent.contains("TEST_REST_WAIT"));
		// verify output tree
		OutputTree outputTree=RestManager.getInstance().getJobManager().getOutputTree(job.getId(), JobQueue.OUTPUT);
		assertTrue(outputTree!=null);
		
		Collection<String> list = RestManager.getInstance().getJobManager().getJclTypes();
		assertNotNull(list);
		assertTrue(list.contains(AntFactory.ANT_TYPE));
	}
	
	/**
	 * Verify that the job is in the input queue
	 * 
	 * @param jobId
	 * @return
	 */
	private Job verifyInputQueue(String jobId) throws Exception {
		// verify input queue (is on hold so I will find it for shure)
		Job job = null;
		while (true) {
			// verify if is finished that is if it is in the output queue
			job = RestManager.getInstance().getJobManager().getJobById(jobId, JobQueue.INPUT);
			if (job != null) {
				assertEquals(job.getId(), jobId);
				assertTrue(job.getJcl().isHold());
				break;
			}
			Thread.sleep(500);
		}
		return job;
	}

	/**
	 * Verify that the job is in the running queue
	 * 
	 * @param jobId
	 * @throws Exception
	 */
	private void verifyRunningQueue(String jobId) throws Exception {
		// is a wait job that wait for 30 second so I should find it in the
		// running queue verify output
		while (true) {
			// verify if is finished that is if it is in the output queue
			Job job = RestManager.getInstance().getJobManager().getJobById(jobId, JobQueue.RUNNING);
			if (job != null) {
				assertEquals(job.getId(), jobId);
				assertNotNull(RestManager.getInstance().getJobManager().getJobSystemActivity(job.getId()));
				break;
			}
			Thread.sleep(500);
		}
	}

	/**
	 * Verify that the job is in the output queue
	 * 
	 * @param jobId
	 * @throws Exception
	 */
	private Job verifyOutputQueue(String jobId) throws Exception {
		// is a wait job that wait for 30 second so I should find it in the
		// running queue verify output
		Job job = null;
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			job = RestManager.getInstance().getJobManager().getJobById(jobId, JobQueue.OUTPUT);
			if (job != null) {
				assertEquals(job.getId(), jobId);
				break;
			}
		}
		return job;
	}

	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
