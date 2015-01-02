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
package org.pepstock.jem.node.executors.certificates;

import java.security.Key;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.resources.ResourcesUtil;

/**
 * Returns the key used internally to decrypt and encrypt resource properties.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class GetKey extends DefaultExecutor<Key> {

	private static final long serialVersionUID = 1L;

	/**
	 * Reads the cluster KEY 
	 * 
	 * @return key generated key
	 * @throws if I/O error occurs 
	 */
	@Override
	public Key execute() throws ExecutorException {
		// gets the key of cluster
		Key key = ResourcesUtil.getInstance().getKey();
		// if null, exception
		if (key == null){
			throw new ExecutorException(NodeMessage.JEMC116E);
		}
		return key;
	}
}