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
package org.pepstock.jem.node.executors.nodes;

import java.util.concurrent.Callable;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;


/**
 * Performs a java <code>top</code> command on machine, returning the list of processes and system info.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2
 * 
 */
public class Top extends DefaultExecutor<String>{

	private static final long serialVersionUID = 1L;

	/**
	 * Calls with reflection a class to extract processes list of machines and system info.
	 *
	 * @return list of processes and system info in string format
	 * @throws Exception occurs if errors
	 */
	@Override
	public String execute() throws ExecutorException{
		try {
			// I need the reflection to avoid to distribute 
			// Sigar in web site
			@SuppressWarnings("unchecked")
			Callable<String> top = (Callable<String>) Class.forName("org.pepstock.jem.node.system.Top").newInstance();
			return top.call();
		} catch (Exception e) {
			throw new ExecutorException(NodeMessage.JEMC244E, e);
		}
	}
}