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
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.editor.AbstractSyntaxHighlighter;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Indent action represents a menu item to add to menu bar when the editor is started for 
 * XML contents.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Indent extends MenuItemAction {
	
	/**
	 * Constructs the menu item using the highlighter object
	 * 
	 * @param highlighter instance
	 */
    public Indent(AbstractSyntaxHighlighter highlighter) {
	    super(highlighter);
	    // creates the menu item, adding the image
		Image imgIndent = new Image(Images.INSTANCE.editIndent());
		imgIndent.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		MenuItem indent = new MenuItem(imgIndent +"  Indent", true, new IndentCommand());
		// sets Font weight to normal (GWT uses bold as default)
		indent.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		// sets menu item 
		setItem(indent);
	}
    
    private class IndentCommand implements Command {
		@Override
		public void execute() {
			// when clicked, it performs a RPC call to indent XML text
			// if editor has text
			if (getHighlighter().getEditor().getText().trim().length() > 0){
				Services.QUEUES_MANAGER.indent(getHighlighter().getEditor().getText(), new ServiceAsyncCallback<String>() {
					@Override
					public void onJemSuccess(String result) {
						getHighlighter().getEditor().setText(result);
					}

					@Override
					public void onJemFailure(Throwable caught) {
						new Toast(MessageLevel.ERROR, caught.getMessage(), "Indent Content error!").show();
					}

					@Override
                    public void onJemExecuted() {
						// do nothing
                    }
				});
			}
		}
    }
    
}
