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

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.Configuration;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Tests if JEM environment configuration file, updated by user interface, is consistent and valid (creating a temporary configuration).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class CheckJemEnvConfiguration extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;
	
	private String content = null;
	
	/**
	 * Constructs the object with JEM environment configuration file content to test
	 * @param content configuration file content to check
	 */
	public CheckJemEnvConfiguration(String content) {
		this.content = content;
	}

	/**
	 * Checks if content could be a JEM environment configuration file
	 * @return always TRUE
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		try {
			Configuration.unmarshall(content);
		} catch (Exception e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			// NOTE that we are using the message of cause exception
			// where is written which row has got the error
			throw new ExecutorException(NodeMessage.JEMC241E, e, e.getCause().getMessage());
		}
		return Boolean.TRUE;
	}
}