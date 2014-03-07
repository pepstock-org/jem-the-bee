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
 * Discard action represents a menu item to add to menu bar. It moves content value
 * as text of editor, overriding text of editor, if changed as well.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Discard extends MenuItemAction {

	/**
	 * Constructs the menu item using the highlighter object
	 * 
	 * @param highlighter
	 *            instance
	 */
	public Discard(AbstractSyntaxHighlighter highlighter) {
		super(highlighter);
		// creates the menu item, adding the image
		Image imgDiscard = new Image(Images.INSTANCE.editUndo());
		imgDiscard.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		MenuItem discard = new MenuItem(imgDiscard + "  Discard", true, new Command() {
			@Override
			public void execute() {
				// when clicked, it sets editor text with content
				// if editor has text
				getHighlighter().getEditor().setText(getHighlighter().getContent());
			}
		});
		// sets Font weight to normal (GWT uses bold as default)
		discard.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		// sets menu item
		setItem(discard);
	}
}
