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
package org.pepstock.jem.gwt.client.editor.viewers;

import org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter;
import org.pepstock.jem.gwt.client.editor.Editor;
import org.pepstock.jem.gwt.client.editor.Mode;
import org.pepstock.jem.gwt.client.editor.actions.Indent;
import org.pepstock.jem.gwt.client.editor.actions.SelectAll;

import com.google.gwt.user.client.ui.MenuBar;


/**
 * Component that browses XML contents, using ACE editor
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class XmlViewer extends AbstractSyntaxHighlighter{

	/**
	 * Constructs syntax highlighter with a specific element ID 
	 * @param id ID for editor ELEMENT
	 */
	public XmlViewer(String id) {
		super(id);
	}

	/**
	 * Constructs syntax highlighter with a new element ID 
	 */
	public XmlViewer() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setEditorAttributes(org.pepstock.jem.gwt.client.editor.Editor)
	 */
    @Override
    public void setEditorAttributes(Editor editor) {
    	// sets XML mode, readonly
		editor.setMode(Mode.XML);
		editor.setReadOnly(true);
		editor.setHighlightActiveLine(false);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setMenuItems(com.google.gwt.user.client.ui.MenuBar, boolean)
	 */
    @Override
    public void setMenuItems(MenuBar menu) {
    	// Uses Indent and SelectAll
    	Indent indent = new Indent(this);
    	SelectAll selectAll = new SelectAll(this);
    
	    if (getContent() == null){
	    	indent.getItem().setEnabled(false);
	    	selectAll.getItem().setEnabled(false);
	    }
	    menu.addItem(selectAll.getItem());
	    menu.addSeparator();
	    menu.addItem(indent.getItem());
    }
}