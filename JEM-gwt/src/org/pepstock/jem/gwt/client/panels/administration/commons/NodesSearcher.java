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
package org.pepstock.jem.gwt.client.panels.administration.commons;

import org.pepstock.jem.gwt.client.commons.SearcherListenerWidget;

/**
 * Common panel used to insert a string as a filter to search necessary data.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NodesSearcher extends SearcherListenerWidget {
	
	/**
	 *  Construct the panel, with label for text field
	 * @param preferences 
	 */
	public NodesSearcher(String preferences) {
		super("Nodes pattern (regEx): ", -1, "*", preferences);
	}
}