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
package org.pepstock.jem.jbpm.tasks;

import java.util.Map;
import java.util.Properties;

/**
 * Sigleton which collects all system properties and environment variables to use inside the code
 * substituting variables on resources properties or other.<br>
 * it uses 2 properties instaces, one with system properties and another with
 * the metadata of BPMN2 and parameters of tasks (loaded before every
 * task execution).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
final class JobsProperties {
	
	private static final JobsProperties INSTANCE = new JobsProperties();
	
	private final Properties initProperties = new Properties();
	
	private Properties properties = null;

	/**
	 * Initializes the properties loading OOTB
	 * System properties and environment variables
	 */
	private JobsProperties() {
		// creates properties using system and env variables for substitution
		initProperties.putAll(System.getenv());
		initProperties.putAll(System.getProperties());
	}
	
	/**
	 * Singleton method to get the single instance
	 * @return job properties instance
	 */
	static final JobsProperties getInstance(){
		return INSTANCE;
	}
	
	/**
	 * @return the initProperties
	 */
	private Properties getInitProperties() {
		return initProperties;
	}

	/**
	 * @return the properties
	 */
	Properties getProperties() {
		// if properties are null, means 
		// that parameters haven't been loaded
		if (properties == null){
			return initProperties;
		}
		return properties;
	}

	/**
	 * Loads metadata of BMPN2
	 * @param metadata metadata of workflow
	 */
	void loadMetaData(Map<String, Object> metadata){
		if (metadata != null){
			// loads metadata on init properties 
			// because they don't change during job lifecycle
			INSTANCE.getInitProperties().putAll(metadata);
		}
	}
	
	/**
	 * Loads parameters of JBPM task
	 * @param parms parameters of task
	 */
	void loadParameters(Map<String, Object> parms){
		// initialize the properties
		properties = new Properties();
		// adds the init ones
		properties.putAll(initProperties);
		if (parms != null){
			// adds the parameters
			properties.putAll(parms);
		}
	}
}
