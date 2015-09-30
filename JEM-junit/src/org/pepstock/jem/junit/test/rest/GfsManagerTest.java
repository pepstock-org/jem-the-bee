package org.pepstock.jem.junit.test.rest;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.ant.AntFactory;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.JobQueue;
import org.pepstock.jem.rest.services.GfsManager;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class GfsManagerTest extends TestCase {

	/**
	 * 
	 * @throws Exception
	 */
	public void testFiles() throws Exception {
		File file = getJcl("TEST_REST_LOAD_DATA.xml");
      	boolean uploaded = RestManager.getInstance().getGfsManager().putFile(GfsFileType.SOURCE_NAME, "/test_rest/", file);
		assertEquals(uploaded, true);
		byte[] response = RestManager.getInstance().getGfsManager().getFile(GfsFileType.SOURCE_NAME, "/test_rest/TEST_REST_LOAD_DATA.xml", null);
		assertNotNull(response);
		Collection<GfsFile> all = RestManager.getInstance().getGfsManager().getFilesList(GfsFileType.SOURCE_NAME, "/test_rest/", null);
		if (all == null || all.isEmpty()){
			throw new Exception("Directory is null or empty");
		}
		boolean deleted = RestManager.getInstance().getGfsManager().delete(GfsFileType.SOURCE_NAME, "/test_rest/TEST_REST_LOAD_DATA.xml", null);
		assertEquals(deleted, true);
		try {
	        byte[] response1 = RestManager.getInstance().getGfsManager().getFile(GfsFileType.SOURCE_NAME, "/test_rest/TEST_REST_LOAD_DATA.xml", null);
	        throw new Exception("Unable to delete /test_rest/TEST_REST_LOAD_DATA.xml");
        } catch (RestException e) {
        }
	}

	/**
	 * Load data for testing {@link GfsManager}
	 * 
	 * @throws Exception
	 */
	private void loadData() throws Exception{
		File jcl=getJcl("TEST_REST_LOAD_DATA.xml");
		// get jobid
		String jobId=RestManager.getInstance().getJobManager().submit(FileUtils.readFileToString(jcl), AntFactory.ANT_TYPE);
		// verify output
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Job job = RestManager.getInstance().getJobManager().getJobById(jobId, JobQueue.OUTPUT);
			if(job!=null){
				assertEquals(job.getResult().getReturnCode(), 0);
				break;
			}
		}		
	}
	
	private File getJcl(String name) {
		return new File(this.getClass().getResource("jcls/" + name).getFile());
	}
}
