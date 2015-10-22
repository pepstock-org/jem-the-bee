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
package org.pepstock.jem.gwt.client.panels.administration.redo;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.util.ColumnIndex;

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
			case ColumnIndex.COLUMN_1: 
				// sorts by label of node
				diff = o1.getId().compareTo(o2.getId());
				break;
			case ColumnIndex.COLUMN_2: 
				// sorts by cpu
				diff = o1.getQueueName().compareTo(o2.getQueueName());
				break;
			case ColumnIndex.COLUMN_3: 
				// sorts by memory avail
				diff = o1.getAction().compareTo(o2.getAction());
				break;
			case ColumnIndex.COLUMN_4: 
				// sorts by memory avail
				diff = o1.getCreation().compareTo(o2.getCreation());
				break;
			case ColumnIndex.COLUMN_5:
				// sorts by process cpu 
				diff = sortByEntityID(o1, o2);
				break;
			case ColumnIndex.COLUMN_6: 
				// sorts by process tot cpu
				diff = sortByEntity(o1, o2);
				break;
				
			default:
				// sorts by label of node
				diff = o1.getId().compareTo(o2.getId());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

	private int sortByEntityID(RedoStatement o1, RedoStatement o2) {
		String id1 = o1.getEntityId() != null ? o1.getEntityId() : ""; 
		String id2 = o2.getEntityId() != null ? o2.getEntityId() : "";
		return id1.compareTo(id2);
	}
	
	private int sortByEntity(RedoStatement o1, RedoStatement o2) {
		String id1 = o1.getEntityToString() != null ? o1.getEntityToString() : ""; 
		String id2 = o2.getEntityToString() != null ? o2.getEntityToString() : "";
		return id1.compareTo(id2);
	}
}
