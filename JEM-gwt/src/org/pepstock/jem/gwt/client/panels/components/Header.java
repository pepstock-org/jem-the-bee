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
package org.pepstock.jem.gwt.client.panels.components;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Header of popup of inspector. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public abstract class Header extends FlexTable {

	static {
		Styles.INSTANCE.inspector().ensureInjected();
		Styles.INSTANCE.common().ensureInjected();
	}

	protected PopupPanel parent = null;
	
	/**
	 * Creates the component, using an icon, a name and a parent!
	 * @param icon the image
	 * @param text job name
	 * @param parent the parent popup that can be closed by clicking close icon
	 */
	public Header(ImageResource icon, String text, PopupPanel parent) {
		this.parent = parent;
		setWidth(Sizes.HUNDRED_PERCENT);
		setHeight(Sizes.toString(Sizes.INSPECTOR_HEADER_HEIGHT_PX));
		setCellSpacing(3);
		addStyleName(Styles.INSTANCE.inspector().gradientBackground());
		
		/* 			0	1						2
		 * 		-------------------------------------
		 * 	0	| ICON NAME						X   |
		 * 		-------------------------------------
		 */

		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		// 0-0 > icona
		cf.addStyleName(0, 0, Styles.INSTANCE.inspector().headerDefaultPadding());
		cf.setHorizontalAlignment(RowIndex.ROW_1,ColumnIndex.COLUMN_1, HasHorizontalAlignment.ALIGN_LEFT);
		setWidget(0, 0, new Image(icon));

		// 0-1 > title
		cf.addStyleName(0, 1, Styles.INSTANCE.inspector().headerDefaultPadding());
		cf.setWidth(ColumnIndex.COLUMN_1, 1, Sizes.HUNDRED_PERCENT);
		cf.setHorizontalAlignment(RowIndex.ROW_1,ColumnIndex.COLUMN_2, HasHorizontalAlignment.ALIGN_LEFT);
		cf.addStyleName(0, 1, Styles.INSTANCE.inspector().main());
		setHTML(0, 1, text);
		
		// 0-2 > close icon
		cf.addStyleName(0, 2, Styles.INSTANCE.inspector().headerClosePadding());
		Image closeImage = new Image(Images.INSTANCE.close24());
		closeImage.addStyleName(Styles.INSTANCE.common().pointer());
		closeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onClose();
			}
		});
		cf.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		cf.setHorizontalAlignment(RowIndex.ROW_1,ColumnIndex.COLUMN_3, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(0, 2, closeImage);
	}

	/**
	 * @param event
	 */
	public void onClose() {
		parent.hide();
	}
	
}
