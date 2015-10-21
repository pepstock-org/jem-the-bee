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
package org.pepstock.jem.gwt.client.panels.swarm;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.util.ColumnIndex;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesComparator extends IndexedColumnComparator<NodeInfoBean> {

    private static final long serialVersionUID = 1L;

	/**
	 * @param index
	 * @param preferenceKey
	 */
    public NodesComparator(int index, String preferenceKey) {
	    super(index, preferenceKey);
    }

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(NodeInfoBean o1, NodeInfoBean o2) {
		int diff = 0;
		switch(getIndex()){
			case ColumnIndex.COLUMN_1: 
				// sorts by label of node
				diff = o1.getLabel().compareTo(o2.getLabel());
				break;
			case ColumnIndex.COLUMN_2: 
				// sorts by host name
				diff = o1.getHostname().compareTo(o2.getHostname());
				break;
			case ColumnIndex.COLUMN_3: 
				// sorts by domain
				diff = o1.getExecutionEnvironment().getEnvironment().compareTo(o2.getExecutionEnvironment().getEnvironment());
				break;
			case ColumnIndex.COLUMN_4: 
				// sorts by status
				diff = o1.getStatus().compareTo(o2.getStatus());
				break;
			case ColumnIndex.COLUMN_5: 
				// sorts by os
				diff = o1.getSystemName().compareTo(o2.getSystemName());
				break;
			default:
				// sorts by label of node
				diff = o1.getLabel().compareTo(o2.getLabel());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : diff * -1;
	}

}
