/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.CellTableStyle;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.panels.components.RemovePanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.InputPanel;
import org.pepstock.jem.log.MessageLevel;

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
 * A widget that provide insertion and removal values in a list  
 * @author Marco "Fuzzo" Cuccato
 */
public class ListEditor extends HorizontalPanel implements InspectListener<String>, ClickHandler, ResizeCapable {

	// common styles
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.administration().ensureInjected();
	}

	// input panel instantiation delayed because needs header
	private InputPanel inputPanel = null;

	private VerticalPanel listContainer = new VerticalPanel();
    private VerticalPanel scrollHolder = new VerticalPanel();
	private Label label = new Label();
	
	private ScrollPanel scrollPanel = new ScrollPanel();
	private CellList<String> cellList = new CellList<String>(new ListEditorCell(), (Resources) GWT.create(CellTableStyle.class));
	private RemovePanel removePanel = new RemovePanel();
	private final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();

	private List<String> values = new ArrayList<String>();
	private ListEditorListener<String[]> listener = null;
	private String regExValidator = null;
	
	/**
	 * Builds the editor
	 * @param inputPanelHeader the header text of input panel
	 * @param listHeader 
	 * @param regExValidator 
	 */
	public ListEditor(String inputPanelHeader, String listHeader, String regExValidator) {
		super();
		this.regExValidator = regExValidator;
		
		// input panel instantiation
		inputPanel = new InputPanel(inputPanelHeader);
		inputPanel.setListener(this);
		
		// listContainer
		cellList.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if (selectionModel.getSelectedObject() != null) {
					removePanel.setEnabled(true);
				}
			}
		});
	    scrollHolder.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    scrollHolder.addStyleName(Styles.INSTANCE.administration().nodeList());
	    scrollHolder.add(scrollPanel);
		scrollPanel.setWidget(cellList);

		listContainer.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    listContainer.setSpacing(5);
	    label.setText(listHeader);
		label.addStyleName(Styles.INSTANCE.common().bold());
		label.setHeight(Sizes.toString(InputPanel.LABEL_HEIGHT));
		
		listContainer.add(label);
		listContainer.add(scrollHolder);
		listContainer.setCellHeight(scrollHolder, Sizes.HUNDRED_PERCENT);

		removePanel.setClickHandler(this);

		// add all to main (this) panel
		add(inputPanel);
		add(listContainer);
		add(removePanel);
		setCellWidth(listContainer, Sizes.HUNDRED_PERCENT);
		setCellHeight(listContainer, Sizes.HUNDRED_PERCENT);
	}

	/**
	 * @return a <code>String[]</code> containing the list values
	 */
	public String[] getValues() {
		return values.toArray(new String[0]);
	}
	
	/**
	 * Set the list values that comes from paraemter
	 * @param values the <code>String[]</code> that should contains all pre-selected values
	 */
	public void setValues(String[] values) {
		if (values != null) {
			clearUI();
			List<String> valueList = new ArrayList<String>();
			for (String v : values) {
				valueList.add(v);
			}
			while (valueList.contains("")) {
				valueList.remove("");
			}
			this.values.addAll(valueList);
			renderList();
			inputPanel.clear();
			// trigger add for all new values
			if (listener != null) {
				listener.valuesChanged(getValues());
			}
		}
	}
	
	private void addValue(String newValue) {
		if (validate(newValue)) {
			if (!values.contains(newValue)) {
				// adds the new value to data provider
				values.add(newValue);
				// update list
				renderList();
				// clear the input panel to be ready for another value
				inputPanel.clear();
				// trigger listener
				if (listener != null) {
					listener.valuesChanged(getValues());
				}
			}
		} else {
			new Toast(MessageLevel.WARNING, "The value '" + newValue + "' must match the regular expression '" + regExValidator + "'.", "Illegal value").show();
		}
	}
	
	private boolean validate(String value) {
		boolean toReturn;
		if (regExValidator != null && !regExValidator.trim().isEmpty()) {
			try {
				toReturn = value.matches(regExValidator);
			} catch (Exception e) {
				LogClient.getInstance().warning(e.getMessage(), e);
				toReturn = false;
				new Toast(MessageLevel.ERROR, "Unable to parse regular expression '" + regExValidator + "'. Check the Resource Descriptor.", "Validator error").show();
			}
		} else {
			toReturn = true;
		}
		return toReturn;
	}
	
	private void removeValue(String value) {
		// remove the value from data provider
		values.remove(value);
		// update list
		renderList();
		// disable remove button
		removePanel.setEnabled(false);
		// trigger listener
		if (listener != null) {
			listener.valuesChanged(getValues());
		}
	}
	
	/**
	 * Drop all list values and bring the editor back to original state
	 */
	protected void clearUI() {
		values.clear();
		renderList();
		inputPanel.clear();
		removePanel.setEnabled(false);
	}

	private void renderList() {
		// sets again the data provider to cellList
		cellList.setRowCount(values.size(), true);
		cellList.setRowData(0, values);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
    @Override
    public void onClick(ClickEvent event) {
    	// remove button has been clicked
		String selected = selectionModel.getSelectedObject();
		removeValue(selected);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(String object) {
    	// add button has been clicked
    	addValue(object);
    }

    /**
     * @return the registered listener
     */
	public ListEditorListener<String[]> getListener() {
		return listener;
	}

	/**
	 * Set a {@link ListEditorListener}
	 * @param listener the listener
	 */
	public void setListener(ListEditorListener<String[]> listener) {
		this.listener = listener;
	}

	@Override
	public void onResize(int availableWidth, int availableHeight) {
		// list's scrollpanel needs to be resized
		int newAvailableHeight = availableHeight - 6*listContainer.getSpacing() - label.getOffsetHeight();
		scrollPanel.setHeight(Sizes.toString(newAvailableHeight));
	}

}
