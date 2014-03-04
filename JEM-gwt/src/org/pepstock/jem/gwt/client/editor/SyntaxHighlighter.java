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
package org.pepstock.jem.gwt.client.editor;

import org.pepstock.jem.gwt.client.ResizeCapable;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface for ACE editor
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public interface SyntaxHighlighter extends IsWidget, ResizeCapable{
	
	/**
	 * @return the content
	 */
	String getContent();

	/**
	 * @param content the content to set
	 */
	void setContent(String content);
	
	/**
	 * Starts editor (if not already started).
	 * Furthermore adds menu item on menu bar
	 */
	void startEditor();
	
	/**
	 * Destroy EDITOR, when inspector is hidden
	 */
	void destroyEditor();
	
	/**
	 * @return the changed
	 */
	boolean isChanged();

	/**
	 * @param changed the changed to set
	 */
	void setChanged(boolean changed);

	/**
	 * @return the editorStarted
	 */
	boolean isEditorStarted();
}