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

import java.util.List;

import javax.naming.RefAddr;
import javax.naming.Reference;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.log.JemException;

/**
 * Is the extension of a normal JNDI reference which must be aware when about the data descritpions creations.
 * This reference usually interacts with data description (files). For instance, FTP resource.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class ResourceLoaderReference extends Reference {

	private static final long serialVersionUID = 1L;

	/**
	 * Calls the super class constructor
	 * @param className The non-null class name of the object to which this reference refers.
	 */
	public ResourceLoaderReference(String className) {
		super(className);
	}

	/**
	 * Calls the super class constructor
	 * @param className The non-null class name of the object to which this reference refers.
	 * @param addr The non-null address of the object.
	 */
	public ResourceLoaderReference(String className, RefAddr addr) {
		super(className, addr);
	}

	/**
	 * Calls the super class constructor
	 * @param className The non-null class name of the object to which this reference refers.
	 * @param factory The possibly null class name of the object's factory.
	 * @param factoryLocation The possibly null location from which to load the factory (e.g. URL)
	 */
	public ResourceLoaderReference(String className, String factory, String factoryLocation) {
		super(className, factory, factoryLocation);
	}

	/**
	 * Calls the super class constructor
	 * @param className The non-null class name of the object to which this reference refers.
	 * @param addr The possibly null class name of the object's factory
	 * @param factory The possibly null class name of the object's factory.
	 * @param factoryLocation The possibly null location from which to load the factory (e.g. URL)
	 */
	public ResourceLoaderReference(String className, RefAddr addr, String factory, String factoryLocation) {
		super(className, addr, factory, factoryLocation);
	}

	/**
	 * Method which is called when the reference is created inside the job execution, passing all data descriptions and
	 * The label used to call the resource inside the data description definition
	 * 
	 * @param res resource related to the reference
	 * @param ddList list of data descriptions
	 * @param sourceName the resource inside the data description definition
	 * @throws JemException if errors occurs during the loading
	 */
	public abstract void loadResource(Resource res, List<DataDescriptionImpl> ddList, String sourceName) throws JemException;
	
}
