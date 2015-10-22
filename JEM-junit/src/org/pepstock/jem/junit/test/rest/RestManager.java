package org.pepstock.jem.junit.test.rest;

import org.pepstock.jem.junit.init.JemTestManager;
import org.pepstock.jem.junit.init.RestConf;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.rest.HTTPBaseAuthRestClient;
import org.pepstock.jem.rest.MultiRestClient;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.SingleRestClient;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.services.CertificatesManager;
import org.pepstock.jem.rest.services.GfsManager;
import org.pepstock.jem.rest.services.JobsManager;
import org.pepstock.jem.rest.services.LoginManager;
import org.pepstock.jem.rest.services.NodesManager;
import org.pepstock.jem.rest.services.StatisticsManager;
import org.pepstock.jem.rest.services.SwarmNodesManager;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RestManager{
	
	private LoginManager loginManager;

	private JobsManager jobManager;
	
	private GfsManager gfsManager;
	
	private NodesManager nodesManager;
	
	private CertificatesManager certificatesManager;
	
	private StatisticsManager statisticsManager;
	
	private SwarmNodesManager swarmNodesManager;
	
	private RestClient restClient;
	
	private String user;
	
	private static RestManager INSTANCE = null; 
	
	public static RestManager getInstance() throws Exception{
		if (INSTANCE == null){
			INSTANCE = new RestManager(MultiRestClient.class);
		}
		return INSTANCE;
	}
	
	
	/**
	 * @throws Exception 
	 * 
	 */
	private RestManager(Class<?> client) throws Exception{
		RestConf conf=JemTestManager.getSharedInstance().getRestConf();
		if(conf==null){
			throw new Exception("Rest configuration is null. Check Configuration.xml file !");
		}
		user = conf.getUser();
		
		if (MultiRestClient.class.equals(client)){
			restClient = new MultiRestClient(conf.getUrl());
		} else if (SingleRestClient.class.equals(client)){
			restClient = new SingleRestClient(conf.getUrl());
		} else if (HTTPBaseAuthRestClient.class.equals(client)){
			restClient = new HTTPBaseAuthRestClient(conf.getUrl(), conf.getUser(), conf.getPassword());
		}
		loginManager = new LoginManager(restClient);
		gfsManager=new GfsManager(restClient);
		jobManager=new JobsManager(restClient);
		nodesManager = new NodesManager(restClient);
		certificatesManager = new CertificatesManager(restClient);
		statisticsManager = new StatisticsManager(restClient);
		swarmNodesManager = new SwarmNodesManager(restClient);
		login(conf);
	}
	
	private void login(RestConf conf) throws RestException{
		LoggedUser user = loginManager.getUser();
		if (user == null) {
			// Creo l'oggetto account con uid e pwd
			Account account = new Account();
			account.setUserid(conf.getUser());
			account.setPassword(conf.getPassword());
			// faccio login e mi salvo logged user
			user = loginManager.login(account);
		}
		System.out.println("User "+user+" has logged in succesfully.");
	}

	
	
	/**
	 * @return the loginManager
	 */
	public LoginManager getLoginManager() {
		return loginManager;
	}

	/**
	 * @return the jobManager
	 */
	public JobsManager getJobManager() {
		return jobManager;
	}

	/**
	 * @return the gfsManager
	 */
	public GfsManager getGfsManager() {
		return gfsManager;
	}

	/**
	 * @return the nodesManager
	 */
	public NodesManager getNodesManager() {
		return nodesManager;
	}

	/**
	 * @return the certificatesManager
	 */
	public CertificatesManager getCertificatesManager() {
		return certificatesManager;
	}

	/**
	 * @return the statisticsManager
	 */
	public StatisticsManager getStatisticsManager() {
		return statisticsManager;
	}

	/**
	 * @return the swarmNodesManager
	 */
	public SwarmNodesManager getSwarmNodesManager() {
		return swarmNodesManager;
	}
}
