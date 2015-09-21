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
package org.pepstock.jem.rest.entities;

import org.pepstock.jem.node.Queues;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public enum JobQueue {
	
	INPUT(Queues.INPUT_QUEUE, "input"),
	RUNNING(Queues.RUNNING_QUEUE, "running"),
	OUTPUT(Queues.OUTPUT_QUEUE, "output"),
	ROUTING(Queues.ROUTING_QUEUE, "routing");
	
	
	private String name = null;
	
	private String path = null;

	/**
	 * @param name
	 * @param description
	 * @param path
	 */
	private JobQueue(String name, String path) {
		this.name = name;
		this.path = path;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	public static JobQueue getQueueByName(String name){
		for (JobQueue queue : values()){
			if (queue.getName().equalsIgnoreCase(name)){
				return queue;
			}
		}
		return null;
	}

	public static JobQueue getQueueByPath(String path){
		for (JobQueue queue : values()){
			if (queue.getPath().equalsIgnoreCase(path)){
				return queue;
			}
		}
		return null;		
	}

}
