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
import org.pepstock.jem.log.LogAppl;

import com.hazelcast.client.HazelcastClient;

/**
 * Submits JCL into JEM by a proxy on web side.<br>
 * Is a command (to execute by command line) which uses the following parameters:<br>
 * 
 * <ul>
 * <li><code>-jcl [url]</code> mandatory, indicates the JCL which describes the
 * job. Must be a valid URL where to read JCL content</li>
 * <li><code>-type [type]</code> mandatory indicates the type of JCL. Must be a
 * valid value, defined from JEM factories implemented.</li>
 * <li><code>-host [address:port]</code> mandatory indicates the address and port of a
 * "proxy" instance.</li>
 * <li><code>-env [String environment]</code> mandatory indicates the local
 * environment on which the user desires to connect.<br>
 * </li>
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
 * <b>ProxySubmit -jcl ... -type ... -host ... -env ... -password [-wait true/false]]
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
 * @version 3.0
 * 
 */
public class ProxySubmit extends AbstractConnectedClusterSubmit {

	private static final String COMMAND = "jem_proxy_submit";
	
	private String host = null;	
	
	private String environment = null;

	/**
	 * Creates the submit engine passing the command name
	 */
	public ProxySubmit() {
		super(COMMAND);
		// adds host
		Map<String, SubmitArgument> arguments = getArguments();
		arguments.put(SubmitParameters.HOST.getName(), SubmitParameters.createArgument(SubmitParameters.HOST, true));
		arguments.put(SubmitParameters.ENV.getName(), SubmitParameters.createArgument(SubmitParameters.ENV, true));
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
		SubmitArgument saEnv= arguments.get(SubmitParameters.ENV.getName());
		setEnvironment(saEnv.getValue());

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.ConnectedClusterSubmit#createClient()
	 */
	@Override
	public HazelcastClient createClient() throws SubmitException {
		HazelcastClient client = HazelcastUtil.getInstance(getHost(), getEnvironment(), getPassword(), getPrivateKey(), getPrivateKeyPassword(), getUserID());
		HazelcastClientLifeCycler listener = new HazelcastClientLifeCycler(this, client);
		client.getLifecycleService().addLifecycleListener(listener);
		return client;
	}
	
	/**
	 * Main method! Parses the arguments, creates the client, submits job.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		Times.start();
		ProxySubmit submit = new ProxySubmit();
		SubmitResult sr = submit.execute(args);
		LogAppl.getInstance().emit(SubmitMessage.JEMW010I, submit.getResident());
		Times.end();
		LogAppl.getInstance().emit(SubmitMessage.JEMW011I, Times.toReadable());
		System.exit(sr.getRc());
	}
}
