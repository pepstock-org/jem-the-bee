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
package org.pepstock.jem.gwt.client.panels.resources.inspector;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.panels.components.NewObjectHeader;
import org.pepstock.jem.node.resources.Resource;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Header component for role adding. A text field is used to add Role name
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class NewResourceHeader extends NewObjectHeader {

	private Resource resource = null;
	
	/**
	 * Creates the header with text field to assign the resource name to empty resource object passed by argument
	 * @param resource new resource to fill with all necessary data
	 * @param parent 
	 */
	public NewResourceHeader(Resource resource, PopupPanel parent) {
		super(Images.INSTANCE.cube64(), "Type here the new resource name...", parent);
		this.resource = resource;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}



	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}



	@Override
	public void onNameTyped(String name) {
		resource.setName(name);
	}
	
}