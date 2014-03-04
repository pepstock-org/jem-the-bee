/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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


/**
 * Interface that identify object that provides a hide capability 
 * @author Andrea "Stock" Stocchero
 */
public interface HideHandler {
	
	/**
	 * This method is called when a message box is close.<br>
	 * Button pressed is passed as argument.
	 * 
	 * @param buttonPressed button pressed
	 */
	void onHide(PreferredButton buttonPressed);

}