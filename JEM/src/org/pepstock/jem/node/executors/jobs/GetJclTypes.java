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
package org.pepstock.jem.node.executors.jobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Returns the list of JCL types installed in JEM
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class GetJclTypes extends DefaultExecutor<Map<String, String>>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Calls an executor to extract all JCL factories, type and description
	 * 
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public Map<String, String> execute() throws ExecutorException {
		// creates a maps with:
		// key=JCL type
		// value=JCL type description
		Map<String, String> map = new HashMap<String, String>();
		for (Entry<String, JemFactory> factory : Main.FACTORIES_LIST.entrySet()){
			map.put(factory.getKey(), factory.getValue().getTypeDescription());
		}
		// returns map
		return map;
	}
	
}