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
package org.pepstock.jem.gwt.client.panels.jobs.commons.inspector;

import org.pepstock.jem.OutputListItem;

import com.google.gwt.user.client.ui.Anchor;

/**
 * Is an anchor with information about the output item to show
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class ItemAnchor extends Anchor {

	private OutputListItem item = null;
	
	/**
	 * Constructs the anchor using the item label 
	 * @param item 
	 * 
	 */
	public ItemAnchor(OutputListItem item) {
		super(item.getLabel());
		this.item = item;
	}

	/**
	 * @return the item
	 */
	public OutputListItem getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(OutputListItem item) {
		this.item = item;
	}
}