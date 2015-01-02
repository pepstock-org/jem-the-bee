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
package org.pepstock.jem.node.executors.datasetsrules;

import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;


/**
 * Executor which tests the datasets rules file
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class CheckDatasetsRules extends DefaultExecutor<Boolean> {

	private static final long serialVersionUID = 1L;
	
	private String content = null;
	
	/**
	 * Constructs the executor with datasets rules file content 
	 * @param content content of datasets rules file
	 */
	public CheckDatasetsRules(String content){
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		// tests datasets rules file if is correct and the return
		// the test result
		try {
			Main.DATA_PATHS_MANAGER.testRules(content);
			return Boolean.TRUE;
		} catch (MessageException e) {
			throw new ExecutorException(e.getMessageInterface(), e, e.getObjects());
		}
	}
}