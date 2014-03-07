/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco Cuccato
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
package org.pepstock.jem.gwt.client.notify;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.panels.components.Header;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Header component of popup panel to show toasts.
 * 
 * @author Marco Cuccato
 *
 */
public class NotifyHeader extends Header {

	/**
	 * Constructs object using the header container, a popup panel
	 * 
	 * @param parent popup panel where the header will be placed
	 */
	public NotifyHeader(PopupPanel parent) {
		super(Images.INSTANCE.notifyNormal(), "Messages history", parent);
	}
	
}