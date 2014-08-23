package org.pepstock.jem.junit.test.rest;

import java.io.File;

import junit.framework.TestCase;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.rest.entities.JclContent;
import org.pepstock.jem.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.rest.entities.Jobs;
import org.pepstock.jem.rest.services.JobsManager;

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
		PreJob prejob = RestManager.getSharedInstance().createJob(jcl, "ant");
		// get jobid
		String jobId = RestManager.getSharedInstance().getJobManager()
				.submit(prejob);
		Jobs jobs = verifyInputQueue(jobId);
		// release job
		RestManager.getSharedInstance().getJobManager().release(jobs);
		verifyRunningQueue(jobId);
		jobs=verifyOutputQueue(jobId);
		// verify jcl content
		JclContent jclContent=RestManager.getSharedInstance().getJobManager().getJcl(jobs);
		assertTrue(jclContent.getContent().contains("TEST_REST_WAIT"));
		// verify output tree
		JobOutputTreeContent outputTree=RestManager.getSharedInstance().getJobManager().getOutputTree(jobs);
		assertTrue(outputTree!=null);
	}

	/**
	 * Verify that the job is in the input queue
	 * 
	 * @param jobId
	 * @return
	 */
	private Jobs verifyInputQueue(String jobId) throws Exception {
		// verify input queue (is on hold so I will find it for shure)
		Jobs jobs = null;
		while (true) {
			// verify if is finished that is if it is in the output queue
			jobs = RestManager.getSharedInstance().getJobManager()
					.getInputQueue(jobId);
			if (jobs != null && jobs.getJobs() != null
					&& !jobs.getJobs().isEmpty()) {
				Job job = jobs.getJobs().iterator().next();
				assertEquals(job.getId(), jobId);
				assertTrue(job.getJcl().isHold());
				break;
			}
			Thread.sleep(500);
		}
		return jobs;
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
			Jobs jobs = RestManager.getSharedInstance().getJobManager()
					.getRunningQueue(jobId);
			if (jobs != null && jobs.getJobs() != null
					&& !jobs.getJobs().isEmpty()) {
				Job job = jobs.getJobs().iterator().next();
				assertEquals(job.getId(), jobId);
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
	private Jobs verifyOutputQueue(String jobId) throws Exception {
		// is a wait job that wait for 30 second so I should find it in the
		// running queue verify output
		Jobs jobs = null;
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			jobs = RestManager.getSharedInstance().getJobManager()
					.getOutputQueue(jobId);
			if (jobs != null && jobs.getJobs() != null
					&& !jobs.getJobs().isEmpty()) {
				Job job = jobs.getJobs().iterator().next();
				assertEquals(job.getId(), jobId);
				break;
			}
		}
		return jobs;
	}

	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
