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

import java.util.Map;

import org.pepstock.jem.commands.util.HazelcastUtil;

import com.hazelcast.core.HazelcastInstance;

/**
 * Submits JCL into JEM.<br>
 * Is a command (to execute by command line) which uses the following parameters:<br>
 * 
 * <ul>
 * <li><code>-env [String environment]</code> mandatory indicates the local
 * environment on which the user desires to connect.<br>
 * </li>
 * <li><code>-port [int port]</code> mandatory indicates the port to connect to,
 * is the auto-increment port on jem-env-hazelcast.xml.<br>
 * </li>
 * <li><code>-jcl [url]</code> mandatory, indicates the JCL which describes the
 * job. Must be a valid URL where to read JCL content</li>
 * <li><code>-type [type]</code> mandatory indicates the type of JCL. Must be a
 * valid value, defined from JEM factories implemented.</li>
 * <li><code>-password [String password]</code> mandatory indicates hazelcast
 * password needed to connect to the environment.</li>
 * <li><code>-wait [true/false]</code> optional allows to end the process to submit without
 * waiting for end of job.</li>
 * <li><code>-printOutput [true/false]</code> optional in case you choose no wait it will
 * print on standard output the job log</li>
 * <li><code>-privateKey [String path of the private key]</code> optional
 * indicates the pem private key to use in case JEM was installed with login
 * protocol enabled.</li>
 * <li><code>-privateKeyPwd [String password of the private key]</code> optional
 * indicates the pem private key password to use in case JEM was installed with login
 * protocol enabled.</li>
 * </ul>
 * <p>
 * 
 * <b>Submit -jcl ... -type ... -env ... -port ... -password [-wait true/false]]
 * [-printOutput true/false] [-privateKey ...] [-privateKeyPwd ...]</b>
 * <p>
 * </b> The "submitter": <br>
 * <br>
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
public class LocalHostSubmit extends AbstractConnectedClusterSubmit {

	private static final String COMMAND = "jem_localhost_submit";
	
	private String environment = null;
			
	private String port = null;		

	/**
	 * Creates the submit engine passing the command name
	 */
	public LocalHostSubmit() {
		super(COMMAND);
		Map<String, SubmitArgument> arguments = getArguments();
		arguments.put(SubmitParameters.ENV.getName(), SubmitParameters.createArgument(SubmitParameters.ENV, true));
		arguments.put(SubmitParameters.PORT.getName(), SubmitParameters.createArgument(SubmitParameters.PORT, true));
	}
	
	/**
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.ConnectedClusterSubmit#beforeJobSubmit()
	 */
	@Override
	public void beforeJobSubmit() throws SubmitException {
		super.beforeJobSubmit();
		// gets ENVIRONMENT value
		Map<String, SubmitArgument> arguments = getArguments();
		SubmitArgument saEnv= arguments.get(SubmitParameters.ENV.getName());
		setEnvironment(saEnv.getValue());

		// gets PORT value
		SubmitArgument saPort= arguments.get(SubmitParameters.PORT.getName());
		setPort(saPort.getValue());
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.ConnectedClusterSubmit#createClient()
	 */
	@Override
	public HazelcastInstance createClient() throws SubmitException {
		return HazelcastUtil.getLocalInstance(getEnvironment(), 
				getPort(), 
				getPassword(), 
				getPrivateKey(), 
				getPrivateKeyPassword(), 
				getUserID());
	}

	/**
	 * Main method! Parses the arguments, creates the client, submits job.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		LocalHostSubmit submit = new LocalHostSubmit();
		SubmitResult sr = submit.execute(args);
		System.exit(sr.getRc());
	}
}
