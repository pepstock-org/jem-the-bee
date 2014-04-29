package org.pepstock.jem.junit.test.rest;

import java.io.File;

import junit.framework.TestCase;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.gwt.client.rest.GfsManager;
import org.pepstock.jem.gwt.server.rest.entities.GfsFileList;
import org.pepstock.jem.gwt.server.rest.entities.GfsRequest;
import org.pepstock.jem.gwt.server.rest.entities.Jobs;

public class GfsManagerTest extends TestCase {

	/**
	 * This class will test the following:
	 * <ul>
	 * <li>{@link GfsManager#getFileData(GfsRequest)}</li>
	 * <li>{@link GfsManager#getFilesListData(GfsRequest)}</li>
	 * </ul>
	 * 
	 * 
	 * @throws Exception
	 */
	public void testGfsManager() throws Exception {
		loadData();
		GfsRequest gfsRequest = new GfsRequest();
		gfsRequest.setItem("test_rest/Data1");
		String resposnse=RestManager.getSharedInstance().getGfsManager().getFileData(gfsRequest);
		assertNotNull(resposnse);
		gfsRequest.setItem("test_rest/");
		GfsFileList list=RestManager.getSharedInstance().getGfsManager().getFilesListData(gfsRequest);
		assertNotNull(list);
	}

	/**
	 * Load data for testing {@link GfsManager}
	 * 
	 * @throws Exception
	 */
	private void loadData() throws Exception{
		File jcl=getJcl("TEST_REST_LOAD_DATA.xml");
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
	
	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
