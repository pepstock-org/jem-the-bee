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
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.FtpResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public final class FTPAttributesPanel  extends ResourcesPropertiesPanel{
	
	/**
	 * Alert message if a FTP required attribute is missing.
	 */
	private static String NO_REQUIRED_FIELD = "FTP Required Attribute empty!";

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
	
	private CheckBox binary = new CheckBox();
	
	/**
	 * @param resource 
	 * 
	 */
	public FTPAttributesPanel(Resource resource) {
		super(resource);
		FlexTable table = getTable();

		setWidth(Sizes.HUNDRED_PERCENT);
		table.setCellPadding(5);
		table.setHTML(1, 0, "URL: <font color=\"red\"><b>*</b></font>");
		table.setWidget(1, 1, url);
		table.setHTML(1, 2, "The FTP server URL of the form <code>&lt;scheme&gt;://&lt;authority&gt;</code>");
		table.setHTML(2, 0, "User ID: <font color=\"red\"><b>*</b></font>");
		table.setWidget(2, 1, userid);
		table.setHTML(2, 2, " the FTP server user on whose behalf the connection is being made");
		table.setHTML(3, 0, "Password: <font color=\"red\"><b>*</b></font>");
		table.setWidget(3, 1, password);
		table.setHTML(3, 2, " the user's password");
		table.setHTML(4, 0, "Binary file type:");
		table.setWidget(4, 1, binary);
		table.setHTML(4, 2, "Sets the file type to be transferred to BINARY, otherwise is ASCII");
	    
		table.setHTML(5, 0, "<font color=\"red\"><b>*</b></font> Mandatory.");
	    
	    FlexCellFormatter cf = table.getFlexCellFormatter();
	    cf.setWordWrap(0, 0, false);
	    cf.setWordWrap(1, 0, false);
	    cf.setWordWrap(2, 0, false);
	    cf.setWordWrap(3, 0, false);
	    cf.setWordWrap(4, 0, false);
	    
	    loadProperties();
	    
	    url.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(FtpResource.URL, url.getText());
			}
		});
	    url.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(FtpResource.URL, url.getText());
			}
	    });
	    userid.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(FtpResource.USERID, userid.getText());
			}
		});
	    userid.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(FtpResource.USERID, userid.getText());
			}
	    });
	    password.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(FtpResource.PASSWORD, password.getText()).setVisible(false);
			}
		});
	    password.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(FtpResource.PASSWORD, password.getText()).setVisible(false);
			}
	    });
	    binary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setPropertyValue(FtpResource.BINARY, String.valueOf(binary.getValue()));
			}
		});
	}

	/**
	 * 
	 */
	public final void loadProperties(){
		for (ResourceProperty property : getResource().getProperties().values()) {
			if (property.getName().equalsIgnoreCase(FtpResource.URL)){
				url.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(FtpResource.USERID)){
				userid.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(FtpResource.PASSWORD)){
				password.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(FtpResource.BINARY)){
				binary.setValue(Boolean.parseBoolean(property.getValue()));
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
		if(!"".equals(description)){
			new Toast(MessageLevel.ERROR, NO_REQUIRED_FIELD, "<ul>" + description + "</ul>").show();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	/**
	 * Initialize the {@link ResourcesPropertiesPanel#getResource()} in case
	 * of creation (not editing) of a new {@link FtpResource}. 
	 */
	public void initializeResource(){
		getResource().setType(FtpResource.TYPE);
	}
}