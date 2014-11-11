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
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class ResourceLoaderReference extends Reference {

	/**
	 * @param className
	 */
	public ResourceLoaderReference(String className) {
		super(className);
	}

	/**
	 * @param className
	 * @param addr
	 */
	public ResourceLoaderReference(String className, RefAddr addr) {
		super(className, addr);
	}

	/**
	 * @param className
	 * @param factory
	 * @param factoryLocation
	 */
	public ResourceLoaderReference(String className, String factory, String factoryLocation) {
		super(className, factory, factoryLocation);
	}

	/**
	 * @param className
	 * @param addr
	 * @param factory
	 * @param factoryLocation
	 */
	public ResourceLoaderReference(String className, RefAddr addr, String factory, String factoryLocation) {
		super(className, addr, factory, factoryLocation);
	}

	public abstract void loadResource(Resource res, List<DataDescriptionImpl> ddList, String sourceName) throws JemException;
	
}
