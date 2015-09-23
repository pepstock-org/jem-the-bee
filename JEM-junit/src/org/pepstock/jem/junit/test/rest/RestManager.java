package org.pepstock.jem.junit.test.rest;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.PreJcl;
import org.pepstock.jem.junit.init.JemTestManager;
import org.pepstock.jem.junit.init.RestConf;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.rest.MultiRestClient;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.services.GfsManager;
import org.pepstock.jem.rest.services.JobsManager;
import org.pepstock.jem.rest.services.LoginManager;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RestManager {

	private JobsManager jobManager;
	
	private GfsManager gfsManager;
	
	private RestClient restClient;
	
	private String user;
	
	private static RestManager restManager;
	
	/**
	 * 
	 * @return RestManager for rest call
	 * @throws Exception
	 */
	public static RestManager getSharedInstance() throws Exception {
		if(restManager==null){
			restManager=new RestManager();
		}
		return restManager;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	private RestManager() throws Exception{
		RestConf conf=JemTestManager.getSharedInstance().getRestConf();
		if(conf==null){
			throw new Exception("Rest configuration is null. Check Configuration.xml file !");
		}
		user=conf.getUser();
		restClient = new MultiRestClient(conf.getUrl());
		LoginManager man = new LoginManager(restClient);
		gfsManager=new GfsManager(restClient);
		jobManager=new JobsManager(restClient);
		LoggedUser user = man.getUser();
		if (user == null) {
			// Creo l'oggetto account con uid e pwd
			Account account = new Account();
			account.setUserId(conf.getUser());
			account.setPassword(conf.getPassword());
			// faccio login e mi salvo logged user
			user = man.login(account);
		}
		System.out.println("User "+user+" has logged in succesfully.");
	}

	/**
	 * 
	 * @return job manager for rest call
	 */
	public JobsManager getJobManager() {
		return jobManager;
	}

	/**
	 * 
	 * @return gfs manager for rest call
	 */
	public GfsManager getGfsManager() {
		return gfsManager;
	}

	/**
	 * Create job from jcl
	 * 
	 * @param jcl
	 * @param jobType
	 * @return
	 * @throws Exception
	 */
	public PreJcl createJcl(File jcl, String jobType) throws Exception{
//		URL urlJcl = new URL("file:"+jcl.getAbsolutePath());
//		PreJob preJob = Factory.createPreJob(urlJcl);
		PreJcl preJcl = new PreJcl();
		preJcl.setContent(FileUtils.readFileToString(jcl));
		preJcl.setType(jobType);
//		Job job = new Job();
//		job.setUser(user);
//		preJob.setJob(job);
		return preJcl;
	}

}
