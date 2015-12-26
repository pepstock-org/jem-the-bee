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
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStoreException;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.pepstock.jem.commands.CreateNode;
import org.pepstock.jem.commands.util.NodeProperties;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.util.TimeUtils;

/**
 * Creates a node using the minimum arguments to use into Docker container run.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public abstract class StartUp {

	private static final String FILE_LOCK = "new_install";
	
	// standard mount point of GFS
	static final String JEM_GFS_MOUNT_POINT = "/mnt/jem";
	
	// standard mount point of GFS as File
	static final File JEM_GFS_FILE = new File(JEM_GFS_MOUNT_POINT);
	
	private String environment = null;
	
	private String home = null;
	
	/**
	 * @return the environment
	 */
	String getEnvironment() {
		return environment;
	}

	/**
	 * @return the home
	 */
	String getHome() {
		return home;
	}
	

	/**
	 * It's called to read all environment variables
	 * 
	 * @throws ConfigurationException if some mandatory env variables are missing
	 */
	void readEnvironmentVariables() throws ConfigurationException {
		// gets the jem home
		home = System.getenv(ConfigKeys.JEM_HOME);
		// throws an exception if missing
		if (home == null || "".equalsIgnoreCase(home)){
			throw new ConfigurationException(ConfigKeys.JEM_HOME+" is missing"); 
		}
		// gets the JEM environment 
		// this is mandatory otherwise it throws an exception
		environment = System.getenv(Keys.JEM_ENVIRONMENT_VARIABLE);
		if (environment == null || "".equalsIgnoreCase(environment)){
			throw new ConfigurationException(Keys.JEM_ENVIRONMENT_VARIABLE+" is missing"); 
		}
	}
	
	/**
	 * Method to add or change the standard properties
	 * @param props create node properties to feed
	 * @throws ConfigurationException if any exception occurs
	 */
	abstract void loadProperties(Properties props) throws ConfigurationException;
	
	/**
	 * Returns the bash command
	 * @return the bash command
	 */
	abstract String getCommand();
	
	/**
	 * Checks if the JEM node is already configured.
	 * it uses when to define a volume into Docker run command of an 
	 * existing and already configured environment
	 * @return true if already configured otherwise false
	 */
	abstract boolean hasConfigured();

	/**
	 * Executes all necessary steps to submit the job.
	 * 
	 * @param args arguments passed by command line
	 * @return result of job execution
	 */
	public int execute(String[] args){
		// sets default return code
		int rc = 0;
		File fileLock = null;
		try {
			// reads mandatory variables
			readEnvironmentVariables();

			// checks if already configured
			// if yes, returns and run the JEM
			if (hasConfigured()){
				return rc;
			}

			// creates the node properties...
			Properties props = new Properties();
			// .. loading from classpath
			URL res = this.getClass().getClassLoader().getResource(Keys.CREATE_NODE_PROPERTIES);
			props.load(res.openStream());
			// sets jem env
			props.setProperty(NodeProperties.JEM_ENVIRONMENT_NAME_PROP, environment);
			
			// if GFS mount point doesn't exists
			// it creates
			if (!JEM_GFS_FILE.exists()){
				JEM_GFS_FILE.mkdirs();
			}
			
			// checks all GFS folders and if not exists it creates them
			props.setProperty(NodeProperties.JEM_OUTPUT_PROP, createFolder(JEM_GFS_FILE, "output").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_DATA_PROP, createFolder(JEM_GFS_FILE, "data").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_SOURCE_PROP, createFolder(JEM_GFS_FILE, "src").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_BINARY_PROP, createFolder(JEM_GFS_FILE, "bin").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_CLASSPATH_PROP, createFolder(JEM_GFS_FILE, "classes").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_LIBRARY_PROP, createFolder(JEM_GFS_FILE, "lib").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_PERSISTENCE_PROP, createFolder(JEM_GFS_FILE, "persistence").getAbsolutePath());
			
			// adds additional properties 
			// different from JEM NODE and
			// JEM web
			loadProperties(props);
			
			// stores the JEM node on a temporary file
			File tempProperties = File.createTempFile("jem", "tmp");
			props.store(new FileWriter(tempProperties), "Docker setup");
			
			// serializes when more than 1 container is starting at the same time
			// it uses a file of GFS to serialize the configuration creation
			fileLock = new File(JEM_GFS_FILE, FILE_LOCK);
			if (!fileLock.createNewFile()){
				while (fileLock.exists()){
					try {
						// waits for the completition 
						// of another Docker container
						Thread.sleep(TimeUtils.SECOND);
					} catch (InterruptedException e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
			// calls the cretae node 
			CreateNode.main(new String[]{"-properties", tempProperties.getAbsolutePath()});
			
		} catch (IOException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (ParseException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (MessageException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (ConfigurationException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (KeyStoreException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} finally {
			// unlocks always teh file
			if (fileLock !=null){
				fileLock.delete();	
			}
		}
		return rc;
	}
	
	/**
	 * Creates the GFS folder is doesn't exists
	 * @param parent parent folder
	 * @param what GFS folder
	 * @return the created file
	 */
	private static File createFolder(File parent, String what){
		// creates file
		File file = new File(parent, what);
		// if not exists
		if (!file.exists()){
			// creates!!
			file.mkdirs();
		}
		return file;
	}

}
