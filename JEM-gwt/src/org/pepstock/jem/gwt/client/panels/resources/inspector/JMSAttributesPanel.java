/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
import org.pepstock.jem.gwt.client.commons.MandatoryTextBox;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.JmsResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is the panel to manage creation or editing  of
 * {@link JmsResource}. In this panel it is possible to modify
 * all the attributes of the <code>JmsResource</code>.
 * 
 * @author Alessandro Zambrini
 *
 */
public class JMSAttributesPanel  extends ResourcesPropertiesPanel{
	
	/**
	 * Alert message if a JMS required attribute is missing.
	 */
	private static String NO_REQUIRED_FIELD = "JMS Required Attribute empty!";
	
	/**
	 * Alert message if a <code>JMS provider URL is empty</code> is missing.
	 */
	private static String NO_PROVIDER_URL_DESCRIPTION = "JMS provider URL is empty. <br/>Please type a JMS provider URL.";
	
	/**
	 * Alert message if a <code>JMS Initial Context Factory</code> is missing.
	 */
	private static String NO_INITIAL_CONTEXT_FACTORY_DESCRIPTION = "JMS Initial Context Factory is empty. <br/>Please type a JMS Initial Context Factory.";
	
	private MandatoryTextBox providerUrl = new MandatoryTextBox();
	
	private MandatoryTextBox initialContextFactory = new MandatoryTextBox();
	
	// not mandatory
	private TextBox userid = new TextBox();

	// not mandatory	
	private PasswordTextBox password = new PasswordTextBox();
	
	/**
	 * @param resource 
	 * 
	 */
	public JMSAttributesPanel(Resource resource) {
		super(resource);
		FlexTable table = getTable();

		setWidth(Sizes.HUNDRED_PERCENT);
		table.setCellPadding(5);
		table.setHTML(0, 0, "JMS Provider URL: <font color=\"red\"><b>*</b></font>");
		table.setWidget(0, 1, providerUrl);
		table.setHTML(0, 2, "The JMS provider URL in the form: <code>&lt;scheme&gt;://&lt;host&gt;:&lt;port&gt;</code>");
	    
		table.setHTML(1, 0, "JMS Initial Context Factory: <font color=\"red\"><b>*</b></font>");
		table.setWidget(1, 1, initialContextFactory);
		table.setHTML(1, 2, "The JMS Initial Context Factory to create the connection to use <code>JMS</code>");
	    
		table.setHTML(2, 0, "User ID:");
		table.setWidget(2, 1, userid);
		table.setHTML(2, 2, " The JMS security principal on whose behalf the connection is being made");	    

		table.setHTML(3, 0, "Password:");
		table.setWidget(3, 1, password);
		table.setHTML(3, 2, " The JMS security credentials");
	    
		table.setHTML(4, 0, "<font color=\"red\"><b>*</b></font> Mandatory.");
	    
	    FlexCellFormatter cf = table.getFlexCellFormatter();
	    cf.setWordWrap(0, 0, false);
	    cf.setWordWrap(1, 0, false);
	    cf.setWordWrap(2, 0, false);
	    cf.setWordWrap(3, 0, false);
	    cf.setWordWrap(4, 0, false);
	    
	    loadProperties();
	    
	    providerUrl.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JmsResource.PROVIDER_URL, providerUrl.getText());
			}
		});
	    providerUrl.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(JmsResource.PROVIDER_URL, providerUrl.getText());
			}
	    });
	    initialContextFactory.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JmsResource.INITIAL_CONTEXT_FACTORY, initialContextFactory.getText());
			}
		});
	    initialContextFactory.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(JmsResource.INITIAL_CONTEXT_FACTORY, initialContextFactory.getText());
			}
	    });
	    userid.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JmsResource.USERID, userid.getText());
			}
		});
	    userid.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(JmsResource.USERID, userid.getText());
			}
	    });
	    password.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JmsResource.PASSWORD, password.getText()).setVisible(false);
			}
		});
	    password.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(JmsResource.USERID, userid.getText());
			}
		});
	}
	
	/**
	 * This method gets the property values from the 
	 * {@link JmsResource} and puts them in the form fields inside this
	 * panel.
	 */
	public final void loadProperties(){
		for (ResourceProperty property : getResource().getProperties().values()){
			if (property.getName().equalsIgnoreCase(JmsResource.PROVIDER_URL)){
				providerUrl.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(JmsResource.INITIAL_CONTEXT_FACTORY)){
				initialContextFactory.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(JmsResource.USERID)){
				userid.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(JmsResource.PASSWORD)){
				password.setText(property.getValue());
			} 
		}
	}
	
	/**
	 * Check for mandatory attributes.
	 */
	public boolean checkMandatory(){
		String description = "";
		if(null == providerUrl.getText() || "".equals(providerUrl.getText().trim())){
			description = "<li>" + NO_PROVIDER_URL_DESCRIPTION + "</li>";
		}
		if(null == initialContextFactory.getText() || "".equals(initialContextFactory.getText().trim())){
			description = description + "<li>" + NO_INITIAL_CONTEXT_FACTORY_DESCRIPTION + "</li>";
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
	 * of creation (not editing) of a new {@link JmsResource}. 
	 */
	public void initializeResource(){
		getResource().setType(JmsResource.TYPE);
	}

}