package org.pepstock.jem.junit.test.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.rest.entities.Jobs;
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
		PreJob prejob = RestManager.getSharedInstance().createJob(jcl, "ant");
		// get jobid
		String jobId = RestManager.getSharedInstance().getJobManager()
				.submit(prejob);
		Job job = verifyInputQueue(jobId);
		
		Collection<Job> jobs = new ArrayList<Job>();
		jobs.add(job);
		// release job
		RestManager.getSharedInstance().getJobManager().release(jobs, Queues.INPUT_QUEUE);
		verifyRunningQueue(jobId);
		job = verifyOutputQueue(jobId);

		// verify jcl content
		String jclContent = RestManager.getSharedInstance().getJobManager().getJcl(job, Queues.OUTPUT_QUEUE);
		assertTrue(jclContent.contains("TEST_REST_WAIT"));
		// verify output tree
		JobOutputTreeContent outputTree=RestManager.getSharedInstance().getJobManager().getOutputTree(job, Queues.OUTPUT_QUEUE);
		assertTrue(outputTree!=null);
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
			Jobs jobs = RestManager.getSharedInstance().getJobManager()
					.getInputQueue(jobId);
			if (jobs != null && jobs.getJobs() != null
					&& !jobs.getJobs().isEmpty()) {
				job = jobs.getJobs().iterator().next();
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
	private Job verifyOutputQueue(String jobId) throws Exception {
		// is a wait job that wait for 30 second so I should find it in the
		// running queue verify output
		Job job = null;
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Jobs jobs = RestManager.getSharedInstance().getJobManager()
					.getOutputQueue(jobId);
			if (jobs != null && jobs.getJobs() != null
					&& !jobs.getJobs().isEmpty()) {
				job = jobs.getJobs().iterator().next();
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
