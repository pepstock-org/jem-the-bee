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
package org.pepstock.jem.node.executors.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

import com.hazelcast.core.ILock;


/**
 * Executor which saves a XML JEM configuration file, updated by user interface
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public abstract class SaveXmlConfigurationFile extends DefaultExecutor<ConfigurationFile> {

	private static final long serialVersionUID = 1L;
	
	private ConfigurationFile configFile = null;
	
	/**
	 * Constructs the object using updated (by user interface) configuration file 
	 * @param configFile configuration file updated by user interface
	 */
	public SaveXmlConfigurationFile(ConfigurationFile configFile){
		this.configFile = configFile;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public final ConfigurationFile execute() throws ExecutorException {
		// gets system property which
		// represents the config file path
		String property = getSystemProperty();

		// gets file name
		String jemNodeConfigFile = System.getProperty(property);
		File file = new File(jemNodeConfigFile);

		// synchronizes all access to be consistent
		ILock writeSynch = null;
		writeSynch = Main.getHazelcast().getLock(property);
		writeSynch.lock();
		try {
			// checks if the config file, updated from UI, has still consistent with file
			// present on file system
			// if not, that means someone else has changed the conf before you
			if (file.lastModified() != configFile.getLastModified()){
				throw new ExecutorException(NodeMessage.JEMC208E, configFile.getLastModified(), file.lastModified());
			}
			// writes file
			FileUtils.writeStringToFile(file, configFile.getContent());
			// recreates teh configuration file to get back 
			file = new File(jemNodeConfigFile);
			ConfigurationFile newConfigFile = new ConfigurationFile();
			newConfigFile.setContent(configFile.getContent());
			newConfigFile.setType("xml");
			newConfigFile.setLastModified(file.lastModified());
			return newConfigFile;
		} catch (IOException e) {
			throw new ExecutorException(NodeMessage.JEMC238E, e, file.toString());
		} finally {
			// always unlock 
			writeSynch.unlock();
		}
	}
	
	/**
	 * Returns the system property name which represents config file path
	 * @return property name which represents config file path
	 */
	public abstract String getSystemProperty();
}