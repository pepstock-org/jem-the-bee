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
package org.pepstock.jem.node.resources;

/**
 * Common resources utility for keys management, 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ResourcePropertiesUtil {

	/**
	 * Creates a empty object
	 */
	private ResourcePropertiesUtil() {
		
	}

	/**
	 * Adds a resource property with default values
	 * @param resource resource what to add a resource in
	 * @param name name of property
	 * @param value value of proprrty
	 */
	public static final void addProperty(Resource resource, String name, String value){
		addProperty(resource, name, value, true, false);
	}

	/**
	 * Adds a resource property with default values
	 * @param resource resource what to add a resource in
	 * @param name name of property
	 * @param value value of proprrty
	 * @param visible if the resource is visible or not
	 */
	public static final void addProperty(Resource resource, String name, String value, boolean visible){
		addProperty(resource, name, value, visible, false);
	}

	/**
	 * Adds a resource property with default values
	 * @param resource resource what to add a resource in
	 * @param name name of property
	 * @param value value of proprrty
	 * @param visible if the resource is visible or not
	 * @param override if the resource is overriable
	 */
	public static final void addProperty(Resource resource, String name, String value, boolean visible, boolean override){
		ResourceProperty prop = new ResourceProperty();
		prop.setName(name);
		prop.setValue(value);
		prop.setVisible(visible);
		prop.setOverride(false);
		
		resource.getProperties().put(name, prop);
	}
}