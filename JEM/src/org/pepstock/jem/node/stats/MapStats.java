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
 * Is a bean with all information about statistics on a map of Hazelcast.
 * <br>
 * It contains all information about Hazelcast structures, from node perspective.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class MapStats implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private long ownedEntryCount = Long.MIN_VALUE;
	
	private long backupEntryCount = Long.MIN_VALUE;
	
	private long ownedEntryMemoryCost = Long.MIN_VALUE;
	
	private long backupEntryMemoryCost = Long.MIN_VALUE;
	
	private long lockedEntryCount = Long.MIN_VALUE;
	
	private long dirtyEntryCount = Long.MIN_VALUE;
	
	private long hits = Long.MIN_VALUE;
	
	private MapOperationsStats operationsStats = new MapOperationsStats();

	/**
	 * Empty constructor
	 */
	public MapStats() {
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
	 * @return the ownedEntryCount
	 */
	public long getOwnedEntryCount() {
		return ownedEntryCount;
	}

	/**
	 * @param ownedEntryCount the ownedEntryCount to set
	 */
	public void setOwnedEntryCount(long ownedEntryCount) {
		this.ownedEntryCount = ownedEntryCount;
	}

	/**
	 * @return the backupEntryCount
	 */
	public long getBackupEntryCount() {
		return backupEntryCount;
	}

	/**
	 * @param backupEntryCount the backupEntryCount to set
	 */
	public void setBackupEntryCount(long backupEntryCount) {
		this.backupEntryCount = backupEntryCount;
	}

	/**
	 * @return the ownedEntryMemoryCost
	 */
	public long getOwnedEntryMemoryCost() {
		return ownedEntryMemoryCost;
	}

	/**
	 * @param ownedEntryMemoryCost the ownedEntryMemoryCost to set
	 */
	public void setOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
		this.ownedEntryMemoryCost = ownedEntryMemoryCost;
	}

	/**
	 * @return the backupEntryMemoryCost
	 */
	public long getBackupEntryMemoryCost() {
		return backupEntryMemoryCost;
	}

	/**
	 * @param backupEntryMemoryCost the backupEntryMemoryCost to set
	 */
	public void setBackupEntryMemoryCost(long backupEntryMemoryCost) {
		this.backupEntryMemoryCost = backupEntryMemoryCost;
	}

	/**
	 * @return the lockedEntryCount
	 */
	public long getLockedEntryCount() {
		return lockedEntryCount;
	}

	/**
	 * @param lockedEntryCount the lockedEntryCount to set
	 */
	public void setLockedEntryCount(long lockedEntryCount) {
		this.lockedEntryCount = lockedEntryCount;
	}

	/**
	 * @return the dirtyEntryCount
	 */
	public long getDirtyEntryCount() {
		return dirtyEntryCount;
	}

	/**
	 * @param dirtyEntryCount the dirtyEntryCount to set
	 */
	public void setDirtyEntryCount(long dirtyEntryCount) {
		this.dirtyEntryCount = dirtyEntryCount;
	}

	/**
	 * @return the hits
	 */
	public long getHits() {
		return hits;
	}

	/**
	 * @param hits the hits to set
	 */
	public void setHits(long hits) {
		this.hits = hits;
	}

	/**
	 * @return the operationsStats
	 */
	public MapOperationsStats getOperationsStats() {
		return operationsStats;
	}

	/**
	 * @param operationsStats the operationsStats to set
	 */
	public void setOperationsStats(MapOperationsStats operationsStats) {
		this.operationsStats = operationsStats;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MapStats [name=" + name + ", ownedEntryCount=" + ownedEntryCount + ", backupEntryCount=" + backupEntryCount + ", ownedEntryMemoryCost=" + ownedEntryMemoryCost + ", backupEntryMemoryCost=" + backupEntryMemoryCost + ", lockedEntryCount="
				+ lockedEntryCount + ", dirtyEntryCount=" + dirtyEntryCount + ", hits=" + hits + ", operationsStats=" + operationsStats + "]";
	}
}