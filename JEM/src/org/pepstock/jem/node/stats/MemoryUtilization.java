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
public class MemoryUtilization implements Serializable {

	private static final long serialVersionUID = 1L;

	private long available = Long.MIN_VALUE;

	private long used = Long.MIN_VALUE;

	private long free = Long.MIN_VALUE;

	/**
	 * Mem: 4067888K av, 2191504K used, 1876384K free
	 */
	public MemoryUtilization() {
	}

	/**
	 * @return the available
	 */
	public long getAvailable() {
		return available;
	}

	/**
	 * @param available the available to set
	 */
	public void setAvailable(long available) {
		this.available = available;
	}

	/**
	 * @return the used
	 */
	public long getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed(long used) {
		this.used = used;
	}

	/**
	 * @return the free
	 */
	public long getFree() {
		return free;
	}

	/**
	 * @param free the free to set
	 */
	public void setFree(long free) {
		this.free = free;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MemoryUtilization [available=" + available + ", used=" + used + ", free=" + free + "]";
	}

}