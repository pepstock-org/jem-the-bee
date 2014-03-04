/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all processes chain of job in execution.<br>
 * Stores cpu and memory information as well.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class OSProcess implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constant for data not available
	 */
	public static final long NOT_AVAILABLE = -1L;

	private long pid = NOT_AVAILABLE;
	
	private String command = null; 
	
	private long cpu = NOT_AVAILABLE;
	
	private long memory = NOT_AVAILABLE;
	
	private List<OSProcess> children = new ArrayList<OSProcess>();
	
	/**
	 * Emtpry constructor 
	 */
	public OSProcess() {
	}


	/**
	 * @return the pid
	 */
	public long getPid() {
		return pid;
	}


	/**
	 * @param pid the pid to set
	 */
	public void setPid(long pid) {
		this.pid = pid;
	}


	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}


	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}


	/**
	 * @return the cpu
	 */
	public long getCpu() {
		return cpu;
	}


	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(long cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the memory
	 */
	public long getMemory() {
		return memory;
	}


	/**
	 * @param memory the memory to set
	 */
	public void setMemory(long memory) {
		this.memory = memory;
	}


	/**
	 * @return the children
	 */
	public List<OSProcess> getChildren() {
		return children;
	}


	/**
	 * @param children the children to set
	 */
	public void setChildren(List<OSProcess> children) {
		this.children = children;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OSProcess [pid=" + pid + ", command=" + command + ", cpu=" + cpu + ", memory=" + memory + "]";
	}
}