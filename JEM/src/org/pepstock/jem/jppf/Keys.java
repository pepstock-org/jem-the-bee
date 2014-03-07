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
package org.pepstock.jem.jppf;

import java.lang.management.ManagementFactory;

/**
 * Container of JEM keys used to pass the XML element attributes to main program
 * by a set of properties, serialized in XML.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
@SuppressWarnings("javadoc")
public final class Keys {
	
	public static final int DEFAULT_PARALLEL_TASK_NUMBER =  ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	
	public static final String JEM_JOB_NAME = "jem.job.name";
	
	public static final String JEM_TASK_NAME = "jem.task.name";
	
	public static final String JEM_RUNNABLE = "jem.runnable";

	public static final String JEM_CONTEXT = "jem.context";
	
	public static final String JEM_DATASOURCE = "jem.datasource";
	
	public static final String JEM_TASK_NUMBER = "jem.task.number";
	
	public static final String JEM_DELIMITER = "jem.delimiter";
	
	public static final String JEM_DELIMITER_STRING = "jem.delimiter.string";
	
	public static final String JEM_CHUNKABLE_DATA_DESCRIPTION = "jem.chunkable.data.description";
	
	public static final String JEM_MERGED_DATA_DESCRIPTION = "jem.merged.data.description";
	
	public static final String JEM_CHUNKS = "jem.chunks";
	
	public static final String JEM_TEMPORARY_FILES = "jem.temporary.files";
	
	public static final String JEM_JPPF_DRIVER_PREFIX = "jem";
	
	public static final String JEM_JPPF_SERVER_SUFFIX = ".jppf.server.host";
	
	public static final String JEM_JPPF_PORT_SUFFIX = ".jppf.server.port";
	
	public static final String JEM_JPPF_DISCOVERY_ENABLED = "jppf.discovery.enabled";
	
	public static final String JEM_JPPF_LOCAL_EXECUTION_ENABLED = "jppf.local.execution.enabled";
	
	public static final String JEM_JPPF_DRIVERS = "jppf.drivers";
	
	public static final String JPPF_CONFIG_DATADESCRIPTION = "JPPF-CONFIG";

	/**
	 * To avoid any instantiation
	 */
	private Keys() {
	}
}