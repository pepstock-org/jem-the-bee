/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Andrea "Stock" Stocchero
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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigurationException;

/**
 * Is the class responsable of the upadte of the configuration file during the
 * creation of a new node or a new environmnet with a new node. Remember that a
 * default environment has the following folder structure: <br>
 * <ul>
 * <li>envname</li>
 * <ul>
 * <li>bin</li>
 * <ul>
 * <li>jem-env.cmd
 * <li>jem-env.sh
 * </ul>
 * <li>config</li>
 * <ul>
 * <li>jem-env-hazelcast.xml</li>
 * <li>wrapper.conf</li>
 * </ul>
 * <li>nodename</li>
 * <ul>
 * <li>bin</li>
 * <ul>
 * <li>jem.cmd</li>
 * <li>jem.sh</li>
 * <li>jem-setenv.cmd</li>
 * <li>jem-setenv.sh</li>
 * </ul>
 * <li>config</li>
 * <ul>
 * <li>jem-node.xml</li>
 * <li>wrapper.conf</li>
 * </ul>
 * <li>logs</li> <li>policy</li>
 * <p>
 * in all these files will be substitutes the string #[variable.name] with the
 * value chose by the user
 */
public class ConfsUpdater {

	/**
	 * Is the node attributes that define the folders of the new nodes
	 */
	private NodeAttributes nodeAttributes;

	/**
	 * @param nodeAttributes
	 */
	public ConfsUpdater(NodeAttributes nodeAttributes) {
		this.nodeAttributes = nodeAttributes;
	}

	/**
	 * Updates the config/bin files of the new environment those files are:<br>
	 * <ul>
	 * <li>[gfs]/[environment]/config/jem-env-hazelcast.xml</li>
	 * <li>[gfs]/[environment]/config/jem-env.xml</li>
	 * <li>[environment]/config/wrapper.cong</li>
	 * 
	 * @throws MessageException if any exception occurs
	 */
	public void updateEnvConfigs() throws MessageException {
		try {
			String fs = System.getProperty("file.separator");
			// update jem-env-hazelcast.xml
			String hazelcastXmlName = "jem-env-hazelcast.xml";
			String hazelcastXml = nodeAttributes.getGfsConfigDirectory().getAbsolutePath() + fs + "config" + fs + hazelcastXmlName;
			updateFile(new File(hazelcastXml));
			// copy file also in the web distribution
			File src = new File(hazelcastXml);
			File dest = new File(nodeAttributes.getWarDir().getPath() + fs + "WEB-INF" + fs + "config" + fs + hazelcastXmlName);
			FileUtils.copyFile(src, dest);
			// update jem-env.xml
			String jemEnvXmlName = "jem-env.xml";
			String jemEnvXml = nodeAttributes.getGfsConfigDirectory().getAbsolutePath() + fs + "config" + fs + jemEnvXmlName;
			updateFile(new File(jemEnvXml));
			// update env-common.conf 
			String envCommonName = "env_common.conf";
			String envCommon = nodeAttributes.getEnvDir().getAbsolutePath() + fs + "config" + fs + envCommonName;
			updateFile(new File(envCommon));
		} catch (IOException e) {
			throw new MessageException(NodeMessage.JEMC006E, e);
		}
	}

	/**
	 * Updates the config/bin files of the new node those files are:<br>
	 * <ul>
	 * <li>[environment]/[node]/bin/jem.sh</li>
	 * <li>[environment]/[node]/config/jem-node.xml</li>
	 * <li>[environment]/[node]/config/node_wrapper.conf</li>
	 * 
	 * @throws MessageException if any exception occurs
	 */
	public void updateNodeConfigs() throws MessageException {
		String fs = System.getProperty("file.separator");
		// update jem.sh
		String jemsh = nodeAttributes.getNodeDir() + fs + "bin" + fs + "jem.sh";
		updateFile(new File(jemsh));
		// update jem-node.xml
		String jemNodeXml = nodeAttributes.getNodeDir() + fs + "config" + fs + "jem-node.xml";
		updateFile(new File(jemNodeXml));
		// update node_wrapper.conf
		String wrapperConf = nodeAttributes.getNodeDir() + fs + "config" + fs + "node_wrapper.conf";
		updateFile(new File(wrapperConf));
	}

	/**
	 * update the template file passed as parameter setting the value of the
	 * variable to substitute
	 * 
	 * @param fileToUpdate
	 * @throws MessageException if any exception occurs
	 */
	private void updateFile(File fileToUpdate) throws MessageException {
		try {
			String content = FileUtils.readFileToString(fileToUpdate);
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_AFFINITY_PROP), nodeAttributes.getNodeProperties().getAffinity());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_BINARY_PROP), nodeAttributes.getNodeProperties().getBinaryPath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_CLASSPATH_PROP), nodeAttributes.getNodeProperties().getClasspath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_DATA_PROP), nodeAttributes.getNodeProperties().getDataPath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_DOMAIN_PROP), nodeAttributes.getNodeProperties().getDomain());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_ENVIRONMENT_NAME_PROP), nodeAttributes.getNodeProperties().getEnvironmentName());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_ENVIRONMENT_PASSWORD_PROP), nodeAttributes.getNodeProperties().getEnvironmentPassword());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_LIBRARY_PROP), nodeAttributes.getNodeProperties().getLibraryPath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_MULTICAST_GROUP_PROP), nodeAttributes.getNodeProperties().getMulticastGroup());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_MULTICAST_PORT_PROP), nodeAttributes.getNodeProperties().getMulticastPort());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_NODE_NAME_PROP), nodeAttributes.getNodeName());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_OUTPUT_PROP), nodeAttributes.getNodeProperties().getOutputPath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_PERSISTENCE_PROP), nodeAttributes.getNodeProperties().getPersistencePath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_PORT_AUTOINCREMENT_PROP), nodeAttributes.getNodeProperties().getAutoincrementPort());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_SOURCE_PROP), nodeAttributes.getNodeProperties().getSourcePath());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_LOGIN_PROTOCOL_ENABLE_PROP), Boolean.toString(nodeAttributes.getNodeProperties().isLoginModuleEnable()));
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_KEYSTORE_NAME_PROP), nodeAttributes.getNodeProperties().getKeystoreName());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_KEYSTORE_PWD_PROP), nodeAttributes.getNodeProperties().getKeystorePwd());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_CRYPT_KEY_PWD_PROP), nodeAttributes.getNodeProperties().getCryptKeyPwd());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_NODE_XMX), nodeAttributes.getNodeProperties().getNodeXmx());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_INTERFACES_ENABLE), Boolean.toString(nodeAttributes.getNodeProperties().isInterfacesEnable()));
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_INTERFACE), nodeAttributes.getNodeProperties().getInterface());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_DB_DRIVER), nodeAttributes.getNodeProperties().getJemDbDriver());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_DB_URL), nodeAttributes.getNodeProperties().getJemDbUrl());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_DB_USER), nodeAttributes.getNodeProperties().getJemDbUser());
			content = content.replace(getVariableSubstituteFormat(NodeProperties.JEM_DB_PASSWORD), nodeAttributes.getNodeProperties().getJemDbPassword());
			FileUtils.writeStringToFile(fileToUpdate, content);
		} catch (IOException e) {
			throw new MessageException(NodeMessage.JEMC006E, e);
		} catch (ConfigurationException e) {
			throw new MessageException(NodeMessage.JEMC006E, e);
		}
	}

	/**
	 * Convert the variable name in #[variable name] that is the format we used
	 * in the template configuration to set the variable to substitute when we
	 * generate a new node with the CreateNode command
	 */
	private String getVariableSubstituteFormat(String variableName) {
		return "#[" + variableName + "]";
	}
}