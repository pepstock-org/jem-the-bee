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
package org.pepstock.jem.gwt.client.panels.roles.inspector;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
@SuppressWarnings("javadoc")
public class TreeOptions extends ScrollPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
    public static final String VIEWS = "views";

    public static final String JOBS = "jobs";
   
	public static final String NODES = "nodes";
	
	public static final String SWARM = "swarm";
	
	public static final String ROLES = "roles";

	public static final String RESOURCES = "resources";
	
	public static final String GFS = "gfs";
	
	public static final String CERTIFICATES = "certificates";
	
	public static final String ADMINISTRATION = "admin";
	
	private InspectListener<String> listener = null;
	
	private VerticalPanel selected = null;
	
	public TreeOptions() {
		setWidth(Sizes.HUNDRED_PERCENT);
		setHeight(Sizes.HUNDRED_PERCENT);

		/*-------------------------+
		 | create options          |
		 +-------------------------*/			

		VerticalPanel views = createItem("Views", VIEWS);
		VerticalPanel jobs = createItem("Jobs", JOBS);
		VerticalPanel nodes = createItem("Nodes", NODES);
		VerticalPanel roles = createItem("Roles", ROLES);
		VerticalPanel swarm = createItem("Swarm", SWARM);
		VerticalPanel resources = createItem("Resources", RESOURCES);
		VerticalPanel gfs = createItem("Global file system", GFS);
		VerticalPanel certificates = createItem("Certificates", CERTIFICATES);
		VerticalPanel admin = createItem("Administration", ADMINISTRATION);

		Grid grid = new Grid(9, 1);
		grid.setWidget(0, 0, views);
		grid.setWidget(1, 0, jobs);
		grid.setWidget(2, 0, nodes);
		grid.setWidget(3, 0, roles);
		grid.setWidget(4, 0, certificates);
		grid.setWidget(5, 0, swarm);		
		grid.setWidget(6, 0, resources);
		grid.setWidget(7, 0, gfs);
		grid.setWidget(8, 0, admin);
		
		add(grid);
		
		selectPanel(views);
	}
	
	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<String> listener) {
		this.listener = listener;
	}

	
	private void selectPanel(VerticalPanel select){
		if (select.equals(selected)){
			return;
		}
		if (selected != null){
			selected.setStyleName(Styles.INSTANCE.common().adminUnselectedTreeItem());
		}
		select.setStyleName(Styles.INSTANCE.common().adminSelectedTreeItem());
		selected = select;

	}

	/**
	 * 
	 * @param description
	 * @param option
	 * @return
	 */
	private final VerticalPanel createItem(String description, final String option){
		/*-------------------------+
		 | Node inspect            |
		 +-------------------------*/	
	
		// this is the inside panel
		final VerticalPanel options = new VerticalPanel();
		options.setSpacing(8);
		options.setWidth(Sizes.HUNDRED_PERCENT);
		
		final Anchor anchor = new Anchor(description);
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (listener != null){
					listener.inspect(option);
					selectPanel(options);
				}
			}
		});
		options.add(anchor);
		return options;
	}
}