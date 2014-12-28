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

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.util.locks.LockException;
import org.pepstock.jem.util.locks.ReadLock;


/**
 * Executor which returns datasets rules
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class GetDatasetsRules extends DefaultExecutor<ConfigurationFile> {

	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public ConfigurationFile execute() throws ExecutorException {
		// checks if a datasets rules file is defined
		if (Main.DATA_PATHS_MANAGER.getDatasetRulesFile() != null) {
			// locks the access to file to avoid multiple accesses
			ReadLock read = new ReadLock(Main.getHazelcast(), Queues.DATASETS_RULES_LOCK);
			try {
				read.acquire();
				// reads the file
				String content = FileUtils.readFileToString(Main.DATA_PATHS_MANAGER.getDatasetRulesFile());
				// creates the bean to return
				ConfigurationFile policy = new ConfigurationFile();
				// sets content and type (always XML)
				policy.setContent(content);
				policy.setType("xml");
				// sets last modified time
				policy.setLastModified(Main.DATA_PATHS_MANAGER.getDatasetRulesFile().lastModified());
				// returns the policy
				return policy;
			} catch (IOException e) {
				throw new ExecutorException(NodeMessage.JEMC238E, e, Main.DATA_PATHS_MANAGER.getDatasetRulesFile().toString());
			} catch (LockException e) {
				throw new ExecutorException(NodeMessage.JEMC260E, e, Queues.DATASETS_RULES_LOCK);
			} finally {
				// unlock always
				try {
					read.release();
				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC261E, e, Queues.DATASETS_RULES_LOCK);
				}
			}
		}
		// returns null if dataset rules doesn't exist
		// it mustn't be here
		return null;
	}
}