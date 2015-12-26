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
package org.pepstock.jem.commands.docker;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.AbstractExecutionEnvironment;
import org.pepstock.jem.commands.util.NodeProperties;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.persistence.mongo.MongoFactory;
import org.pepstock.jem.node.persistence.sql.factories.MySqlSQLContainerFactory;

/**
 * Creates a node using the minimum arguments to use into Docker container run.
 * <br>
 * list of Environment Varibales available using Docker link:
 * <br>
 * JEMDB_PORT=tcp://172.17.0.2:3306
 * JEMDB_PORT_3306_TCP=tcp://172.17.0.2:3306
 * JEMDB_PORT_3306_TCP_ADDR=172.17.0.2
 * JEMDB_PORT_3306_TCP_PORT=3306
 * JEMDB_PORT_3306_TCP_PROTO=tcp
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class StartUpNode extends StartUp{
	
	private static final String COMMAND = "jem-node.sh";
	
	private String domain = null;
	
	private String affinity = null;
	
	// by default MySQL
	private String type = MySqlSQLContainerFactory.DATABASE_TYPE;
	
	private String url = null;
	
	private String user = null;
	
	private String password = null;

	/**
	 * It's called to read all environment variables for JEM node
	 * 
	 * @throws ConfigurationException if some mandatory env variables are missing
	 */
	public void readEnvironmentVariables() throws ConfigurationException {
		// loads common variables
		super.readEnvironmentVariables();
		// gets domain
		domain = System.getenv(Keys.JEM_DOMAIN_VARIABLE);
		// if not set, uses the default domain
		if (domain == null || "".equalsIgnoreCase(domain)){
			domain = AbstractExecutionEnvironment.DEFAULT_DOMAIN; 
		}
		// gets affinities
		String affinityEnvVar = System.getenv(Keys.JEM_AFFINITY_VARIABLE);
		// if not set, uses the default affinities
		if (affinityEnvVar != null && !"".equalsIgnoreCase(affinityEnvVar)){
			affinity = affinityEnvVar; 
		}
		// loads the database URL variable
		String dbAddress = System.getenv(Keys.JEM_DB_URL_VARIABLE);
		// it's mandatory!!
		if (dbAddress == null || "".equalsIgnoreCase(dbAddress)){
			throw new ConfigurationException(Keys.JEM_DB_URL_VARIABLE+" are missing"); 
		}

		// gets the JEMDB variable cretaed by DOcker with --link
		String dbAddressByDockerLink = System.getenv(Keys.JEM_DB_PORT_VARIABLE);
		try {
			// reads the URI
			URI uri = new URI(dbAddress);
			// gets scheme to understand
			// if it's a MYSQL or MONGO
			// because the config of JEM node is different
			type = uri.getScheme();
			if (!MySqlSQLContainerFactory.DATABASE_TYPE.equalsIgnoreCase(type) &&
				!MongoFactory.DATABASE_TYPE.equalsIgnoreCase(type)){
				throw new ConfigurationException("DB type "+type+" is not valid. Only "+MySqlSQLContainerFactory.DATABASE_TYPE+" or "+MongoFactory.DATABASE_TYPE+" is accepted"); 
			}
			// gets the host
			String host = null;
			// if the --link is used
			if (dbAddressByDockerLink != null){
				// reads the URI from Docker
				URI uriByDockerLink = new URI(dbAddressByDockerLink);
				// gets host and port
				host = uriByDockerLink.getHost() + ":"+String.valueOf(uriByDockerLink.getPort());
			} else {
				// if here, DOCKER run without --link
				// if host is not set, error!
				if (uri.getHost() == null){
					throw new ConfigurationException("Host of DB URL is missing");
				}
				// gets host and port
				host = uri.getHost() + ":"+String.valueOf(uri.getPort());
			}
			// gets the user info from DB URL
			// could be missing
			if (uri.getUserInfo() != null && uri.getUserInfo().contains(":")){
				String[] userInfo = StringUtils.split(uri.getUserInfo(), ":");
				user = userInfo[0];
				password = userInfo[1];
			}
			// creates URL for connection 
			// based on type of database
			if (type.equalsIgnoreCase(MySqlSQLContainerFactory.DATABASE_TYPE)){
				url = MessageFormat.format(Keys.MYSQL_URL_FORMAT, host);
			} else {
				url = MessageFormat.format(Keys.MONGO_URL_FORMAT, host);
			}
		} catch (URISyntaxException e) {
			throw new ConfigurationException(Keys.JEM_DB_PORT_VARIABLE+" is wrong: "+dbAddress); 
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.docker.StartUp#loadProperties(java.util.Properties)
	 */
	@Override
	void loadProperties(Properties props) throws ConfigurationException {
		// loads DOMAIN, always
		props.setProperty(NodeProperties.JEM_DOMAIN_PROP, domain);
		// loads affinity is set otherwise the default
		if (affinity != null){
			props.setProperty(NodeProperties.JEM_AFFINITY_PROP, affinity);
		}

		// sets the JAVA driver to connect to database
		if (type.equalsIgnoreCase(MongoFactory.DATABASE_TYPE)){
			props.setProperty(NodeProperties.JEM_DB_DRIVER, "com.mongodb.MongoClient");
		} else {
			props.setProperty(NodeProperties.JEM_DB_DRIVER, "com.mysql.jdbc.Driver");
		}
		// sets JEM DB URL
		props.setProperty(NodeProperties.JEM_DB_URL, url);
		// sets JEM DB user if exists
		if (user != null){
			props.setProperty(NodeProperties.JEM_DB_USER, user);
		}
		// sets JEM DB password if exists
		if (password != null){
			props.setProperty(NodeProperties.JEM_DB_PASSWORD, password);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.docker.StartUp#getCommand()
	 */
	@Override
	String getCommand() {
		return COMMAND;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.commands.docker.StartUp#hasAlreadyInstalled()
	 */
	@Override
	boolean hasConfigured() {
		return checkGfsPersistence() && checkHomeNode();
	}
	
	/**
	 * Checks if JEM node is already configured
	 * @return true is already configured otherwise false
	 */
	private boolean checkHomeNode(){
		// checks if there is the home with ENVIROMENT 
		// already mounted
		File env = new File(getHome(), getEnvironment());
		if (env.exists()){
			// if persistence exists
			File node = new File(env, "/node-000/bin");
			return node.exists();
		}
		return false;
	}
	
	/**
	 * Checks if JEM node is already configured
	 * @return true is already configured otherwise false
	 */
	private boolean checkGfsPersistence(){
		// checks if there is the persistent with ENVIROMENT 
		// already mounted
		File persistence = new File(JEM_GFS_FILE, "persistence");
		if (persistence.exists()){
			// if persistence exists
			File env = new File(persistence, getEnvironment());
			return env.exists();
		}
		return false;
	}

	/**
	 * Main method!
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		StartUpNode create = new StartUpNode();
		System.exit(create.execute(args));
	}
	
}
