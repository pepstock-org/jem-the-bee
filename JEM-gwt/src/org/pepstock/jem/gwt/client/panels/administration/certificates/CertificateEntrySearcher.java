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
package org.pepstock.jem.gwt.client.panels.administration.certificates;

import org.pepstock.jem.gwt.client.commons.SearcherFilterableListenerWidget;


/**
 * Crtificares panel used to insert a string as a alias filter to search necessary certificates.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class CertificateEntrySearcher extends SearcherFilterableListenerWidget {

	/**
	 * Construct the panel, with label for text field
	 */
	public CertificateEntrySearcher() {
		super("Alias: ", -1, "");
	}
}