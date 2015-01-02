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
package org.pepstock.jem.gwt.client.services;

import com.google.gwt.core.client.GWT;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public final class Services {
	/**
	 * 
	 */
	public static final String QUEUES = "jobsManager";
	/**
	 * 
	 */
	public static final String NODES = "nodesManager";
	/**
	 * 
	 */
	public static final String SWARM_NODES = "swarmManager";
	/**
	 * 
	 */
	public static final String LOGIN = "loginManager";
	/**
	 * 
	 */
	public static final String ROLES = "rolesManager";
	/**
	 * 
	 */
	public static final String ROUTING_CONFIG = "routingConfigManager";
	/**
	 * 
	 */
	public static final String COMMON_RESOURCES = "commonResourcesManager";
	/**
	 * 
	 */
	public static final String RESOURCE_DEFINITIONS = "resourceDefinitionsManager";
	/**
	 * 
	 */
	public static final String STATS = "statsManager";
	/**
	 * 
	 */
	public static final String INFO = "infoService";
	/**
	 * 
	 */
	public static final String GFS = "gfsManager";
	/**
	 * 
	 */
	public static final String CERTIFICATES = "certificatesManager";
	/**
	 * 
	 */
	public static final JobsManagerServiceAsync QUEUES_MANAGER = GWT.create(JobsManagerService.class);

	/**
	 * 
	 */
	public static final NodesManagerServiceAsync NODES_MANAGER = GWT.create(NodesManagerService.class);

	/**
	 * 
	 */
	public static final SwarmManagerServiceAsync SWARM_NODES_MANAGER = GWT.create(SwarmManagerService.class);

	/**
	 * 
	 */
	public static final LoginManagerServiceAsync LOGIN_MANAGER = GWT.create(LoginManagerService.class);

	/**
	 * 
	 */
	public static final RolesManagerServiceAsync ROLES_MANAGER = GWT.create(RolesManagerService.class);

	/**
	 * 
	 */
	public static final CommonResourcesManagerServiceAsync COMMON_RESOURCES_MANAGER = GWT.create(CommonResourcesManagerService.class);

	/**
	 * 
	 */
	public static final StatisticsManagerServiceAsync STATS_MANAGER = GWT.create(StatisticsManagerService.class);

	/**
	 * 
	 */
	public static final InfoServiceAsync INFO_SERVICE = GWT.create(InfoService.class);
	/**
	 * 
	 */
	public static final GfsManagerServiceAsync GFS_MANAGER = GWT.create(GfsManagerService.class);
	/**
	 * 
	 */
	public static final RoutingConfigManagerServiceAsync ROUTING_CONFIG_MANAGER = GWT.create(RoutingConfigManagerService.class);
	/**
	 * 
	 */
	public static final CertificatesManagerServiceAsync CERTIFICATES_MANAGER = GWT.create(CertificatesManagerService.class);

	/**
	 * 
	 */
	public static final ResourceDefinitionsManagerServiceAsync RESOURCE_DEFINITIONS_MANAGER = GWT.create(ResourceDefinitionsManagerService.class);
	
	/**
	 * To avoid any instantiation
	 */
	private Services() {
	}


}