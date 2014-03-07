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
package org.pepstock.jem.gwt.client.panels.administration.nodessystem.inspector;

import org.pepstock.jem.gwt.client.charts.KeyData;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class SystemData extends KeyData {
	
	private double machineCpuPercent = 0;
	
	private double processCpuPercent = 0;
	
	private long processMemoryUtil = 0;

	/**
	 * @return the machineCpuPercent
	 */
	public double getMachineCpuPercent() {
		return machineCpuPercent;
	}

	/**
	 * @param machineCpuPercent the machineCpuPercent to set
	 */
	public void setMachineCpuPercent(double machineCpuPercent) {
		this.machineCpuPercent = machineCpuPercent;
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
	 * @return the processMemoryUtil
	 */
	public long getProcessMemoryUtil() {
		return processMemoryUtil;
	}

	/**
	 * @param processMemoryUtil the processMemoryUtil to set
	 */
	public void setProcessMemoryUtil(long processMemoryUtil) {
		this.processMemoryUtil = processMemoryUtil;
	}
}