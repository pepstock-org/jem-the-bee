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
package org.pepstock.jem.gwt.client.panels.jobs.commons;

import org.pepstock.jem.OutputListItem;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Uses to show the list of output spaces produced by job.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class OutputTreeItem extends FlowPanel {

	private OutputListItem item = null;
	
	/**
	 * Constructs the panel using the list of output items produced by job
	 * 
	 * @param item list of output items
	 */
	public OutputTreeItem(OutputListItem item) {
		this.item = item;
		Label label = new Label(item.getLabel());
		add(label);
	}
	
	/**
	 * @return the item
	 */
	public OutputListItem getItem() {
		return item;
	}
}