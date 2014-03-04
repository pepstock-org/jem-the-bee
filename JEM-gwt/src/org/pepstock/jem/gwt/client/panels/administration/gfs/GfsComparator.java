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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.stats.LightMemberSample;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class GfsComparator extends IndexedColumnComparator<LightMemberSample> {

    private static final long serialVersionUID = 1L;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public GfsComparator(int index) {
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
				diff = o1.getKey().compareTo(o2.getKey());
				break;
			case 1: 
				// sorts by input
				diff = (int)(o1.getGfsFree() - o2.getGfsFree());
				break;
			case 2: 
				// sorts by input
				diff = (int)(o1.getGfsFree() - o2.getGfsFree());
				break;
			case 3: 
				// sorts by input
				diff = (int)(o1.getGfsUsed() - o2.getGfsUsed());
				break;
			case 4: 
				// sorts by input
				diff = (int)(o1.getGfsUsed() - o2.getGfsUsed());
				break;
				
			default:
				// sorts by label of node
				diff = o1.getKey().compareTo(o2.getKey());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}