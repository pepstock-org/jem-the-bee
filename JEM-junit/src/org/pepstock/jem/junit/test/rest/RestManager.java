package org.pepstock.jem.junit.test.rest;

import java.io.File;
import java.net.URL;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.gwt.client.rest.GfsManager;
import org.pepstock.jem.gwt.client.rest.JobsManager;
import org.pepstock.jem.gwt.client.rest.LoginManager;
import org.pepstock.jem.gwt.client.security.LoggedUser;
import org.pepstock.jem.gwt.server.rest.entities.Account;
import org.pepstock.jem.junit.submitter.JemTestManager;
import org.pepstock.jem.junit.submitter.RestConf;
import org.pepstock.jem.util.MultiRestClient;
import org.pepstock.jem.util.RestClient;

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
			account.setUserid(conf.getUser());
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
	public PreJob createJob(File jcl, String jobType) throws Exception{
		URL urlJcl = new URL("file:"+jcl.getAbsolutePath());
		PreJob preJob = Factory.createPreJob(urlJcl);
		preJob.setJclType(jobType);
		Job job = new Job();
		job.setUser(user);
		preJob.setJob(job);
		return preJob;
	}

}
