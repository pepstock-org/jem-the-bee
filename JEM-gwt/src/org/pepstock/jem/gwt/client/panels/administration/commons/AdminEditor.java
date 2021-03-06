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

import org.pepstock.jem.gwt.client.editor.SyntaxHighlighter;

/**
 * Special ACE editor with the description attribute to show on messages 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface AdminEditor extends SyntaxHighlighter {
	
	/**
	 * Returns the description of file in editing
	 * @return the description of file in editing
	 */
	String getDescription();

	/**
	 * Sets the description of file in editing
	 * 
	 * @param description the description to set
	 */
	void setDescription(String description);
}
