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
package org.pepstock.jem.gwt.client.panels.administration.nodesqueues;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.stats.LightMemberSample;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesComparator extends IndexedColumnComparator<LightMemberSample> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
			case 0: 
				// sorts by label of node
				diff = o1.getMemberLabel().compareTo(o2.getMemberLabel());
				break;
			case 3: 
				// sorts by input
				diff = (int)(o1.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryCount());
				break;
			case 4: 
				// sorts by input
				diff = (int)(o1.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryMemoryCost());
				break;

			case 5: 
				// sorts by running
				diff = (int)(o1.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryCount());
				break;
			case 6: 
				// sorts by running
				diff = (int)(o1.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryMemoryCost());
				break;

			case 7: 
				// sorts by output
				diff = (int)(o1.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryCount());
				break;
			case 8: 
				// sorts by output
				diff = (int)(o1.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryMemoryCost() - o2.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryMemoryCost());
				break;

			case 9: 
				// sorts by routing
				diff = (int)(o1.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryCount() - o2.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryCount());
				break;
			case 10: 
				// sorts by routing
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