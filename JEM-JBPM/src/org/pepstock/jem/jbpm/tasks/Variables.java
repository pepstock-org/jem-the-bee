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
package org.pepstock.jem.jbpm.tasks;

import java.util.Properties;

/**
 * Singleton with all system properties and environmetn variables. THis is used for variable substitution, with variable format ${var}.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Variables {
	
	private static Variables INSTANCE = null;
	
	private final Properties properties = new Properties();

	/**
	 * Loads the system and environment properties
	 */
	private Variables() {
		// loads JVM properties to Properties instance
		// to be able to substitute variables in configuration
		properties.putAll(System.getProperties());
		properties.putAll(System.getenv());
	}

	/**
	 * Returns the instance of singleton
	 * @return the instance of singleton
	 */
	public static synchronized Variables getInstance(){
		if (INSTANCE == null){
			INSTANCE = new Variables();
		}
		return INSTANCE;
	}

	/**
	 * @return the properties
	 */
	Properties getProperties() {
		return properties;
	}
	
}
