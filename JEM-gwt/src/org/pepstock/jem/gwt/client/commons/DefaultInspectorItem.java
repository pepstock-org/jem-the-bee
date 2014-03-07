/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.commons;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Default inspector item. Is a resizable scrollpanel.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public class DefaultInspectorItem extends ScrollPanel implements ResizeCapable {

	/**
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
    }
}