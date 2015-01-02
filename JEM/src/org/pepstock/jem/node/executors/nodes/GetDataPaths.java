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
package org.pepstock.jem.node.executors.nodes;

import java.util.List;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Returns the data paths names to be checked at node start up.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 * 
 */
public class GetDataPaths extends DefaultExecutor<List<String>>{

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the data paths names to be checked at node start up.
	 * 
	 * @return the data paths names
	 * @throws Exception occurs if errors
	 */
	@Override
	public List<String> execute() throws ExecutorException {
		return Main.DATA_PATHS_MANAGER.getDataPathsNames();
	}

}