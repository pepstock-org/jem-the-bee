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
package org.pepstock.jem.gwt.client.panels.roles.inspector.commons;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public final class PermissionItem extends HorizontalPanel {
	
	// common styles
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private Label labelText = new Label();
	
	private CheckBox checkBox = new CheckBox();
	
	private String permission = null;
	
	private static final int CHECK_BOX_SIZE = 24;

	/**
	 * @param label 
	 * @param descritpion 
	 * @param permission 
	 * 
	 */
	public PermissionItem(String label, String descritpion, String permission) {
		this.permission = permission;
		
		VerticalPanel checkBoxPanel = new VerticalPanel();
		checkBoxPanel.add(this.checkBox);
		checkBoxPanel.setWidth(Sizes.toString(CHECK_BOX_SIZE));
		
		VerticalPanel labelPanel = new VerticalPanel();
		labelText.setText(label);
		
		changeStyle();
		Label descriptionText = new Label(descritpion);
		descriptionText.addStyleName(Styles.INSTANCE.common().permissionDescription());
		labelPanel.add(labelText);
		labelPanel.add(descriptionText);
		
		add(checkBoxPanel);
		setCellVerticalAlignment(checkBoxPanel, ALIGN_MIDDLE);
		add(labelPanel);

		checkBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				changeStyle();
			}
		});
	}
	
	
	
	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}



	/**
	 * @param permission the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	
	/**
	 * @return
	 */
	public boolean getValue(){
		return checkBox.getValue();
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(boolean value){
		checkBox.setValue(value);
		changeStyle();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEnabled(){
		return checkBox.isEnabled();
	}

	/**
	 * 
	 * @param value
	 */
	public void setEnabled(boolean value){
		checkBox.setEnabled(value);
		changeStyle();
	}
	
	/**
	 * 
	 * @param handler
	 */
	public void addClickHandler(ClickHandler handler){
		checkBox.addClickHandler(handler);
	}

	/**
	 * 
	 */
	private void changeStyle(){
		labelText.removeStyleName(Styles.INSTANCE.common().permissionLabelDisabled());
		labelText.removeStyleName(Styles.INSTANCE.common().permissionLabelSelected());
		labelText.removeStyleName(Styles.INSTANCE.common().permissionLabel());
		if (!checkBox.isEnabled()){
			labelText.addStyleName(Styles.INSTANCE.common().permissionLabelDisabled());
		} else {
			if (checkBox.getValue()){
				labelText.addStyleName(Styles.INSTANCE.common().permissionLabelSelected());
			} else {
				labelText.addStyleName(Styles.INSTANCE.common().permissionLabel());
			}
		}
		
	}

}