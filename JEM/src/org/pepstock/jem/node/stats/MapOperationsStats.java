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

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class MapOperationsStats implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long numberOfPuts = Long.MIN_VALUE;

	private long numberOfGets = Long.MIN_VALUE;

	private long totalPutLatency = Long.MIN_VALUE;

	private long totalGetLatency = Long.MIN_VALUE;

	private long totalRemoveLatency = Long.MIN_VALUE;

	private long numberOfRemoves = Long.MIN_VALUE;

	private long numberOfEvents = Long.MIN_VALUE;

	private long numberOfOtherOperations = Long.MIN_VALUE;

	/**
	 * 
	 */
	public MapOperationsStats() {
	}

	/**
	 * @return the numberOfPuts
	 */
	public long getNumberOfPuts() {
		return numberOfPuts;
	}

	/**
	 * @param numberOfPuts the numberOfPuts to set
	 */
	public void setNumberOfPuts(long numberOfPuts) {
		this.numberOfPuts = numberOfPuts;
	}

	/**
	 * @return the numberOfGets
	 */
	public long getNumberOfGets() {
		return numberOfGets;
	}

	/**
	 * @param numberOfGets the numberOfGets to set
	 */
	public void setNumberOfGets(long numberOfGets) {
		this.numberOfGets = numberOfGets;
	}

	/**
	 * @return the totalPutLatency
	 */
	public long getTotalPutLatency() {
		return totalPutLatency;
	}

	/**
	 * @param totalPutLatency the totalPutLatency to set
	 */
	public void setTotalPutLatency(long totalPutLatency) {
		this.totalPutLatency = totalPutLatency;
	}

	/**
	 * @return the totalGetLatency
	 */
	public long getTotalGetLatency() {
		return totalGetLatency;
	}

	/**
	 * @param totalGetLatency the totalGetLatency to set
	 */
	public void setTotalGetLatency(long totalGetLatency) {
		this.totalGetLatency = totalGetLatency;
	}

	/**
	 * @return the totalRemoveLatency
	 */
	public long getTotalRemoveLatency() {
		return totalRemoveLatency;
	}

	/**
	 * @param totalRemoveLatency the totalRemoveLatency to set
	 */
	public void setTotalRemoveLatency(long totalRemoveLatency) {
		this.totalRemoveLatency = totalRemoveLatency;
	}

	/**
	 * @return the numberOfRemoves
	 */
	public long getNumberOfRemoves() {
		return numberOfRemoves;
	}

	/**
	 * @param numberOfRemoves the numberOfRemoves to set
	 */
	public void setNumberOfRemoves(long numberOfRemoves) {
		this.numberOfRemoves = numberOfRemoves;
	}

	/**
	 * @return the numberOfEvents
	 */
	public long getNumberOfEvents() {
		return numberOfEvents;
	}

	/**
	 * @param numberOfEvents the numberOfEvents to set
	 */
	public void setNumberOfEvents(long numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	/**
	 * @return the numberOfOtherOperations
	 */
	public long getNumberOfOtherOperations() {
		return numberOfOtherOperations;
	}

	/**
	 * @param numberOfOtherOperations the numberOfOtherOperations to set
	 */
	public void setNumberOfOtherOperations(long numberOfOtherOperations) {
		this.numberOfOtherOperations = numberOfOtherOperations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MapOperationsStats [numberOfPuts=" + numberOfPuts + ", numberOfGets=" + numberOfGets + ", totalPutLatency=" + totalPutLatency + ", totalGetLatency=" + totalGetLatency + ", totalRemoveLatency=" + totalRemoveLatency + ", numberOfRemoves="
				+ numberOfRemoves + ", numberOfEvents=" + numberOfEvents + ", numberOfOtherOperations=" + numberOfOtherOperations + "]";
	}


}