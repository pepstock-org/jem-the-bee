/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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

/**
 * Provide methods for Resource Inspector 
 * @author Marco "Fuzzo" Cuccato
 */
public interface ResourceUIComponent {

	/**
	 * Checks for mandatory elements
	 * @return <code>true</code> if all mandatory attributes are set
	 */
	boolean checkMandatory();
	
	/**
	 * Validate the values
	 * @return <code>true</code> if the value match the validation regular expression (optionally provided by Descriptor
	 */
	boolean validate();
	
	/**
	 * Load resource properties to UI
	 */
	void loadProperties();

}
