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
 * Executor which returns affinity policy if exists
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class GetAffinityPolicy extends DefaultExecutor<ConfigurationFile> {

	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public ConfigurationFile execute() throws ExecutorException {
		// checks if a affinity loader has been defined
		// checks if is a Script affinity loader
		if (Main.getAffinityLoader() != null && Main.getAffinityLoader() instanceof ScriptAffinityLoader) {
			ScriptAffinityLoader loader = (ScriptAffinityLoader) Main.getAffinityLoader();
			// checks if it has a script file
			if (loader.getScriptFile() != null) {
				// locks the access to file to avoid multiple accesses
				ILock writeSynch = null;
				writeSynch = Main.getHazelcast().getLock(Queues.AFFINITY_LOADER_LOCK);
				writeSynch.lock();
				try {
					// reads teh file and prepare the bean to return
					String content = FileUtils.readFileToString(loader.getScriptFile());
					ConfigurationFile policy = new ConfigurationFile();
					policy.setContent(content);
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