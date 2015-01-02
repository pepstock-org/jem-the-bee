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
package org.pepstock.jem.gwt.client.panels.resources;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.node.resources.Resource;

/**
 * Is the column comparator to sort cell table for table with roles
 * @author Andrea "Stock" Stocchero
 *
 */
@SuppressWarnings("serial")
public class ResourcesComparator extends IndexedColumnComparator<Resource> {

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public ResourcesComparator(int index) {
		super(index, PreferencesKeys.RESOURCES_SORT);
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    @Override
    public int compare(Resource o1, Resource o2) {
		int diff = 0;
		switch(getIndex()){
			case 1: 
				// sorts by role name
				diff = o1.getName().compareTo(o2.getName());
				break;
			case 2: 
				// sorts by type
				diff = o1.getType().compareTo(o2.getType());
				break;
			case 4: 
				// sorts by last modified
				diff = sortByLastModified(o1, o2);
				break;
			case 5: 
				// sorts by user
				diff = sortByUser(o1, o2);
				break;				
			default:
				// sorts by role name
				diff = o1.getName().compareTo(o2.getName());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : diff * -1;
    }
	
    private int sortByUser(Resource o1, Resource o2) {
		String user1 = o1.getUser() != null ? o1.getUser() : ""; 
		String user2 = o2.getUser() != null ? o2.getUser() : "";
		return user1.compareTo(user2);
    }
    
    private int sortByLastModified(Resource o1, Resource o2) {
    	int diff;
		if (o1.getLastModified() == null){
			if (o2.getLastModified() == null){
				diff = 0 ;
			} else {
				diff = 1;
			}
		} else {
			if (o2.getLastModified() == null){
				diff = -1 ;
			} else {
				diff = o1.getLastModified().compareTo(o2.getLastModified());
			}
		}
		return diff;
    }
    
}