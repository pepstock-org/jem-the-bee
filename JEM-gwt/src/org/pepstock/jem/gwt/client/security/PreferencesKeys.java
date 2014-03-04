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
package org.pepstock.jem.gwt.client.security;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
@SuppressWarnings("javadoc")
public class PreferencesKeys {
	
	public static final int DEFAULT_MAXIMUM_PREFERENCES = 10;
	
    public static final String JOB_SEARCH_INPUT = "job.search.input";
	
	public static final String JOB_SEARCH_RUNNING = "job.search.running";
	
	public static final String JOB_SEARCH_OUTPUT = "job.search.output";
	
	public static final String JOB_SEARCH_ROUTING = "job.search.routing";
	
	public static final String JOB_SEARCH_STATUS = "job.search.status";
	
	public static final String JOB_EDIT_FONTSIZE = "job.edit.fontsize";
	
	public static final String JOB_SUBMIT_TYPE = "job.submit.type";
	
	public static final String SWARM_SEARCH = "swarm.search";
	
	public static final String NODES_SEARCH = "nodes.search";
	
	public static final String ROLES_SEARCH = "roles.search";
	
	public static final String RESOURCES_SEARCH = "resources.search";
	
	public static final String EXPLORER_SEARCH_DATA = "explorer.search.data";
	
	public static final String EXPLORER_SEARCH_LIBRARY = "explorer.search.library";
	
	public static final String EXPLORER_SEARCH_SOURCES = "explorer.search.sources";
	
	public static final String EXPLORER_SEARCH_CLASS = "explorer.search.class";
	
	public static final String EXPLORER_SEARCH_BINARY = "explorer.search.binary";
	
	public static final String ADMIN_RESOURCES_CONTENTION = "administration.resources.contention";
	
	public static final String ADMIN_NODES_CONFIGURATION = "administration.nodes.configuration";
	
	public static final String ADMIN_NODES_COMMANDS = "administration.nodes.commands";
	
    public static final String JOB_SORT_INPUT = "job.sort.input";
	
	public static final String JOB_SORT_RUNNING = "job.sort.running";
	
	public static final String JOB_SORT_OUTPUT = "job.sort.output";
	
	public static final String JOB_SORT_ROUTING = "job.sort.routing";

	public static final String NODES_SORT = "nodes.sort";
	
	public static final String SWARM_SORT = "swarm.sort";
	
	public static final String ROLES_SORT = "roles.sort";
	
	public static final String RESOURCES_SORT = "resources.sort";
	
	public static final String EXPLORER_SORT_DATA = "explorer.sort.data";
	
	public static final String EXPLORER_SORT_LIBRARY = "explorer.sort.library";
	
	public static final String EXPLORER_SORT_SOURCES = "explorer.sort.sources";
	
	public static final String EXPLORER_SORT_CLASS = "explorer.sort.class";
	
	public static final String EXPLORER_SORT_BINARY = "explorer.sort.binary";
	
	public static final String NOTIFY_SORT = "notify.sort";

	/**
	 * To avoid any instantiation 
	 */
    private PreferencesKeys() {
    }
}
