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
package org.pepstock.jem.gwt.client.panels.administration.nodessystem.inspector;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.util.ColumnIndex;

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
			case ColumnIndex.COLUMN_1: 
				// sorts by label of node
				diff = o1.getMemberLabel().compareTo(o2.getMemberLabel());
				break;
			case ColumnIndex.COLUMN_2: 
				// sorts by label of node
				diff = o1.getKey().compareTo(o2.getKey());
				break;
			case ColumnIndex.COLUMN_3: 
				// sorts by cpu
				double totCpu = o1.getCpuPercent() - o2.getCpuPercent();
				diff = (int)(totCpu * 10000);
				break;
			case ColumnIndex.COLUMN_4: 
				// sorts by memory avail
				diff = (int)(o1.getMemoryAvailable() - o2.getMemoryAvailable());
				break;
			case ColumnIndex.COLUMN_5: 
				// sorts by memory free
				diff = (int)(o1.getMemoryFree() - o2.getMemoryFree());
				break;
			case ColumnIndex.COLUMN_6: 
				// sorts by process cpu 
				double totPCpu = o1.getProcessCpuPercent() - o2.getProcessCpuPercent(); 
				diff = (int)(totPCpu*10000);
				break;
			case ColumnIndex.COLUMN_7: 
				// sorts by process tot cpu
				diff = (int)(o1.getProcessTotalCpu() - o2.getProcessTotalCpu());
				break;
			case ColumnIndex.COLUMN_8: 
				// sorts by process memory used
				diff = (int)(o1.getProcessMemoryUsed() - o2.getProcessMemoryUsed());
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
