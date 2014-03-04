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
package org.pepstock.jem.gwt.client.panels.administration.redo;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.persistence.RedoStatement;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class RedoComparator extends IndexedColumnComparator<RedoStatement> {

	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public RedoComparator(int index) {
		super(index);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(RedoStatement o1, RedoStatement o2) {
		int diff = 0;
		switch(getIndex()){
			case 0: 
				// sorts by label of node
				diff = o1.getId().compareTo(o2.getId());
				break;
			case 1: 
				// sorts by cpu
				diff = o1.getQueueName().compareTo(o2.getQueueName());
				break;
			case 2: 
				// sorts by memory avail
				diff = o1.getAction().compareTo(o2.getAction());
				break;
			case 3: 
				// sorts by process cpu 
				diff = o1.getJobId().compareTo(o2.getJobId()); 
				break;
			case 4: 
				// sorts by process tot cpu
				diff = o1.getJob().getName().compareTo(o2.getJob().getName()); 
				break;
				
			default:
				// sorts by label of node
				diff = o1.getId().compareTo(o2.getId());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}