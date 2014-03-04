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
package org.pepstock.jem.gwt.client.panels.administration;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component which shows output produced by job. Uses highlighter in PLAIn to show data.<br>
 * Every file is requested by RPC only when the user asks for. 
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
	
    public static final String WORKLOAD_JOBS_OPTION = "workload-jobs";

    public static final String CURRENT_QUEUES_STATUS_OPTION = "current-queues-status";
   
	public static final String QUEUES_STATUS_OPTION = "queues-status";

	public static final String INTERNAL_MAPS_OPTION = "internal_maps";
	
	public static final String SECRET_UTILITY_OPTION = "secret-utility";

	public static final String NODES_SYSTEM_STATUS_OPTION = "nodes-system-status";
	
	public static final String NODES_QUEUES_STATUS_OPTION = "nodes-queues-status";
	
	public static final String GRS_CONTENTIONS_OPTION = "grs-contentions";
	
	public static final String NODES_CONFIG_OPTION = "nodes-configuration";
	
	public static final String NODES_COMMANDS_OPTION = "nodes-commands";
	
	public static final String REDO_PANEL_OPTION = "redo-panel";
	
	public static final String GFS_PANEL_OPTION = "gfs-panel";
	
	public static final String MEMORY_PANEL_OPTION = "memory-panel";
	
	public static final String CERTIFICATES_PANEL_OPTION = "certificate-panel";
	
	public static final String CLUSTER_CONFIG_OPTION = "cluster-configuration";
	
	private InspectListener<String> listener = null;
	
	private CellPanel selected = null;
	
	public TreeOptions() {
		setWidth(Sizes.HUNDRED_PERCENT);
		setHeight(Sizes.HUNDRED_PERCENT);

		/*-------------------------+
		 | create options          |
		 +-------------------------*/			
		
		// checks id user is authorized to see input tab	
		CellPanel cconfig = createItem("Configuration", CLUSTER_CONFIG_OPTION, null, Permissions.ADMINISTRATION_CLUSTER_CONFIGURATION);
		CellPanel nconfig = createItem("Configuration", NODES_CONFIG_OPTION, null, Permissions.ADMINISTRATION_NODES_CONFIGURATION);	
		CellPanel ncmd = createItem("Commands", NODES_COMMANDS_OPTION, null, Permissions.ADMINISTRATION_NODES_COMMANDS);
		CellPanel grs = createItem("Resources contention", GRS_CONTENTIONS_OPTION, null, Permissions.ADMINISTRATION_CLUSTER_GRS);
		CellPanel secret = createItem("Secret utility", SECRET_UTILITY_OPTION, null, Permissions.ADMINISTRATION_SECURITY_SECRET);
		CellPanel cqueues = createItem("Current Status", CURRENT_QUEUES_STATUS_OPTION, null, Permissions.ADMINISTRATION_QUEUES_CURRENT);
		CellPanel internals = createItem("Current Internals", INTERNAL_MAPS_OPTION, null, Permissions.ADMINISTRATION_QUEUES_INTERNAL_MAPS);
		CellPanel jobs = createItem("Jobs Workload", WORKLOAD_JOBS_OPTION, null, Permissions.ADMINISTRATION_CLUSTER_WORKLOAD);
		CellPanel queues = createItem("Statistics", QUEUES_STATUS_OPTION, null, Permissions.ADMINISTRATION_QUEUES_STATISTICS);
		CellPanel nsystem = createItem("System status", NODES_SYSTEM_STATUS_OPTION, null, Permissions.ADMINISTRATION_NODES_SYSTEM);
		CellPanel nqueues = createItem("Queues status", NODES_QUEUES_STATUS_OPTION, null, Permissions.ADMINISTRATION_NODES_QUEUES);
		CellPanel redo = createItem("Redo statements", REDO_PANEL_OPTION, null, Permissions.ADMINISTRATION_CLUSTER_REDO);
		CellPanel gfs = createItem("GFS usage", GFS_PANEL_OPTION, null, Permissions.ADMINISTRATION_CLUSTER_GFS_USAGE);
		CellPanel memory = createItem("Memory usage", MEMORY_PANEL_OPTION, null, Permissions.ADMINISTRATION_CLUSTER_MEMORY_USAGE);
		CellPanel certificate = createItem("Certificates manager", CERTIFICATES_PANEL_OPTION, null, Permissions.ADMINISTRATION_SECURITY_CERTIFICATE);

		List<CellPanel> panels = new LinkedList<CellPanel>();
		List<DisclosurePanel> dPanels = new LinkedList<DisclosurePanel>();

		if (ClientPermissions.isAuthorized(Permissions.ADMINISTRATION, Permissions.ADMINISTRATION_CLUSTER_FOLDER)) {
			if (cconfig != null) {
				panels.add(cconfig);
			}
			if (jobs != null) {
				panels.add(jobs);
			}
			if (gfs != null) {
				panels.add(gfs);
			}
			if (memory != null) {
				panels.add(memory);
			}
			if (grs != null) {
				panels.add(grs);
			}
			if (redo != null) {
				panels.add(redo);
			}
			if (!panels.isEmpty()) {
				dPanels.add(createContainer("Cluster", Images.INSTANCE.planet24(), panels.toArray(new CellPanel[0])));
			}
		}
		panels.clear();
		if (ClientPermissions.isAuthorized(Permissions.ADMINISTRATION, Permissions.ADMINISTRATION_NODES_FOLDER)){
			if (nconfig !=null) {
				panels.add(nconfig);
			}
			if (ncmd !=null) {
				panels.add(ncmd);
			}
			if (nsystem !=null) {
				panels.add(nsystem);
			}
			if (nqueues !=null) {
				panels.add(nqueues);
			}
			if (!panels.isEmpty()){
				dPanels.add(createContainer("Nodes", Images.INSTANCE.networkPc24(), panels.toArray(new CellPanel[0])));
			}
		}
		panels.clear();
		if (ClientPermissions.isAuthorized(Permissions.ADMINISTRATION, Permissions.ADMINISTRATION_QUEUES_FOLDER)){
			if (cqueues !=null) {
				panels.add(cqueues);
			}
			if (queues !=null) {
				panels.add(queues);
			}
			if (internals !=null) {
				panels.add(internals);
			}
			if (!panels.isEmpty()){
				dPanels.add(createContainer("Queues", Images.INSTANCE.list24(), panels.toArray(new CellPanel[0])));
			}
		}
		panels.clear();
		if (ClientPermissions.isAuthorized(Permissions.ADMINISTRATION, Permissions.ADMINISTRATION_SECURITY_FOLDER)){
			if (certificate !=null) {
				panels.add(certificate);
			}
			if (secret !=null) {
				panels.add(secret);
			}
			if (!panels.isEmpty()){
				dPanels.add(createContainer("Security", Images.INSTANCE.key24(), panels.toArray(new CellPanel[0])));
			}
		}
		
		Grid grid = new Grid(dPanels.size(), 1);
		int index = 0;
		for (DisclosurePanel panel : dPanels){
			grid.setWidget(index, 0, panel);
			index++;
		}
		add(grid);
	}
	
	
	
	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<String> listener) {
		this.listener = listener;
	}
	
	private void selectPanel(CellPanel select){
		if (select.equals(selected)) {
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
	private final CellPanel createItem(String description, final String option, ImageResource icon, String permission){
		if (!ClientPermissions.isAuthorized(Permissions.ADMINISTRATION, permission)) {
			return null;
		}
	
		/*-------------------------+
		 | Node inspect            |
		 +-------------------------*/	
	
		// this is the inside panel
		final HorizontalPanel options = new HorizontalPanel();
		options.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		options.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
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
		
		// add the icon if present
		if (icon != null) {
			options.add(new Image(icon));
		}
		
		options.add(anchor);
		options.setCellWidth(anchor, Sizes.HUNDRED_PERCENT);
		return options;
	}
	
	private final DisclosurePanel createContainer(String label, ImageResource icon, CellPanel... panels){
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setSpacing(8);
		if (icon != null) {
			header.add(new Image(icon));
		}
		header.add(new Label(label));
		
		DisclosurePanel disclosure = new DisclosurePanel();
		disclosure.setHeader(header);
		disclosure.setAnimationEnabled(true);
		
		VerticalPanel panel = new VerticalPanel();
		for (int i=0; i<panels.length; i++){
			panel.add(panels[i]);
		}
		disclosure.setContent(panel);
		return disclosure;
	}
	
	
}