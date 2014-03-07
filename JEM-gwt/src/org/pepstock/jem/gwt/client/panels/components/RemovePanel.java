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
package org.pepstock.jem.gwt.client.panels.components;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.InputPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Component to manage the  search permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RemovePanel extends VerticalPanel {
	
	/**
	 * 
	 */
	public static final int WIDTH = 100;

	private final Button remove = new Button("Remove");
	
	private ClickHandler clickHandler = null;
	
	/**
	 * Constructs all UI using role instance information
	 * 
	 * @param role
	 * 
	 */
	public RemovePanel() {
		setWidth(Sizes.toString(WIDTH));
		setSpacing(5);
		// sets disable the buttons because
		// they will be available when a permission will select
		// or inserted in text box
		remove.setEnabled(false);
		remove.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (clickHandler != null){
					clickHandler.onClick(event);
				}
			}
		});

		// sets a blank so the button is aligned the celllist
		Label label = new Label();
		label.addStyleName(Styles.INSTANCE.common().bold());
		label.setHeight(Sizes.toString(InputPanel.LABEL_HEIGHT));
		add(label);
		add(remove);
	}

	/**
	 * @return the clickHandler
	 */
	public ClickHandler getClickHandler() {
		return clickHandler;
	}

	/**
	 * @param clickHandler the clickHandler to set
	 */
	public void setClickHandler(ClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}
	
	
	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled){
		remove.setEnabled(enabled);
	}
	
}