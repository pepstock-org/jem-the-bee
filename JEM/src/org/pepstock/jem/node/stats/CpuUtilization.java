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
package org.pepstock.jem.node.stats;

import java.io.Serializable;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class CpuUtilization implements Serializable {

	private static final long serialVersionUID = 1L;

	private long user = Long.MIN_VALUE;

	private long system = Long.MIN_VALUE;

	private long idle = Long.MIN_VALUE;

	private long total = Long.MIN_VALUE;
	
	private double percent = Double.MIN_NORMAL;

	/**
	 * {User=370345, SoftIrq=0, Idle=8431572, Stolen=0, Wait=0, Total=22297025,
	 * Irq=4288, Nice=0, Sys=116484}
	 */
	public CpuUtilization() {
	}

	/**
	 * @return the user
	 */
	public long getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(long user) {
		this.user = user;
	}

	/**
	 * @return the system
	 */
	public long getSystem() {
		return system;
	}

	/**
	 * @param system the system to set
	 */
	public void setSystem(long system) {
		this.system = system;
	}

	/**
	 * @return the idle
	 */
	public long getIdle() {
		return idle;
	}

	/**
	 * @param idle the idle to set
	 */
	public void setIdle(long idle) {
		this.idle = idle;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	
	
	/**
	 * @return the percent
	 */
	public double getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(double percent) {
		this.percent = percent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CpuUtilization [user=" + user + ", system=" + system + ", idle=" + idle + ", total=" + total + "]";
	}

}