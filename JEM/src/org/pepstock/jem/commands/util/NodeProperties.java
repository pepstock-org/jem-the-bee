/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone Businaro
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

import java.io.File;
import java.util.Properties;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigurationException;

/**
 * This class contains and get accessed to the properties needed to create a new
 * nodo or a new environmet. The propertie name are the constant of the class.
 * 
 * @author Simone Businaro
 * @version 1.0
 * 
 */
public class NodeProperties {

	/**
	 * property that indicates the max ram size in MB for the jem node : * * * *
	 * * {@value}
	 */
	public static final String JEM_NODE_XMX = "jem.node.Xmx";

	/**
	 * property that indicates default max ram size in MB for the jem node: * *
	 * * {@value}
	 */
	public static final String DEFAULT_NODE_XMX = "512";

	/**
	 * property that indicates if the login module is enables: {@value}
	 */
	public static final String JEM_LOGIN_PROTOCOL_ENABLE_PROP = "jem.login.protocol.enable";

	/**
	 * property that indicates the name of the keystore: {@value}
	 */
	public static final String JEM_KEYSTORE_NAME_PROP = "jem.keystore.name";

	/**
	 * property that indicates the password of the keystoret: {@value}
	 */
	public static final String JEM_KEYSTORE_PWD_PROP = "jem.keystore.pwd";

	/**
	 * property that indicates the password for the crypt key in the keystore: *
	 * * {@value}
	 */
	public static final String JEM_CRYPT_KEY_PWD_PROP = "jem.crypt.key.pwd";

	/**
	 * property that indicates the name of the environment: {@value}
	 */
	public static final String DEFAULT_NOT_SET_PROP = "|NOT_SET|";

	/**
	 * property that indicates the name of the environment: {@value}
	 */
	public static final String JEM_ENVIRONMENT_NAME_PROP = "jem.environment.name";

	/**
	 * property that indicates the password needed to connect to the hazelcast
	 * environment: {@value}
	 */
	public static final String JEM_ENVIRONMENT_PASSWORD_PROP = "jem.environment.password";

	/**
	 * property that indicates the name of the node: {@value}
	 */
	public static final String JEM_NODE_NAME_PROP = "jem.node.name";

	/**
	 * property that indicates the name of the domain: {@value}
	 */
	public static final String JEM_DOMAIN_PROP = "jem.domain";

	/**
	 * property that indicates the affinity: {@value}
	 */
	public static final String JEM_AFFINITY_PROP = "jem.affinity";

	/**
	 * property that indicates the output path see: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_OUTPUT_PROP = "jem.output";

	/**
	 * property that indicates the data path: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_DATA_PROP = "jem.data";

	/**
	 * property that indicates the source path: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_SOURCE_PROP = "jem.source";

	/**
	 * property that indicates the binary path: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_BINARY_PROP = "jem.binary";

	/**
	 * property that indicates the classpath: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_CLASSPATH_PROP = "jem.classpath";

	/**
	 * property that indicates the library path: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_LIBRARY_PROP = "jem.library";

	/**
	 * property that indicates the persistence path: {@value}
	 * 
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public static final String JEM_PERSISTENCE_PROP = "jem.persistence";

	/**
	 * property that indicates multicast group in hazelcast configuration: * * *
	 * * {@value}
	 */
	public static final String JEM_MULTICAST_GROUP_PROP = "jem.multicast.group";

	/**
	 * property that indicates multicast port in hazelcast configuration: * * *
	 * * {@value}
	 */
	public static final String JEM_MULTICAST_PORT_PROP = "jem.multicast.port";

	/**
	 * property that indicates autoincrement port in hazelcast configuration: *
	 * * {@value}
	 */
	public static final String JEM_PORT_AUTOINCREMENT_PROP = "jem.port.autoincrement";

	/**
	 * property that indicates if hazelcast interfaces are enabled {@value}
	 */
	public static final String JEM_INTERFACES_ENABLE = "jem.interfaces.enable";

	/**
	 * property that indicates the hazelcast interface to be used {@value}
	 */
	public static final String JEM_INTERFACE = "jem.interface";

	/**
	 * property that indicates the driver for the db where the hazelcast map are
	 * persisted {@value}
	 */
	public static final String JEM_DB_DRIVER = "jem.db.driver";

	/**
	 * property that indicates the url for the db where the hazelcast map are
	 * persisted {@value}
	 */
	public static final String JEM_DB_URL = "jem.db.url";

	/**
	 * property that indicates the user for the db where the hazelcast map are
	 * persisted {@value}
	 */
	public static final String JEM_DB_USER = "jem.db.user";

	/**
	 * property that indicates the default user for the db where the hazelcast
	 * map are persisted {@value}
	 */
	public static final String DEFAULT_JEM_DB_USER = "root";

	/**
	 * property that indicates the driver for the db where the hazelcast map are
	 * persisted {@value}
	 */
	public static final String JEM_DB_PASSWORD = "jem.db.password";

	/**
	 * value for default multicast group: {@value}
	 */
	public static final String DEFAULT_MULTICAST_GROUP = "233.0.0.1";

	/**
	 * value for default multicast port: {@value}
	 */
	public static final String DEFAULT_MULTICAST_PORT = "54327";

	/**
	 * value for default auto increment port: {@value}
	 */
	public static final String DEFAULT_AUTOINCREMENT_PORT = "5710";

	/**
	 * value for default interface: {@value}
	 */
	public static final String DEFAULT_INTERFACE = "10.10.1.*";

	Properties properties;

	/**
	 * 
	 * @param properties the properties passed by the user to configure new node
	 *            and/or environment
	 */
	public NodeProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * check if mandatory property are setted
	 * 
	 * @throws ConfigurationException if any mandatory property is not setteds
	 * 
	 */
	public void checkMandatoryTag() throws ConfigurationException {
		getEnvironmentName();
		getBinaryPath();
		getClasspath();
		getCryptKeyPwd();
		getDataPath();
		getEnvironmentPassword();
		getJemDbDriver();
		getJemDbUrl();
		getKeystorePwd();
		getLibraryPath();
		getOutputPath();
		getPersistencePath();
		getSourcePath();
	}

	/**
	 * 
	 * @return the environment name set in property:
	 *         {@value #JEM_ENVIRONMENT_NAME_PROP}
	 * @throws ConfigurationException if property is null
	 */
	public String getEnvironmentName() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_ENVIRONMENT_NAME_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_ENVIRONMENT_NAME_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_ENVIRONMENT_NAME_PROP));
	}

	/**
	 * 
	 * @return the environment name set in property:
	 *         {@value #JEM_ENVIRONMENT_PASSWORD_PROP}
	 * @throws ConfigurationException if property is null
	 */
	public String getEnvironmentPassword() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_ENVIRONMENT_PASSWORD_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_ENVIRONMENT_PASSWORD_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_ENVIRONMENT_PASSWORD_PROP));
	}

	/**
	 * 
	 * @return the node name set in property: {@value #JEM_NODE_NAME_PROP}
	 */
	public String getNodeName() {
		String propValue = properties.getProperty(JEM_NODE_NAME_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		}
		return null;
	}

	/**
	 * 
	 * @return the domain set in property: {@value #JEM_DOMAIN_PROP}
	 */
	public String getDomain() {
		String propValue = properties.getProperty(JEM_DOMAIN_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		} else {
			return Jcl.DEFAULT_DOMAIN;
		}
	}

	/**
	 * 
	 * @return the affinity set in property {@value #JEM_AFFINITY_PROP}
	 */
	public String getAffinity() {
		String propValue = properties.getProperty(JEM_AFFINITY_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		} else {
			return Jcl.DEFAULT_AFFINITY;
		}
	}

	/**
	 * 
	 * @return the output path set in property {@value #JEM_OUTPUT_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getOutputPath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_OUTPUT_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_OUTPUT_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_OUTPUT_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_OUTPUT_PROP));
	}

	/**
	 * 
	 * @return the data path set in property {@value #JEM_DATA_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getDataPath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_DATA_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_DATA_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_DATA_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_DATA_PROP));
	}

	/**
	 * 
	 * @return the source path set in property {@value #JEM_SOURCE_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getSourcePath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_SOURCE_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_SOURCE_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_SOURCE_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_SOURCE_PROP));
	}

	/**
	 * 
	 * @return the binary path set in property {@value #JEM_BINARY_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getBinaryPath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_BINARY_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_BINARY_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_BINARY_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_BINARY_PROP));
	}

	/**
	 * 
	 * @return the class path set in property {@value #JEM_CLASSPATH_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getClasspath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_CLASSPATH_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_CLASSPATH_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_CLASSPATH_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_CLASSPATH_PROP));
	}

	/**
	 * 
	 * @return the class path set in property {@value #JEM_LIBRARY_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getLibraryPath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_LIBRARY_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_LIBRARY_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_LIBRARY_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_LIBRARY_PROP));
	}

	/**
	 * 
	 * @return the persistence path set in property
	 *         {@value #JEM_PERSISTENCE_PROP}
	 * @see org.pepstock.jem.node.configuration.Paths
	 * @throws ConfigurationException if property is not set and if the path does not exist
	 *             or contains spaces
	 */
	public String getPersistencePath() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_PERSISTENCE_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			checkPath(propValue, JEM_PERSISTENCE_PROP);
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_PERSISTENCE_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_PERSISTENCE_PROP));
	}

	/**
	 * 
	 * @return the multicast group set in property
	 *         {@value #JEM_MULTICAST_GROUP_PROP}
	 *         <p>
	 *         if the property is not set, return the default value
	 *         {@value #DEFAULT_MULTICAST_GROUP}
	 * 
	 */
	public String getMulticastGroup() {
		String propValue = properties.getProperty(JEM_MULTICAST_GROUP_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		} else {
			return DEFAULT_MULTICAST_GROUP;
		}
	}

	/**
	 * 
	 * @return the multicast port set in property
	 *         {@value #JEM_MULTICAST_PORT_PROP} if the property is not set,
	 *         return the default value {@value #DEFAULT_MULTICAST_PORT}
	 */
	public String getMulticastPort() {
		String propValue = properties.getProperty(JEM_MULTICAST_PORT_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		} else {
			return DEFAULT_MULTICAST_PORT;
		}
	}

	/**
	 * 
	 * @return the auto increment port set in property
	 *         {@value #JEM_PORT_AUTOINCREMENT_PROP} if the property is not set,
	 *         return the default value {@value #JEM_PORT_AUTOINCREMENT_PROP}
	 */
	public String getAutoincrementPort() {
		String propValue = properties.getProperty(JEM_PORT_AUTOINCREMENT_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		} else {
			return DEFAULT_AUTOINCREMENT_PORT;
		}
	}

	/**
	 * 
	 * @return true if the {@value #JEM_LOGIN_PROTOCOL_ENABLE_PROP} is set to
	 *         true, false otherwise
	 */
	public boolean isLoginModuleEnable() {
		String propValue = properties.getProperty(JEM_LOGIN_PROTOCOL_ENABLE_PROP);
		if ("true".equalsIgnoreCase(propValue)){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return the name of the keystore setted in the property
	 *         {@value #JEM_KEYSTORE_NAME_PROP}, default is
	 *         {@value #DEFAULT_JEM_KEYSTORE_NAME}
	 * @throws ConfigurationException
	 */
	public String getKeystoreName() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_KEYSTORE_NAME_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue + "_" + getEnvironmentName() + ".keystore";
		} else {
			return getEnvironmentName() + ".keystore";
		}
	}

	/**
	 * 
	 * @return the name of the user keystore name that contains the user
	 *         certificates
	 * @throws ConfigurationException
	 */
	public String getUserKeystoreName() throws ConfigurationException {
		return "user_certificates_" + getEnvironmentName() + ".keystore";
	}

	/**
	 * 
	 * @return the password of the keystore setted in the property
	 *         {@value #JEM_KEYSTORE_PWD_PROP}
	 * @throws ConfigurationException if property is not set
	 */
	public String getKeystorePwd() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_KEYSTORE_PWD_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_KEYSTORE_PWD_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_KEYSTORE_PWD_PROP));
	}

	/**
	 * 
	 * @return the password of the symmetric key used for encryption setted in
	 *         the property {@value #JEM_CRYPT_KEY_PWD_PROP}, default is
	 *         {@value #DEFAULT_NOT_SET_PROP}
	 * @throws ConfigurationException if property is not set
	 */
	public String getCryptKeyPwd() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_CRYPT_KEY_PWD_PROP);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_CRYPT_KEY_PWD_PROP);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_CRYPT_KEY_PWD_PROP));

	}

	/**
	 * 
	 * @return the the max ram size in MB for the jem node setted in the
	 *         property {@value #JEM_NODE_XMX}, default is
	 *         {@value #DEFAULT_NODE_XMX}
	 */
	public String getNodeXmx() {
		String propValue = properties.getProperty(JEM_NODE_XMX);
		if (propValue != null && !"".equals(propValue.trim())) {
			return propValue;
		} else {
			return DEFAULT_NODE_XMX;
		}
	}

	/**
	 * 
	 * @return true if the {@value #JEM_INTERFACE} is set , false otherwise
	 */
	public boolean isInterfacesEnable() {
		String propValue = properties.getProperty(JEM_INTERFACE);
		if (propValue != null && !"".equals(propValue.trim())){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return the the interface setted in the property {@value #JEM_INTERFACE},
	 *         default is {@value #DEFAULT_INTERFACE}
	 */
	public String getInterface() {
		String propValue = properties.getProperty(JEM_INTERFACE);
		if (propValue != null && !"".equals(propValue.trim())){
			return propValue;
		}
		return DEFAULT_INTERFACE;
	}

	/**
	 * 
	 * @return the the db driver setted in the property {@value #JEM_DB_DRIVER}
	 * @throws ConfigurationException if the prperty is not been set
	 */
	public String getJemDbDriver() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_DB_DRIVER);
		if (propValue != null && !"".equals(propValue.trim())){
			return propValue.trim();
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_DB_DRIVER);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_DB_DRIVER));
	}

	/**
	 * 
	 * @return the the db url setted in the property {@value #JEM_DB_URL}
	 * @throws ConfigurationException if the prperty is not been set
	 */
	public String getJemDbUrl() throws ConfigurationException {
		String propValue = properties.getProperty(JEM_DB_URL);
		if (propValue != null && !"".equals(propValue.trim())){
			return propValue.trim();
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC066E, JEM_DB_URL);
		throw new ConfigurationException(NodeMessage.JEMC066E.toMessage().getFormattedMessage(JEM_DB_URL));
	}

	/**
	 * 
	 * @return the the db user setted in the property {@value #JEM_DB_USER}
	 */
	public String getJemDbUser() {
		String propValue = properties.getProperty(JEM_DB_USER);
		if (propValue != null && !"".equals(propValue.trim())){
			return propValue.trim();
		} else {
			return DEFAULT_JEM_DB_USER;
		}
	}

	/**
	 * 
	 * @return the the db password setted in the property
	 *         {@value #JEM_DB_PASSWORD}
	 */
	public String getJemDbPassword() {
		String propValue = properties.getProperty(JEM_DB_PASSWORD);
		if (propValue != null){
			return propValue.trim();
		}
		return "";
	}

	/**
	 * checks for empty space inside propValue and if it find them throw an
	 * exception
	 * 
	 * @param propValue
	 * @throws ConfigurationException propValue contains spaces
	 */
	private void checkPath(String propValue, String propName) throws ConfigurationException {
		if (propValue.contains(" ")) {
			LogAppl.getInstance().emit(NodeMessage.JEMC087E, propValue, propName);
			throw new ConfigurationException(NodeMessage.JEMC087E.toMessage().getFormattedMessage(propValue, propName));
		}
		File path = new File(propValue);
		if (!path.exists()) {
			LogAppl.getInstance().emit(NodeMessage.JEMC088E, propValue, propName);
			throw new ConfigurationException(NodeMessage.JEMC088E.toMessage().getFormattedMessage(propValue, propName));
		}
	}
}
