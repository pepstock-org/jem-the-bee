/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Andrea "Stock" Stocchero
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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Wrapper of ACE editor. Not all methods of ACE are implemented.
 * Only the necessary ones.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class Editor extends Widget implements RequiresResize {

	private final String id;

	private JavaScriptObject editor;

	/**
	 * Creates a editor element with a increment ID.
	 */
	public Editor() {
		this(String.valueOf(CurrentEditorID.getCurrentId()));
		// increments new id
		CurrentEditorID.incrementCurrentId();
	}
	/**
	 * Creates a editor element with passed ID.
	 * @param idParm element ID
	 */
	public Editor(String idParm) {
		// create a new element ID
		id = "_editor_" + idParm;
	
		// creates ELement
		Element element = Document.get().createPreElement();
		element.setId(id);
		
		// COMMON styles
		// this styles must be set to 0
		element.getStyle().setMargin(0, Unit.PX);
		element.getStyle().setTop(0, Unit.PX);
		element.getStyle().setBottom(0, Unit.PX);
		element.getStyle().setLeft(0, Unit.PX);
		element.getStyle().setRight(0, Unit.PX);
		element.getStyle().setZIndex(0);
		// set element
		setElement(element);
	}
	
	/**
	 * Start the editor.
	 */
	public native void start() /*-{
		var editor = $wnd.ace.edit(this.@org.pepstock.jem.gwt.client.editor.Editor::id);
		editor.getSession().setUseWorker(false);
		this.@org.pepstock.jem.gwt.client.editor.Editor::editor = editor;
		editor.resize();
		this.@org.pepstock.jem.gwt.client.editor.Editor::redisplay();
	}-*/;

	/**
	 * Re-display editor content.
	 */
	public native void redisplay() /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.renderer.onResize(true);
		editor.renderer.updateFull();
		editor.resize();
		editor.focus();
		
	}-*/;

	/**
	 * Destroy editor.
	 */
	public native void destroy() /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.destroy();
	}-*/;

	/**
	 * Sets THEME 
	 * @param theme to set
	 */
	public void setTheme(final Theme theme) {
		setTheme(theme.getName());
	}


	/**
	 * Sets THEME by a string
	 * @param themeName string
	 */
	public native void setTheme(String themeName) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.setTheme("ace/theme/" + themeName);
	}-*/;

	/**
	 * Sets MODE
	 * @param mode to set
	 */
	public void setMode(final Mode mode) {
		setMode(mode.getName());
	}

	/**
	 * Sets MODE by string
	 * @param modeName
	 */
	public native void setMode(String modeName) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		var mode = "ace/mode/" + modeName;
		editor.getSession().setMode(mode);
	}-*/;



	/**
	 * Sets font size. See here possible values: http://www.w3schools.com/cssref/pr_font_font-size.asp
	 * @param fontSize font size to use
	 */
	public native void setFontSize(String fontSize) /*-{
		var elementId = this.@org.pepstock.jem.gwt.client.editor.Editor::id;
		var elt = $doc.getElementById(elementId);
		if (elt != null)
			elt.style.fontSize = fontSize;
	}-*/;

	/**
	 * Gets content of editor 
	 * @return the text 
	 */
	public native String getText() /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		return editor.getSession().getValue();
	}-*/;

	/**
	 * Sets content for editing
	 * @param text the text to set
	 */
	public native void setText(String text) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.getSession().setValue(text);
	}-*/;

	/**
	 * Sets editor in READ-ONLY. 
	 * @param readOnly true sets read-only
	 */
	public native void setReadOnly(boolean readOnly) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.setReadOnly(readOnly);
	}-*/;

	/**
	 * Sets highlight current row. Used only for browse.
	 * @param shouldHighlight true sets highlight
	 */
	public native void setHighlightActiveLine(boolean shouldHighlight) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.setHighlightActiveLine(shouldHighlight);
	}-*/;

	/**
	 * Select all content
	 * @return the text 
	 */
	public native String selectAll() /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		return editor.selectAll();
	}-*/;
	
	/**
	 * Set or unset the visibility of the print margin.
	 *
	 * @param showPrintMargin true if the print margin should be shown, false otherwise
	 */
	public native void setShowPrintMargin(boolean showPrintMargin) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.renderer.setShowPrintMargin(showPrintMargin);
	}-*/;

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ResizeComposite#onResize()
	 */
	@Override
	public void onResize() {
		redisplay();
	}
	
	/**
	 * Register a handler for change events generated by the editor.
	 *
	 * @param callback the change event handler
	 */
	public native void addOnChangeHandler(EditorChangeHandler callback) /*-{
		var editor = this.@org.pepstock.jem.gwt.client.editor.Editor::editor;
		editor.getSession().on("change", function(e) {
			callback.@org.pepstock.jem.gwt.client.editor.EditorChangeHandler::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
}
