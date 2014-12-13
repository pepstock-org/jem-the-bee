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
package org.pepstock.jem.node.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.pepstock.jem.rest.maps.StatsMapAdapter;

/**
 * Is a bean with a subset of all information usually extract inside the JEM node.
 * This bean is used to show information in the user interface
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LightMemberSample extends AbstractMemberSample implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String key = null;
	
	private String time = null;
	
	private double cpuPercent = 0;
	
	private long memoryAvailable = 0;
	
	private long memoryFree = 0;
	
	private double processCpuPercent = 0;
	
	private long processTotalCpu = 0;
	
	private long processMemoryUsed = 0;

	private long processMemoryFree = 0;

	private List<FileSystemUtilization> fileSystems = new LinkedList<FileSystemUtilization>();

	// PAY ATTENTION: HashMap are not supported by REST. For this reason there is a specific adapter
	@XmlJavaTypeAdapter(StatsMapAdapter.class)
	private Map<String, LightMapStats> mapsStats = new HashMap<String, LightMapStats>();
	
	// PAY ATTENTION: HashMap are not supported by REST. For this reason there is a specific adapter
	@XmlJavaTypeAdapter(StatsMapAdapter.class)
	private Map<String, LightMapStats> internalMapsStats = new HashMap<String, LightMapStats>();
	
	/**
	 * Empty construcotor
	 */
	public LightMemberSample() {
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the cpuPercent
	 */
	public double getCpuPercent() {
		return cpuPercent;
	}
	
	/**
	 * @param cpuPercent the cpuPercent to set
	 */
	public void setCpuPercent(double cpuPercent) {
		this.cpuPercent = cpuPercent;
	}
	
	/**
	 * @return the memoryAvailable
	 */
	public long getMemoryAvailable() {
		return memoryAvailable;
	}
	
	/**
	 * @param memoryAvailable the memoryAvailable to set
	 */
	public void setMemoryAvailable(long memoryAvailable) {
		this.memoryAvailable = memoryAvailable;
	}
	
	/**
	 * @return the memoryFree
	 */
	public long getMemoryFree() {
		return memoryFree;
	}
	
	/**
	 * @param memoryFree the memoryFree to set
	 */
	public void setMemoryFree(long memoryFree) {
		this.memoryFree = memoryFree;
	}
	
	/**
	 * @return the processCpuPercent
	 */
	public double getProcessCpuPercent() {
		return processCpuPercent;
	}
	
	/**
	 * @param processCpuPercent the processCpuPercent to set
	 */
	public void setProcessCpuPercent(double processCpuPercent) {
		this.processCpuPercent = processCpuPercent;
	}
	
	/**
	 * @return the processTotalCpuPercent
	 */
	public long getProcessTotalCpu() {
		return processTotalCpu;
	}
	
	/**
	 * @param processTotalCpuPercent the processTotalCpuPercent to set
	 */
	public void setProcessTotalCpu(long processTotalCpuPercent) {
		this.processTotalCpu = processTotalCpuPercent;
	}
	
	/**
	 * @return the processMemoryUsed
	 */
	public long getProcessMemoryUsed() {
		return processMemoryUsed;
	}
	
	/**
	 * @param processMemoryUsed the processMemoryUsed to set
	 */
	public void setProcessMemoryUsed(long processMemoryUsed) {
		this.processMemoryUsed = processMemoryUsed;
	}
	
	/**
	 * @return the processMemoryFree
	 */
	public long getProcessMemoryFree() {
		return processMemoryFree;
	}

	/**
	 * @param processMemoryFree the processMemoryFree to set
	 */
	public void setProcessMemoryFree(long processMemoryFree) {
		this.processMemoryFree = processMemoryFree;
	}

	/**
	 * @return the fileSystems
	 */
	public List<FileSystemUtilization> getFileSystems() {
		return fileSystems;
	}

	/**
	 * @param fileSystems the fileSystems to set
	 */
	public void setFileSystems(List<FileSystemUtilization> fileSystems) {
		this.fileSystems = fileSystems;
	}

	/**
	 * @return the mapsStats
	 */
	public Map<String, LightMapStats> getMapsStats() {
		return mapsStats;
	}
	
	/**
	 * @param mapsStats the mapsStats to set
	 */
	public void setMapsStats(Map<String, LightMapStats> mapsStats) {
		this.mapsStats = mapsStats;
	}
	
	/**
	 * @return the internalMapsStats
	 */
	public Map<String, LightMapStats> getInternalMapsStats() {
		return internalMapsStats;
	}

	/**
	 * @param internalMapsStats the internalMapsStats to set
	 */
	public void setInternalMapsStats(Map<String, LightMapStats> internalMapsStats) {
		this.internalMapsStats = internalMapsStats;
	}
}