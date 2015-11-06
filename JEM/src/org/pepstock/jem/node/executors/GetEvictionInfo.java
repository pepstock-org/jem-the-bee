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
package org.pepstock.jem.node.executors;

import org.pepstock.jem.node.persistence.EvictionHelper;


/**
 * Extracts if the mapname passed is evicted or not
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 * 
 */
public class GetEvictionInfo extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;
	
	private String mapName = null;
	
	/**
	 * Creates the executor with map name to check 
	 * @param mapName map name to check 
	 */
	public GetEvictionInfo(String mapName) {
		super();
		this.mapName = mapName;
	}

	/**
	 * returns if the map is evicted
	 * @return if the map is evicted
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		return EvictionHelper.isEvicted(mapName);
	}
}