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
package org.pepstock.jem.gwt.client.notify;


import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;

/**
 * Is the column comparator to sort cell table for table with toast already sent
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NotifyComparator extends IndexedColumnComparator<ToastMessage> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting.
	 * @param index index of column, chosen for sorting
	 */
	public NotifyComparator(int index) {
		super(index, PreferencesKeys.NOTIFY_SORT);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ToastMessage o1, ToastMessage o2) {
		int diff = 0;
		switch(getIndex()){
			case 0:
				// sorts by date of toast
				diff = o1.getDate().compareTo(o2.getDate());
				break;
			case 1:
				// sorts by level (info, warning or error) of toast
				diff = o1.getLevel().getIntLevel() - o2.getLevel().getIntLevel();
				break;
			case 2: 
				// sorts by title
				diff = o1.getTitle().compareTo(o2.getTitle());
				break;
			case 3: 
				// sorts by message
				diff = o1.getMessage().compareTo(o2.getMessage());
				break;
			default:
				// default, sorts by date
				diff = o1.getDate().compareTo(o2.getDate());
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}