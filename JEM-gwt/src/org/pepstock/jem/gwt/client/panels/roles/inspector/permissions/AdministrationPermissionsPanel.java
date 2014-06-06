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
package org.pepstock.jem.gwt.client.panels.roles.inspector.permissions;

import java.util.Iterator;

import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.CheckBoxPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.PermissionItem;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * panel to manage all JOBS domain permissions. It-s managing all check boxes and add and remove of permissions
 * into role object
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class AdministrationPermissionsPanel extends CheckBoxPermissionsPanel {
	
	// creates all check boxes
	private PermissionItem adminAll = new PermissionItem("All", "allows to use all actions implemented in administration view", Permissions.ADMINISTRATION_STAR);
	
	private PermissionItem adminClusterWorkload = new PermissionItem("Cluster workload", "allows to see workload chart (jobs submitted and checked)", Permissions.ADMINISTRATION_CLUSTER_WORKLOAD);
	private PermissionItem adminClusterGrs = new PermissionItem("Cluster GRS", "allows to see all active contentions in the cluster", Permissions.ADMINISTRATION_CLUSTER_GRS);
	private PermissionItem adminClusterRedo = new PermissionItem("Cluster Redo", "allows to see all REDO statements, if there are", Permissions.ADMINISTRATION_CLUSTER_REDO);
	private PermissionItem adminClusterGfs = new PermissionItem("Cluster GFS usage", "allows to see the global file system usage", Permissions.ADMINISTRATION_CLUSTER_GFS_USAGE);
	private PermissionItem adminClusterConfig = new PermissionItem("Cluster configuration", "allows to manage environment configurations (both JEM and Hazelcast)", Permissions.ADMINISTRATION_CLUSTER_CONFIGURATION);
	
	private PermissionItem adminNodesConfig = new PermissionItem("Nodes configuration", "allows to manage nodes configurations (both JEM and affinity loader)", Permissions.ADMINISTRATION_NODES_CONFIGURATION);
	private PermissionItem adminNodesCommand = new PermissionItem("Nodes commands", "allows to perform commands on nodes", Permissions.ADMINISTRATION_NODES_COMMANDS);
	private PermissionItem adminNodesSystem = new PermissionItem("Nodes System statistics", "allows to see the resources consumption of nodes", Permissions.ADMINISTRATION_NODES_SYSTEM);
	private PermissionItem adminNodesQueue = new PermissionItem("Nodes Queues statistics", "allows to see the queues data distributionon nodes", Permissions.ADMINISTRATION_NODES_QUEUES);

	private PermissionItem adminQueuesCurrent = new PermissionItem("Queues current usage", "allows to see current utilization of queues", Permissions.ADMINISTRATION_QUEUES_CURRENT);
	private PermissionItem adminQueuesStats = new PermissionItem("Queues statistics", "allows to see last samples about utilization of queues", Permissions.ADMINISTRATION_QUEUES_STATISTICS);
	private PermissionItem adminQueuesInternals = new PermissionItem("Internal Maps statistics", "allows to see last samples about utilization of internal maps", Permissions.ADMINISTRATION_QUEUES_INTERNAL_MAPS);

	private PermissionItem adminSecuritySecret = new PermissionItem("Security Secret utility", "allows to have SECRET utility to hash and crypt properties of common resources", Permissions.ADMINISTRATION_SECURITY_SECRET);
	private PermissionItem adminSecurityCertificate = new PermissionItem("Security Certificate manager", "allows to manage all certificate of user, necessary with socket interceptor", Permissions.ADMINISTRATION_SECURITY_CERTIFICATE);	

	/**
	 * Counstructs UI panel, using role argument to set check boxes
	 * 
	 * @param role role instance to update
	 * 
	 */
	public AdministrationPermissionsPanel(Role role) {
		super(role);

		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			// if is a jobs permission
			if (permission.startsWith(Permissions.ADMINISTRATION) || permission.startsWith(Permissions.STAR)){
				// is if set for all put unable all other checkbox
				if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_STAR) || permission.startsWith(Permissions.STAR)){
					// sets ALL
					adminAll.setValue(true);
					for (int i=0; i<Permissions.ADMINISTRATION_ALL.length; i++){
						// gest checkbox to set false 
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.ADMINISTRATION_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
				} else {
					// gets check box and sets it true
					PermissionItem checkBox = getPermissionItemByPermission(permission);
					if (checkBox != null){
						checkBox.setValue(true);
					}
				}
			}
		}

		// sets actions for ALL check box 
		adminAll.addClickHandler(new AdminAllClickHandler());

		loadCheckBoxAction(adminClusterConfig);
		loadCheckBoxAction(adminClusterWorkload);
		loadCheckBoxAction(adminClusterGrs);
		loadCheckBoxAction(adminClusterRedo);
		loadCheckBoxAction(adminClusterGfs);
		loadCheckBoxAction(adminNodesConfig);
		loadCheckBoxAction(adminNodesCommand);
		loadCheckBoxAction(adminNodesSystem);
		loadCheckBoxAction(adminNodesQueue);
		loadCheckBoxAction(adminQueuesCurrent);
		loadCheckBoxAction(adminQueuesStats);
		loadCheckBoxAction(adminQueuesInternals);
		loadCheckBoxAction(adminSecuritySecret);
		loadCheckBoxAction(adminSecurityCertificate);

		setItems(adminAll,
				adminClusterConfig,
				adminClusterWorkload,
				adminClusterGrs,
				adminClusterRedo,
				adminClusterGfs,
				adminNodesConfig,
				adminNodesCommand,
				adminNodesSystem,
				adminNodesQueue,
				adminQueuesCurrent,
				adminQueuesStats,
				adminQueuesInternals,
				adminSecuritySecret,
				adminSecurityCertificate);
	}
	
	@Override
	public final void loadCheckBoxAction(final PermissionItem item){
		super.loadCheckBoxAction(item);
		item.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addPermission(item, Permissions.ADMINISTRATION_CLUSTER, Permissions.ADMINISTRATION_CLUSTER_FOLDER);
				addPermission(item, Permissions.ADMINISTRATION_NODES, Permissions.ADMINISTRATION_NODES_FOLDER);
				addPermission(item, Permissions.ADMINISTRATION_QUEUES, Permissions.ADMINISTRATION_QUEUES_FOLDER);
				addPermission(item, Permissions.ADMINISTRATION_SECURITY, Permissions.ADMINISTRATION_SECURITY_FOLDER);
			}
		});
	}
	
	private class AdminAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// checks if all is checked
			if (adminAll.getValue()){
				// if list doesn't contain the permission all
				if (!getRole().getPermissions().contains(Permissions.ADMINISTRATION_STAR)) {
					// scans all permissions
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						// removes ALL permissions of JOBS
						if (permission.startsWith(Permissions.ADMINISTRATION)){
							iter.remove();
						}
					}
					// scans all check boxes and sets FALSE
					for (int i=0; i<Permissions.ADMINISTRATION_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.ADMINISTRATION_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					// adds the ALL permission
					getRole().getPermissions().add(Permissions.ADMINISTRATION_STAR);
				}
			} else {
				// remove ALL permission and set enable all other check boxes
				getRole().getPermissions().remove(Permissions.ADMINISTRATION_STAR);
				for (int i=0; i<Permissions.ADMINISTRATION_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.ADMINISTRATION_ALL[i]);
					if (checkBox != null){
						checkBox.setEnabled(true);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param checkBox
	 * @param permission
	 * @param mainPermission
	 */
	private void addPermission(PermissionItem item, String mainPermission, String folder){
		if (item.getPermission().startsWith(mainPermission)){
			if (item.getValue()){
				if (!getRole().getPermissions().contains(folder)){
					getRole().getPermissions().add(folder);
				}
			} else {
				if (getRole().getPermissions().contains(folder)){
					getRole().getPermissions().remove(folder);
					for (String perm : getRole().getPermissions()){
						if (perm.startsWith(mainPermission)){
							getRole().getPermissions().add(folder);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * look for the right check box starting from permission name
	 * 
	 * @param permission permssion to check
	 * @return check box 
	 */
	private PermissionItem getPermissionItemByPermission(String permission){
		if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_CLUSTER_WORKLOAD)){
			return adminClusterWorkload;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_CLUSTER_GRS)){
			return adminClusterGrs;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_CLUSTER_REDO)){
			return adminClusterRedo;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_SECURITY_SECRET)){
			return adminSecuritySecret;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_SECURITY_CERTIFICATE)){
			return adminSecurityCertificate;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_CLUSTER_GFS_USAGE)){
			return adminClusterGfs;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_CLUSTER_CONFIGURATION)){
			return adminClusterConfig;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_NODES_CONFIGURATION)){
			return adminNodesConfig;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_NODES_COMMANDS)){
			return adminNodesCommand;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_NODES_SYSTEM)){
			return adminNodesSystem;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_NODES_QUEUES)){
			return adminNodesQueue;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_QUEUES_CURRENT)){
			return adminQueuesCurrent;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_QUEUES_STATISTICS)){
			return adminQueuesStats;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_QUEUES_INTERNAL_MAPS)){
			return adminQueuesInternals;
		} else if (permission.equalsIgnoreCase(Permissions.ADMINISTRATION_STAR)){
			return adminAll;
		} 
		return null;
	}

}