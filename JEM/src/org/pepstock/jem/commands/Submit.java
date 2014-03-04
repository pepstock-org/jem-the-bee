/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.util.Map;

import org.pepstock.jem.commands.util.HazelcastUtil;

import com.hazelcast.core.HazelcastInstance;

/**
 * Submits JCL into JEm.<br>
 * Is a command (to execute by command line) which uses the following parameters:<br>
 * 
 * <ul>
 * <li><code>-jcl [url]</code> mandatory, indicates the JCL which describes the
 * job. Must be a valid URL where to read JCL content</li>
 * <li><code>-type [type]</code> mandatory indicates the type of JCL. Must be a
 * valid value, defined from JEM factories implemented.</li>
 * <li><code>-host [http address]</code> mandatory indicates the address of a
 * "web node" to receive the list of members and cluster information.</li>
 * <li><code>-password [String password]</code> mandatory indicates hazelcast
 * password needed to connect to the environment.</li>
 * <li><code>-wait [true/false]</code> optional allows to end the process to submit without
 * waiting for end of job.</li>
 * <li><code>-printOutput [true/false</code> optional in case you choose no wait it will
 * print on standard output the job log</li>
 * <li><code>-privateKey [String path of the private key]</code> optional
 * indicates the pem private key to use in case JEM was installed with login
 * protocol enabled.</li>
 * <li><code>-privateKeyPwd [String password of the private key]</code> optional
 * indicates the pem private key password to use in case JEM was installed with login
 * protocol enabled.</li>
 * </ul>
 * <p>
 * <b>Submit -jcl ... -type ... -host ... -password [-wait true/false]]
 * [-printOutput true/false] [-privateKey ...] [-privateKeyPwd ...]</b>
 * <p>
 * <br>
 * The "submitter": <br>
 * <ul>
 * <li>asks to JEM cluster a unique ID, used like JOB ID, using Hazelcast
 * features to create a unique ID in the cluster</li>
 * <li>puts the JOB in PRE-INPUT queue</li>
 * <li>waits for notification of the end of job (if -nowait is not set), using
 * Hazelcast features of publish/subscribe (topic) pattern</li>
 * </ul>
 * <br>
 * Is possible to have help from command line by <code>-help</code> argument.
 * <br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class Submit extends AbstractConnectedClusterSubmit {

	private static final String COMMAND = "jem_submit";
	
	private String host = null;		

	/**
	 * Creates the submit engine passing the command name
	 */
	public Submit() {
		super(COMMAND);
		// adds host
		Map<String, SubmitArgument> arguments = getArguments();
		arguments.put(SubmitParameters.HOST.getName(), SubmitParameters.createArgument(SubmitParameters.HOST, true));
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

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.ConnectedClusterSubmit#beforeJobSubmit()
	 */
	@Override
	public void beforeJobSubmit() throws SubmitException {
		super.beforeJobSubmit();
		// gets HOST
		Map<String, SubmitArgument> arguments = getArguments();
		SubmitArgument sahost= arguments.get(SubmitParameters.HOST.getName());
		setHost(sahost.getValue());
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.ConnectedClusterSubmit#createClient()
	 */
	@Override
	public HazelcastInstance createClient() throws SubmitException {
		return HazelcastUtil.getInstance(getHost(), getPassword(), getPrivateKey(), getPrivateKeyPassword(), getUserID());
	}
	
	/**
	 * Main method! Parses the arguments, creates the client, submits job.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		Submit submit = new Submit();
		SubmitResult sr = submit.execute(args);
		System.exit(sr.getRc());
	}
}
