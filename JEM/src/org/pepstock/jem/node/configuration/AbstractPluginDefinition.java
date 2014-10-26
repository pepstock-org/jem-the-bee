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
package org.pepstock.jem.node.configuration;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * Represents abstract class, used to define in configuration plugins to load.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class AbstractPluginDefinition implements Serializable{

	private static final long serialVersionUID = 1L;

	private Properties properties = new Properties();

	private String className = null;
	
	private List<ClassPath> classpath = null;

	/**
	 * Empty constructor
	 */
	public AbstractPluginDefinition() {
		
	}

	/**
	 * Returns the class name of factory implementation, to instantiate by
	 * <code>Class.ForName</code>
	 * 
	 * @return class name to instantiate
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class name of factory implementation, to instantiate by
	 * <code>Class.ForName</code>
	 * 
	 * @param className class name to instantiate
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Returns the parameters passed to factory to use after instantiation
	 * 
	 * @return the parameters
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Sets the parameters passed to factory to use to use after instantiation
	 * 
	 * @param parameters the parameters to set
	 */
	public void setProperties(Properties parameters) {
		this.properties = parameters;
	}

	/**
	 * @return the classpath
	 */
	public List<ClassPath> getClasspath() {
		return classpath;
	}

	/**
	 * @param classpath the classpath to set
	 */
	public void setClasspath(List<ClassPath> classpath) {
		this.classpath = classpath;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractPuginDefinition [properties=" + properties + ", className=" + className + "]";
	}
}