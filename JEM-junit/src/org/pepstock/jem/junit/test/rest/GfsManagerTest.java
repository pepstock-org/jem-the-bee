package org.pepstock.jem.junit.test.rest;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJcl;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.rest.entities.GfsRequest;
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
	public void testGfsUpload() throws Exception {
	}

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
		gfsRequest.setType(GfsFileType.DATA);
		String resposnse=RestManager.getSharedInstance().getGfsManager().getFile(gfsRequest);
		assertNotNull(resposnse);
		gfsRequest.setItem("test_rest/");
		gfsRequest.setType(GfsFileType.DATA);
		Collection<GfsFile> list=RestManager.getSharedInstance().getGfsManager().getFilesList(gfsRequest);
		assertNotNull(list);
	}

	/**
	 * Load data for testing {@link GfsManager}
	 * 
	 * @throws Exception
	 */
	private void loadData() throws Exception{
		File jcl=getJcl("TEST_REST_LOAD_DATA.xml");
		PreJcl prejob=RestManager.getSharedInstance().createJcl(jcl, "ant");
		// get jobid
		String jobId=RestManager.getSharedInstance().getJobManager().submit(prejob);
		// verify output
		while (true) {
			Thread.sleep(500);
			// verify if is finished that is if it is in the output queue
			Job job = RestManager.getSharedInstance().getJobManager().getJobById(jobId, JobQueue.OUTPUT);
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
