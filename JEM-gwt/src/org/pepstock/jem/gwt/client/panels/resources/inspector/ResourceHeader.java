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
package org.pepstock.jem.gwt.client.panels.resources.inspector;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.panels.components.Header;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Header component for resource updating.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ResourceHeader extends Header  {

	/**
	 * @param resourceName
	 * @param parent
	 */
	public ResourceHeader(String resourceName, PopupPanel parent) {
		super(Images.INSTANCE.cube64(), resourceName, parent);
	}

}