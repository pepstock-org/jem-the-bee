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
package org.pepstock.jem.node.configuration;

import java.util.List;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.sgm.DataPaths;
import org.pepstock.jem.node.sgm.Path;

import com.thoughtworks.xstream.XStream;

/**
 * Main configuration bean object. Contains all "children" to configure properly
 * JEM node.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Configuration {

	private ExecutionEnvironment executionEnviroment = null;
	
	private JavaRuntimes javaRuntimes = null;

	private List<Factory> factories = null;

	private Paths paths = null;

	private List<Listener> listeners = null;

	private Node node = null;

	private Database database = null;

	private StatsManager statsManager = null;

	private List<CommonResourceDefinition> resourceDefinitions = null;
	
	private String datasetsRules = null;

	/**
	 * Empty constructor.
	 */
	public Configuration() {
	}

	/**
	 * Returns the execution environment configured for node.
	 * 
	 * @return the execution environment configured for node
	 */
	public ExecutionEnvironment getExecutionEnviroment() {
		return executionEnviroment;
	}

	/**
	 * Sets the execution environment configured for node.
	 * 
	 * @param executionEnviroment
	 */
	public void setExecutionEnviroment(ExecutionEnvironment executionEnviroment) {
		this.executionEnviroment = executionEnviroment;
	}
	
	/**
	 * Returns the JAVA runtime installed on machine and available
	 * @return the javaRuntimes
	 */
	public JavaRuntimes getJavaRuntimes() {
		return javaRuntimes;
	}

	/**
	 * Sets the JAVA runtime installed on machine and available
	 * @param javaRuntimes the javaRuntimes to set
	 */
	public void setJavaRuntimes(JavaRuntimes javaRuntimes) {
		this.javaRuntimes = javaRuntimes;
	}

	/**
	 * Returns the list of defined factories for job task and JCL.
	 * 
	 * @return the list of defined factories for job task and JCL
	 */
	public List<Factory> getFactories() {
		return factories;
	}

	/**
	 * Sets the list of defined factories for job task and JCL.
	 * 
	 * @param factories Returns the list of defined factories for job task and
	 *            JCL
	 */
	public void setFactories(List<Factory> factories) {
		this.factories = factories;
	}

	/**
	 * Returns the paths container, used to gather all path definition
	 * necessary.
	 * 
	 * @return the paths container
	 */
	public Paths getPaths() {
		return paths;
	}

	/**
	 * Sets the paths container, used to gather all path definition necessary.
	 * 
	 * @param paths the paths container
	 */
	public void setPaths(Paths paths) {
		this.paths = paths;
	}

	/**
	 * Returns the list of defined listeners for job life-cycle.
	 * 
	 * @return the list of defined listeners for job life-cycle.
	 */
	public List<Listener> getListeners() {
		return listeners;
	}

	/**
	 * Sets the list of defined listeners for job life-cycle.
	 * 
	 * @param listeners the list of defined listeners for job life-cycle.
	 */
	public void setListeners(List<Listener> listeners) {
		this.listeners = listeners;
	}

	/**
	 * Returns the list of defined resource definitions for custom resources
	 * configuration.
	 * 
	 * @return the list of defined resource definitions for custom resources
	 *         configuration.
	 */
	public List<CommonResourceDefinition> getResourceDefinitions() {
		return resourceDefinitions;
	}

	/**
	 * Sets the list of defined resource definitions for custom resources
	 * configuration.
	 * 
	 * @param resourceDefinitions the list of defined resource definitions for
	 *            custom resources configuration.
	 */
	public void setResourceDefinitions(List<CommonResourceDefinition> resourceDefinitions) {
		this.resourceDefinitions = resourceDefinitions;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the database
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(Database database) {
		this.database = database;
	}

	/**
	 * @return the statisticsManager
	 */
	public StatsManager getStatsManager() {
		return statsManager;
	}

	/**
	 * @param statisticsManager the statisticsManager to set
	 */
	public void setStatsManager(StatsManager statisticsManager) {
		this.statsManager = statisticsManager;
	}

	/**
	 * @return the datasetsRules
	 */
	public String getDatasetsRules() {
		return datasetsRules;
	}

	/**
	 * @param datasetsRules the datasetsRules to set
	 */
	public void setDatasetsRules(String datasetsRules) {
		this.datasetsRules = datasetsRules;
	}

	/**
	 * 
	 * @param xmlConfiguration
	 * @return the jem configuration
	 * @throws ConfigurationException if any exception occurs during the
	 *             unmarshall process
	 */
	public static Configuration unmarshall(String xmlConfiguration) throws ConfigurationException {
		// load Xstream and set alias to have a configuration tags user friendly
		XStream xstream = new XStream();
		xstream.alias(ConfigKeys.CONFIGURATION_TAG, Configuration.class);
		xstream.aliasField(ConfigKeys.STATISTICS_MANAGER_ALIAS, Configuration.class, ConfigKeys.STATISTICS_MANAGER_FIELD);
		xstream.aliasField(ConfigKeys.DATASETS_RULES_ALIAS, Configuration.class, ConfigKeys.DATASETS_RULES_FIELD);
		
		xstream.alias(ConfigKeys.NODE_ALIAS, Node.class);
		xstream.alias(ConfigKeys.DATABASE_ELEMENT, Database.class);
		xstream.alias(ConfigKeys.FACTORY_ALIAS, Factory.class);
		xstream.aliasAttribute(Factory.class, ConfigKeys.CLASS_NAME_FIELD, ConfigKeys.CLASS_NAME_ATTRIBUTE_ALIAS);
		xstream.aliasAttribute(Factory.class, ConfigKeys.CLASS_LOADER_FIELD, ConfigKeys.CLASS_LOADER_ATTRIBUTE_ALIAS);

		xstream.processAnnotations(ClassPath.class);
		xstream.alias(ConfigKeys.LISTENER_ALIAS, Listener.class);
		xstream.aliasAttribute(Listener.class, ConfigKeys.CLASS_NAME_FIELD, ConfigKeys.CLASS_NAME_ATTRIBUTE_ALIAS);
		xstream.processAnnotations(ClassPath.class);
		xstream.aliasAttribute(StatsManager.class, ConfigKeys.PATH_FIELD, ConfigKeys.PATH_ATTRIBUTE_ALIAS);
		xstream.alias(ConfigKeys.RESOURCE_DEFINITION_ALIAS, CommonResourceDefinition.class);
		xstream.aliasAttribute(CommonResourceDefinition.class, ConfigKeys.CLASS_NAME_FIELD, ConfigKeys.CLASS_NAME_ATTRIBUTE_ALIAS);
		xstream.alias(ConfigKeys.RESOURCES_DEFINITION_ALIAS, CommonResourcesDefinition.class);
		xstream.addImplicitCollection(CommonResourcesDefinition.class, ConfigKeys.RESOURCES_ELEMENT);
		
		xstream.aliasField(ConfigKeys.RESOURCE_DEFINITIONS_ALIAS, Configuration.class, ConfigKeys.RESOURCE_DEFINITIONS_FIELD);
		xstream.aliasField(ConfigKeys.EXECUTION_ENVIRONMENT_ALIAS, Configuration.class, ConfigKeys.EXECUTION_ENVIRONMENT_FIELD);
		xstream.aliasField(ConfigKeys.PARALLEL_JOBS_ALIAS, ExecutionEnvironment.class, ConfigKeys.PARALLEL_JOBS_FIELD);
		xstream.alias(ConfigKeys.AFFINITY_FACTORY_ALIAS, AffinityFactory.class);
		
		xstream.alias(ConfigKeys.DATA_ALIAS, DataPaths.class);
		xstream.aliasField(ConfigKeys.DATA_ALIAS, Paths.class, ConfigKeys.DATA_ELEMENT);
		xstream.addImplicitCollection(DataPaths.class, ConfigKeys.PATHS_ELEMENT);
		xstream.processAnnotations(Path.class);
		
		xstream.aliasField(ConfigKeys.JAVA_RUNTIMES_ALIAS, Configuration.class, ConfigKeys.JAVA_RUNTIMES_FIELD);
		xstream.processAnnotations(JavaRuntimes.class);
		xstream.processAnnotations(Java.class);
		
		Object config = null;
		try {
			config = xstream.fromXML(xmlConfiguration);
		} catch (Exception e) {
			throw new ConfigurationException(NodeMessage.JEMC006E.toMessage().getContent(), e);
		}
		if (!(config instanceof Configuration)) {
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.CONFIGURATION_TAG));
		}
		return (Configuration) config;
	}
}