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
 * Is a bean all information about usage statistics of a queue of Hazelcast
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class QueueOperationsStats implements Serializable {

	private static final long serialVersionUID = 1L;

	private long numberOfOffers = Long.MIN_VALUE;

	private long numberOfPolls = Long.MIN_VALUE;

	private long numberOfEmptyPolls = Long.MIN_VALUE;

	private long numberOfRejectedOffers = Long.MIN_VALUE;

	/**
	 * Empty constructor
	 */
	public QueueOperationsStats() {
	}

	/**
	 * @return the numberOfOffers
	 */
	public long getNumberOfOffers() {
		return numberOfOffers;
	}

	/**
	 * @param numberOfOffers the numberOfOffers to set
	 */
	public void setNumberOfOffers(long numberOfOffers) {
		this.numberOfOffers = numberOfOffers;
	}

	/**
	 * @return the numberOfPolls
	 */
	public long getNumberOfPolls() {
		return numberOfPolls;
	}

	/**
	 * @param numberOfPolls the numberOfPolls to set
	 */
	public void setNumberOfPolls(long numberOfPolls) {
		this.numberOfPolls = numberOfPolls;
	}

	/**
	 * @return the numberOfEmptyPolls
	 */
	public long getNumberOfEmptyPolls() {
		return numberOfEmptyPolls;
	}

	/**
	 * @param numberOfEmptyPolls the numberOfEmptyPolls to set
	 */
	public void setNumberOfEmptyPolls(long numberOfEmptyPolls) {
		this.numberOfEmptyPolls = numberOfEmptyPolls;
	}

	/**
	 * @return the numberOfRejectedOffers
	 */
	public long getNumberOfRejectedOffers() {
		return numberOfRejectedOffers;
	}

	/**
	 * @param numberOfRejectedOffers the numberOfRejectedOffers to set
	 */
	public void setNumberOfRejectedOffers(long numberOfRejectedOffers) {
		this.numberOfRejectedOffers = numberOfRejectedOffers;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QueueOperationsStats [numberOfOffers=" + numberOfOffers + ", numberOfPolls=" + numberOfPolls + ", numberOfEmptyPolls=" + numberOfEmptyPolls + ", numberOfRejectedOffers=" + numberOfRejectedOffers + "]";
	}

}