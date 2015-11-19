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
package org.pepstock.jem.factories;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.VariableSubstituter;


/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public abstract class AbstractFactory implements JemFactory {

	private static final long serialVersionUID = 1L;

	private static final String VALUES_SEPARATOR = ";";
	
	private final Properties systemProperties = new Properties();

	private Properties properties = null;
	
	private boolean useSudo = false;
	
	private List<String> classpath = null;

	/**
	 * Loads all system properties
	 */
	public AbstractFactory() {
		// loads JVM properties to Properties instance
		// to be able to substitute variables in configuration
		systemProperties.putAll(System.getProperties());
	}

	/**
	 * Saves the properties on own instance and load source code path to current System properties, necessary
	 * to validate JCL due to they could have import or include tasks.
	 * 
	 * @see org.pepstock.jem.factories.JemFactory#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) throws JemException {
		this.properties = properties;
		useSudo = Parser.parseBoolean(properties.getProperty(JobTaskFactory.SWITCH_USER_KEY), false);
	}
	

	/**
	 * Returns all properties configured for this factory
	 * 
	 * @return the properties
	 */
	@Override
	public Properties getProperties() {
		return properties;
	}
	


	/**
	 * @return the useSudo
	 */
	public boolean isUseSudo() {
		return useSudo;
	}

	/**
	 * @param useSudo the useSudo to set
	 */
	public void setUseSudo(boolean useSudo) {
		this.useSudo = useSudo;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JobTaskFactory#setClassPath(java.util.List)
	 */
	@Override
	public void setClassPath(List<String> classpath) {
		this.classpath = classpath;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JobTaskFactory#getClassPath()
	 */
	@Override
	public List<String> getClassPath() {
		return classpath;
	}

	/**
	 * Utility method to resolve properties used in JCL, like classPath.
	 * 
	 * @param valueParm value inserted in JCL property
	 * @param pathKey kind of path 
	 * @return absolute paths for values
	 * @throwsException if a substitution exception occurs
	 */
	public String resolvePathNames(String valueParm, String pathKey) {
		String value = VariableSubstituter.substitute(valueParm, systemProperties);
		String pathSeparator = System.getProperty("path.separator");
		StringBuilder sb = new StringBuilder();
		String[] filesNames = value.split(VALUES_SEPARATOR);
		for (int i = 0; i < filesNames.length; i++) {
			File filePath = getFile(FilenameUtils.normalize(filesNames[i], true), pathKey);
			if (i > 0) {
				sb.append(pathSeparator);
			}
			sb.append(filePath.getAbsolutePath());
		}
		return sb.toString();
	}

	
	private File getFile(String fileName, String pathKey) {
		//checks if the filename is a absolute file name
		// if absolute path is equals return the file 
		// otherwise checks path
		File checkFile = new File(fileName);
		String checkFileName = FilenameUtils.normalize(checkFile.getAbsolutePath(), true);
		if (checkFileName.equalsIgnoreCase(fileName)){
			return checkFile;
		}
		
		// gets the path from the environment
		// variables
		String path = System.getProperty(pathKey);
		if (fileName.startsWith(path)){
			return new File(fileName);
		}
		
		// create file object in class path
		return new File(path, fileName);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JemFactory#afterNodeStarted()
	 */
	@Override
	public void afterNodeStarted() throws ConfigurationException{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.NodeLifeCycleListener#beforeNodeStopped()
	 */
	@Override
	public void beforeNodeStopped() {
		// do nothing
		
	}
}