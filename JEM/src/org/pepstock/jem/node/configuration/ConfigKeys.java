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
package org.pepstock.jem.node.configuration;

/**
 * This interface contains all keys used on configuration files and for
 * environment variables
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class ConfigKeys {
	/**
	 * Is the variable name to use in command line to configure HAZELCAST node.<br>
	 * The value of property is configuration file name.<br>
	 */
	public static final String HAZELCAST_CONFIG = "hazelcast.config";
	
	/**
	 * Is the variable name of property which must be true to have IPv4 instead of v6.<br>
	 * IPv6 has same problems with Hazelcast
	 */
	public static final String IPV4 = "java.net.preferIPv4Stack";

	/**
	 * Is the variable name to use in command line to start JEM node.<br>
	 * The value of property is configuration file name.<br>
	 * Example:<br>
	 * <code>-Djem.config=jem-node.xml</code>
	 */
	public static final String JEM_CONFIG = "jem.config";
	
	/**
	 * Is the variable name to use in command line to start JEM node.<br>
	 * The value of property is configuration file name present in the gfs.<br>
	 * Example:<br>
	 * <code>-Djem.env=jem-env.xml</code>
	 */
	public static final String JEM_ENV_CONF = "jem.env";
	
	/**
	 * Is the variable name to use to store the configuration folder of JEM environment.
	 */
	public static final String JEM_ENV_CONF_FOLDER = "jem.env.config.folder";
	
	/**
	 * Is the variable name to use to start JEM node in maintanance.<br>
	 * The value of property is boolean. Default is false.<br>
	 * Example:<br>
	 * <code>-Djem.access.maint=true</code>
	 */
	public static final String JEM_ACCESS_MAINT = "jem.access.maint";
	
	/**
	 * Is the variable name to use to start checking the version of cluster.<br>
	 * The value of property is boolean. Set to true, if the node doesn't have the same version of cluster, it ends with exception. Default is false.<br>
	 * Example:<br>
	 * <code>-Djem.check.version=true</code>
	 */
	public static final String JEM_CHECK_VERSION = "jem.check.version";

	/**
	 * Is the variable that define the root folder of the Jem installation, it
	 * must be set in the OS system variable.
	 */
	public static final String JEM_HOME = "JEM_HOME";

	/**
	 * Is the variable that define the path of the environment and is set when
	 * the node is launched. This variable is than put in the System properties
	 * for future use inside other configuration.
	 */
	public static final String JEM_ENVIRONMENT = "JEM_ENVIRONMENT";

	/**
	 * Is the variable that define the path of the current node and is set when
	 * the node is launched. This variable is than put in the System properties
	 * for future use inside other configuration.
	 */
	public static final String JEM_NODE = "JEM_NODE";

	/**
	 * If the LEM node is started with the YAJSW because of some bug of YAJSW
	 * all the environment variable name are set to lower case and
	 * JEM_ENVIRONMENT is going to be wrapper.app.env.jem_environment, JEM_NODE
	 * is going to be wrapper.app.env.jem_node and JEM_GFS is going to be
	 * wrapper.app.env.jem_gfs (This because those variable are set in the
	 * wrapper.conf while JAVA_HOME is set in the OS).
	 */
	public static final String WRAPPER_APP_ENV = "wrapper.app.env.";
	
	/**
	 * Is the environment variable name to use to pass the property to job
	 * execution, for data description for data (set only if dataPaths count is 1), otherwise doesn't exist
	 */
	public static final String JEM_DATA_PATH_NAME = "jem.data";

	/**
	 * Is the environment variable name to use to pass the property to job
	 * execution, for data description for sysout
	 */
	public static final String JEM_OUTPUT_PATH_NAME = "jem.output";

	/**
	 * Is the environment variable name to use to pass the property to job
	 * execution, for path where the source files, that can be included at
	 * runtime in the jcl,are stored
	 */
	public static final String JEM_SOURCE_PATH_NAME = "jem.source";

	/**
	 * Is the environment variable name to use to pass the property to job
	 * execution, for path where the binary files are stored
	 */
	public static final String JEM_BINARY_PATH_NAME = "jem.binary";

	/**
	 * Is the environment variable name to use to pass the property to job
	 * execution, for path where the jars and zip are stored
	 */
	public static final String JEM_CLASSPATH_PATH_NAME = "jem.classpath";

	/**
	 * Is the environment variable name to use to pass the property to job
	 * execution, for path where the all the native system libraries needed by
	 * the executable files are stored
	 */
	public static final String JEM_LIBRARY_PATH_NAME = "jem.library";

	/**
	 * Is the environment variable name for path where the db files, needed for
	 * the persistence of the clusters queue and maps, are stored
	 */
	public static final String JEM_PERSISTENCE_PATH_NAME = "jem.persistence";
	
	/**
	 * Is the environment variable name for job ID
	 */
	public static final String JEM_JOB_ID = "jem.job.id";

	/**
	 * Tag name used for configuration object (the root of configuration XML
	 * file).
	 */
	public static final String CONFIGURATION_TAG = "configuration";

	/**
	 * Tag name used for execution environment. Is an alias, to do not use the
	 * real instance name
	 * 
	 * @see ConfigKeys#EXECUTION_ENVIRONMENT_FIELD
	 */
	public static final String EXECUTION_ENVIRONMENT_ALIAS = "execution-environment";

	/**
	 * Field name use for execution environment. In XML file, there is the alias
	 * <code>execution-environment</code>
	 * 
	 * @see ConfigKeys#EXECUTION_ENVIRONMENT_ALIAS
	 */
	public static final String EXECUTION_ENVIRONMENT_FIELD = "executionEnviroment";
	
	/**
	 * Tag name used for datasets rules. Is an alias, to do not use the
	 * real instance name
	 * 
	 */
	public static final String DATASETS_RULES_ALIAS = "datasetsRules";

	/**
	 * Tag name used for datasets rules. Is field name, equals to alias
	 * 
	 */
	public static final String DATASETS_RULES_FIELD = "datasetsRules";
	
	/**
	 * Property name used for datasets rules.
	 * 
	 */
	public static final String DATASETS_RULES = "datasetsRules";

	/**
	 * Tag name used for rules main element
	 * 
	 */
	public static final String RULES_ALIAS = "rules";
	
	/**
	 * Tag name used for list of patterns
	 * 
	 */
	public static final String PATTERNS_ALIAS = "patterns";

	/**
	 * Tag name used for parallel jobs. Is an alias, to do not use the
	 * real instance name
	 * 
	 * @see ConfigKeys#PARALLEL_JOBS_FIELD
	 */
	public static final String PARALLEL_JOBS_ALIAS = "parallel-jobs";

	/**
	 * Field name use for parallel jobs. In XML file, there is the alias
	 * <code>parallel-jobs</code>
	 * 
	 * @see ConfigKeys#PARALLEL_JOBS_ALIAS
	 */
	public static final String PARALLEL_JOBS_FIELD = "parallelJobs";
	
	/**
	 * Tag name used for paths
	 */
	public static final String PATHS_ELEMENT = "paths";

	/**
	 * Attribute name alias for <code>name</code> fields
	 */
	public static final String NAME_ATTRIBUTE_ALIAS = "name";

	/**
	 * Attribute name for <code>name</code> fields
	 */
	public static final String NAME_FIELD = "name";
	
	/**
	 * Tag name used for factories
	 */
	public static final String FACTORIES_ELEMENT = "factories";

	/**
	 * Alias used for factory
	 */
	public static final String FACTORY_ALIAS = "factory";

	/**
	 * Alias used for affinity factory
	 */
	public static final String AFFINITY_FACTORY_ALIAS = "affinity-factory";

	/**
	 * Alias used for node class to load
	 */
	public static final String NODE_ALIAS = "node";

	/**
	 * Alias used for listener
	 */
	public static final String LISTENER_ALIAS = "listener";

	/**
	 * Alias for <code>className</code> fields
	 */
	public static final String CLASS_NAME_ATTRIBUTE_ALIAS = "className";
	
	/**
	 * Attribute name for <code>className</code> fields
	 */
	public static final String CLASS_NAME_FIELD = "className";

	/**
	 * Alias used for database class to load
	 */
	public static final String DATABASE_ELEMENT = "database";
	
	/**
	 * Alias used for statistics manager class to load
	 */
	public static final String STATISTICS_MANAGER_ALIAS = "statistics-manager";

	/**
	 * Alias used for statistics manager class to load
	 */
	public static final String STATISTICS_MANAGER_FIELD = "statsManager";

	/**
	 * Alias for <code>path</code> fields
	 */
	public static final String PATH_ATTRIBUTE_ALIAS = "path";
	
	/**
	 * Attribute name for <code>path</code> fields
	 */
	public static final String PATH_FIELD = "path";
	
	/**
	 * Alias for <code>data</code> element
	 */
	public static final String DATA_ALIAS = "data";
	
	/**
	 * Tag name for <code>data</code> element
	 */
	public static final String DATA_ELEMENT = "data";
	
	/**
	 * Alias for <code>enable</code> fields
	 */
	public static final String ENABLE_ATTRIBUTE_ALIAS = "enable";
	
	/**
	 * Attribute name for <code>path</code> fields
	 */
	public static final String ENABLE_FIELD = "enable";

	/**
	 * Tag name used for environment inside of execution environment element
	 */
	public static final String ENVIRONMENT = "environment";

	/**
	 * Tag name used for domain inside of execution environment element
	 */
	public static final String DOMAIN = "domain";

	/**
	 * Tag name used for affinity inside of execution environment element
	 */
	public static final String AFFINITY = "affinity";

	/**
	 * Alias used for resource
	 */
	public static final String RESOURCE_ALIAS = "resource";

	/**
	 * Alias used for resources
	 */
	public static final String RESOURCES_ALIAS = "resources";
	
	/**
	 * Alias used for resource
	 */
	public static final String RESOURCE_DEFINITION_ALIAS = "resource-definition";
	
	/**
	 * Alias used for resource
	 */
	public static final String RESOURCES_DEFINITION_ALIAS = "resources-definition";
	
	/**
	 * Alias used for resource
	 */
	public static final String RESOURCES_ELEMENT = "resources";

	/**
	 * Alias used for resources
	 */
	public static final String RESOURCE_DEFINITIONS_ALIAS = "resource-definitions";

	/**
	 * Field name use for resources def inside <code>Configuration</code>. In XML file, there is the alias
	 * <code>resources-def</code>
	 */
	public static final String RESOURCE_DEFINITIONS_FIELD = "resourceDefinitions";
	
	/**
	 * Field name use for resources to define <code>customProperties</code>. In XML file, there is the alias
	 * <code>customProperties</code>
	 */
	public static final String RESOURCE_CUSTOM_PROPERTIES_FIELD = "customProperties";
	
	/**
	 * Attribute name for <code>type</code> fields
	 */
	public static final String TYPE_FIELD = "type";

	/**
	 * Attribute alias for <code>type</code> fields
	 */
	public static final String TYPE_ATTRIBUTE_ALIAS = "type";

	/**
	 * Attribute name for <code>user</code> fields
	 */
	public static final String USER_FIELD = "user";

	/**
	 * Attribute alias for <code>user</code> fields
	 */
	public static final String USER_ATTRIBUTE_ALIAS = "user";

	/**
	 * Attribute name for <code>lastModified</code> fields
	 */
	public static final String LAST_MODIFIED_FIELD = "lastModified";

	/**
	 * Attribute alias for <code>lastModified</code> fields
	 */
	public static final String LAST_MODIFIED_ATTRIBUTE_ALIAS = "lastModified";

	
	/**
	 * Attribute name for <code>properties</code> fields
	 */
	public static final String PROPERTIES_FIELD = "properties";

	/**
	 * Attribute alias for <code>property</code> fields
	 */
	public static final String PROPERTY_ATTRIBUTE_ALIAS = "property";

	/**
	 * is the section inside the manifest relative to the jem
	 */
	public static final String JEM_MANIFEST_SECTION = "JEM_the_BEE";

	/**
	 * is the name of the attribute inside the manifest relative to the jem
	 * version
	 */
	public static final String JEM_MANIFEST_VERSION = "Jem_version";

	/**
	 * is the name of the attribute inside the manifest relative to the creation
	 * timestamp
	 */
	public static final String JEM_MANIFEST_CREATION_TIME = "Creation_time";

	/**
	 * java version
	 */
	public static final String JAVA_VERSION = "java.version";

	/**
	 * java home
	 */
	public static final String JAVA_HOME = "java.home";

	/**
	 * java vendor
	 */
	public static final String JAVA_VENDOR = "java.vendor";

	/**
	 * java user name
	 */
	public static final String JAVA_USER_NAME = "user.name";

	/**
	 * To avoid any instantiation
	 */
	private ConfigKeys() {
	}
	
}
