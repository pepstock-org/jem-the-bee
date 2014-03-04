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

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.events.FontSizeEvent;
import org.pepstock.jem.gwt.client.events.FontSizeEventHandler;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Abstract class which manages ACE editor, delegating attributes to set to editor and
 * Menubar.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public abstract class AbstractSyntaxHighlighter extends VerticalPanel implements SyntaxHighlighter{
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private String content = null;
	
	private Editor editor = null;
	
	private boolean editorStarted = false;
	
    private MenuBar menu = new MenuBar();
    
    private MenuItem selectedFontItem = null;
    
    private boolean changed = false;
    
    private boolean readOnly = false;
    
    private MenuItem small = null;
    private MenuItem medium = null;
    private MenuItem large = null;
    private MenuItem extraLarge = null;
    
    private HandlerRegistration eventRegistration = null;
    
    private FontSize currentFontSize = null;
    
    /**
     * Empty constructor. Editor element ID will be calculated
     * automatically (a progressive number) and initial text is empty.
     */
    public AbstractSyntaxHighlighter() {
    	this(null);
    }
    
    /**
     * Creates the editor using a specific element ID. Text is empty.
     * @param id element id of editor
     */
    public AbstractSyntaxHighlighter(String id) {
		// sets CSS
		// pay attention to zIndex. MUST be SET HIGH
		menu.addStyleName(Styles.INSTANCE.common().editMenuBar());
		menu.setHeight(Sizes.toString(Sizes.TABBAR_HEIGHT_PX));
		menu.setAnimationEnabled(true);
		add(menu);
		
		// if id is null, use empty constructor
		if (id == null){
			editor = new Editor();
		} else {
			editor = new Editor(id);
		}
		add(editor);
	}
    
	/**
	 * Returns if content of editor is changed.
	 * 
	 * @return <code>true</code> if text of editor is changed, otherwise false.
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Sets if the text of editor is changed.
	 * 
	 * @param changed the changed to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * Returns if editor is started.
	 * 
	 * @return the editorStarted
	 */
	public boolean isEditorStarted() {
		return editorStarted;
	}

	/**
	 * Returns the original content, which initialize the editor.
	 * 
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the original content, which initialize the editor.<br>
	 * If editor is already started, sets the content to editor text.
	 * 
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
		// if editor is already started, sets TEXT
		if (editorStarted){
			editor.setText(content);
		}
	}

	/**
	 * Returns editor instance.
	 * 
	 * @return the editor
	 */
	public Editor getEditor() {
		return editor;
	}

	/**
	 * Returns if editor is readOnly.
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		editor.setReadOnly(readOnly);
	}

	/**
	 * Starts editor (if not already started).
	 * Furthermore adds menu item on menu bar
	 */
	public void startEditor(){
		if (!editorStarted){
			// start the first editor and set its theme and mode
			// must be called before calling setTheme/setMode/etc.
			editor.start(); 
			
			editor.setTheme(Theme.JEM);
			// delegates to set editor attributes
			setEditorAttributes(editor);

			// sets text, remove print margin and flag 
			// to check if editor is started
			editor.setShowPrintMargin(false);
			editor.setText(content);
			editorStarted = true;
			
			// adds change listener
			editor.addOnChangeHandler(new EditorChangeHandler() {

				@Override
				public void invokeAceCallback(JavaScriptObject obj) {
					// if content not null, checks if content is equals to text
					if (getContent() != null) {
						changed = !getContent().equalsIgnoreCase(getEditor().getText());
					} else {
						// if there is not content, is considered NOT changed!
						changed = false;
					}
					// fires the event
					onChange(changed);
				}
			});

			// gets the user preferences for editor 
			String pref = CurrentUser.getInstance().getStringPreference(PreferencesKeys.JOB_EDIT_FONTSIZE);
			// default is SMALL
			if (pref == null){
				pref = FontSize.SMALL.getCssValue();
			}
			
		    // Font size item
		    MenuBar fontSizeMenu = new MenuBar(true);
		    small = createMenuItem(FontSize.SMALL, pref);
		    medium = createMenuItem(FontSize.MEDIUM, pref);
		    large = createMenuItem(FontSize.LARGE, pref);
		    extraLarge = createMenuItem(FontSize.EXTRA_LARGE, pref);
		    
		    fontSizeMenu.addItem(small);
		    fontSizeMenu.addItem(medium);
		    fontSizeMenu.addItem(large);
		    fontSizeMenu.addItem(extraLarge);
		    
		    // if content is null, no ctions available
		    if (content == null){
		    	small.setEnabled(false);
		    	medium.setEnabled(false);
		    	large.setEnabled(false);
		    	extraLarge.setEnabled(false);
		    }
		    
		    // load images and creates menu item
		    Image imgFontSize = new Image(Images.INSTANCE.editFontSize());
		    imgFontSize.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		    MenuItem fontSizeMenuItem = new MenuItem(imgFontSize +"  Size", true, fontSizeMenu);
		    // sets font weight always to normal (no bold as GWT uses as default)
		    fontSizeMenuItem.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		    
		    menu.addItem(fontSizeMenuItem);
		    // calls here specific menu bar customization
		    setMenuItems(menu);
		    
		    
		    // adds a font size event handler so is aware when a editor changes font size
		    eventRegistration = EventBus.INSTANCE.addHandler(FontSizeEvent.TYPE, new SyntaxHighlighterFontSizeEventHandler());
		    
		} else {
			// sets attributes 
			setEditorAttributes(editor);
			editor.setFontSize(currentFontSize.getCssValue());
		}
	}
	
	private class SyntaxHighlighterFontSizeEventHandler implements FontSizeEventHandler {
		@Override
		public void onChange(FontSizeEvent event) {
			// called whene editor is started
			if (isEditorStarted()){
				FontSize fontsize = event.getFontSize();
				// saves font size
				currentFontSize = fontsize;
				// sets font size on editor
				// PAY attention that when editor is in a popup, if popup is not showing, 
				// the element of editor is null.
				editor.setFontSize(fontsize.getCssValue());
				// Enables the previous menu item, removing style
				selectedFontItem.setEnabled(true);
				selectedFontItem.removeStyleName(Styles.INSTANCE.common().editMenuItemDisabled());

				// looks for menu item of new font size
				MenuItem item = null;
				if (fontsize.getName().equals(FontSize.SMALL.getName())){
					item = small;
				} else if (fontsize.getName().equals(FontSize.MEDIUM.getName())){
					item = medium;
				} else if (fontsize.getName().equals(FontSize.LARGE.getName())){
					item = large;
				} else if (fontsize.getName().equals(FontSize.EXTRA_LARGE.getName())){
					item = extraLarge;
				} else {
					item = medium;
				}
				// Disables menu item and add disabled style
				item.setEnabled(false);
				item.addStyleName(Styles.INSTANCE.common().editMenuItemDisabled());
				selectedFontItem = item;
			}
		}
	}
	
	/**
	 * Method to set specific attributes on editor 
	 * 
	 * @param editor editor to change
	 */
	public abstract void setEditorAttributes(Editor editor);
	
	/**
	 * Method to add and change menubar of editor
	 * @param menu menu to change
	 * @param jclNotAvailable if JCL non available 
	 */
	public abstract void setMenuItems(MenuBar menu);

	private MenuItem createMenuItem(final FontSize fontsize, String preference){
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	    builder.appendHtmlConstant("<div style='font-size: "+fontsize.getCssValue()+";'>"+fontsize.getName()+"</div>");
	    final MenuItem item = new MenuItem(builder.toSafeHtml());
	    item.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				// sets preferences
				CurrentUser.getInstance().setStringPreference(PreferencesKeys.JOB_EDIT_FONTSIZE, fontsize.getCssValue());
				// fires new event
				FontSizeEvent event = new FontSizeEvent(fontsize);
				EventBus.INSTANCE.fireEvent(event);
			}
		});
		if (preference.equalsIgnoreCase(fontsize.getCssValue())){
			selectedFontItem = item;
			item.setEnabled(false);
			item.addStyleName(Styles.INSTANCE.common().editMenuItemDisabled());
			editor.setFontSize(fontsize.getCssValue());
			currentFontSize = fontsize;
		}
	    return item;
	}
	
	/**
	 * Destroy EDITOR, when inspector is hidden
	 */
	public void destroyEditor(){
		if (editorStarted){
			editor.destroy();
			if (eventRegistration != null){
				eventRegistration.removeHandler();
			}
		}
	}
	
	/**
	 * Called by editor listener when the text of editor is changed
	 * @param changed <code>true</code> if text is changed
	 */
	public void onChange(boolean changed){
		// NOP
	}
	
	/**
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	// MenuBar has got borders
    	menu.setWidth(Sizes.toString(availableWidth));
    	editor.setWidth(Sizes.toString(availableWidth));
    	editor.setHeight(Sizes.toString(availableHeight - Sizes.TABBAR_HEIGHT_PX - Sizes.MAIN_TAB_PANEL_BORDER));
    	if (isEditorStarted()){
    		editor.onResize();
    	}
    }
}