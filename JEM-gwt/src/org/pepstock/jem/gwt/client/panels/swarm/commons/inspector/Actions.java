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
package org.pepstock.jem.gwt.client.panels.swarm.commons.inspector;

import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.swarm.NodeActionListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * ActionsButtonPanel that user can do after a role updating o a new role adding. At the moment we have 2 actions:<br>
 * <br>
 * <ul>
 * <li><b>Save<b>: to consolidate the updates</li>
 * <li><b>Cancel<b>: to discard the updates</li>
 * </ul>
 * 
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Actions extends VerticalPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private NodeActionListener listener = null;
	
	/**
	 * 
	 */
	public Actions() {
		this(false);
	}

	/**
	 * Creates the UI using the Role argument
	 * @param onlyCancel 
	 * 
	 */
	public Actions(boolean onlyCancel) {

		HorizontalPanel subsomponent = new HorizontalPanel();
		subsomponent.setSpacing(4);

		if (!onlyCancel){
			Button saveButton = new Button("Save", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// do!
					getListener().save();
				}
			});
			saveButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
			subsomponent.add(saveButton);
		}
		
		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				getListener().cancel();
			}
		});
		if (onlyCancel){
			cancelButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		}
		subsomponent.add(cancelButton);
	
		setSpacing(6);
		add(subsomponent);
		setCellHorizontalAlignment(subsomponent, HasHorizontalAlignment.ALIGN_LEFT);
	}

	/**
	 * @return the inspector
	 */
	public NodeActionListener getListener() {
		return listener;
	}

	/**
	 * @param inspector the inspector to set
	 */
	public void setListener(NodeActionListener inspector) {
		this.listener = inspector;
	}

	
	
}