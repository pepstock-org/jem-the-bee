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
package org.pepstock.jem;

import java.io.Serializable;

/**
 * Contains all information about the current consumption of CPU and memory of
 * task in execution.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class JobSystemActivity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constant for data not available
	 */
	public static final long NOT_AVAILABLE = -1L;
	
	private boolean active = false;
	
	private long cpu = NOT_AVAILABLE;

	private double cpuPerc = NOT_AVAILABLE;
	
	private long memory = NOT_AVAILABLE;
	
	private OSProcess process = null;
			
	/**
	 * Empty constructor
	 */
	public JobSystemActivity() {
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
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
	 * @return the cpuPerc
	 */
	public double getCpuPerc() {
		return cpuPerc;
	}

	/**
	 * @param cpuPerc the cpuPerc to set
	 */
	public void setCpuPerc(double cpuPerc) {
		this.cpuPerc = cpuPerc;
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
	 * @return the process
	 */
	public OSProcess getProcess() {
		return process;
	}

	/**
	 * @param process the process to set
	 */
	public void setProcess(OSProcess process) {
		this.process = process;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobSystemActivity [active=" + active + ", cpu=" + cpu + ", cpuPerc=" + cpuPerc + ", memory=" + memory + "]";
	}


}