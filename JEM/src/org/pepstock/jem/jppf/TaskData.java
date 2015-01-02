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

/**
 * Contains the index of task in task list of JPPF job, total tasks and if is the last one.<br>
 * This is helpful to parallelize wrokload correctly.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class TaskData {
	
	private int index = 0;
	
	private int total = 0;

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	void setTotal(int total) {
		this.total = total;
	}
	
	/**
	 * @return if task is the last of job chain
	 */
	public boolean isLast(){
		return (index+1) == total;
	}
	
	/**
	 * @return if task is the first of job chain
	 */
	public boolean isFirst(){
		return index == 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaskData [index=" + index + ", total=" + total + (isFirst() ? ", first" : "") + (isLast() ? ", last" : "")  + "]";
	}
	

}
