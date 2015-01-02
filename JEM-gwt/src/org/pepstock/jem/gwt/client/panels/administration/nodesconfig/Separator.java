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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Separator extends HorizontalPanel {
	
	/**
	 * Default width
	 */
	public static final int WIDTH = 6;
	
	private final Image closeIcon = new Image(Images.INSTANCE.tabLeft());
	
	private final Image openIcon = new Image(Images.INSTANCE.tabRight());
	
	private boolean open = true;
	
	private SeparatorListener listener = null;

	/**
	 * 
	 */
	public Separator() {
		setWidth(Sizes.toString(WIDTH));
		add(closeIcon);
		setCellVerticalAlignment(closeIcon, ALIGN_MIDDLE);
		closeIcon.getElement().getStyle().setCursor(Cursor.POINTER);
		closeIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setOpen(false);
			}
		});
		
		
		openIcon.setVisible(false);
		add(openIcon);
		setCellVerticalAlignment(openIcon, ALIGN_MIDDLE);
		openIcon.getElement().getStyle().setCursor(Cursor.POINTER);
		openIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setOpen(true);
			}
		});
	}


	/**
	 * @return the opened
	 */
	public boolean isOpen() {
		return open;
	}


	/**
	 * @param opened the opened to set
	 */
	public void setOpen(boolean opened) {
		if (this.open != opened){
			this.open = opened;
			handleClick();
		}
	}

	
	
	/**
	 * @return the closeIcon
	 */
	public Image getCloseIcon() {
		return closeIcon;
	}


	/**
	 * @return the openIcon
	 */
	public Image getOpenIcon() {
		return openIcon;
	}


	/**
	 * @return the listener
	 */
	public SeparatorListener getListener() {
		return listener;
	}


	/**
	 * @param listener the listener to set
	 */
	public void setListener(SeparatorListener listener) {
		this.listener = listener;
	}



	private void handleClick(){
		if (open){
			openIcon.setVisible(false);
			closeIcon.setVisible(true);
		} else {
			closeIcon.setVisible(false);
			openIcon.setVisible(true);
		}
		if (listener != null){
			listener.changeSeparator(open);
		}
	}
	
}
