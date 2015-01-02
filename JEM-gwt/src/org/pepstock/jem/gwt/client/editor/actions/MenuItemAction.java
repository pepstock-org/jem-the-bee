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
package org.pepstock.jem.gwt.client.editor.actions;

import org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Abstract class which represents a menu item to add to acton bar.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class MenuItemAction {
	
	private MenuItem item = null;

	private AbstractSyntaxHighlighter highlighter = null;
	
	/**
	 * Constructs the object saving the syntax highlighter
	 * 
	 * @param highlighter instance
	 */
    public MenuItemAction(AbstractSyntaxHighlighter highlighter) {
	    this.highlighter = highlighter;
    }

	/**
	 * @return the item
	 */
	public MenuItem getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(MenuItem item) {
		this.item = item;
	}

	/**
	 * @return the highlighter
	 */
	public AbstractSyntaxHighlighter getHighlighter() {
		return highlighter;
	}

}
