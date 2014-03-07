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
package org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.RegExValidatingTextBox;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Component to manage the  search permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NetworkPanel extends VerticalPanel {

	private static final String ADDRESS_LABEL = "IP Address:";

    private static final String ADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    
    private static final String ADDRESS_OK_TITLE = "Address (Ip Address).";

    private static final String ADDRESS_ERROR_TITLE = "Please insert a correct Address (Ip Address).";

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

	private final RegExValidatingTextBox addressBox = new RegExValidatingTextBox(ADDRESS_ERROR_TITLE, ADDRESS_PATTERN);

	private InspectListener<String> listener = null;

	/**
	 * Constructs all UI using role instance information
	 * 
	 * @param header
	 * 
	 */
	public NetworkPanel(String header) {
		setWidth(Sizes.toString(WIDTH));
		setSpacing(SPACING);
		// sets disable the buttons because
		// they will be available when address properties will be selected
		// or inserted in text boxes
		add.setEnabled(false);

		Label label = new Label(header);
		label.addStyleName(Styles.INSTANCE.common().bold());
		label.setHeight(Sizes.toString(LABEL_HEIGHT));
		add(label);

		addressBox.setVisibleLength(40);
		addressBox.setTitle(ADDRESS_OK_TITLE);
		addressBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				// if some text is inserted, add button is enabled
				if (addressBox.isValidText()) {
					add.setEnabled(true);
				} else {
					add.setEnabled(false);
				}
			}
		});
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// adds the network address when click on add button
				if (listener != null){
					listener.inspect(addressBox.getText());
				}
			}
		});
		Label addressLabel = new Label();
		addressLabel.setText(ADDRESS_LABEL);
		add(addressLabel);
		add(addressBox);
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
		addressBox.setText("");
		setEnabled(false);
	}

	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled){
		add.setEnabled(enabled);
	}
}