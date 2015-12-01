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
package org.pepstock.jem.node.configuration;

import java.io.Serializable;

/**
 * Represents factory of JEM, for job task creation, during configuration stage.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Factory extends AbstractPluginDefinition implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String classLoader = null;
	
	private String type = null;
	
	private String description = null;

	/**
	 * @return the classLoader
	 */
	public String getClassLoader() {
		return classLoader;
	}

	/**
	 * @param classLoader the classLoader to set
	 */
	public void setClassLoader(String classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Factory [classLoader=" + classLoader + ", type=" + type + ", description=" + description + ", getClassName()=" + getClassName() + ", getProperties()=" + getProperties() + "]";
	}
}