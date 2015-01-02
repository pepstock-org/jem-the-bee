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

import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.SimpleFilter;


/**
 * Nodes panel used to insert a string as a node ipaddress filter to search necessary nodes.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesFilter extends SimpleFilter {

	private SearchListener listener = null;
	
	/**
	 *  Construct the panel, with label for text field
	 */
	public NodesFilter() {
		super("Nodes IP address filter :");
	}

	/**
	 * @return the listener
	 */
	public SearchListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(SearchListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param textToSearch filter for job name
	 */
	public void onSearch(String textToSearch){
		if (listener != null){
			listener.search(textToSearch);
		}
	}
}