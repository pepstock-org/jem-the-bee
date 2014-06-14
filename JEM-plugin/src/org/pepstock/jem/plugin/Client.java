/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.gwt.client.rest.GfsManager;
import org.pepstock.jem.gwt.client.rest.JobsManager;
import org.pepstock.jem.gwt.client.rest.LoginManager;
import org.pepstock.jem.gwt.client.rest.UploadListener;
import org.pepstock.jem.gwt.client.security.LoggedUser;
import org.pepstock.jem.gwt.server.rest.entities.Account;
import org.pepstock.jem.gwt.server.rest.entities.GfsRequest;
import org.pepstock.jem.gwt.server.rest.entities.JclContent;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputListArgument;
import org.pepstock.jem.gwt.server.rest.entities.JobOutputTreeContent;
import org.pepstock.jem.gwt.server.rest.entities.Jobs;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.event.EnvironmentEventListener;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.util.RestClient;
import org.pepstock.jem.util.RestClientFactory;

/**
 * Contains all necessary references to maintain the connection with JEm (by
 * REST) and exposes all methods to get all information.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Client {

	// constant of singleton
	private static final Client INSTANCE = new Client();
	
	private static final List<EnvironmentEventListener> ENVIRONMENT_LISTENERS = new ArrayList<EnvironmentEventListener>();

	// rest managers
	private LoginManager loginManager = null;
	private JobsManager jobsManager = null;
	private GfsManager gfsManager = null;

	// current entities
	private LoggedUser user = null;
	private Coordinate current;

	/**
	 * private constructor
	 */
	private Client() {
	}

	
	/**
	 * Returns the client instance inside of plugin
	 * 
	 * @return the client instance inside of plugin
	 */
	public static Client getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the logged user if connected
	 * 
	 * @return the user
	 */
	public LoggedUser getUser() {
		return user;
	}

	/**
	 * Returns the current coordinate used to connect to JEm
	 * 
	 * @return the current coordinate
	 */
	public Coordinate getCurrent() {
		return current;
	}

	/**
	 * Checks if the user is logged
	 * 
	 * @return <code>true</code> if logged
	 */
	public boolean isLogged() {
		return user != null;
	}

	/**
	 * Adds a environment listener
	 * 
	 * @param listener
	 *            listener to be added
	 */
	public void addEnvironmentEventListener(EnvironmentEventListener listener) {
		ENVIRONMENT_LISTENERS.add(listener);
	}

	/**
	 * Removes a environment listener
	 * 
	 * @param listener
	 *            listener to be removed
	 */
	public void removeEnvironmentEventListener(EnvironmentEventListener listener) {
		ENVIRONMENT_LISTENERS.remove(listener);
	}

	/**
	 * Returns the collection of listeners
	 * 
	 * @return the envListeners collection of listener
	 */
	List<EnvironmentEventListener> getEnvListeners() {
		return ENVIRONMENT_LISTENERS;
	}

	/**
	 * Logins to JEM using the coordinates
	 * 
	 * @param coordinate
	 *            coordinate used to connect to JEM
	 * @throws JemException 
	 */
	public void login(Coordinate coordinate) throws JemException {
		// creates a RESTclient, using host and REST context
		RestClient client = RestClientFactory.getClient(coordinate.getHost() + "/" + coordinate.getRestContext());
		// creates managers instance
		loginManager = new LoginManager(client);
		jobsManager = new JobsManager(client);
		gfsManager = new GfsManager(client);

		// gets logged user
		user = loginManager.getUser();
		if (user == null) {
			// creates a new account
			Account account = new Account();
			account.setUserId(coordinate.getUserId());
			account.setPassword(coordinate.getPassword());

			// login to JEM
			user = loginManager.login(account);
			// saves coordinate
			current = coordinate;

			// fires to all listener a new event
			for (EnvironmentEventListener listener : ENVIRONMENT_LISTENERS) {
				listener.environmentConnected(new EnvironmentEvent(this, current));
			}
		}
	}

	/**
	 * Gets all jobs into INPUT queue
	 * 
	 * @param filter
	 *            filter to use
	 * @return a list of jobs
	 * @throws JemException 
	 *             if any exception occurs
	 */
	public Jobs refreshInput(String filter) throws JemException {
		Jobs jobs = jobsManager.getInputQueue(filter);
		return (jobs != null) ? jobs : new Jobs();
	}

	/**
	 * Gets all jobs into OUTPUT queue
	 * 
	 * @param filter
	 *            filter to use
	 * @return a list of jobs
	 * @throws JemException
	 *             if any exception occurs
	 */
	public Jobs refreshOutput(String filter) throws JemException {
		Jobs jobs = jobsManager.getOutputQueue(filter);
		return (jobs != null) ? jobs : new Jobs();
	}

	/**
	 * Gets all jobs into RUNNING queue
	 * 
	 * @param filter
	 *            filter to use
	 * @return a list of jobs
	 * @throws JemException
	 *             if any exception occurs
	 */
	public Jobs refreshRunning(String filter) throws JemException {
		Jobs jobs = jobsManager.getRunningQueue(filter);
		return (jobs != null) ? jobs : new Jobs();
	}

	/**
	 * Gets all jobs into ROUTING queue
	 * 
	 * @param filter
	 *            filter to use
	 * @return a list of jobs
	 * @throws JemException
	 *             if any exception occurs
	 */
	public Jobs refreshRouting(String filter) throws JemException {
		Jobs jobs = jobsManager.getRoutingQueue(filter);
		return (jobs != null) ? jobs : new Jobs();
	}

	/**
	 * Submits a file as JOB. BE aware that from plugin the JCL type is not set.
	 * JEM node will do that.
	 * 
	 * @param jcl
	 *            file with a JCL
	 * @return JOB ID after submit
	 * @throws JemException
	 *             if any exception occurs
	 */
	public String submit(File jcl) throws JemException  {
		try {
			// gets url of file to submit
			URL url = jcl.toURI().toURL();
			// creates a prejob
			PreJob preJob = Factory.createPreJob(url);
			Job job = new Job();
			// BE AWARE: NO JCL type
			job.setUser(user.getId());
			preJob.setJob(job);
			// submits job and returns JOB ID
			return jobsManager.submit(preJob);
		} catch (MalformedURLException e) {
			throw new JemException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new JemException(e.getMessage(), e);
		}
	}
	
	/**
	 * Creates a jobs object used for RESTto getinformation
	 * @param job job instance
	 * @param queueName where to get info, queue name
	 * @return jobs object with job and queue name fields
	 */
	private Jobs getJobs(Job job, String queueName){
		Jobs jobs = new Jobs();
		jobs.setQueueName(queueName);
		List<Job> jobsList = new ArrayList<Job>();
		jobsList.add(job);
		jobs.setJobs(jobsList);
		return jobs;
	}

	/**
	 * Returns the JCL content of JOB
	 * @param job job instance used to get JCL content
	 * @param queueName where to get JCL, queue name
	 * @return jcl content in string format
	 * @throws JemException if any exception occurs
	 */
	public JclContent getJcl(Job job, String queueName) throws JemException {
		return jobsManager.getJcl(getJobs(job, queueName));
	}

	/**
	 * Returns the output file system tree of JOB
	 * @param job job instance 
	 * @param queueName where to get info, queue name
	 * @return output tree object
	 * @throws JemException if any exception occurs
	 */
	public JobOutputTreeContent getOutputTree(Job job, String queueName) throws JemException {
		return jobsManager.getOutputTree(getJobs(job, queueName));
	}

	/**
	 * Returns the content file from output folder in string format
	 * @param jobOutputFileContent parameter with all job information and needed file
	 * @return output file content
	 * @throws JemException if any exception occurs
	 */
	public OutputFileContent getOutputFileContent(JobOutputListArgument jobOutputFileContent) throws JemException {
		return jobsManager.getOutputFileContent(jobOutputFileContent);
	}

	/**
	 * Returns a file from GFS 
	 * @param type GFS type
	 * @param path relative path of file
	 * @param pathName data path name
	 * @return file content in string format
	 * @throws JemException if any exception occurs
	 */
	public String getGfsFile(int type, String path, String pathName) throws JemException {
		return gfsManager.getFile(createGfsRequest(type, path, pathName));
	}

	/**
	 * Returns the list of files from GFS 
	 * @param type GFS type
	 * @param path path relative path of folder
	 * @param pathName data path name
	 * @return list of files of GFS
	 * @throws JemException if any exception occurs
	 */
	public Collection<GfsFile> getGfsFileList(int type, String path, String pathName) throws JemException {
		return gfsManager.getFilesList(createGfsRequest(type, path, pathName)).getGfsFiles();
	}
	
	/**
	 * Upload a file to GFS
	 * @param file file coordinates necessary to upload file
	 * @return REST status
	 * @throws JemException if any exception occurs
	 */
	public int upload(UploadedGfsFile file) throws JemException {
		return gfsManager.upload(file);
	}
	
	/**
	 * Deletes a file from GFS 
	 * @param type GFS type
	 * @param path relative path of file
	 * @param pathName data path name
	 * @return file content in string format
	 * @throws JemException if any exception occurs
	 */
	public void delete(int type, String path, String pathName) throws JemException {
		gfsManager.delete(createGfsRequest(type, path, pathName));
	}
	
	/**
	 * Adds a upload listener. Used for upload
	 * 
	 * @param listener upload listeners
	 */
	public void addUploadListener(UploadListener listener){
		gfsManager.addUploadListener(listener);
	}
	
	/**
	 * Removes a upload listener. Used for upload
	 * 
	 * @param listener upload listeners
	 */
	public void removeUploadListener(UploadListener listener){
		gfsManager.removeUploadListener(listener);
	}
	
	/**
	 * Returns a gfs request, with path or file and data path name, if exist 
	 * @param path path relative path of folder
	 * @param pathName data path name
	 * @return a gfs request
	 */
	private GfsRequest createGfsRequest(int type, String path, String pathName){
		GfsRequest request = new GfsRequest();
		request.setItem(path);
		request.setPathName(pathName);
		request.setType(type);
		return request;
	}

	/**
	 * Logoff from JEM, remaining inside of Eclipse
	 * 
	 * @throws JemException if any exception occurs during logoff
	 */
	public void logout() throws JemException {
		logout(false);
	}

	/**
	 * Logoff from JEM, passing if Eclipse is shutting down or not.
	 * 
	 * @param isShuttingDown if <code>true</code>, Eclipse is shutting down
	 * @throws JemException if any exception occurs during logoff
	 */
	public void logout(boolean isShuttingDown) throws JemException {
		// checks if connected
		if (isLogged()) {
			try {
				// logoff!!
				loginManager.logoff();
			} catch (Exception e) {
				// ignore! It must logoff!
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// resets user
			user = null;
			// if Eclipse is shutting down, no event will be fired
			if (!isShuttingDown) {
				// fires the event to all listeners
				for (EnvironmentEventListener listener : ENVIRONMENT_LISTENERS) {
					listener.environmentDisconnected(new EnvironmentEvent(this, current));
				}
			}
		} else {
			// resets user anyway
			user = null;
		}
	}

	/**
	 * Returns <code>true</code> if you are authorized to permission, by the
	 * domain.
	 * 
	 * @param domain
	 *            container of subsets of permission, by category
	 * @param permission
	 *            simple permission
	 * @return <code>true</code> if authorized, otherwise <code>false</code>
	 */
	public boolean isAuthorized(String domain, String permission) {
		Boolean authorized = user.isAuthorized(permission);
		return authorized.booleanValue();
	}
}
