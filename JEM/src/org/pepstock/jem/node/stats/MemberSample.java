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
package org.pepstock.jem.node.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class MemberSample extends AbstractMemberSample implements Serializable {

	private static final long serialVersionUID = 1L;

	private long currentTimeMillis = System.currentTimeMillis(); 

	private CpuUtilization cpu = new CpuUtilization();
	
	private MemoryUtilization memory = new MemoryUtilization();

	private ProcessCpuUtilization processCpu = new ProcessCpuUtilization();

	private ProcessMemoryUtilization processMemory = new ProcessMemoryUtilization();
	
	private FileSystemUtilization fileSystem = new FileSystemUtilization();
	
	private Map<String, MapStats> mapsStats = new HashMap<String, MapStats>();

	private Map<String, QueueStats> queuesStats = new HashMap<String, QueueStats>();
	/**
	 * 
	 */
	public MemberSample() {
	}

	/**
	 * @return the currentTimeMillis
	 */
	public long getCurrentTimeMillis() {
		return currentTimeMillis;
	}

	/**
	 * @param currentTimeMillis the currentTimeMillis to set
	 */
	public void setCurrentTimeMillis(long currentTimeMillis) {
		this.currentTimeMillis = currentTimeMillis;
	}

	/**
	 * @return the cpu
	 */
	public CpuUtilization getCpu() {
		return cpu;
	}

	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(CpuUtilization cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the processCpu
	 */
	public ProcessCpuUtilization getProcessCpu() {
		return processCpu;
	}

	/**
	 * @param processCpu the processCpu to set
	 */
	public void setProcessCpu(ProcessCpuUtilization processCpu) {
		this.processCpu = processCpu;
	}

	/**
	 * @return the processMemory
	 */
	public ProcessMemoryUtilization getProcessMemory() {
		return processMemory;
	}

	/**
	 * @param processMemory the processMemory to set
	 */
	public void setProcessMemory(ProcessMemoryUtilization processMemory) {
		this.processMemory = processMemory;
	}

	/**
	 * @return the memory
	 */
	public MemoryUtilization getMemory() {
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public void setMemory(MemoryUtilization memory) {
		this.memory = memory;
	}

	
	
	/**
	 * @return the mapsStats
	 */
	public Map<String, MapStats> getMapsStats() {
		return mapsStats;
	}

	/**
	 * @param mapsStats the mapsStats to set
	 */
	public void setMapsStats(Map<String, MapStats> mapsStats) {
		this.mapsStats = mapsStats;
	}

	
	/**
	 * @return the queuesStats
	 */
	public Map<String, QueueStats> getQueuesStats() {
		return queuesStats;
	}

	/**
	 * @param queuesStats the queuesStats to set
	 */
	public void setQueuesStats(Map<String, QueueStats> queuesStats) {
		this.queuesStats = queuesStats;
	}

	
	/**
	 * @return the fileSystem
	 */
	public FileSystemUtilization getFileSystem() {
		return fileSystem;
	}

	/**
	 * @param fileSystem the fileSystem to set
	 */
	public void setFileSystem(FileSystemUtilization fileSystem) {
		this.fileSystem = fileSystem;
	}

}