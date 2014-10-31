/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
import org.pepstock.jem.node.stats.FileSystemUtilization;
import org.pepstock.jem.node.stats.LightMemberSample;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class GfsComparator extends IndexedColumnComparator<LightMemberSample> {

    private static final long serialVersionUID = 1L;
    
    private GfsTable table = null;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 * @param table table instance to be sorted
	 */
	public GfsComparator(int index, GfsTable table) {
		super(index);
		this.table = table;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(LightMemberSample o1, LightMemberSample o2) {
		LightMemberSample one = isAscending() ? o1 : o2; 
		LightMemberSample two = isAscending() ? o2 : o1;
		
		FileSystemUtilization fsu1 = null;
		FileSystemUtilization fsu2 = null;
		if (table.getFileSystemName() != null){
			fsu1 = Util.getFileSystemUtilization(one, table.getFileSystemName());
			fsu2 = Util.getFileSystemUtilization(two, table.getFileSystemName());
		} else {
			// if file system name, sort only by time
			super.setIndex(0);
		}
		
		int diff = 0;
		switch(getIndex()){
			case 0: 
				// sorts by label of node
				diff = one.getKey().compareTo(two.getKey());
				break;
			case 1: 
				// sorts by free
				diff = (int)(fsu1.getFree() - fsu2.getFree());
				break;
			case 2: 
				// sorts by free
				diff = (int)(fsu1.getFree() - fsu2.getFree());
				break;
			case 3: 
				// sorts by used
				diff = (int)(fsu1.getUsed() - fsu2.getUsed());
				break;
			case 4: 
				// sorts by used
				diff = (int)(fsu1.getUsed() - fsu2.getUsed());
				break;
				
			default:
				// sorts by label of node
				diff = one.getKey().compareTo(two.getKey());
				break;
		}
		// checks if Ascending otherwise negative
		return diff;
	}

}