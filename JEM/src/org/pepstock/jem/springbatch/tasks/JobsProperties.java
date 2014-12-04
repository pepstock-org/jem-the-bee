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
package org.pepstock.jem.springbatch.tasks;

import java.util.Properties;

import org.pepstock.jem.util.VariableSubstituter;

/**
 * Utility class used to load properties already loaded by SpringBatch and to use to substitute place holders
 * programmatically inside the JEM layer for SpringBatch
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class JobsProperties {
	
	private static final JobsProperties INSTANCE = new JobsProperties();
	
	private Properties properties = null;

	/**
	 * To avoid any instantiation as singleton
	 */
	private JobsProperties() {
		properties = new Properties();
		// creates properties using system and env variables for substitution
		properties.putAll(System.getenv());
		properties.putAll(System.getProperties());
	}
	
	/**
	 * Returns job properties instance, unique during the job execution
	 * @return job properties instance
	 */
	public static final JobsProperties getInstance(){
		return INSTANCE;
	}

	/**
	 * @return the properties
	 */
	Properties getProperties() {
		return properties;
	}

	void loadProperties(Properties props){
		if (props != null){
			properties.putAll(props);
		}
	}
	
	/**
	 * Replaces the variables inside a string if there are
	 * @param value string to be changed
	 * @return string with values updated
	 */
	public String replacePlaceHolders(String value){
		return VariableSubstituter.substitute(value, properties);
	}
}
