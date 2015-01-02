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
package org.pepstock.jem.node.security;

/**
 * Container of all permissions used inside of web app to check and activate the
 * several features of appliction.
 * 
 * Create, read, update and delete (CRUD)
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Permissions {
	/**
	 * Separator inside of permission. The format is :
	 * [domain]:[permission]:[object]
	 */
	public static final String PERMISSION_SEPARATOR = ":";

	/**
	 * Domain for panels to show in main panel.
	 */
	public static final String STAR = "*";

	/**
	 * Domain for panels to show in main panel.
	 */
	public static final String ALL_BY_REGEX = ".*";

	/*-------------------------------
	 ! F I L E                      ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check accessing on files USED on
	 * RUNTIME
	 */
	/**
	 * Domain for files access
	 */
	public static final String FILES = "files";

	/**
	 * Read action for file
	 */
	public static final String READ = "read";

	/**
	 * Write action for file
	 */
	public static final String WRITE = "write";

	/**
	 * Execute action for file
	 */
	public static final String EXECUTE = "execute";

	/**
	 * Permission tag for files ALL files and all actions
	 */
	public static final String FILES_STAR = FILES + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag to read files actions
	 */
	public static final String FILES_READ = FILES + PERMISSION_SEPARATOR + READ + PERMISSION_SEPARATOR;

	/**
	 * Permission tag to write files actions
	 */
	public static final String FILES_WRITE = FILES + PERMISSION_SEPARATOR + WRITE + PERMISSION_SEPARATOR;

	/**
	 * Permission tag to write files actions
	 */
	public static final String FILES_EXECUTE = FILES + PERMISSION_SEPARATOR + EXECUTE + PERMISSION_SEPARATOR;

	/**
	 * Permission tag for ALL read actions on all files
	 */
	public static final String FILES_READ_ALL = FILES_READ + STAR;

	/**
	 * Permission tag for ALL write actions on all files
	 */
	public static final String FILES_WRITE_ALL = FILES_WRITE + STAR;

	/**
	 * Permission tag for ALL write actions on all files
	 */
	public static final String FILES_EXECUTE_ALL = FILES_EXECUTE + STAR;

	/*-------------------------------
	 ! D A T A S O U R C E          ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check accessing on datasources USED
	 * on RUNTIME
	 */

	/**
	 * Domain for datasources access
	 */
	public static final String DATASOURCES = "datasources";

	/**
	 * Permission tag for ALL datasources access
	 */
	public static final String DATASOURCES_ALL = DATASOURCES + PERMISSION_SEPARATOR + STAR;

	/*-------------------------------
	 ! S U R R O G A T E            ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check if you can change submit user
	 * USED on RUNTIME
	 */

	/**
	 * Domain for surrogate access
	 */
	public static final String SURROGATE = "surrogate";

	/**
	 * Permission tag for ALL surrogate access
	 */
	public static final String SURROGATE_ALL = SURROGATE + PERMISSION_SEPARATOR + STAR;

	/*-------------------------------
	 ! S T O R M  N O D E S         ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check if you can act on swarm odes
	 * by WEB interface
	 */

	/**
	 * Domain for action related to swarm nodes instances
	 */
	public static final String SWARM = "swarm-nodes";

	/**
	 * Permission tag for all swarm node actions
	 */
	public static final String SWARM_NODES_STAR = SWARM + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for swarm node start action
	 */
	public static final String SWARM_NODES_START = SWARM + PERMISSION_SEPARATOR + "start";

	/**
	 * Permission tag for swarm node start action
	 */
	public static final String SWARM_NODES_DRAIN = SWARM + PERMISSION_SEPARATOR + "drain";

	/**
	 * Permission tag for view swarm nodes configuration
	 */
	public static final String SWARM_NODES_VIEW_CONFIG = SWARM + PERMISSION_SEPARATOR + "viewconfig";

	/**
	 * Permission tag for edit swarm nodes configuration
	 */
	public static final String SWARM_NODES_EDIT_CONFIG = SWARM + PERMISSION_SEPARATOR + "editconfig";

	/**
	 * Permission tag arrays with all permissions for swarm NODES domain
	 */
	public static final String[] SWARM_NODES_ALL = { SWARM_NODES_START, SWARM_NODES_DRAIN, SWARM_NODES_VIEW_CONFIG, SWARM_NODES_EDIT_CONFIG };

	/*-------------------------------
	 ! N O D E S                    ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check if you can act on nodes by WEB
	 * interface
	 */

	/**
	 * Domain for action related to nodes instances
	 */
	public static final String NODES = "nodes";

	/**
	 * Permission tag for all node actions
	 */
	public static final String NODES_STAR = NODES + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for node start action
	 */
	public static final String NODES_START = NODES + PERMISSION_SEPARATOR + "start";

	/**
	 * Permission tag for node drain action
	 */
	public static final String NODES_DRAIN = NODES + PERMISSION_SEPARATOR + "drain";

	/**
	 * Permission tag for node update action
	 */
	public static final String NODES_UPDATE = NODES + PERMISSION_SEPARATOR + "update";

	/**
	 * Permission tag arrays with all permissions for NODES domain
	 */
	public static final String[] NODES_ALL = { NODES_START, NODES_DRAIN, NODES_UPDATE };

	/*-------------------------------
	 !  PERMISSIONS DOMAIN          ! 
	 -------------------------------*/
	/*
	 * 
	 */

	/**
	 * Domain for panels to show in main panel.
	 */
	public static final String VIEW = "view";

	/**
	 * Domain for action related to jobs instances
	 */
	public static final String JOBS = "jobs";

	/**
	 * Domain for action related to roles instances
	 */
	public static final String SEARCH = "search";

	/**
	 * Domain for resource management
	 */
	public static final String RESOURCES = "resources";

	/**
	 * Domain for action related to roles instances
	 */
	public static final String ROLES = "roles";

	/**
	 * Domain for action related to administration web console
	 */
	public static final String ADMINISTRATION = "administration";

	/**
	 * Domain for action related to global file system web console
	 */
	public static final String GFS = "gfs";

	/*-------------------------------
	 ! V I E W                      ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check what you can see in terms of
	 * views (tab panel)
	 */

	/**
	 * Permission tag to show job all panel
	 */
	public static final String VIEW_STAR = VIEW + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag to show job input panel
	 */
	public static final String VIEW_INPUT = VIEW + PERMISSION_SEPARATOR + "input";

	/**
	 * Permission tag to show job running panel
	 */
	public static final String VIEW_RUNNING = VIEW + PERMISSION_SEPARATOR + "running";

	/**
	 * Permission tag to show job output panel
	 */
	public static final String VIEW_OUTPUT = VIEW + PERMISSION_SEPARATOR + "output";

	/**
	 * Permission tag to show job routing panel
	 */
	public static final String VIEW_ROUTING = VIEW + PERMISSION_SEPARATOR + "routing";

	/**
	 * Permission tag to show nodes panel
	 */
	public static final String VIEW_NODES = VIEW + PERMISSION_SEPARATOR + "nodes";

	/**
	 * Permission tag to show nodes panel
	 */
	public static final String VIEW_STATUS = VIEW + PERMISSION_SEPARATOR + "status";

	/**
	 * Permission tag to show admin panel
	 */
	public static final String VIEW_ADMIN = VIEW + PERMISSION_SEPARATOR + "admin";

	/**
	 * Permission tag to show roles panel
	 */
	public static final String VIEW_ROLES = VIEW + PERMISSION_SEPARATOR + "roles";

	/**
	 * Permission tag to show resources panel
	 */
	public static final String VIEW_RESOURCES = VIEW + PERMISSION_SEPARATOR + "resources";

	/**
	 * Permission tag to show resources panel
	 */
	public static final String VIEW_GFS_EXPLORER = VIEW + PERMISSION_SEPARATOR + "gfsexplorer";

	/**
	 * Permission tag to show nodes panel
	 */
	public static final String VIEW_SWARM_NODES = VIEW + PERMISSION_SEPARATOR + "swarm-nodes";

	/**
	 * Permission tag arrays with all permissions for VIEW domain
	 */
	public static final String[] VIEW_ALL = { VIEW_INPUT, VIEW_RUNNING, VIEW_OUTPUT, VIEW_ROUTING, VIEW_NODES, VIEW_ADMIN, VIEW_ROLES, VIEW_RESOURCES, VIEW_STATUS, VIEW_GFS_EXPLORER, VIEW_SWARM_NODES };

	/*-------------------------------
	 ! J O B S                      ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check what you can see in terms of
	 * views (tab panel)
	 */
	/**
	 * Permission tag for all job action
	 */
	public static final String JOBS_STAR = JOBS + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for job purge action
	 */
	public static final String JOBS_PURGE = JOBS + PERMISSION_SEPARATOR + "purge";

	/**
	 * Permission tag for job hold action
	 */
	public static final String JOBS_HOLD = JOBS + PERMISSION_SEPARATOR + "hold";

	/**
	 * Permission tag for job release action
	 */
	public static final String JOBS_RELEASE = JOBS + PERMISSION_SEPARATOR + "release";

	/**
	 * Permission tag for job cancel action
	 */
	public static final String JOBS_CANCEL = JOBS + PERMISSION_SEPARATOR + "cancel";

	/**
	 * Permission tag for job cancel action
	 */
	public static final String JOBS_KILL = JOBS + PERMISSION_SEPARATOR + "kill";

	/**
	 * Permission tag for job submit action
	 */
	public static final String JOBS_SUBMIT = JOBS + PERMISSION_SEPARATOR + "submit";

	/**
	 * Permission tag for job update action
	 */
	public static final String JOBS_UPDATE = JOBS + PERMISSION_SEPARATOR + "update";

	/**
	 * Permission tag arrays with all permissions for JOBS domain
	 */
	public static final String[] JOBS_ALL = { JOBS_PURGE, JOBS_HOLD, JOBS_RELEASE, JOBS_CANCEL, JOBS_KILL, JOBS_SUBMIT, JOBS_UPDATE };

	/*-------------------------------
	 !  R E S O U R C E             ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check the WEB interface, to define
	 * common resources
	 */

	/**
	 * Permission tag for ALL resources actions
	 */
	public static final String RESOURCES_STAR = RESOURCES + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for resource set action
	 */
	public static final String RESOURCES_CREATE = RESOURCES + PERMISSION_SEPARATOR + "create";

	/**
	 * Permission tag for resource remove action
	 */
	public static final String RESOURCES_DELETE = RESOURCES + PERMISSION_SEPARATOR + "delete";

	/**
	 * Permission tag for resource set action
	 */
	public static final String RESOURCES_UPDATE = RESOURCES + PERMISSION_SEPARATOR + "update";

	/**
	 * Permission tag for resource get action
	 */
	public static final String RESOURCES_READ = RESOURCES + PERMISSION_SEPARATOR + "read";

	/**
	 * Permission tag arrays with all permissions for RESOURCES domain
	 */
	public static final String[] RESOURCES_ALL = { RESOURCES_CREATE, RESOURCES_DELETE, RESOURCES_READ, RESOURCES_UPDATE };

	/*-------------------------------
	 ! R O L E S                    ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check what you can manage on WEB app
	 */

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ROLES_STAR = ROLES + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for role create action
	 */
	public static final String ROLES_CREATE = ROLES + PERMISSION_SEPARATOR + "create";

	/**
	 * Permission tag for role remove action
	 */
	public static final String ROLES_DELETE = ROLES + PERMISSION_SEPARATOR + "delete";

	/**
	 * Permission tag for role remove action
	 */
	public static final String ROLES_UPDATE = ROLES + PERMISSION_SEPARATOR + "update";

	/**
	 * Permission tag for role remove action
	 */
	public static final String ROLES_READ = ROLES + PERMISSION_SEPARATOR + "read";

	/**
	 * Permission tag arrays with all permissions for ROLES domain
	 */
	public static final String[] ROLES_ALL = { ROLES_CREATE, ROLES_DELETE, ROLES_READ, ROLES_UPDATE };

	/*-------------------------------
	 ! C E R T I C A T E            ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to manage certificate
	 */

	/**
	 * Domain for action related to certificates instances
	 */
	public static final String CERTIFICATES = "certificates";
	/**
	 * Permission tag for ALL certificate actions
	 */
	public static final String CERTIFICATES_STAR = CERTIFICATES + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for certificate create action
	 */
	public static final String CERTIFICATES_CREATE = CERTIFICATES + PERMISSION_SEPARATOR + "create";

	/**
	 * Permission tag for certificate remove action
	 */
	public static final String CERTIFICATES_DELETE = CERTIFICATES + PERMISSION_SEPARATOR + "delete";

	/**
	 * Permission tag for certificate remove action
	 */
	public static final String CERTIFICATES_READ = CERTIFICATES + PERMISSION_SEPARATOR + "read";

	/**
	 * Permission tag arrays with all permissions for certificates domain
	 */
	public static final String[] CERTIFICATES_ALL = { CERTIFICATES_CREATE, CERTIFICATES_DELETE, CERTIFICATES_READ };

	/*-------------------------------
	 ! S E A R C H                  ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check what you can search from WEB
	 * app
	 */

	/**
	 * Permission tag for job search action
	 */
	public static final String SEARCH_JOBS = SEARCH + PERMISSION_SEPARATOR + JOBS + PERMISSION_SEPARATOR;
	/**
	 * Permission tag for job search action
	 */
	public static final String SEARCH_NODES = SEARCH + PERMISSION_SEPARATOR + NODES + PERMISSION_SEPARATOR;

	/*-------------------------------
	 ! A D M I N I S T R A T I O N  ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check what you can use in
	 * administration console
	 */

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_STAR = ADMINISTRATION + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for role create action
	 */
	public static final String ADMINISTRATION_SECURITY = ADMINISTRATION + PERMISSION_SEPARATOR + "security";

	/**
	 * 
	 */
	public static final String ADMINISTRATION_SECURITY_FOLDER = ADMINISTRATION_SECURITY + PERMISSION_SEPARATOR + "folder";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_SECURITY_SECRET = ADMINISTRATION_SECURITY + PERMISSION_SEPARATOR + "secret";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_SECURITY_CERTIFICATE = ADMINISTRATION_SECURITY + PERMISSION_SEPARATOR + "certificate";

	/**
	 * Permission tag for role create action
	 */
	public static final String ADMINISTRATION_CLUSTER = ADMINISTRATION + PERMISSION_SEPARATOR + "cluster";

	/**
	 * 
	 */
	public static final String ADMINISTRATION_CLUSTER_FOLDER = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + "folder";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_CLUSTER_STAR = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_CLUSTER_WORKLOAD = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + "workload";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_CLUSTER_GRS = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + "grs";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_CLUSTER_REDO = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + "redo";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_CLUSTER_GFS_USAGE = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + "gfs-usage";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_CLUSTER_CONFIGURATION = ADMINISTRATION_CLUSTER + PERMISSION_SEPARATOR + "configuration";

	/**
	 * Permission tag for role remove action
	 */
	public static final String ADMINISTRATION_NODES = ADMINISTRATION + PERMISSION_SEPARATOR + "nodes";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_NODES_STAR = ADMINISTRATION_NODES + PERMISSION_SEPARATOR + STAR;

	/**
	 * 
	 */
	public static final String ADMINISTRATION_NODES_FOLDER = ADMINISTRATION_NODES + PERMISSION_SEPARATOR + "folder";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_NODES_CONFIGURATION = ADMINISTRATION_NODES + PERMISSION_SEPARATOR + "configuration";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_NODES_COMMANDS = ADMINISTRATION_NODES + PERMISSION_SEPARATOR + "commands";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_NODES_SYSTEM = ADMINISTRATION_NODES + PERMISSION_SEPARATOR + "system";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_NODES_QUEUES = ADMINISTRATION_NODES + PERMISSION_SEPARATOR + "queues";

	/**
	 * Permission tag for role remove action
	 */
	public static final String ADMINISTRATION_QUEUES = ADMINISTRATION + PERMISSION_SEPARATOR + "queues";

	/**
	 * 
	 */
	public static final String ADMINISTRATION_QUEUES_FOLDER = ADMINISTRATION_QUEUES + PERMISSION_SEPARATOR + "folder";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_QUEUES_STAR = ADMINISTRATION_QUEUES + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_QUEUES_CURRENT = ADMINISTRATION_QUEUES + PERMISSION_SEPARATOR + "current";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_QUEUES_STATISTICS = ADMINISTRATION_QUEUES + PERMISSION_SEPARATOR + "statistics";

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String ADMINISTRATION_QUEUES_INTERNAL_MAPS = ADMINISTRATION_QUEUES + PERMISSION_SEPARATOR + "internalMaps";

	/**
	 * 
	 */
	public static final String[] ADMINISTRATION_ITEMS_ALL = { ADMINISTRATION_CLUSTER_FOLDER, ADMINISTRATION_NODES_FOLDER, ADMINISTRATION_QUEUES_FOLDER, ADMINISTRATION_SECURITY_FOLDER };

	/**
	 * 
	 */
	public static final String[] ADMINISTRATION_ALL = { ADMINISTRATION_CLUSTER_GRS, ADMINISTRATION_CLUSTER_REDO, ADMINISTRATION_SECURITY_SECRET, ADMINISTRATION_SECURITY_CERTIFICATE, ADMINISTRATION_CLUSTER_WORKLOAD, ADMINISTRATION_CLUSTER_GFS_USAGE,
			ADMINISTRATION_CLUSTER_CONFIGURATION, ADMINISTRATION_NODES_COMMANDS, ADMINISTRATION_NODES_CONFIGURATION, ADMINISTRATION_NODES_QUEUES, ADMINISTRATION_NODES_SYSTEM, ADMINISTRATION_QUEUES_CURRENT,
			ADMINISTRATION_QUEUES_STATISTICS, ADMINISTRATION_QUEUES_INTERNAL_MAPS };

	/*-------------------------------
	 ! G F S                        ! 
	 -------------------------------*/
	/*
	 * All these permissions are defined to check what you can see in GFS
	 * explorer
	 */

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String GFS_STAR = GFS + PERMISSION_SEPARATOR + STAR;

	/**
	 * Permission tag for role create action
	 */
	public static final String GFS_DATA = GFS + PERMISSION_SEPARATOR + "data";

	/**
	 * Permission tag for role create action
	 */
	public static final String GFS_LIBRARY = GFS + PERMISSION_SEPARATOR + "library";

	/**
	 * Permission tag for role create action
	 */
	public static final String GFS_SOURCES = GFS + PERMISSION_SEPARATOR + "sources";

	/**
	 * Permission tag for role create action
	 */
	public static final String GFS_CLASS = GFS + PERMISSION_SEPARATOR + "class";

	/**
	 * Permission tag for role create action
	 */
	public static final String GFS_BINARY = GFS + PERMISSION_SEPARATOR + "binary";

	/**
	 * Permission tag arrays with all permissions for GFS domain
	 */
	public static final String[] GFS_ALL = { GFS_BINARY, GFS_CLASS, GFS_DATA, GFS_LIBRARY, GFS_SOURCES };

	/*-------------------------------
	 ! A D V A N C E D              ! 
	 -------------------------------*/
	/*
	 * permission to use the RMI connection during job execution. necessary for
	 * extended ANT utilities
	 */

	/**
	 * Permission tag for ALL role actions
	 */
	public static final String INTERNAL_SERVICES = "internal_services";
	
	/**
	 * Permission to access to local fs without having the administration role
	 */
	public static final String LOCAL_FILE_SYSTEM_ACCESS = "local_fs_access";

	private static final String[] EQUALS_TO_CHECK = new String[] { STAR, INTERNAL_SERVICES, LOCAL_FILE_SYSTEM_ACCESS, RESOURCES_STAR, RESOURCES_CREATE, RESOURCES_DELETE, RESOURCES_UPDATE, RESOURCES_READ, FILES_STAR, NODES_STAR, NODES_DRAIN, NODES_START, NODES_UPDATE, VIEW_STAR,
			VIEW_INPUT, VIEW_RUNNING, VIEW_OUTPUT, VIEW_ROUTING, VIEW_STATUS, VIEW_NODES, VIEW_ADMIN, VIEW_ROLES, VIEW_GFS_EXPLORER, VIEW_SWARM_NODES, JOBS_STAR, JOBS_PURGE, JOBS_HOLD, JOBS_RELEASE, JOBS_CANCEL, JOBS_KILL, JOBS_SUBMIT, JOBS_UPDATE,
			ROLES_STAR, ROLES_CREATE, ROLES_DELETE, ROLES_UPDATE, ROLES_READ, CERTIFICATES_STAR, CERTIFICATES_CREATE, CERTIFICATES_DELETE, CERTIFICATES_READ, GFS_STAR, GFS_BINARY, GFS_CLASS, GFS_DATA, GFS_LIBRARY, GFS_SOURCES, SWARM_NODES_START,
			SWARM_NODES_DRAIN, SWARM_NODES_STAR, SWARM_NODES_VIEW_CONFIG, SWARM_NODES_EDIT_CONFIG };

	private static final String[] STARTSWITH_TO_CHECK = new String[] { FILES_READ, FILES_WRITE, FILES_EXECUTE, DATASOURCES + PERMISSION_SEPARATOR, SURROGATE + PERMISSION_SEPARATOR, SEARCH_JOBS, SEARCH_NODES, ADMINISTRATION_CLUSTER_STAR,
			ADMINISTRATION_CLUSTER_GRS, ADMINISTRATION_CLUSTER_REDO, ADMINISTRATION_SECURITY_CERTIFICATE, ADMINISTRATION_SECURITY_SECRET, ADMINISTRATION_CLUSTER_WORKLOAD, ADMINISTRATION_CLUSTER_GFS_USAGE, 
			ADMINISTRATION_CLUSTER_CONFIGURATION, ADMINISTRATION_NODES_STAR, ADMINISTRATION_NODES_COMMANDS, ADMINISTRATION_NODES_CONFIGURATION, ADMINISTRATION_NODES_QUEUES, ADMINISTRATION_NODES_SYSTEM, ADMINISTRATION_QUEUES_STAR,
			ADMINISTRATION_QUEUES_CURRENT, ADMINISTRATION_QUEUES_STATISTICS, ADMINISTRATION_QUEUES_INTERNAL_MAPS };

	/**
	 * Private constructor to avoid any instantiations
	 */
	private Permissions() {
	}

	/**
	 * Checks the syntax of permission
	 * 
	 * @param permission permission string
	 * @return return true if syntax is correct
	 */
	public static boolean checkPermissionSyntax(String permission) {
		// checks equals
		for (String perm : EQUALS_TO_CHECK){
			if (perm.equalsIgnoreCase(permission)){
				return true;
			}
		}
		// checks starts with
		for (String perm : STARTSWITH_TO_CHECK){
			if (permission.startsWith(perm)){
				return true;
			}
		}
		
		return false;
	}	
}
