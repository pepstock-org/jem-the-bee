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
package org.pepstock.jem.util;

import java.util.LinkedList;
import java.util.List;


/**
 * This is the result when a plugin has been loaded, both using a custom class laode and using the standard one (in this case the classpath is empty). 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class ObjectAndClassPathContainer {
	
	private Object object = null;
	
	private ClassLoader loader = null;
	
	private List<String> classPath = new LinkedList<String>();

	/**
	 * Empty because is a bean
	 */
	public ObjectAndClassPathContainer() {
	
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the classPath
	 */
	public List<String> getClassPath() {
		return classPath;
	}

	/**
	 * @param classPath the classPath to set
	 */
	public void setClassPath(List<String> classPath) {
		this.classPath = classPath;
	}

	/**
	 * @return the loader
	 */
	public ClassLoader getLoader() {
		return loader;
	}

	/**
	 * @param loader the loader to set
	 */
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}

}
