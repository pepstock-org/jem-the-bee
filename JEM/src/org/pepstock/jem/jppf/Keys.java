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
public final class Keys {
	
	/**
	 * Uses the available processors as maximum degree of parallelism (as default) 
	 */
	public static final int DEFAULT_PARALLEL_TASK_NUMBER =  ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	/**
	 * Property to set the job name
	 */
	public static final String JEM_JOB_NAME = "jem.job.name";
	/**
	 * Property to set JPPF task name
	 */
	public static final String JEM_TASK_NAME = "jem.task.name";
	/**
	 * Property to set the runnable class to be executed 
	 */
	public static final String JEM_RUNNABLE = "jem.runnable";
	/**
	 * Property to save the contex to pass to JPPF
	 */
	public static final String JEM_CONTEXT = "jem.context";
	/**
	 * Property to set the datasource name
	 */
	public static final String JEM_DATASOURCE = "jem.datasource";
	/**
	 * Property to set the number of tasks to execute
	 */
	public static final String JEM_TASK_NUMBER = "jem.task.number";
	/**
	 * Property to set the char delimiter in the file to chunk the file 
	 * for all tasks
	 */
	public static final String JEM_DELIMITER = "jem.delimiter";
	/**
	 * Property to set the string delimiter in the file to chunk the file 
	 * for all tasks
	 */
	public static final String JEM_DELIMITER_STRING = "jem.delimiter.string";
	/**
	 * Property to set the data description name to apply the chunk for tasks
	 */
	public static final String JEM_CHUNKABLE_DATA_DESCRIPTION = "jem.chunkable.data.description";
	/**
	 * Property to set the data description name to merge the result of tasks
	 */
	public static final String JEM_MERGED_DATA_DESCRIPTION = "jem.merged.data.description";
	/**
	 * Property to set internally the chunks
	 */
	public static final String JEM_CHUNKS = "jem.chunks";
	/**
	 * Property to set internally the temporary files to be merged
	 */
	public static final String JEM_TEMPORARY_FILES = "jem.temporary.files";
	/**
	 * Prefix of the property to set the driver JPPF
	 */
	public static final String JEM_JPPF_DRIVER_PREFIX = "jem";
	/**
	 * Suffix of the property to set the server host
	 */
	public static final String JEM_JPPF_SERVER_SUFFIX = ".jppf.server.host";
	/**
	 * Suffix of the property to set the server port
	 */
	public static final String JEM_JPPF_PORT_SUFFIX = ".jppf.server.port";
	/**
	 * Property to enable the discovery of JPPF servers. By default, the discovery is disable
	 */
	public static final String JEM_JPPF_DISCOVERY_ENABLED = "jppf.discovery.enabled";
	/**
	 * Property to enable the locla execution. By default, the local execution should be disable
	 */
	public static final String JEM_JPPF_LOCAL_EXECUTION_ENABLED = "jppf.local.execution.enabled";
	/**
	 * Property to set the list of JPPF drivers
	 */
	public static final String JEM_JPPF_DRIVERS = "jppf.drivers";

	/**
	 * The default data description name to read JPPF configuration from a dataset 
	 */
	public static final String JPPF_CONFIG_DATADESCRIPTION = "JPPF-CONFIG";

	/**
	 * To avoid any instantiation
	 */
	private Keys() {
	}
}