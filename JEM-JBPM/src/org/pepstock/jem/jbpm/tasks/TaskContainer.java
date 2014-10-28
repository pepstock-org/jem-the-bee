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
package org.pepstock.jem.jbpm.tasks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.pepstock.jem.jbpm.Task;

/**
 * Contains all task (JE work item implementation) currently in execution. When JOB lockingScope is set,
 * it contains all JEM work items, otherwise only one instance of the task (JEM work item) in execution phase.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class TaskContainer {
	
	private static final TaskContainer INSTANCE = new TaskContainer();
	
	private final Map<String, Task> tasks = new ConcurrentHashMap<String, Task>();

	/**
	 * to avoid any instantiation
	 */
	private TaskContainer() {
		
	}

	/**
	 * Return the singleton instance
	 * @return
	 */
	public static TaskContainer getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Returns all tasks to use for lock resources
	 * @return map with tasks to use for lock resources
	 */
	Map<String, Task> getTasks(){
		return tasks;
	}
	
}
