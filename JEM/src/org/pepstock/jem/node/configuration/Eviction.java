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
package org.pepstock.jem.node.configuration;

import java.io.Serializable;

/**
 * Contains the information about the eviction to apply to hazelcast map.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Eviction implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int maxSize = 0;
	
	private int percentage = 0;

	/**
	 * Creates the object with max size and percentage of eviction
	 * @param maxSize amount of element per node
	 * @param percentage percentage to evict
	 */
	public Eviction(int maxSize, int percentage) {
		this.maxSize = maxSize;
		this.percentage = percentage;
	}
	
	/**
	 * Necessary to serialize the object
	 */
	public Eviction() {
	}

	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the percentage
	 */
	public int getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Eviction [maxSize=" + maxSize + ", percentage=" + percentage + "]";
	}
}
