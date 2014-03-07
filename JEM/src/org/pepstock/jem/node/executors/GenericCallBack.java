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
package org.pepstock.jem.node.executors;

import java.util.concurrent.Future;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

import com.hazelcast.core.ExecutionCallback;

/**
 * A default call-back implementation.<br>
 * It returns a result about the execution, checking if the future-task is
 * canceled of not.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class GenericCallBack implements ExecutionCallback<ExecutionResult> {
	
	/**
	 * Empty constructor
	 */
	public GenericCallBack() {
	}

	/**
	 * Method called when the execution is ended.<br>
	 * It writes only information records if the task is canceled or if a
	 * exception occurs.
	 * 
	 * @see ExecutionResult
	 * @param future future task executed
	 */
	@Override
	public void done(Future<ExecutionResult> future) {
		try {
			if (!future.isCancelled()) {
				ExecutionResult result = future.get();
				LogAppl.getInstance().emit(NodeMessage.JEMC113I, result.getDescription());
			}
		} catch (Exception e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC112E, e);
		}
	}
}