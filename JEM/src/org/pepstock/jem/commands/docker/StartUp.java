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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.pepstock.jem.commands.CreateNode;
import org.pepstock.jem.commands.OptionBuilderLock;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.commands.util.NodeProperties;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;

/**
 * Creates a node using the minimum arguments to use into Docker container run.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public abstract class StartUp {

	static final String JEM_GFS_MOUNT_POINT = "/mnt/jem";

	static final File JEM_GFS_FILE = new File(JEM_GFS_MOUNT_POINT);
	
	private final Map<String, CreateNodeArgument> arguments = new HashMap<String, CreateNodeArgument>();

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
	 * @return the arguments
	 */
	Map<String, CreateNodeArgument> getArguments() {
		return arguments;
	}

	/**
	 * Parse arguments of command line
	 * @param args arguments passed by command line
	 * @throws ParseException if any exception occurs during arguments parsing 
	 */
	
	final void parseArguments(String[] args) throws ParseException{
		// parses args using command line
		ArgumentsParser parser = new ArgumentsParser(getCommand());
		synchronized (OptionBuilderLock.getLock()) {
			// scans defined arguments and creates the option
			for (CreateNodeArgument argument : arguments.values()){
				@SuppressWarnings("static-access")
				Option op = OptionBuilder.withArgName(argument.getParameter().getName()).hasArg().withDescription(argument.getParameter().getDescription()).create(argument.getParameter().getName());
				// sets if is required
				op.setRequired(argument.isRequired());
				// adds to parser
				parser.getOptions().add(op);
			}
			// gets the result in a properties
			Properties properties = parser.parseArg(args);
			
			// scans all properties to add value
			for(Object keyString : properties.keySet()){
				String key = keyString.toString();
				CreateNodeArgument sa = arguments.get(key);
				sa.setValue(properties.getProperty(key));
			}
		}
	}
	
	/**
	 * It's called to read all environment variables
	 * 
	 * @throws ConfigurationException if some mandatory env variables are missing
	 */
	void readArguments() {
	}	
	

	/**
	 * It's called to read all environment variables
	 * 
	 * @throws ConfigurationException if some mandatory env variables are missing
	 */
	void readEnvironmentVariables() throws ConfigurationException {
		home = System.getenv(ConfigKeys.JEM_HOME);
		if (home == null || "".equalsIgnoreCase(home)){
			throw new ConfigurationException(ConfigKeys.JEM_HOME+" is missing"); 
		}
		
		environment = System.getenv(Keys.JEM_ENVIRONMENT_VARIABLE);
		if (environment == null || "".equalsIgnoreCase(environment)){
			throw new ConfigurationException(Keys.JEM_ENVIRONMENT_VARIABLE+" is missing"); 
		}
	}
	
	abstract void loadProperties(Properties props) throws ConfigurationException;
	
	abstract String getCommand();
	
	abstract boolean hasConfigured();

	/**
	 * Executes all necessary steps to submit the job.
	 * 
	 * @param args arguments passed by command line
	 * @return result of job execution
	 */
	public int execute(String[] args){
		int rc = 0;
		try {
			readEnvironmentVariables();

			// if the folders on persistence and home
			// of environment are already installed then 
			// skip config
			if (hasConfigured()){
				return rc;
			}
			parseArguments(args);
			readArguments();
			
			Properties props = new Properties();
			URL res = this.getClass().getClassLoader().getResource(Keys.CREATE_NODE_PROPERTIES);
			props.load(res.openStream());
			props.setProperty(NodeProperties.JEM_ENVIRONMENT_NAME_PROP, environment);
			
			if (!JEM_GFS_FILE.exists()){
				JEM_GFS_FILE.mkdirs();
			}
			
			props.setProperty(NodeProperties.JEM_OUTPUT_PROP, createFolder(JEM_GFS_FILE, "output").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_DATA_PROP, createFolder(JEM_GFS_FILE, "data").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_SOURCE_PROP, createFolder(JEM_GFS_FILE, "src").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_BINARY_PROP, createFolder(JEM_GFS_FILE, "bin").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_CLASSPATH_PROP, createFolder(JEM_GFS_FILE, "classes").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_LIBRARY_PROP, createFolder(JEM_GFS_FILE, "lib").getAbsolutePath());
			props.setProperty(NodeProperties.JEM_PERSISTENCE_PROP, createFolder(JEM_GFS_FILE, "persistence").getAbsolutePath());
			
			loadProperties(props);
			
			for (Entry<Object, Object> entry : props.entrySet()){
				System.err.println(entry.getKey()+"="+entry.getValue());
			}
			
			File tempProperties = File.createTempFile("jem", "tmp");
			props.store(new FileWriter(tempProperties), "Docker setup");
			
			CreateNode.main(new String[]{"-properties", tempProperties.getAbsolutePath()});
			
		} catch (IOException e) {
			e.printStackTrace();
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (ParseException e) {
			e.printStackTrace();
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (MessageException e) {
			e.printStackTrace();
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		} catch (KeyStoreException e) {
			e.printStackTrace();
			LogAppl.getInstance().ignore(e.getMessage(), e);
			rc = 1;
		}
		return rc;
	}
	
	private static File createFolder(File parent, String what){
		File file = new File(parent, what);
		if (!file.exists()){
			file.mkdirs();
		}
		return file;
	}

}
