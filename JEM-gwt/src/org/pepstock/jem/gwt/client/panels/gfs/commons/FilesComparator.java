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
package org.pepstock.jem.gwt.client.panels.gfs.commons;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;

/**
 * Is the column comparator to sort cell table for table with nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class FilesComparator extends IndexedColumnComparator<GfsFile> {

	private static final long serialVersionUID = -5637883008662740233L;

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 * @param preferenceKey preference key to extract the previous choice
	 */
	public FilesComparator(int index, String preferenceKey) {
		super(index, preferenceKey);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(GfsFile o1, GfsFile o2) {
		int diff = 0;
		switch(getIndex()) {
			case 1:
				diff = sortByLength(o1, o2);
				break;
			case 2:
				diff = sortByLastModified(o1, o2);
				break;
			case 3:
				diff = sortByPath(o1, o2);
				break;				
			default:
				diff = sortByName(o1, o2);
				break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}
	
	private int sortByLastModified(GfsFile o1, GfsFile o2) {
		int diff = 0;
		if (o1.getLastModified() > o2.getLastModified()){
			diff = 1;
		} else if (o1.getLastModified() < o2.getLastModified()){
			diff = -1;
		}
		return diff;
	}
	
	private int sortByLength(GfsFile o1, GfsFile o2) {
		int diff = 0;
		if (o1.getLength() > o2.getLength()){
			diff = 1;
		} else if (o1.getLength() < o2.getLength()){
			diff = -1;
		}
		return diff;
	}
	
	private int sortByName(GfsFile o1, GfsFile o2) {
		int diff;
		// sorts by label of node
		if (o1.isDirectory()) {
			if (o2.isDirectory()) {
				diff = o1.getName().compareTo(o2.getName());
			} else {
				diff = -1;
			}
		} else {
			if (o2.isDirectory()) {
				diff = 1;
			} else {
				diff = o1.getName().compareTo(o2.getName());
			}
		}
		return diff;
	}
	
	private int sortByPath(GfsFile o1, GfsFile o2) {
		int diff;
		// sorts by label of node
		if (o1.getDataPathName() != null) {
			if (o2.getDataPathName() != null) {
				diff = o1.getDataPathName().compareTo(o2.getDataPathName());
				if (diff == 0){
					diff = sortByName(o1, o2);
				}
			} else {
				diff = -1;
			}
		} else {
			if (o2.getDataPathName() != null) {
				diff = 1;
			} else {
				diff = sortByName(o1, o2);
			}
		}
		return diff;
	}

}