/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.commands;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.Result;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.util.CmdConsole;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.TimeUtils;

/**
 * Extends the command line engine to submit job remotely (usually by HTTP).
 * <br>
 * It contains the necessary parameters for that, as USER, PASSWORD and URL to connect it.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class AbstractRemoteSubmit extends SubmitCommandLine {

	private String user = null;
	
	private String password = null;

	private String host = null;
	
	/**
	 * Creates the submit engine passing the command name
	 * 
	 * @param commandName command name  (necessary on help)
	 */
	public AbstractRemoteSubmit(String commandName) {
		super(commandName);
		// adds arguments
		Map<String, SubmitArgument> arguments = getArguments();
		arguments.put(SubmitParameters.HOST.getName(), SubmitParameters.createArgument(SubmitParameters.HOST, true));
		arguments.put(SubmitParameters.PASSWORD.getName(), SubmitParameters.createArgument(SubmitParameters.PASSWORD));
		arguments.put(SubmitParameters.USER.getName(), SubmitParameters.createArgument(SubmitParameters.USER));
	}
	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Methods called to submit the job and to get job id.
	 * @param preJob PreJob to use to submit job
	 * @return job id 
	 * @throws SubmitException if any exception occurs
	 */
	public abstract String getJobId(PreJob preJob) throws SubmitException;
	
	/**
	 * Methods called to get the result of job execution, by job id.
	 * @param jobId job id used to get job result
	 * @return the result of job execution
	 * @throws SubmitException if any exception occurs
	 */
	public abstract Job getEndedJob(String jobId) throws SubmitException;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.SubmitCommandLine#beforeJobSubmit()
	 */
	@Override
	public void beforeJobSubmit() throws SubmitException{
		super.beforeJobSubmit();
		// gets arguments
		Map<String, SubmitArgument> arguments = getArguments();
		SubmitArgument saHost = arguments.get(SubmitParameters.HOST.getName());
		setHost(saHost.getValue());
		
		// for remote submit, it overrides the default for WAIT! it uses FALSE
		// instead of TRUE
		if (arguments.containsKey(SubmitParameters.WAIT.getName())){
			// gets wait argument
			SubmitArgument sakey = arguments.get(SubmitParameters.WAIT.getName());
			setWait(Parser.parseBoolean(sakey.getValue(), false));	
		} else {
			setWait(false);
		}
		
		// gets user
		if (arguments.containsKey(SubmitParameters.USER.getName())){
			SubmitArgument sa = arguments.get(SubmitParameters.USER.getName());
			setUser(sa.getValue() == null ? getUserID() : sa.getValue());
		} else {
			setUser(getUserID());
		}
	
		if (arguments.containsKey(SubmitParameters.PASSWORD.getName())){
			SubmitArgument sa = arguments.get(SubmitParameters.PASSWORD.getName());
			setPassword(sa.getValue());
		}
		// checks if there is the password
		// if not, asks passwrod from STD input
		if (getPassword() == null){
			CmdConsole console = new CmdConsole();
			try {
				setPassword(console.readPassword(getUser()));
			} catch (IOException e) {
				throw new SubmitException(SubmitMessage.JEMW008E, e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.SubmitCommandLine#jobSubmit()
	 */
	@Override
	public void jobSubmit() throws SubmitException {
		// add JEM url handler factory if the user will use
		// JEM url to add JCL content from GFS of JEM
		SetURLFactory.install();
		
		String jobName= null;
		// gets URL of JCL content, reads and loads it into Prejob object,
		// setting the JCL type
		URL url = null;
		try {
			url = new URL(getJcl());
		} catch (MalformedURLException ex) {
			// ignore
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			// if it's not a URL, try as a file
			File jcl = new File(getJcl());
			try {
				url = jcl.toURI().toURL();
				// loads file name. it will set as job name
				jobName = jcl.getName();
			} catch (MalformedURLException e) {
				throw new SubmitException(SubmitMessage.JEMW006E, e, getJcl());
			}
		}

		// creates Prejob loading from URL
		PreJob preJob;
		try {
			preJob = Factory.createPreJob(url);
		} catch (InstantiationException e) {
			throw new SubmitException(SubmitMessage.JEMW007E, e, getJcl());
		}
		// sets JCL type whcich wa an argument
		preJob.setJclType(getType());

		// creates a empty job
		Job job = new Job();
		setJob(job);
		// gets user id
		job.setUser(getUser() == null ? getUserID() : getUser());
		// loads all line arguments (the -D properties).
		// could be useful to factories, listeners and during job execution to
		// job itself
		job.setInputArguments(ManagementFactory.getRuntimeMXBean().getInputArguments());
		job.setNowait(true);
		
		// uses file name as job name, if file name exists
		if (jobName != null){
			job.setName(jobName);
		}

		// loads prejob with job
		preJob.setJob(job);

		// submit the prejob by http. The HTTP call returns the JOB ID
		String jobId = getJobId(preJob);
		job.setId(jobId);
		
		LogAppl.getInstance().emit(NodeMessage.JEMC020I, job.toString());

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.SubmitCommandLine#afterJobSubmit()
	 */
	@Override
	public int afterJobSubmit() throws SubmitException {
		int rc = 0;
		if (isWait()){
			Job resultJob = null;
			while(resultJob == null){
				try {
					Thread.sleep(30 * TimeUtils.SECOND);
					resultJob = getEndedJob(getJob().getId());
					if (resultJob != null){
						// logs return code and exception if exists
						LogAppl.getInstance().emit(NodeMessage.JEMC021I, resultJob.toString(), String.valueOf(resultJob.getResult().getReturnCode()));
						if (resultJob.getResult().getReturnCode() != Result.SUCCESS) {
							LogAppl.getInstance().emit(NodeMessage.JEMC021I, resultJob.toString(), resultJob.getResult().getExceptionMessage());
						}
						// sets return code to exit
						Result res = resultJob.getResult();
						if (res != null) {
							rc = resultJob.getResult().getReturnCode();
						} else {
							rc = 1;
						}
					}
				} catch (InterruptedException e) {
					throw new SubmitException(SubmitMessage.JEMW004E, e);
				}
			}
		}
		return rc;
	}

}
