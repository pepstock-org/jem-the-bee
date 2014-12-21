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

import java.util.ArrayList;
import java.util.List;

/**
 * Bean used on XML configuration which is the container of all the resource definitions.
 * 
 * @see CommonResourceDefinition
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class CommonResourcesDefinition extends CommonResourceDefinition {
	
	private static final long serialVersionUID = 1L;

	private List<CommonResourceDefinition> resources = new ArrayList<CommonResourceDefinition>();

	/**
	 * @return the resources
	 */
	public List<CommonResourceDefinition> getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<CommonResourceDefinition> resources) {
		this.resources = resources;
	}
}