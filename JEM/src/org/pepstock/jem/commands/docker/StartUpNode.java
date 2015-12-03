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

import org.pepstock.jem.AbstractExecutionEnvironment;
import org.pepstock.jem.commands.util.NodeProperties;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.persistence.mongo.MongoFactory;
import org.pepstock.jem.node.persistence.sql.factories.MySqlSQLContainerFactory;

/**
 * Creates a node using the minimum arguments to use into Docker container run.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class StartUpNode extends StartUp{
	
	private static final String COMMAND = "jem-node.sh";
	
	private String domain = null;
	
	private String affinity = null;
	
	private String type = MySqlSQLContainerFactory.DATABASE_TYPE;
	
	private String url = null;
	
	private String user = null;
	
	private String password = null;

	/**
	 * Constructs the object saving the command name (necessary on help) and adding arguments definitions.
	 * 
	 * @param commandName command name  (necessary on help)
	 */
	public StartUpNode() {
		getArguments().put(CreateNodeParameters.DB_USER.getName(), CreateNodeParameters.createArgument(CreateNodeParameters.DB_USER));
		getArguments().put(CreateNodeParameters.DB_PASSWORD.getName(), CreateNodeParameters.createArgument(CreateNodeParameters.DB_PASSWORD));
		getArguments().put(CreateNodeParameters.DB_TYPE.getName(), CreateNodeParameters.createArgument(CreateNodeParameters.DB_TYPE, true));
	}

	/**
	 * It's called to read all environment variables
	 * 
	 * @throws ConfigurationException if some mandatory env variables are missing
	 */
	@Override
	void readArguments() {
		super.readArguments();
		CreateNodeArgument dbtype = getArguments().get(CreateNodeParameters.DB_TYPE.getName());
		if (dbtype.getValue().equalsIgnoreCase(MySqlSQLContainerFactory.DATABASE_TYPE) ||
				dbtype.getValue().equalsIgnoreCase(MongoFactory.DATABASE_TYPE)){
			type = dbtype.getValue();
		}

		if (getArguments().containsKey(CreateNodeParameters.DB_USER.getName())){
			CreateNodeArgument environment = getArguments().get(CreateNodeParameters.DB_USER.getName());
			user = environment.getValue();
		}
		
		if (getArguments().containsKey(CreateNodeParameters.DB_PASSWORD.getName())){
			CreateNodeArgument environment = getArguments().get(CreateNodeParameters.DB_PASSWORD.getName());
			password = environment.getValue();
		}
	}	
	

	/**
	 * It's called to read all environment variables
	 * 
	 * @throws ConfigurationException if some mandatory env variables are missing
	 */
	public void readEnvironmentVariables() throws ConfigurationException {
		super.readEnvironmentVariables();
		domain = System.getenv(Keys.JEM_DOMAIN_VARIABLE);
		if (domain == null || "".equalsIgnoreCase(domain)){
			domain = AbstractExecutionEnvironment.DEFAULT_DOMAIN; 
		}
		String affinityEnvVar = System.getenv(Keys.JEM_AFFINITY_VARIABLE);
		if (affinityEnvVar != null && !"".equalsIgnoreCase(affinityEnvVar)){
			affinity = affinityEnvVar; 
		}
		
		String dbAddress = System.getenv(Keys.JEM_DB_PORT_VARIABLE);
		if (dbAddress == null || "".equalsIgnoreCase(dbAddress)){
			throw new ConfigurationException(Keys.JEM_DB_PORT_VARIABLE+" is missing"); 
		}
	
		try {
			URI uri = new URI(dbAddress);
			if (type.equalsIgnoreCase(MySqlSQLContainerFactory.DATABASE_TYPE)){
				url = MessageFormat.format(Keys.MYSQL_URL_FORMAT, uri.getHost(), String.valueOf(uri.getPort()));
			} else {
				url = MessageFormat.format(Keys.MONGO_URL_FORMAT, uri.getHost(), String.valueOf(uri.getPort()));
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
		props.setProperty(NodeProperties.JEM_DOMAIN_PROP, domain);
		if (affinity != null){
			props.setProperty(NodeProperties.JEM_AFFINITY_PROP, affinity);
		}

		if (type.equalsIgnoreCase(MongoFactory.DATABASE_TYPE)){
			props.setProperty(NodeProperties.JEM_DB_DRIVER, "com.mongodb.MongoClient");
		} else {
			props.setProperty(NodeProperties.JEM_DB_DRIVER, "com.mysql.jdbc.Driver");
		}
		
		props.setProperty(NodeProperties.JEM_DB_URL, url);
		if (user != null){
			props.setProperty(NodeProperties.JEM_DB_USER, user);
		}
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
	 * Main method! Parses the arguments, creates the client, submits job.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		StartUpNode create = new StartUpNode();
		System.exit(create.execute(args));
	}
	
}
