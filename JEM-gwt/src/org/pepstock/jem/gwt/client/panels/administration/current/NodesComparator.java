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
package org.pepstock.jem.gwt.client.panels.administration.current;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.util.ColumnIndex;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@SuppressWarnings("serial")
public class NodesComparator extends IndexedColumnComparator<LightMemberSample> {

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public NodesComparator(int index) {
		super(index);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(LightMemberSample o1, LightMemberSample o2) {
		int diff = 0;
		switch(getIndex()){
			case ColumnIndex.COLUMN_1: 
				// sorts by label of node
				diff = o1.getMemberLabel().compareTo(o2.getMemberLabel());
				break;
			case ColumnIndex.COLUMN_4: 
				// sorts by input
				diff = (int)(o1.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryCount());
				break;
			case ColumnIndex.COLUMN_5: 
				// sorts by input memory
				diff = (int)(o1.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryMemoryCost());
				break;
			case ColumnIndex.COLUMN_6: 
				// sorts by running
				diff = (int)(o1.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryCount());
				break;
			case ColumnIndex.COLUMN_7: 
				// sorts by running memory
				diff = (int)(o1.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryMemoryCost());
				break;
			case ColumnIndex.COLUMN_8: 
				// sorts by output
				diff = (int)(o1.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryCount());
				break;
			case ColumnIndex.COLUMN_9: 
				// sorts by output memory
				diff = (int)(o1.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryMemoryCost());
				break;
			case ColumnIndex.COLUMN_10: 
				// sorts by routing
				diff = (int)(o1.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryCount());
				break;
			case ColumnIndex.COLUMN_11: 
				// sorts by routing memory
				diff = (int)(o1.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryMemoryCost());
				break;
				
			default:
				// sorts by label of node
				diff = o1.getMemberLabel().compareTo(o2.getMemberLabel());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}
