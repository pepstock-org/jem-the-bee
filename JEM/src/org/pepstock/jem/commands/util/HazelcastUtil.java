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
package org.pepstock.jem.commands.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.pepstock.jem.commands.SubmitException;
import org.pepstock.jem.commands.SubmitMessage;
import org.pepstock.jem.node.security.socketinterceptor.SubmitInterceptor;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Utility to creates Hazelcast instance to submit jobs.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class HazelcastUtil {
	
	private static final int DEFAULT_RECONNECTION_TIMEOUT = 5000;
	
	private static final int DEFAULT_RECONNECTION_ATTEMPT_LIMIT = 1;
	
	/**
	 * To avoid any instantiation
	 */
	private HazelcastUtil() {
		
	}

	/**
	 * Creates a Hazelcast instance client to connect to JEM which is running on the same machine of submit command.
	 * 
	 * @param env is the name of the JEM environment
	 * @param port is the port number
	 * @param envPassword is the password for the environment
	 * @param privateKeyPathFile path of private key used during socket interceptor 
	 * @param privateKeyPassword password of private key used during socket interceptor 
	 * @param userId userid logged on machine
	 * @return hazelcast instance
	 * @throws SubmitException if any exception occurs
	 */
	public static final HazelcastInstance getLocalInstance(String env, String port, String envPassword, String privateKeyPathFile, String privateKeyPassword, String userId) 
			throws SubmitException {
		try {
			// creates a client configuration for Hazelcast
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getGroupConfig().setName(env).setPassword(envPassword);
			
			ClientNetworkConfig networkConfig = new ClientNetworkConfig();
			clientConfig.setNetworkConfig(networkConfig);
			networkConfig.addAddress(InetAddress.getLocalHost().getHostAddress() + ":" + port);
			
			networkConfig.setConnectionAttemptLimit(DEFAULT_RECONNECTION_ATTEMPT_LIMIT);
			networkConfig.setConnectionTimeout(DEFAULT_RECONNECTION_TIMEOUT);
			networkConfig.setSmartRouting(true);
			
			// check if the environment has the socket interceptor enable is so
			// use it also in the client to login correctly
			if (privateKeyPathFile != null) {
				setSocketInterceptor(networkConfig, privateKeyPathFile, privateKeyPassword, userId);
			}
			// creates a new Client instance of Hazelcast
			return HazelcastClient.newHazelcastClient(clientConfig);
		} catch (UnknownHostException e) {
			throw new SubmitException(SubmitMessage.JEMW005E, e);
		}
	}
	
	/**
	 * Activate the socket interceptor for the client
	 * @param clientConfig Hazelcast client config
	 * @param privateKeyPathFile private key path file
	 * @param privateKeyPassword private key password
	 * @param userId user identification
	 * @throws SubmitException if any errors occurs
	 */
	private static void setSocketInterceptor(ClientNetworkConfig networkConfig, String privateKeyPathFile, String privateKeyPassword, String userId) {
		SocketInterceptorConfig socketInterceptorConfig = new SocketInterceptorConfig();
		socketInterceptorConfig.setClassName(SubmitInterceptor.class.getName());
		socketInterceptorConfig.setEnabled(true);
		socketInterceptorConfig.setProperty(SubmitInterceptor.PRIVATE_KEY_FILE_PATH, privateKeyPathFile);
		socketInterceptorConfig.setProperty(SubmitInterceptor.KEY_PASSWORD, privateKeyPassword);
		socketInterceptorConfig.setProperty(SubmitInterceptor.SUBJECT_ID, userId);
		networkConfig.setSocketInterceptorConfig(socketInterceptorConfig);
	}

	/**
	 * Creates a Hazelcast instance client to connect to JEM which is running on another machine of submit command.
	 * 
	 * @param url is the url of the web node
	 * @param envPassword is the password for the environment
	 * @param privateKeyPathFile path of private key used during socket interceptor 
	 * @param privateKeyPassword password of private key used during socket interceptor 
	 * @param userId userid logged on machine
	 * @return hazelcast instance
	 * @throws SubmitException if any exception occurs
	 */
	public static final HazelcastInstance getInstance(String url, String envPassword, String privateKeyPathFile, String privateKeyPassword, String userId) 
			throws SubmitException {
		String groupName = HttpUtil.getGroupName(url);

		// creates a client configuration for Hazelcast
		ClientConfig clientConfig = new ClientConfig();

		// sets the group name (received by http call) and sets the constant
		// password
		clientConfig.getGroupConfig().setName(groupName).setPassword(envPassword);
		
		// connect to Hazelcast using the complete list of current members
		ClientNetworkConfig networkConfig = new ClientNetworkConfig();
		clientConfig.setNetworkConfig(networkConfig);
		networkConfig.addAddress(HttpUtil.getMembers(url));
		
		networkConfig.setConnectionAttemptLimit(DEFAULT_RECONNECTION_ATTEMPT_LIMIT);
		networkConfig.setConnectionTimeout(DEFAULT_RECONNECTION_TIMEOUT);
		networkConfig.setSmartRouting(true);

		// if properties are not empyt, sets up SocketInterceptor.
		if (privateKeyPathFile != null) {
			setSocketInterceptor(networkConfig, privateKeyPathFile, privateKeyPassword, userId);
		}
		// creates a new Client instance of Hazelcast
		return HazelcastClient.newHazelcastClient(clientConfig);
	}
	
	/**
	 * Creates a Hazelcast instance client to connect to JEM which is running on another machine of submit command.
	 * @param proxy is the address and port of proxy service
	 * @param env environment of JEM
	 * @param envPassword is the password for the environment
	 * @param privateKeyPathFile path of private key used during socket interceptor 
	 * @param privateKeyPassword password of private key used during socket interceptor 
	 * @param userId userid logged on machine
	 * @return hazelcast instance
	 * @throws SubmitException if any exception occurs
	 */
	public static final HazelcastInstance getInstance(String proxy, String env, String envPassword, String privateKeyPathFile, String privateKeyPassword, String userId) 
			throws SubmitException {
		// creates a client configuration for Hazelcast
		ClientConfig clientConfig = new ClientConfig();

		// sets the group name (received by http call) and sets the constant
		// password
		clientConfig.getGroupConfig().setName(env).setPassword(envPassword);

		// connect to Hazelcast using the complete list of current members
		ClientNetworkConfig networkConfig = new ClientNetworkConfig();
		clientConfig.setNetworkConfig(networkConfig);
		networkConfig.addAddress(proxy);
		
		networkConfig.setConnectionAttemptLimit(0);
		networkConfig.setConnectionTimeout(DEFAULT_RECONNECTION_TIMEOUT);
		networkConfig.setSmartRouting(false);

		// if properties are not empyt, sets up SocketInterceptor.
		if (privateKeyPathFile != null) {
			setSocketInterceptor(networkConfig, privateKeyPathFile, privateKeyPassword, userId);
		}
		// creates a new Client instance of Hazelcast
		return HazelcastClient.newHazelcastClient(clientConfig);
	}

}