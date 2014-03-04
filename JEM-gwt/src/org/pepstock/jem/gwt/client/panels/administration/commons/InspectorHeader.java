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
package org.pepstock.jem.gwt.client.panels.administration.commons;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.ImageAndTextAnchor;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class InspectorHeader extends FlexTable  {
	
	static {
		Styles.INSTANCE.inspector().ensureInjected();
	}
	
	/**
	 * 
	 */
	public static final int HEIGHT = 40;
	
	private String title = null;
	
	private BackListener listener = null;

	/**
	 * @param label 
	 * 
	 */
	public InspectorHeader(String label) {
		setHeight(HEIGHT+"px");
		setWidth(Sizes.HUNDRED_PERCENT);

		
		/* 		  0		1					2
		 * 		-------------------------------------
		 * 	0	| Node: <title>				back    |
		 * 		-------------------------------------
		 */
		
		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		cf.setWidth(0, 0, "45px");
		cf.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		setHTML(0, 0, label);
		
		
		cf.setWidth(0, 1, Sizes.HUNDRED_PERCENT);
		cf.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		cf.addStyleName(0, 1, Styles.INSTANCE.inspector().adminTitle());
		
		// 0-1 > back
		cf.setWidth(0, 2, "20%");
		
		// logoff button (and handler)
		final ImageAndTextAnchor button = new ImageAndTextAnchor(Images.INSTANCE.back(), "Back");
		button.addStyleName(Styles.INSTANCE.inspector().adminHeaderBack());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (listener != null){
					listener.back();
				}
			}
		});
		cf.setWordWrap(0, 2, false);
		cf.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);
		cf.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(0, 2, button);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
		setHTML(0, 1, title);
	}

	/**
	 * @return the listener
	 */
	public BackListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(BackListener listener) {
		this.listener = listener;
	}
	
}