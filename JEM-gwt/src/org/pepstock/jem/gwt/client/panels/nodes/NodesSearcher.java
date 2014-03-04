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
package org.pepstock.jem.gwt.client.panels.nodes;

import org.pepstock.jem.gwt.client.commons.SearcherFilterableListenerWidget;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;


/**
 * Nodes panel used to insert a string as a node hostname or IP address filter to search necessary nodes.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesSearcher extends SearcherFilterableListenerWidget {

	/**
	 *  Construct the panel, with label for text field
	 */
	public NodesSearcher() {
		super("Nodes: ", -1, "", PreferencesKeys.NODES_SEARCH);
	}
}