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
package org.pepstock.jem.jppf;

import javax.naming.InitialContext;

/**
 * Uses a ThreadLocal to isolate Task info in multi-threading environment as 
 * JPPF node is. This avoids conflicts on different contexts with different references.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class TaskInfo extends ThreadLocal<InitialContext>{

	private static final ThreadLocal<TaskData> TASK_DATA = new ThreadLocal<TaskData>();
	
	/**
	 * Sets Task Data
	 * @param data task data
	 */
	static final void setTaskData(TaskData data){
		TASK_DATA.set(data);
	}

	/**
	 * Returns task data
	 * @return task data
	 */
	public static final TaskData getTaskData(){
		return TASK_DATA.get();
	}

}