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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class ListHeader extends FlexTable  {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	
	/**
	 * @param label 
	 * 
	 */
	public ListHeader() {
		setHeight(Sizes.toString(Sizes.NODE_LIST_HEADER_PX));
		setWidth(Sizes.toString(Sizes.NODE_LIST_WIDTH));
		
		/* 		  0		1					2
		 * 		-------------------------------------
		 * 	0	| Node: <title>				back    |
		 * 		-------------------------------------
		 */
		
		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		cf.setWordWrap(0, 0, false);
		cf.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		cf.addStyleName(0, 0, Styles.INSTANCE.common().bold());
		setHTML(0, 0, "Nodes list");
	}
	
}