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


/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class LightMapStats extends MapOperationsStats{
	
	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private long ownedEntryCount = 0;
	
	private long ownedEntryMemoryCost = 0;

	private long lockedEntryCount = 0;
	
	private long lockWaitCount = 0;

	private long hits = 0;
	
	/**
	 * 
	 */
	public LightMapStats() {
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
	 * @return the lockWaitCount
	 */
	public long getLockWaitCount() {
		return lockWaitCount;
	}

	/**
	 * @param lockWaitCount the lockWaitCount to set
	 */
	public void setLockWaitCount(long lockWaitCount) {
		this.lockWaitCount = lockWaitCount;
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

}