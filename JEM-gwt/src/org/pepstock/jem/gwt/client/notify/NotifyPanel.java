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
package org.pepstock.jem.gwt.client.notify;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Components which contains the table with all toasts emitted
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NotifyPanel extends AbstractInspector {

	private TableContainer<ToastMessage> main = new TableContainer<ToastMessage>(new NotifyTable());
	
	private VerticalPanel tableContainer = new VerticalPanel();
	
	/**
	 * Constructs the object setting teh inspector without actions (no buttons)
	 */
	public NotifyPanel() {
		super(false);
		
		tableContainer.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		tableContainer.setSpacing(10);
	    tableContainer.add(main);
	    
	    // sets toats to table
	    main.getUnderlyingTable().setRowData(MessagesCollection.getMessages());
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
	    return new NotifyHeader(this);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getContent()
	 */
    @Override
    public Panel getContent() {
    	main.setSize(Sizes.toString(getAvailableWidth()), Sizes.toString(getAvailableHeight()));
	    return tableContainer;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getActions()
	 */
    @Override
    public Panel getActions() {
	    return null;
    }

}
