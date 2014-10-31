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
package org.pepstock.jem.jbpm;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.jbpm.tasks.DataDescription;
import org.pepstock.jem.jbpm.tasks.DataSource;
import org.pepstock.jem.jbpm.tasks.Lock;

/**
 * Represents all task of JBPM process which implements a JEM workitem.<br>
 * BY JEM workitem, is possible to set data description, data source and locks.<br>
 * JBPM uses several ID (task ID, node ID and workitem ID). all these IDs are stored in the object.<br>
 * Task ID is the main ID, set by the developer to the task.<br>
 * Node ID is set by JBPM, is just a counter of all XML nodes.<br>
 * WorkItem ID is set by JBPM, is just a counter of all work items.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Task {
	
	/**
	 * ID value is not assigned
	 */
	public static final long NO_ID = -1;
	
	private String name = null;
	
	private String id = null;
	
	private long nodeId = NO_ID;
	
	private long workItemId = NO_ID;
	
	private int returnCode = 0;
	
	private final List<DataDescription> dataDescriptions = new ArrayList<DataDescription>();
	
	private final List<DataSource> dataSources = new ArrayList<DataSource>();
	
	private final List<Lock> locks = new ArrayList<Lock>(); 

	/**
	 * Empty constructor
	 */
	public Task() {
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the nodeId
	 */
	public long getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the workItemId
	 */
	public long getWorkItemId() {
		return workItemId;
	}

	/**
	 * @param workItemId the workItemId to set
	 */
	public void setWorkItemId(long workItemId) {
		this.workItemId = workItemId;
	}

	/**
	 * @return the dataDescriptions
	 */
	public List<DataDescription> getDataDescriptions() {
		return dataDescriptions;
	}

	/**
	 * @return the dataSources
	 */
	public List<DataSource> getDataSources() {
		return dataSources;
	}

	/**
	 * @return the locks
	 */
	public List<Lock> getLocks() {
		return locks;
	}

	/**
	 * @return the returnCode
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * @param returnCode the returnCode to set
	 */
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "Task [name=" + name + ", id=" + id + ", nodeId=" + nodeId + ", workItemId=" + workItemId + "]";
    }
	
}
