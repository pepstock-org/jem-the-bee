/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Cuc" Cuccato
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
package org.pepstock.jem.gwt.client.panels.components;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractPager;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.DefaultTablePager;
import org.pepstock.jem.gwt.client.commons.HasAbstractTable;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Component which contains the result underlyingTable 
 * 
 * @author Marco "Cuc" Cuccato
 *
 */
public class TableContainer<T> extends ScrollPanel implements HasAbstractTable<T>, ResizeCapable {

	private VerticalPanel scrollable = new VerticalPanel();
	private AbstractTable<T> underlyingTable = null;
	
	/**
	 * Builds teh UI using the underlyingTable underlyingTable
	 * 
	 * @param underlyingTable
	 * @param fullheightTable <code>true</code> if you wont the underlyingTable has full height, false otherwise
	 */
	public TableContainer(AbstractTable<T> underlyingTable) {
		this.underlyingTable = underlyingTable;
		
		scrollable.setWidth(Sizes.HUNDRED_PERCENT);
		scrollable.add(this.underlyingTable.getTable());
		AbstractPager pager = new DefaultTablePager(underlyingTable.getPager());
		scrollable.add(pager);
		scrollable.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		add(scrollable);
	}
	
	/**
	 * @return the underlyingTable
	 */
	@Override
	public AbstractTable<T> getUnderlyingTable() {
		return underlyingTable;
	}

	/**
	 * not supported by this implementation
	 */
	@Override
	public void setUnderlyingTable(AbstractTable<T> underlyingTable) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onResize(int availableWidth, int availableHeight) {
		setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
	}
}