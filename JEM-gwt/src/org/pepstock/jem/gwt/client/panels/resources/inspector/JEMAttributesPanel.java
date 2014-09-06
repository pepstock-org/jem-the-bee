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
package org.pepstock.jem.gwt.client.panels.resources.inspector;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.MandatoryPasswordTextBox;
import org.pepstock.jem.gwt.client.commons.MandatoryTextBox;
import org.pepstock.jem.node.resources.JemResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public final class JEMAttributesPanel  extends ResourcesPropertiesPanel{
	
	/**
	 * Alert message if a <code>URL</code> is missing.
	 */
	private static String NO_URL_DESCRIPTION = "URL is empty. <br/>Please type a URL.";
	
	/**
	 * Alert message if a <code>USERID</code> is missing.
	 */
	private static String NO_USERID_DESCRIPTION = "USERID is empty. <br/>Please type a USERID.";
	
	/**
	 * Alert message if a <code>PASSWORD</code> is missing.
	 */
	private static String NO_PASSWORD_DESCRIPTION = "PASSWORD is empty. <br/>Please type a PASSWORD.";

	private MandatoryTextBox url = new MandatoryTextBox();
	
	private MandatoryTextBox userid = new MandatoryTextBox();
	
	private MandatoryPasswordTextBox password = new MandatoryPasswordTextBox();
	
	/**
	 * @param resource 
	 * 
	 */
	public JEMAttributesPanel(Resource resource) {
		super(resource);
		FlexTable table = getTable();

		setWidth(Sizes.HUNDRED_PERCENT);
		table.setCellPadding(5);
		table.setHTML(1, 0, "URL: <font color=\"red\"><b>*</b></font>");
		table.setWidget(1, 1, url);
		table.setHTML(1, 2, "The JEM server URL (REST context) of the form <code>&lt;scheme&gt;://&lt;authority&gt;</code>");
		table.setHTML(2, 0, "User ID: <font color=\"red\"><b>*</b></font>");
		table.setWidget(2, 1, userid);
		table.setHTML(2, 2, " the JEM user on whose behalf the connection is being made");
		table.setHTML(3, 0, "Password: <font color=\"red\"><b>*</b></font>");
		table.setWidget(3, 1, password);
		table.setHTML(3, 2, " the user's password");
	    
		table.setHTML(4, 0, "<font color=\"red\"><b>*</b></font> Mandatory.");
	    
	    FlexCellFormatter cf = table.getFlexCellFormatter();
	    cf.setWordWrap(0, 0, false);
	    cf.setWordWrap(1, 0, false);
	    cf.setWordWrap(2, 0, false);
	    cf.setWordWrap(3, 0, false);
	    
	    loadProperties();
	    
	    url.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JemResource.URL, url.getText());
			}
		});
	    url.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(JemResource.URL, url.getText());
			}
	    });
	    userid.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JemResource.USERID, userid.getText());
			}
		});
	    userid.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(JemResource.USERID, userid.getText());
			}
	    });
	    password.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JemResource.PASSWORD, password.getText()).setVisible(false);
			}
		});
	    password.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(JemResource.PASSWORD, password.getText()).setVisible(false);
			}
	    });
	}

	/**
	 * 
	 */
	public final void loadProperties(){
		for (ResourceProperty property : getResource().getProperties().values()) {
			if (property.getName().equalsIgnoreCase(JemResource.URL)){
				url.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(JemResource.USERID)){
				userid.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(JemResource.PASSWORD)){
				password.setText(property.getValue());
			}
		}
	}
	
	/**
	 * Check for mandatory attributes.
	 */
	public boolean checkMandatory(){
		String description = "";
		if(null == url.getText() || "".equals(url.getText().trim())){
			description = description + "<li>" + NO_URL_DESCRIPTION + "</li>";
		}
		if(null == userid.getText() || "".equals(userid.getText().trim())){
			description = description + "<li>" + NO_USERID_DESCRIPTION + "</li>";
		}
		if(null == password.getText() || "".equals(password.getText().trim())){
			description = description + "<li>" + NO_PASSWORD_DESCRIPTION + "</li>";
		}
		return true;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	/**
	 * Initialize the {@link ResourcesPropertiesPanel#getResource()} in case
	 * of creation (not editing) of a new {@link JemResource}. 
	 */
	public void initializeResource(){
		getResource().setType(JemResource.TYPE);
	}
}