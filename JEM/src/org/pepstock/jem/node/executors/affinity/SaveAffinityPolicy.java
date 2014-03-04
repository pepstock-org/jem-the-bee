/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.executors.affinity;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.affinity.ScriptAffinityLoader;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

import com.hazelcast.core.ILock;

/**
 * Executor which save affinity policy script, changed by user interface
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class SaveAffinityPolicy extends DefaultExecutor<ConfigurationFile> {

	private static final long serialVersionUID = 1L;

	private ConfigurationFile configFile = null;

	/**
	 * Constructs object using the file bean with all data passed from user
	 * interface
	 * 
	 * @param configFile bean with content and type
	 */
	public SaveAffinityPolicy(ConfigurationFile configFile) {
		this.configFile = configFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public ConfigurationFile execute() throws ExecutorException {
		// checks if a affinity loader has been defined
		// checks if is a Script affinity loader
		if (Main.getAffinityLoader() != null && Main.getAffinityLoader() instanceof ScriptAffinityLoader) {
			ScriptAffinityLoader loader = (ScriptAffinityLoader) Main.getAffinityLoader();
			// checks if if it has a script file
			if (loader.getScriptFile() != null) {
				// locks the access to file to avoid multiple accesses
				ILock writeSynch = null;
				writeSynch = Main.getHazelcast().getLock(Queues.AFFINITY_LOADER_LOCK);
				writeSynch.lock();
				try {
					// checks if the bean is out-of-sync using the last
					// modified time stamp
					// if the values are not equals, means that someone else
					// changed and save the script
					if (loader.getScriptFile().lastModified() != configFile.getLastModified()) {
						throw new ExecutorException(NodeMessage.JEMC208E, configFile.getLastModified(), loader.getScriptFile().lastModified());
					}
					// writes the policy file
					FileUtils.writeStringToFile(loader.getScriptFile(), configFile.getContent());
					// creates bean with necessary data of script file
					ConfigurationFile policy = new ConfigurationFile();
					policy.setContent(configFile.getContent());
					policy.setType(loader.getScriptType());
					policy.setLastModified(loader.getScriptFile().lastModified());
					return policy;
				} catch (IOException e) {
					throw new ExecutorException(NodeMessage.JEMC238E, e, loader.getScriptFile().toString());
				} finally {
					// unlock always
					writeSynch.unlock();
				}
			}
		}
		return null;
	}
}