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
package org.pepstock.jem.gwt.client.panels.administration.queues;

import org.pepstock.jem.gwt.client.panels.administration.current.QueueData;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class DetailedQueueData extends QueueData {

	private long hits = 0;
	
	private long locked = 0;
	
	private long lockWaits = 0;
	
	private long puts = 0;
	
	private long removes = 0;
	
	private long gets = 0;
	
	/**
	 * @return the puts
	 */
	public long getPuts() {
		return puts;
	}

	/**
	 * @param puts the puts to set
	 */
	public void setPuts(long puts) {
		this.puts = puts;
	}


	/**
	 * @return the removes
	 */
	public long getRemoves() {
		return removes;
	}

	/**
	 * @param removes the removes to set
	 */
	public void setRemoves(long removes) {
		this.removes = removes;
	}

	/**
	 * @return the gets
	 */
	public long getGets() {
		return gets;
	}

	/**
	 * @param gets the gets to set
	 */
	public void setGets(long gets) {
		this.gets = gets;
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
	 * @return the locked
	 */
	public long getLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(long locked) {
		this.locked = locked;
	}

	/**
	 * @return the lockWaits
	 */
	public long getLockWaits() {
		return lockWaits;
	}

	/**
	 * @param lockWaits the lockWaits to set
	 */
	public void setLockWaits(long lockWaits) {
		this.lockWaits = lockWaits;
	}
}