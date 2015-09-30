/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.event.EnvironmentEventListener;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestClientFactory;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.entities.JobQueue;
import org.pepstock.jem.rest.services.GfsManager;
import org.pepstock.jem.rest.services.JobsManager;
import org.pepstock.jem.rest.services.LoginManager;
import org.pepstock.jem.rest.services.UploadListener;

/**
 * Contains all necessary references to maintain the connection with JEm (by
 * REST) and exposes all methods to get all information.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
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
	 * @throws RestException 
	 */
	public void login(Coordinate coordinate) throws RestException {
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
	 * Gets all jobs into a passed queue
	 * @param queue queue where perform the query
	 * @param filter
	 *            filter to use
	 * @return a list of jobs
	 * @throws RestException 
	 *             if any exception occurs
	 */
	public Collection<Job> refreshJobs(JobQueue queue, String filter) throws RestException {
		Collection<Job> jobs = jobsManager.getJobs(queue, filter);
		return (jobs != null) ? jobs : new ArrayList<Job>();
	}


	/**
	 * Submits a file as JOB. BE aware that from plugin the JCL type is not set.
	 * JEM node will do that.
	 * 
	 * @param jcl
	 *            file with a JCL
	 * @return JOB ID after submit
	 * @throws RestException
	 *             if any exception occurs
	 */
	public String submit(File jcl) throws RestException  {
		try {
			// submits job and returns JOB ID
			return jobsManager.submit(FileUtils.readFileToString(jcl));
		} catch (IOException e) {
			throw new RestException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}
	}
	
	/**
	 * Returns the JCL content of JOB
	 * @param job job instance used to get JCL content
	 * @param queue where to get JCL, queue name
	 * @return jcl content in string format
	 * @throws RestException if any exception occurs
	 */
	public String getJcl(Job job, JobQueue queue) throws RestException {
		return jobsManager.getJcl(job.getId(), queue);
	}

	/**
	 * Returns the output file system tree of JOB
	 * @param job job instance 
	 * @param queue where to get info, queue name
	 * @return output tree object
	 * @throws RestException if any exception occurs
	 */
	public OutputTree getOutputTree(Job job, JobQueue queue) throws RestException {
		return jobsManager.getOutputTree(job.getId(), queue);
	}

	/**
	 * Returns the content file from output folder in string format
	 * @param job Job used to search file content
	 * @param queue where to get info, queue name
	 * @param item item to search
	 * @return output file content
	 * @throws RestException if any exception occurs
	 */
	public String getOutputFileContent(Job job, JobQueue queue, OutputListItem item) throws RestException {
		return jobsManager.getOutputFileContent(job.getId(), queue, item);
	}

	/**
	 * Returns a file from GFS 
	 * @param type GFS type
	 * @param path relative path of file
	 * @param pathName data path name
	 * @return file content in string format
	 * @throws RestException if any exception occurs
	 */
	public byte[] getGfsFile(int type, String path, String pathName) throws RestException {
		return gfsManager.getFile(GfsFileType.getName(type), path, pathName);
	}

	/**
	 * Returns the list of files from GFS 
	 * @param type GFS type
	 * @param path path relative path of folder
	 * @param pathName data path name
	 * @return list of files of GFS
	 * @throws RestException if any exception occurs
	 */
	public Collection<GfsFile> getGfsFileList(int type, String path, String pathName) throws RestException {
		return gfsManager.getFilesList(GfsFileType.getName(type), path, pathName);
	}
	
	/**
	 * Upload a file to GFS
	 * @param file file coordinates necessary to upload file
	 * @return REST status
	 * @throws RestException if any exception occurs
	 */
	public boolean upload(UploadedGfsFile file) throws RestException {
		return gfsManager.putFile(file);
	}
	
	/**
	 * Deletes a file from GFS 
	 * @param type GFS type
	 * @param path relative path of file
	 * @param pathName data path name
	 * @return file content in string format
	 * @throws RestException if any exception occurs
	 */
	public void delete(int type, String path, String pathName) throws RestException {
		gfsManager.delete(GfsFileType.getName(type), path, pathName);
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
