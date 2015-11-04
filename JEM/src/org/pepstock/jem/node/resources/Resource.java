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
package org.pepstock.jem.node.resources;

import java.io.Serializable;

import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.node.UpdateableItem;

/**
 * Is common resource that all jobs can use for own properties.
 * Is a container of properties
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2	
 *
 */
public final class Resource extends UpdateableItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String type = null;

	private ResourceProperties properties = new ResourceProperties();
	
	private PropertiesWrapper customProperties = new PropertiesWrapper();
	
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
	public ResourceProperties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(ResourceProperties properties) {
		this.properties = properties;
	}

	/**
	 * @return the customProperties
	 */
	public PropertiesWrapper getCustomProperties() {
		return customProperties;
	}

	/**
	 * @param customProperties the customProperties to set
	 */
	public void setCustomProperties(PropertiesWrapper customProperties) {
		this.customProperties = customProperties;
	}

	/**
	 * Returns the custom properties in string format [key-value;]* 
	 * @return the custom properties in string format [key-value;]*
	 */
	public String getCustomPropertiesString(){
		String result = null;
		if (this.customProperties != null){
			for (String key : this.customProperties.keySet()){
				String value = this.customProperties.get(key);
				if (result == null){
					result = key.concat("=").concat(value).concat(";");
				} else {
					result = result.concat(key).concat("=").concat(value).concat(";");
				}
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resource [name=" + getName() + ", type=" + type + ", user=" + getUser() + ", lastModified=" + getLastModified() + "]";
	}
	
}