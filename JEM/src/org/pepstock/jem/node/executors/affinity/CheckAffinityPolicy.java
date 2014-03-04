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

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.node.affinity.ScriptAffinityLoader;
import org.pepstock.jem.node.affinity.SystemInfo;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;


/**
 * Executor which tests the affinity policy script
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class CheckAffinityPolicy extends DefaultExecutor<Result> {

	private static final long serialVersionUID = 1L;
	
	private String content = null;
	
	/**
	 * Constructs the executor with affinitiy policy content 
	 * @param content content of affinitiy policy
	 */
	public CheckAffinityPolicy(String content){
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public Result execute() throws ExecutorException {
		// checks if a affinity loader has been defined
		// checks if is a Script affinity loader
		if (Main.getAffinityLoader() != null && Main.getAffinityLoader() instanceof ScriptAffinityLoader) {
			ScriptAffinityLoader loader = (ScriptAffinityLoader) Main.getAffinityLoader();
			// tests the script if is correct and the return
			// the test result
			try {
				return loader.testScript(content, new SystemInfo());
			} catch (IOException e) {
				throw new ExecutorException(NodeMessage.JEMC238E, e, loader.getScriptFile().toString());
			}
		}
		return null;
	}
}