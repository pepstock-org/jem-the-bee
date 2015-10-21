/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigurationException;

/**
 * Are the attributes of the node that will be create inside the JEM_HOME
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class NodeAttributes {

	private static final String NODE_NAME_PREFIX = "node-";

	/**
	 * Template directory, where the environment template lives
	 */
	private static final String TEMPLATE_SOURCE = "src";

	/**
	 * Template directory of the gfs config
	 */
	private static final String TEMPLATE_GFS = "gfs";

	/**
	 * Is the name of the template directory of an environment and is inside the
	 * $JEM_HOME/TEMPLATE_SOURCE installation
	 */
	public static final String TEMPLATE_ENV_DIRECTORY_NAME = "envname";

	/**
	 * Is the name of the template directory of a node and is inside the
	 * $JEM_HOME/TEMPLATE_SOURCE installation
	 */
	public static final String TEMPLATE_NODE_DIRECTORY_NAME = "nodename";

	/**
	 * Is the name of the template directory of a war distribution
	 */
	public static final String TEMPLATE_WAR_DIRECTORY_NAME = "war";

	/**
	 * Is the name of the template directory of web directory inside the gfs
	 */
	public static final String TEMPLATE_WEB_DIRECTORY_NAME = "web";

	/**
	 * Is the name of the template config directory
	 */
	public static final String TEMPLATE_WEB_CONFIG_DIRECTORY_NAME = "WEB-INF/config";

	/**
	 * The name of the war file
	 */
	public static final String TEMPLATE_WAR_FILE_NAME = "jem_gwt.war";

	/**
	 * Is the template directory of an environment and is inside the
	 * $JEM_HOME/TEMPLATE_SOURCE installation
	 */
	private File templateEnvDirectory;

	/**
	 * Is the template directory of a config global file system inside the
	 * $JEM_HOME/TEMPLATE_SOURCE/ENV_NAME installation
	 */
	private File templateGfsConfigDirectory;

	/**
	 * Is the template directory of a node inside the JEM_HOME/TEMPLATE_SOURCE
	 * installation
	 */
	private File templateNodeDirectory;

	// The name of the node to create
	private String nodeName;

	// The name of the environment in which we want to create the node
	private String envName;

	// The jemHome installation folder
	private String jemHome;

	// The directory of gfs configuration
	private File gfsConfigDirectory;

	// The directory of the environment
	private File envDir;

	// The directory where is present the war distribution for the environment
	private File warDir;

	// The directory where is present the config of war distribution for the
	// environment
	private File warConfigDir;

	// The war file for the web application
	private File warFile;

	// The directory of the node
	private File nodeDir;

	// The directory of the output
	private File output;

	// The directory of the node
	private File data;

	// The directory of the node
	private File source;

	// The directory of the node
	private File binary;

	// The directory of the node
	private File classpath;

	// The directory of the node
	private File library;

	// The directory of the node
	private File persistence;

	// the wrapper of the properties set by the user to configure new
	// node/environment
	private NodeProperties nodeProperties;

	/**
	 * @param jemHome
	 * @param nodeProperties the wrapper of the properties set by the user to
	 *            configure new node/environment
	 */
	public NodeAttributes(String jemHome, NodeProperties nodeProperties) {
		super();
		this.nodeProperties = nodeProperties;
		this.jemHome = jemHome;
	}

	/**
	 * Initialized all the variables and verify if the mandatory paths exist.
	 * 
	 * @throws MessageException if mandatory properties does not exist or if
	 *             paths does not exist.
	 */
	public void init() throws MessageException {
		try {
			String fs = "/";
			envName = nodeProperties.getEnvironmentName();
			envDir = new File(jemHome + fs + envName);
			warDir = new File(nodeProperties.getPersistencePath() + fs + envName + fs + TEMPLATE_WEB_DIRECTORY_NAME + fs + TEMPLATE_WAR_DIRECTORY_NAME);
			warConfigDir = new File(warDir.getAbsolutePath() + fs + TEMPLATE_WEB_CONFIG_DIRECTORY_NAME);
			warFile = new File(nodeProperties.getPersistencePath() + fs + envName + fs + TEMPLATE_WEB_DIRECTORY_NAME + fs +TEMPLATE_WAR_FILE_NAME);
			setNodeName(nodeProperties.getNodeName());
			nodeDir = new File(envDir + fs + nodeName);
			templateEnvDirectory = new File(jemHome + fs + TEMPLATE_SOURCE + fs + TEMPLATE_ENV_DIRECTORY_NAME);
			templateNodeDirectory = new File(jemHome + fs + TEMPLATE_SOURCE + fs + TEMPLATE_ENV_DIRECTORY_NAME + fs + TEMPLATE_NODE_DIRECTORY_NAME);
			templateGfsConfigDirectory = new File(jemHome + fs + TEMPLATE_SOURCE + fs + TEMPLATE_GFS + fs + TEMPLATE_ENV_DIRECTORY_NAME);
			output = new File(nodeProperties.getOutputPath());
			if (!output.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getOutputPath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getOutputPath());
			}
			data = new File(nodeProperties.getDataPath());
			if (!data.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getDataPath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getDataPath());
			}
			source = new File(nodeProperties.getSourcePath());
			if (!source.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getSourcePath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getSourcePath());
			}
			binary = new File(nodeProperties.getBinaryPath());
			if (!binary.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getBinaryPath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getBinaryPath());
			}
			classpath = new File(nodeProperties.getClasspath());
			if (!classpath.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getClasspath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getClasspath());
			}
			library = new File(nodeProperties.getLibraryPath());
			if (!library.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getLibraryPath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getLibraryPath());
			}
			persistence = new File(nodeProperties.getPersistencePath());
			if (!persistence.exists()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC067E, nodeProperties.getPersistencePath());
				throw new MessageException(NodeMessage.JEMC067E, nodeProperties.getPersistencePath());
			}
			gfsConfigDirectory = new File(nodeProperties.getPersistencePath() + fs + nodeProperties.getEnvironmentName());
		} catch (ConfigurationException e) {
			throw new MessageException(NodeMessage.JEMC006E, e);
		}
	}

	/**
	 * @return the output path folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getOutput() {
		return output;
	}

	/**
	 * @return the data path folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getData() {
		return data;
	}

	/**
	 * @return the source path folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getSource() {
		return source;
	}

	/**
	 * @return the binary path folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getBinary() {
		return binary;
	}

	/**
	 * @return the classpath folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getClasspath() {
		return classpath;
	}

	/**
	 * @return the library path folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getLibrary() {
		return library;
	}

	/**
	 * @return the persistence path folder
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public File getPersistence() {
		return persistence;
	}

	/**
	 * @return the nodeProperties
	 * @see org.pepstock.jem.node.configuration.Paths
	 */
	public NodeProperties getNodeProperties() {
		return nodeProperties;
	}

	/**
	 * @return the template directory of a global file system configuration that
	 *         is inside the JEM_HOME/src/gfs/envname installation
	 */
	public File getTemplateGfsConfigDirectory() {
		return templateGfsConfigDirectory;
	}

	/**
	 * @return the directory of a global file system configuration
	 */
	public File getGfsConfigDirectory() {
		return gfsConfigDirectory;
	}

	/**
	 * @return the template directory of an environment that is inside the
	 *         JEM_HOME/util installation
	 */
	public File getTemplateEnvDirectory() {
		return templateEnvDirectory;
	}

	/**
	 * @return the template directory of a node that is inside the JEM_HOME/util
	 *         installation
	 */
	public File getTemplateNodeDirectory() {
		return templateNodeDirectory;
	}

	/**
	 * @return the directory of the jem environment
	 */
	public File getEnvDir() {
		return envDir;
	}

	/**
	 * @return the directory where is present the war distribution for the
	 *         environmnet
	 */
	public File getWarDir() {
		return warDir;
	}

	/**
	 * @return the directory where is present the config of war distribution for
	 *         the environmnet
	 */
	public File getWarConfigDir() {
		return warConfigDir;
	}

	/**
	 * @return the war file containing the distribution for the web application
	 *         of JEM
	 */
	public File getWarFile() {
		return warFile;
	}

	/**
	 * @return directory of the jem node
	 */
	public File getNodeDir() {
		return nodeDir;
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName the nodeName to set. If the nodeName is null then the
	 *            nameNode will be set as node-xxx where xxx is a numeric string
	 *            starting from 000 and incremented each time a new node is
	 *            created.
	 */
	private void setNodeName(String nodeName) {
		// if the nodeName is not been set by the user
		if (nodeName == null) {
			// if the environment does not exit
			if (!getEnvDir().exists()) {
				this.nodeName = NODE_NAME_PREFIX + "000";
			} else {
				// get all the directory starting with "node-"
				String[] automaticNodeNames = getEnvDir().list(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.toLowerCase().startsWith(NODE_NAME_PREFIX) && dir.isDirectory();
					}
				});
				// if no one exist
				if (automaticNodeNames == null || automaticNodeNames.length == 0) {
					this.nodeName = NODE_NAME_PREFIX + "000";
				} else {
					List<Integer> list = new ArrayList<Integer>();
					Integer currIndex = 0;
					for (String currName : automaticNodeNames) {
						try {
							currIndex = Integer.valueOf(currName.substring(NODE_NAME_PREFIX.length()));
						} catch (NumberFormatException e) {
							// ignore
						}
						list.add(currIndex);
					}
					Integer maxIndex = Collections.max(list);
					Integer newIndex = maxIndex + 1;
					String newIndexStr = String.format("%03d", newIndex);
					this.nodeName = NODE_NAME_PREFIX + newIndexStr;
				}
			}
		} else {
			// the node name is the one chose by the user
			this.nodeName = nodeName;
		}
	}

	/**
	 * @return the envName
	 */
	public String getEnvName() {
		return envName;
	}

	/**
	 * @return the jemHome
	 */
	public String getJemHome() {
		return jemHome;
	}

}