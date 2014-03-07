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
import org.pepstock.jem.gwt.client.editor.actions.SelectAll;

import com.google.gwt.user.client.ui.MenuBar;


/**
 * Component that browses policy, with different languages, using ACE editor
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class PolicyViewer extends AbstractSyntaxHighlighter{
	
	private String language = null;
	
	/**
	 * Constructs syntax highlighter with a new element ID 
	 */
    public PolicyViewer() {
	    super();
    }

	/**
	 * Constructs syntax highlighter with a specific element ID 
	 * @param id ID for editor ELEMENT
	 */
    public PolicyViewer(String id) {
	    super(id);
    }

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setEditorAttributes(org.pepstock.jem.gwt.client.editor.Editor)
	 */
    @Override
    public void setEditorAttributes(Editor editor) {
    	// sets language and readonly
		editor.setMode(getLanguage());
		editor.setReadOnly(true);
		editor.setHighlightActiveLine(false);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter#setMenuItems(com.google.gwt.user.client.ui.MenuBar, boolean)
	 */
    @Override
    public void setMenuItems(MenuBar menu) {
    	// adds ONLY select all action
    	SelectAll selectAll = new SelectAll(this);
    	
    	menu.addItem(selectAll.getItem());
	    if (getContent() == null){
	    	selectAll.getItem().setEnabled(false);
	    }
    }
}