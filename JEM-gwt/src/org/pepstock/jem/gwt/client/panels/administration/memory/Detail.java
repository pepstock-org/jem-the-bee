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
package org.pepstock.jem.gwt.client.panels.administration.memory;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class Detail {
	
	private String key = null;
	
	private String time = null;
	
	private long free = 0L;
	
	private long used = 0L;
	
	private double freePercent = 0D;
	
	private double usedPercent = 0D;

	/**
	 * 
	 */
	public Detail() {
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
	 * @return the freePercent
	 */
	public double getFreePercent() {
		return freePercent;
	}

	/**
	 * @param freePercent the freePercent to set
	 */
	public void setFreePercent(double freePercent) {
		this.freePercent = freePercent;
	}

	/**
	 * @return the usedPercent
	 */
	public double getUsedPercent() {
		return usedPercent;
	}

	/**
	 * @param usedPercent the usedPercent to set
	 */
	public void setUsedPercent(double usedPercent) {
		this.usedPercent = usedPercent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "Detail [time=" + time + ", free=" + free + ", used=" + used + ", freePercent=" + freePercent + ", usedPercent=" + usedPercent + "]";
    }

	
}