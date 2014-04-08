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
package org.pepstock.jem.node.executors.datasetsrules;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.util.locks.LockException;
import org.pepstock.jem.util.locks.WriteLock;

/**
 * Executor which save datasets rules file, changed by user interface
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class SaveDatasetsRules extends DefaultExecutor<ConfigurationFile> {

	private static final long serialVersionUID = 1L;

	private ConfigurationFile configFile = null;

	/**
	 * Constructs object using the file bean with all data passed from user
	 * interface
	 * 
	 * @param configFile bean with content and type
	 */
	public SaveDatasetsRules(ConfigurationFile configFile) {
		this.configFile = configFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public ConfigurationFile execute() throws ExecutorException {
		// checks if a datasets rules file is defined
		if (Main.DATA_PATHS_MANAGER.getDatasetRulesFile() != null) {
			// locks the access to file to avoid multiple accesses
			WriteLock write = new WriteLock(Main.getHazelcast(), Queues.DATASETS_RULES_LOCK);
			try {
				write.acquire();
				File file = Main.DATA_PATHS_MANAGER.getDatasetRulesFile();
				// checks if the bean is out-of-sync using the last
				// modified time stamp
				// if the values are not equals, means that someone else
				// changed and save the datasets rules file
				if (file.lastModified() != configFile.getLastModified()) {
					throw new ExecutorException(NodeMessage.JEMC208E, configFile.getLastModified(), file.lastModified());
				}
				
				// writes the datasets rules file
				FileUtils.writeStringToFile(file, configFile.getContent());
				// creates bean with necessary data of datasets rules file
				ConfigurationFile policy = new ConfigurationFile();
				policy.setContent(configFile.getContent());
				policy.setType("xml");
				policy.setLastModified(file.lastModified());
				return policy;
			} catch (IOException e) {
				throw new ExecutorException(NodeMessage.JEMC238E, e, Main.DATA_PATHS_MANAGER.getDatasetRulesFile().toString());
			} catch (LockException e) {
				throw new ExecutorException(NodeMessage.JEMC260E, e, Queues.DATASETS_RULES_LOCK);
			} finally {
				// unlock always
				try {
					write.release();
				} catch (Exception e) {
					throw new ExecutorException(NodeMessage.JEMC261E, e, Queues.DATASETS_RULES_LOCK);
				}
			}
		}
		return null;
	}
}