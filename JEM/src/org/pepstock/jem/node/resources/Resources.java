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
import java.util.ArrayList;
import java.util.List;

/**
 * Is common resource that all jobs can use for own properties.
 * 
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Resources implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<Resource> resourcesList = new ArrayList<Resource>();

	/**
	 * Empty constructor
	 */
	public Resources() {
	}

	/**
	 * @return the resources
	 */
	public List<Resource> getResourcesList() {
		return resourcesList;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResourcesList(List<Resource> resources) {
		this.resourcesList = resources;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resources [resources=" + resourcesList + "]";
	}
	
}