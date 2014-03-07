/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * SelectAll action represents a menu item to add to menu bar. It outlines all
 * editor text to copy in clipboard.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class SelectAll extends MenuItemAction {

	/**
	 * Constructs the menu item using the highlighter object
	 * 
	 * @param highlighter
	 *            instance
	 */
	public SelectAll(AbstractSyntaxHighlighter highlighter) {
		super(highlighter);
		// creates the menu item, adding the image
		Image imgSelectAll = new Image(Images.INSTANCE.editSelectAll());
		imgSelectAll.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		MenuItem selectAll = new MenuItem(imgSelectAll + " Select All", true, new Command() {
			@Override
			public void execute() {
				// if highlighter has editor, performs select all text
				if (getHighlighter().getEditor() != null) {
					getHighlighter().getEditor().selectAll();
				}
			}
		});
		// sets Font weight to normal (GWT uses bold as default)
		selectAll.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		// sets menu item
		setItem(selectAll);
	}
}
