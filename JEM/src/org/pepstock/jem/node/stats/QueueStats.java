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
 * Is a bean with all information about statistics of a queue of Hazelcast
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class QueueStats implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;

	private long aveAge = Long.MIN_VALUE;

	private long minAge = Long.MIN_VALUE;

	private long maxAge = Long.MIN_VALUE;

	private long backupItemCount = Integer.MIN_VALUE;

	private long ownedItemCount = Integer.MIN_VALUE;
	
	private QueueOperationsStats operationsStats = new QueueOperationsStats();

	/**
	 * Empty constructor
	 */
	public QueueStats() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the aveAge
	 */
	public long getAveAge() {
		return aveAge;
	}

	/**
	 * @param aveAge the aveAge to set
	 */
	public void setAveAge(long aveAge) {
		this.aveAge = aveAge;
	}

	/**
	 * @return the minAge
	 */
	public long getMinAge() {
		return minAge;
	}

	/**
	 * @param minAge the minAge to set
	 */
	public void setMinAge(long minAge) {
		this.minAge = minAge;
	}

	/**
	 * @return the maxAge
	 */
	public long getMaxAge() {
		return maxAge;
	}

	/**
	 * @param maxAge the maxAge to set
	 */
	public void setMaxAge(long maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return the backupItemCount
	 */
	public long getBackupItemCount() {
		return backupItemCount;
	}

	/**
	 * @param backupItemCount the backupItemCount to set
	 */
	public void setBackupItemCount(long backupItemCount) {
		this.backupItemCount = backupItemCount;
	}

	/**
	 * @return the ownedItemCount
	 */
	public long getOwnedItemCount() {
		return ownedItemCount;
	}

	/**
	 * @param ownedItemCount the ownedItemCount to set
	 */
	public void setOwnedItemCount(long ownedItemCount) {
		this.ownedItemCount = ownedItemCount;
	}
	
	/**
	 * @return the operationsStats
	 */
	public QueueOperationsStats getOperationsStats() {
		return operationsStats;
	}

	/**
	 * @param operationsStats the operationsStats to set
	 */
	public void setOperationsStats(QueueOperationsStats operationsStats) {
		this.operationsStats = operationsStats;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QueueStats [name=" + name + ", aveAge=" + aveAge + ", minAge=" + minAge + ", maxAge=" + maxAge + ", backupItemCount=" + backupItemCount + ", ownedItemCount=" + ownedItemCount + ", operationsStats=" + operationsStats + "]";
	}

}