/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.util.HttpUtil;

/**
 * Submits JCL into JEM, by http.<br>
 * Is a command (to execute by command line) which accepts 3 arguments: <br>
 * <code>-jcl [url]</code> indicates the JCL which describes the job. Must be a
 * valid URL where to read JCL content<br>
 * <code>-type [type]</code> indicates the type of JCL. Must be a valid value,
 * defined from JEM factories implemented<br>
 * <code>-host [http address]</code> indicates the address of a "node" to submit
 * the job<br>
 * <code>-user [user]</code> indicates the userid to use connecting
 * to JEM<br>
 * <code>-password [pwd]</code> indicates optionally the password of userid to
 * use connecting to JEM<br>
 * <code>-wait [pwd]</code> indicates optionally (bollean value) if must wait for getting the result. Default is false.<br> 
 * <br>
 * <b>HttpSubmit -jcl ... -type ... -host ... -user ... -password ... -wait [true/false]</b><br>
 * <br>
 * The command asks the password of the user, as following:<br>
 * <br>
 * [user]'s password:<br>
 * <br>
 * Password is the same used by web application.<br>
 * <br>
 * Is possible to have help from command line by <code>-help</code> argument.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class HttpSubmit extends AbstractRemoteSubmit {

	private static final String COMMAND = "jem_http_submit";
	
	/**
	 * Creates the submit engine passing the command name
	 */
	public HttpSubmit() {
		super(COMMAND);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.RemoteSubmit#getJobId(org.pepstock.jem.PreJob)
	 */
	@Override
	public String getJobId(PreJob preJob) throws SubmitException {
		return HttpUtil.submit(getUser() == null ? getUserID() : getUser(), getPassword(), getHost(), preJob);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.RemoteSubmit#getEndedJob()
	 */
	@Override
	public Job getEndedJob(String jobId) throws SubmitException {
		// gets the jobid using the userid and password 
		Object obj = HttpUtil.getEndedJobByID(getUser() == null ? getUserID() : getUser(),getPassword(), getHost(), jobId);
		if (obj instanceof Job){
			return (Job) obj;
		}
		return null;
	}
	/**
	 * Main method! Parses the arguments, creates the client, submits job.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		HttpSubmit submit = new HttpSubmit();
		SubmitResult sr = submit.execute(args);
		System.exit(sr.getRc());
	}

}
