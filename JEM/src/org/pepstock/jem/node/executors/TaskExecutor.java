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

import java.util.concurrent.Callable;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.hazelcast.ExecutorServices;

import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;

/**
 * Utility to submit distributed task with generic callback with boolean result value
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class TaskExecutor {

	/**
	 * To avoid any instantiation
	 */
	private TaskExecutor() {
	}
	
	/**
	 * Submit a distributed task with generic callback
	 * @param callable distributed task to submit
	 * @param member member hazelcast where to execute the distributed task
	 */
	public static void submit(Callable<Boolean> callable, Member member){
        // creates the future task
		// gets executor service and executes!
		IExecutorService executorService = Main.getHazelcast().getExecutorService(ExecutorServices.NODE);
		executorService.submitToMember(callable, member, GenericCallBack.DEFAULT);

	}

}
