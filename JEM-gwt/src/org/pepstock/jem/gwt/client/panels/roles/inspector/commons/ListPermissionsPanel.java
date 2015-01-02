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
package org.pepstock.jem.gwt.client.panels.roles.inspector.commons;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.CellTableStyle;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.components.RemovePanel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;


/**
 * Component to manage the  search permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class ListPermissionsPanel extends HorizontalPanel implements InspectListener<String>, ClickHandler, ResizeCapable {
	
	// common styles
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.administration().ensureInjected();
	}

	private Role role = null;

	private RemovePanel remove = new RemovePanel();
	
	private InputPanel inputPanel = null;
	
	private CellList<String> cellList = null;
	
	private ScrollPanel scroller = new ScrollPanel();

	private final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();

	private final List<String> permissionSubList = new ArrayList<String>();
	
	private String mainPermission = null; 

	/**
	 * Constructs all UI using role instance information
	 * 
	 * @param role
	 * @param mainPermission
	 * @param inputLabel 
	 * @param cell 
	 * 
	 */
	public ListPermissionsPanel(Role role, String mainPermission, String inputLabel, AbstractCell<String>  cell) {
		this.role = role;
		this.mainPermission = mainPermission;
		
		cellList = new CellList<String>(cell, (Resources) GWT.create(CellTableStyle.class));
		
		// load only search permission loading the right permisison
		loadSubList();
		
		this.inputPanel = new InputPanel(inputLabel);
		inputPanel.setListener(this);
		add(inputPanel);

		// Panel with permissions list
		// Create a cell to render each value.
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Add a selection model to handle user selection.
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				String selected = selectionModel.getSelectedObject();
				if (selected != null) {
					// when selects a item, remove button will be enabled
					remove.setEnabled(true);
				}
			}
		});
		// Push the data into the widget.
		cellList.setRowCount(permissionSubList.size(), true);
		cellList.setRowData(0, permissionSubList);
		scroller.setWidget(cellList);

	    VerticalPanel scrollHolder = new VerticalPanel();
	    scrollHolder.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    scrollHolder.addStyleName(Styles.INSTANCE.administration().nodeList());
	    scrollHolder.add(scroller);

		VerticalPanel listContainer = new VerticalPanel();
		listContainer.setSpacing(5);
		
		Label label = new Label("Permissions");
		label.addStyleName(Styles.INSTANCE.common().bold());
		label.setHeight(Sizes.toString(InputPanel.LABEL_HEIGHT));
		listContainer.add(label);
		listContainer.add(scrollHolder);
	
		add(listContainer);
		
		remove.setClickHandler(this);
		add(remove);

	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	
	/**
	 * @return the inputPanel
	 */
	public InputPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * @param inputPanel the inputPanel to set
	 */
	public void setInputPanel(InputPanel inputPanel) {
		this.inputPanel = inputPanel;
	}

	/**
	 * @return the mainPermission
	 */
	public String getMainPermission() {
		return mainPermission;
	}

	/**
	 * @param mainPermission the mainPermission to set
	 */
	public void setMainPermission(String mainPermission) {
		this.mainPermission = mainPermission;
	}
	
	/**
	 * Removes the permission from permissions list
	 * 
	 * @param selectedPermission selected permission
	 */
	public void remove(String selectedPermission) {
		role.getPermissions().remove(selectedPermission);
		permissionSubList.remove(selectedPermission);
		// after removing sets again the data provider
		cellList.setRowCount(permissionSubList.size());
		cellList.setRowData(0, permissionSubList);
		// disable remove button
		remove.setEnabled(false);
	}

	/**
	 * Adds the permission from permissions list
	 * 
	 * @param selectedPermission selected permission
	 */
	public void add(String selectedPermission) {
		// creates the complete permission, adding the prefix based on 
		// the checked radiobutton
		String permission = (selectedPermission.startsWith(getMainPermission())) ? selectedPermission : getMainPermission() + Permissions.PERMISSION_SEPARATOR + selectedPermission;
		// if not in the list, adds!
		if (!role.getPermissions().contains(permission)) {
			// adds on both lists (in role object and in helpful object here)
			role.getPermissions().add(permission);
			permissionSubList.add(permission);
			// after adding sets again the data provider
			cellList.setRowCount(permissionSubList.size());
			cellList.setRowData(0, permissionSubList);
			// resets text field
			inputPanel.clear();
			// if new item is selected, sets Remove enable
			// otherwise is not possible to enabled it 
			if (selectionModel.isSelected(permission)) {
				remove.setEnabled(true);
			}
		}
	}
	
	/**
	 * Loads all right permissions for searching in a specific arraylist
	 */
    private final void loadSubList(){
		for (String permission : role.getPermissions()){
			if (permission.startsWith(getMainPermission())){
				permissionSubList.add(permission);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	
    	int widthInputPanel = InputPanel.SPACING + InputPanel.WIDTH + InputPanel.SPACING;
    	
    	int widthRemovePanel = InputPanel.SPACING + RemovePanel.WIDTH + InputPanel.SPACING;

    	int width = availableWidth - widthInputPanel - widthRemovePanel - InputPanel.SPACING - InputPanel.SPACING
    			- Sizes.MAIN_TAB_PANEL_BORDER - Sizes.MAIN_TAB_PANEL_BORDER;
    	
    	int height = availableHeight - InputPanel.SPACING - InputPanel.SPACING - InputPanel.SPACING -
    			InputPanel.LABEL_HEIGHT - Sizes.MAIN_TAB_PANEL_BORDER - Sizes.MAIN_TAB_PANEL_BORDER;
    	
    	scroller.setHeight(Sizes.toString(height));
	    scroller.setWidth(Sizes.toString(width));
	    
    }

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
    @Override
    public void onClick(ClickEvent event) {
		String selected = selectionModel.getSelectedObject();
		remove(selected);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(String object) {
    	add(object);
    }

}