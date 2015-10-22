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
package org.pepstock.jem.gwt.client.panels.nodes;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.util.ColumnIndex;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@SuppressWarnings("serial")
public class NodesComparator extends IndexedColumnComparator<NodeInfoBean> {

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public NodesComparator(int index) {
		super(index, PreferencesKeys.NODES_SORT);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(NodeInfoBean o1, NodeInfoBean o2) {
		int diff = 0;
		switch(getIndex()){
			case ColumnIndex.COLUMN_2: 
				// sorts by label of node
				diff = o1.getLabel().compareTo(o2.getLabel());
				break;
			case ColumnIndex.COLUMN_3: 
				// sorts by host name
				diff = o1.getHostname().compareTo(o2.getHostname());
				break;
			case ColumnIndex.COLUMN_4: 
				diff = o1.getExecutionEnvironment().getDomain().compareTo(o2.getExecutionEnvironment().getDomain());
				break;
			case ColumnIndex.COLUMN_5: 
				diff = o1.getExecutionEnvironment().getStaticAffinities().toString().compareTo(o2.getExecutionEnvironment().getStaticAffinities().toString());
				break;
			case ColumnIndex.COLUMN_6: 
				diff = o1.getExecutionEnvironment().getDynamicAffinities().toString().compareTo(o2.getExecutionEnvironment().getDynamicAffinities().toString());
				break;				
			case ColumnIndex.COLUMN_7: 
				// sorts by status
				diff = o1.getStatus().compareTo(o2.getStatus());
				break;
			case ColumnIndex.COLUMN_8: 
				// sorts by os
				diff = o1.getSystemName().compareTo(o2.getSystemName());
				break;
			case ColumnIndex.COLUMN_9: 
				// sorts by memory
				diff = o1.getExecutionEnvironment().getMemory() - o2.getExecutionEnvironment().getMemory();
				break;
			case ColumnIndex.COLUMN_10: 
				// sorts by parallel jobs
				diff = o1.getExecutionEnvironment().getParallelJobs() - o2.getExecutionEnvironment().getParallelJobs();
				break;
			case ColumnIndex.COLUMN_11: 
				// sorts by jobnames list
				diff = o1.getJobNames().size() - o2.getJobNames().size();
				break;
			default:
				// sorts by label of node
				diff = o1.getLabel().compareTo(o2.getLabel());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}
