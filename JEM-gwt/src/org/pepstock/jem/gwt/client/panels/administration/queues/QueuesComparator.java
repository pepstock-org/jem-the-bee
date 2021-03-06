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

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.util.ColumnIndex;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class QueuesComparator extends IndexedColumnComparator<DetailedQueueData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public QueuesComparator(int index) {
		super(index);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(DetailedQueueData o1, DetailedQueueData o2) {
		int diff = 0;
		switch(getIndex()){
			case ColumnIndex.COLUMN_1: 
				// sorts by label of queue
				diff = o1.getFullName().compareTo(o2.getFullName());
				break;
			case ColumnIndex.COLUMN_3: 
				// sorts by entries
				diff = (int)(o1.getEntries() - o2.getEntries());
				break;
			case ColumnIndex.COLUMN_4: 
				// sorts by hits
				diff = (int)(o1.getHits() - o2.getHits());
				break;
			case ColumnIndex.COLUMN_5: 
				// sorts by locked
				diff = (int)(o1.getLocked() - o2.getLocked());
				break;
			case ColumnIndex.COLUMN_6: 
				// sorts by gets
				diff = (int)(o1.getGets() - o2.getGets());
				break;
			case ColumnIndex.COLUMN_7: 
				// sorts by puts
				diff = (int)(o1.getPuts() - o2.getPuts());
				break;
			case ColumnIndex.COLUMN_8: 
				// sorts by removes
				diff = (int)(o1.getRemoves() - o2.getRemoves());
				break;
				
			default:
				// sorts by label of queue
				diff = o1.getFullName().compareTo(o2.getFullName());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}
