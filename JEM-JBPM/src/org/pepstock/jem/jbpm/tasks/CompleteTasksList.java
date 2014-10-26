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
	 * Stes teh workItem ID to the task
	 * @param nodeId node id used to search task
	 * @param workItemId workitem id, to assinged to task
	 */
	void setWorkItemID(long nodeId, long workItemId){
		for (Task task : tasks.values()){
			if (task.getNodeId() == nodeId){
				task.setWorkItemId(workItemId);
			}
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	Task getTaskByID(String id){
		return tasks.get(id);
	}
	
	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	Task getTaskByNodeID(long nodeId){
		for (Task task : tasks.values()){
			if (task.getNodeId() == nodeId){
				return task;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param workItemId
	 * @return
	 */
	Task getTaskByWorkItemID(long workItemId){
		for (Task task : tasks.values()){
			if (task.getWorkItemId() == workItemId){
				return task;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	Map<String, Task> getTasks(){
		return tasks;
	}
	
}
