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
package org.pepstock.jem.ant.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.AntMessage;

/**
 * Represents a logical name of a database, addressable by name both a java code
 * (JNDI).<br>
 * Example: <br>
 * <code>&lt;dataSource name="jndiname" database="logicalDBname" /&gt;<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataSource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private String resource = null;
	
	private List<Property> properties = new ArrayList<Property>();
	/**
	 * Empty constructor
	 */
	public DataSource() {
	}

	/**
	 * Returns the name of datasource. This is mandatory value because is
	 * used to access to resources by name.
	 * 
	 * @return the name of data description
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of datasource. This is mandatory value because is
	 * used to access to resources by name.
	 * 
	 * @param name the name of data description
	 */
	public void setName(String name) {
		// checks if the name is empty
		if (name.trim().length() == 0){
			throw new BuildException(AntMessage.JEMA021E.toMessage().getFormattedMessage());
		}
		this.name = name;
	}

	/**
	 * Returns the name of common resource to access to. This is mandatory value because must
	 * be defined inside of JEM resources.
	 * 
	 * @return the resource name
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the name of common resource to access to. This is mandatory value because must
	 * be defined inside of JEM resources.
	 * @param resource the resource name to set
	 */
	public void setResource(String resource) {
		// checks if the name is empty
		if (resource.trim().length() == 0){
			throw new BuildException(AntMessage.JEMA022E.toMessage().getFormattedMessage());
		}
		this.resource = resource;
	}

	
	/**
	 * Adds a property definition.
	 * 
	 * @param property property definition
	 */
	public void addProperty(Property property) {
		properties.add(property);
	}

	/**
	 * Returns the list of properties defined into data source
	 * 
	 * @return the list of properties defined into data source
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * Returns the string representation of data description.
	 * 
	 * @return the string representation of data description
	 */
	@Override
	public String toString() {
		return "[datasource=" + getName() + ", resource=" + getResource() + "]";
	}

}