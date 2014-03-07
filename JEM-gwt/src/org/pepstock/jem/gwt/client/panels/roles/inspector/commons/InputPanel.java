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
package org.pepstock.jem.gwt.client.panels.roles.inspector.commons;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Component to manage the  search permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class InputPanel extends VerticalPanel {
	
	/**
	 * 
	 */
	public static final int SPACING = 5;

	/**
	 * 
	 */
	public static final int LABEL_HEIGHT = 24;
	
	/**
	 * 
	 */
	public static final int WIDTH = 300;
	
	// common styles
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private final Button add = new Button("Add");

	private final TextBox textBox = new TextBox();
	
	private InspectListener<String> listener = null;

	/**
	 * Constructs all UI using role instance information
	 * 
	 * @param header
	 * 
	 */
	public InputPanel(String header) {
		setWidth(Sizes.toString(WIDTH));
		setSpacing(SPACING);
		// sets disable the buttons because
		// they will be available when a permission will select
		// or inserted in text box
		add.setEnabled(false);
		
		Label label = new Label(header);
		label.addStyleName(Styles.INSTANCE.common().bold());
		label.setHeight(Sizes.toString(LABEL_HEIGHT));
		add(label);
		
		textBox.setVisibleLength(40);
		textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				// if some text is inserted, add button is enabled
				if (textBox.getText().length() > 0) {
					add.setEnabled(true);
				} else {
					add.setEnabled(false);
				}
			}

		});
		textBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && add.isEnabled()){
					add.click();
				}
			}
		});
		
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// adds the permission when click on add button
				if (listener != null){
					listener.inspect(textBox.getText());
				}
			}
		});
		add(textBox);
		
		add(add);
	}


	/**
	 * @return the listener
	 */
	public InspectListener<String> getListener() {
		return listener;
	}


	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<String> listener) {
		this.listener = listener;
	}
	
	public void clear(){
		textBox.setText("");
	}
	
	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled){
		add.setEnabled(enabled);
	}
}