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
package org.pepstock.jem.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.commands.util.ConfsUpdater;
import org.pepstock.jem.commands.util.NodeAttributes;
import org.pepstock.jem.commands.util.NodeProperties;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.security.keystore.Factory;
import org.pepstock.jem.node.security.keystore.KeyStoreInfo;
import org.pepstock.jem.node.security.keystore.KeyStoreUtil;

/**
 * Create a node on a jem installation. Is a command (to execute by command
 * line) with 1 arguments that is mandatory <br>
 * <code>-properties [string]</code> mandatory, indicates the file of properties
 * used by the user to configure the new node and or environment. A template of
 * properties file is present under the jem_home/bin folder where are documented
 * all the properties needed <br>
 * The creation will use a standard copy of a jem node/environment that is
 * present in the JEM_HOME/src making the proper modification to the config
 * files and the shell scripts. It's possible to have help from command line by
 * <code>-help</code> argument. If a node belong to a new environment a new
 * environment will be created as well with the web distribution in the form of
 * a war file<br>
 * 
 * <br>
 * <b>CreateNode -properties ... </b><br>
 * <br>
 * Is possible to have help from command line by <code>-help</code> argument.<br>
 * 
 * @author Simone Businaro
 * @version 1.0
 * 
 */
public class CreateNode {

	/**
	 * Key for the url for global file system
	 */
	private static final String PROPERTIES = "properties";

	/**
	 * To avoid any instantiation
	 */
	private CreateNode() {
		
	}

	/**
	 * Create the new nodes
	 * 
	 * @param args
	 * @throws MessageException if any exception occurs during the creation of the node
	 * @throws ConfigurationException 
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws KeyStoreException 
	 */
	public static void main(final String[] args) throws MessageException, ParseException, IOException, ConfigurationException, KeyStoreException {

		NodeAttributes nodeAttributes = checkArguments(args);
		nodeAttributes.init();
		if (nodeAttributes.getNodeDir().exists()) {
			LogAppl.getInstance().emit(NodeMessage.JEMC060E, nodeAttributes.getNodeDir().getAbsolutePath());
			throw new MessageException(NodeMessage.JEMC060E, nodeAttributes.getNodeDir().getAbsolutePath());
		}
		if (nodeAttributes.getEnvDir().exists()) {
			createNode(nodeAttributes);
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "Node", nodeAttributes.getNodeDir().getAbsolutePath());
		} else {
			createEnvironment(nodeAttributes);
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "Environment", nodeAttributes.getEnvDir().getAbsolutePath());
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "Node", nodeAttributes.getNodeDir().getAbsolutePath());
		}
	}

	/**
	 * Create the new environment with the new node
	 * 
	 * @param nodeAttributes
	 * @throws MessageException if any exception occurs
	 * @throws IOException 
	 * @throws ConfigurationException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws UnrecoverableKeyException 
	 */
	private static void createEnvironment(NodeAttributes nodeAttributes) throws IOException, ConfigurationException, KeyStoreException, MessageException {
		NodeProperties np = nodeAttributes.getNodeProperties();
		// copy al the template environment directory inside JEM_HOME
		File srcDir = nodeAttributes.getTemplateEnvDirectory();
		File destDir = nodeAttributes.getEnvDir();
		FileUtils.copyDirectory(srcDir, destDir);
		ConfsUpdater confs = new ConfsUpdater(nodeAttributes);
		// copy all the template gfs config environment directory
		if (!nodeAttributes.getGfsConfigDirectory().exists()) {
			File srcGfsConfigDir = nodeAttributes.getTemplateGfsConfigDirectory();
			File destGfsConfigDir = nodeAttributes.getGfsConfigDirectory();
			FileUtils.copyDirectory(srcGfsConfigDir, destGfsConfigDir);			
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "gfs environment", destGfsConfigDir.getAbsolutePath());
			// create encription key and keystore
			// genero il keystore con la chiave e lo memorizzo su file
			// system all'interno del persistence path
			boolean mkdir = new File(np.getPersistencePath() + "/" + np.getEnvironmentName() + "/" + Factory.KEYSTORES_FOLDER).mkdir();
			if (!mkdir){
				LogAppl.getInstance().debug("Unable to create directory");
			}
			File keystoreFile = new File(np.getPersistencePath() + "/" + np.getEnvironmentName() + "/" + Factory.KEYSTORES_FOLDER + "/" + np.getKeystoreName());
			KeyStoreInfo clusterkeystoreInfo = new KeyStoreInfo(KeyStoreInfo.JCEKS_KEYSTORE_TYPE);
			clusterkeystoreInfo.setFile(keystoreFile);
			clusterkeystoreInfo.setBackupFile(new File(keystoreFile.getAbsolutePath() + ".backup"));
			clusterkeystoreInfo.setPassword(np.getKeystorePwd());
			clusterkeystoreInfo.setSymmetricKeyAlias(np.getEnvironmentName());
			clusterkeystoreInfo.setSymmetricKeyPwd(np.getCryptKeyPwd());
			KeyStoreUtil.generate(clusterkeystoreInfo);
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "cluster Keystore", keystoreFile.getAbsolutePath());
			// create user keystore where we will store the x509 certificate
			File usekeystoreFile = new File(np.getPersistencePath() + "/" + np.getEnvironmentName() + "/" + Factory.KEYSTORES_FOLDER + "/" + np.getUserKeystoreName());
			KeyStoreInfo userkeystoreInfo = new KeyStoreInfo(KeyStoreInfo.JKS_KEYSTORE_TYPE);
			userkeystoreInfo.setFile(usekeystoreFile);
			userkeystoreInfo.setBackupFile(new File(usekeystoreFile.getAbsolutePath() + ".backup"));
			userkeystoreInfo.setPassword(np.getKeystorePwd());
			KeyStoreUtil.generate(userkeystoreInfo);
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "users Keystore", usekeystoreFile.getAbsolutePath());
			// update configuration files
			confs.updateEnvGfsConfig();
			FileUtils.copyFileToDirectory(keystoreFile, nodeAttributes.getWarConfigDir());
			// create the war file (if does not exist) for the web distribution of the environment 
			zipDirectory(nodeAttributes.getWarDir(), nodeAttributes.getWarFile());			
			LogAppl.getInstance().emit(NodeMessage.JEMC059I, "war file", nodeAttributes.getWarFile());
		}
		// raname the node foder with the one set by the user
		File newNodeDir = new File(nodeAttributes.getEnvDir() + "/" + NodeAttributes.TEMPLATE_NODE_DIRECTORY_NAME);
		boolean isRenamed = newNodeDir.renameTo(nodeAttributes.getNodeDir());
		if (!isRenamed) {
			throw new MessageException(NodeMessage.JEMC154E);
		}
		// update configuration files
		confs.updateEnvConfigs();
		// update configuration files
		confs.updateNodeConfigs();		
		
		int count = 0;
		// this while is added to solve the issues
		// about removing the directory.
		// happens that zip is still locking the file when delete is performing
		// in this way it tries again when exception occurs
		// it tries only 10 times, and then throw an exception
		while(true){
			try {
				FileUtils.deleteDirectory(nodeAttributes.getWarDir());
				break;
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				// increments number of errors
				count++;
				if (count == 10){
					throw new MessageException(NodeMessage.JEMC247E, e);
				}
				// waits for 500ms to try again
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					LogAppl.getInstance().ignore(e1.getMessage(), e1);
				}
			}
		}
	}

	/**
	 * Create the new node inside the environment
	 * 
	 * @param nodeAttributes
	 * @throws IOException 
	 * @throws Exception
	 */
	private static void createNode(NodeAttributes nodeAttributes) throws MessageException, IOException {
		File srcDir = nodeAttributes.getTemplateNodeDirectory();
		File destDir = nodeAttributes.getNodeDir();
		FileUtils.copyDirectory(srcDir, destDir);
		ConfsUpdater confs = new ConfsUpdater(nodeAttributes);
		confs.updateNodeConfigs();
	}

	/**
	 * Verify the arguments passed to the main method
	 * 
	 * @param args
	 * @return an object with the attributes of the node @see
	 *         org.pepstock.jem.node.creation.NodeAttributes
	 * @throws MessageException if any exception occurs
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws ConfigurationException 
	 */
	@SuppressWarnings("static-access")
	private static NodeAttributes checkArguments(String[] args) throws MessageException, ParseException, IOException, ConfigurationException {
		String jemHome = System.getenv().get(ConfigKeys.JEM_HOME);
		if (jemHome == null) {
			throw new MessageException(NodeMessage.JEMC058E, ConfigKeys.JEM_HOME);
		}
		// -env mandatory
		Option propFile = OptionBuilder.withArgName(PROPERTIES).hasArg().withDescription("The path of the properties file for the configuration of a new node/foder. See template create_node.properties inside JEM_HOME/config").create(PROPERTIES);
		propFile.setRequired(true);
		// -node optional arg

		ArgumentsParser parser = new ArgumentsParser(CreateNode.class.getName());
		parser.getOptions().add(propFile);
		Properties properties = parser.parseArg(args);

		String propertyUrlPath = properties.getProperty(PROPERTIES);
		URL url = null;
		try {
			url = new URL(propertyUrlPath);
		} catch (MalformedURLException ex) {
			// if it's not an URL, try as a file
			File jcl = new File(propertyUrlPath);
			url = jcl.toURI().toURL();
		}
		Properties props = new Properties();
		props.load(url.openStream());
		NodeProperties np = new NodeProperties(props);
		np.checkMandatoryTag();
		return new NodeAttributes(jemHome, np);
	}

	private static void zipDirectory(File directoryPath, File zipPath) throws IOException {
		FileOutputStream fOut = new FileOutputStream(zipPath);
		BufferedOutputStream bOut = new BufferedOutputStream(fOut);
		ZipArchiveOutputStream tOut = new ZipArchiveOutputStream(bOut);
		zip(directoryPath, directoryPath, tOut);
		tOut.finish();
		tOut.close();
		bOut.close();
		fOut.close();
	}

	private static final void zip(File directory, File base, ZipArchiveOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				FileInputStream in = null;
				try {
					in = new FileInputStream(files[i]);
					ZipArchiveEntry entry = new ZipArchiveEntry(files[i].getPath().substring(base.getPath().length() + 1));
					zos.putArchiveEntry(entry);
					IOUtils.copy(in, zos);
					zos.closeArchiveEntry();
				} catch (IOException e) {
					throw e;
				} finally {
					if (in != null){
						in.close();
					}
				}
			}
		}
	}

}