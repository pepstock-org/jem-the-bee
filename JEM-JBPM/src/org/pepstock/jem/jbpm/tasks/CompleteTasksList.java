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

import java.util.Collections;
import java.util.Map;

import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.Task;
import org.pepstock.jem.log.JemRuntimeException;

/**
 * Singleton which contains all tasks of a process, all tasks are JEM work items.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class CompleteTasksList {
	
	private static CompleteTasksList INSTANCE = null;
	
	private Map<String, Task> tasks = null;

	/**
	 * to avoid any instantiation
	 */
	private CompleteTasksList(Map<String, Task> tasks) {
		this.tasks = Collections.unmodifiableMap(tasks);
		
	}
	
	/**
	 * Returns the instance only if already loaded
	 * @return singleton instance
	 */
	public static CompleteTasksList getInstance(){
		return getInstance(null);
	}
	
	/**
	 * Returns the instance loading unmodifiable map of tasks
	 * @param tasks unmodifiable map of tasks
	 * @return singleton instance
	 */
	public synchronized static CompleteTasksList getInstance(Map<String, Task> tasks){
		if (INSTANCE != null){
			return INSTANCE;
		}
		if (tasks == null){
			throw new JemRuntimeException(JBpmMessage.JEMM026E.toMessage().getFormattedMessage());
		}
		INSTANCE = new CompleteTasksList(tasks);
		return INSTANCE;
	}
	
	/**
	 * Sets the node id to the task
	 * @param xmlId XML id, key to search task
	 * @param nodeId node id, assigned by JBPM
	 */
	void setNodeID(String xmlId, long nodeId){
		if (xmlId != null && tasks.containsKey(xmlId)){
			tasks.get(xmlId).setNodeId(nodeId);
		}
	}
	
	/**
	 * Sets the workItem ID to the task
	 * @param nodeId node id used to search task
	 * @param workItemId workitem id, to assign to the task
	 */
	void setWorkItemID(long nodeId, long workItemId){
		for (Task task : tasks.values()){
			if (task.getNodeId() == nodeId){
				task.setWorkItemId(workItemId);
			}
		}
	}
	
	/**
	 * Returns the task using the task ID as KEY
	 * @param id task ID as KEY
	 * @return task or <code>null</code> 
	 */
	Task getTaskByID(String id){
		return tasks.get(id);
	}
	
	/**
	 * Gets the task using the node ID as key
	 * @param nodeId node ID as KEY
	 * @return task with node ID passed or <code>null</code>
	 */
	Task getTaskByNodeID(long nodeId){
		// scans task because node ID is not the KEY of map
		for (Task task : tasks.values()){
			if (task.getNodeId() == nodeId){
				return task;
			}
		}
		return null;
	}

	/**
	 * Gets the task using the workitem ID as key
	 * @param workItemId work item ID as KEY
	 * @return task with workitem ID passed or <code>null</code>
	 */
	Task getTaskByWorkItemID(long workItemId){
		// scans task because work item ID is not the KEY of map
		for (Task task : tasks.values()){
			if (task.getWorkItemId() == workItemId){
				return task;
			}
		}
		return null;
	}

	/**
	 * Returns all tasks
	 * @return all tasks
	 */
	Map<String, Task> getTasks(){
		return tasks;
	}
	
}
