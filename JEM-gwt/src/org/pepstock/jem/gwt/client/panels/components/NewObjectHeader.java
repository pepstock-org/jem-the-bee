/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco Cuccato
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
import org.pepstock.jem.gwt.client.commons.ConfirmMessageBox;
import org.pepstock.jem.gwt.client.commons.HideHandler;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.PreferredButton;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Marco Cuccato
 *
 */
public abstract class NewObjectHeader extends FlexTable {

	static {
		Styles.INSTANCE.inspector().ensureInjected();
		Styles.INSTANCE.common().ensureInjected();
	}

	private PopupPanel parent = null;
	private String editableText = null;
	private TextBox name = null;
	
	/**
	 * Creates the component, using an icon, an user writable name, as argument to show
	 * @param icon the image
	 * @param editableText name or description of the resource user should edit
	 * @param parent the parent popup that can be closed by clicking close icon
	 */
	public NewObjectHeader(ImageResource icon, String editableText, PopupPanel parent) {
		this.parent = parent;
		// init editable name box
		this.editableText = editableText;
		this.name = new TextBox();
		name.setWidth("80%");
		name.setText(editableText);
		// select all editable name when user click on textinput
		name.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				name.selectAll();
			}
		});
		// fire an event that returns the user typed name 
		name.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onNameTyped(name.getText());
			}
		});
		setWidth(Sizes.HUNDRED_PERCENT);
		setCellSpacing(3);
		addStyleName(Styles.INSTANCE.inspector().gradientBackground());
		
		/* 			0	1						2
		 * 		-------------------------------------
		 * 	0	| ICON EDITABLE NAME			X   |
		 * 		-------------------------------------
		 */

		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		// 0-0 > icona
		cf.addStyleName(0, 0, Styles.INSTANCE.inspector().headerDefaultPadding());
		cf.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		setWidget(0, 0, new Image(icon));

		// 0-1 > title
		cf.addStyleName(0, 1, Styles.INSTANCE.inspector().headerDefaultPadding());
		cf.setWidth(0, 1, Sizes.HUNDRED_PERCENT);
		cf.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		name.setStylePrimaryName(Styles.INSTANCE.inspector().inputMain());
		setWidget(0, 1, name);
		
		// 0-2 > close icon
		cf.addStyleName(0, 2, Styles.INSTANCE.inspector().headerClosePadding());
		Image closeImage = new Image(Images.INSTANCE.close24()); 
		closeImage.addStyleName(Styles.INSTANCE.common().pointer());
		closeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onClose(event);
			}
		});
		cf.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		cf.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(0, 2, closeImage);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// deferred focus set
		new Timer() {
			@Override
			public void run() {
				name.setFocus(true);
				name.selectAll();
			}
		}.schedule(0);
	}

	/**
	 * Fired when a click on upper-right corner close icon is clicked.
	 * This method is intended to be overridden, but should always call super.onClose();
	 * @param e
	 */
	public void onClose(ClickEvent e) {
		if (isEdited()) {
			ConfirmMessageBox cd = new ConfirmMessageBox("Unsaved changes", "There are unsaved changes. Close anyway?");
	        cd.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton button) {
			        if (button.getAction() == PreferredButton.YES_ACTION){
			        	parent.hide();
			        }
				}
			});
			cd.open();
		} else {
			parent.hide();
		}
	}
	
	/**
	 * Fired when user type something on new object name textinput
	 * @param name The name typed by user
	 */
	public abstract void onNameTyped(String name);
	
	/**
	 * @return
	 */
	public boolean hasName() {
		if (name != null && !name.getText().trim().isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		if (name != null) {
			return name.getText().trim();
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public boolean isEdited() {
		String headerText = getName();
		if (headerText != null) {
			return !headerText.equals(editableText);
		}
		return false;
	}
}