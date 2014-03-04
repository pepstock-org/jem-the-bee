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
package org.pepstock.jem.node.resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.pepstock.jem.node.UpdateableItem;

/**
 * Is common resource that all jobs can use for own properties.
 * 
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Resource extends UpdateableItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    /**
     * 
     */
    public static final String USERID = "username";

    /**
     * 
     */
    public static final String PASSWORD = "password";

	
	private String type = null;

	private Map<String, ResourceProperty> properties = new HashMap<String, ResourceProperty>();
	
	/**
	 * Empty constructor
	 */
	public Resource() {
	}

	/**
	 * Type-based contructor
	 * @param type the Strign that identify the resource type
	 */
	public Resource(String type) {
		this.type = type;
	}
	
	/**
	 * Returns the type for this resource
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the type for this resource
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the properties
	 */
	public Map<String, ResourceProperty> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, ResourceProperty> properties) {
		this.properties = properties;
	}

	
	/**
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value){
		ResourceProperty prop = properties.get(name);
		if (prop == null){
			prop = new ResourceProperty();
			prop.setName(name);
			properties.put(prop.getName(), prop);
		}
		prop.setValue(value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resource [name=" + getName() + ", type=" + type + ", user=" + getUser() + ", lastModified=" + getLastModified() + "]";
	}
	
}