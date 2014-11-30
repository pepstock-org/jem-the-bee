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
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
final class JobsProperties {
	
	private static final JobsProperties INSTANCE = new JobsProperties();
	
	private final Properties initProperties = new Properties();
	
	private Properties properties = null;

	/**
	 * 
	 */
	private JobsProperties() {
		// creates properties using system and env variables for substitution
		initProperties.putAll(System.getenv());
		initProperties.putAll(System.getProperties());
	}
	
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
		if (properties == null){
			return initProperties;
		}
		return properties;
	}

	void loadMetaData(Map<String, Object> metadata){
		if (metadata != null){
			INSTANCE.getInitProperties().putAll(metadata);
		}
	}
	
	void loadParameters(Map<String, Object> parms){
		properties = new Properties();
		properties.putAll(initProperties);
		if (parms != null){
			properties.putAll(parms);
		}
	}
}
