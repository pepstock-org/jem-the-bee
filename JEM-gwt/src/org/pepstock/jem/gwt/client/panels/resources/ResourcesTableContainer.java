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
package org.pepstock.jem.gwt.client.panels.resources;

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Roles table container for roles
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ResourcesTableContainer extends DockLayoutPanel {

	private ResourcesTable table = null;

	/**
	 * Creates the UI by the argument (the table) 
	 * 
	 * @param table table of roles
	 * 
	 */
	public ResourcesTableContainer(ResourcesTable table) {
		super(Unit.PX);
		this.table = table;
		setWidth(Sizes.HUNDRED_PERCENT);
		setHeight(Sizes.HUNDRED_PERCENT);
		addSouth(table.getPager(), 40);
		ScrollPanel centerScrollable = new ScrollPanel(table.getTable());
		add(centerScrollable);
	}

	/**
	 * @return the table
	 */
	public ResourcesTable getRolesTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setRolesTable(ResourcesTable table) {
		this.table = table;
	}

}