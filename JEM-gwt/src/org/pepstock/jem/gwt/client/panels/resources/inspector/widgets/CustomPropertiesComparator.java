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
package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.resources.ResourceProperty;

/**
 * Is the column comparator to sort cell table for table with properties keys
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@SuppressWarnings("serial")
public class CustomPropertiesComparator extends IndexedColumnComparator<ResourceProperty> {

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting IGNORED HERE
	 */
	public CustomPropertiesComparator(int index) {
		super(index);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ResourceProperty o1, ResourceProperty o2) {
		// EMPTY rows, to be editable, ALWAYS on bottom
		if (o1.getName().equals(CustomPropertiesEditor.NO_VALUE) && o2.getName().equals(CustomPropertiesEditor.NO_VALUE)){
			return 0;
		} else if (o1.getName().equals(CustomPropertiesEditor.NO_VALUE)){
			return 1;
		} else if (o2.getName().equals(CustomPropertiesEditor.NO_VALUE)){
			return -1;
		} else {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}

}