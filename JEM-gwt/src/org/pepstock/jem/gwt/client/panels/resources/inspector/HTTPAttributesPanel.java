/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
import org.pepstock.jem.gwt.client.commons.NumericalTextBox;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.HttpResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Panel for a HTTP Resource attributes 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class HTTPAttributesPanel extends ResourcesPropertiesPanel {

	/**
	 * Alert message if the <code>Request Host Name is empty</code> is missing.
	 */
	private static String NO_REQUEST_HOST_NAME_DESCRIPTION = "Request Host Name is empty. <br/>Please type a Request Host Name.";
		
	/**
	 * Alert message if a HTTP required attribute is missing.
	 */
	private static String NO_REQUIRED_FIELD = "HTTP Required Attribute empty!";
	
	private MandatoryTextBox requestHostName = new MandatoryTextBox();
	
	private NumericalTextBox requestPort = new NumericalTextBox();
	
	private ListBox protocolType = new ListBox();
	
	private TextBox proxyUrl = new TextBox();
	
	private ListBox proxyProtocol = new ListBox();
	
	private NumericalTextBox proxyPort = new NumericalTextBox();
	
	// not mandatory
	private TextBox userid = new TextBox();

	// not mandatory	
	private PasswordTextBox password = new PasswordTextBox();
	
	private TextBox requestLoginQueryString = new TextBox();

	private TextBox requestLoginUserId = new TextBox();

	private PasswordTextBox requestLoginPassword = new PasswordTextBox();

	private TextBox requestLoginParamUserId = new TextBox();

	private TextBox requestLoginParamPassword = new TextBox();
	
	private TextBox requestLogoutQueryString = new TextBox();
	
	private ScrollPanel connectionPanel = new ScrollPanel();
	
	private ScrollPanel proxyPanel = new ScrollPanel();
	
	private ScrollPanel loginPanel = new ScrollPanel();
	/**
	 * @param resource 
	 * 
	 */
	public HTTPAttributesPanel(Resource resource) {
		super(resource);
		// to avoid 2 scrollbars
		super.getScrollableElement().getStyle().setOverflow(Overflow.HIDDEN);
		
		connectionPanel.add(this.createConnectionPropertiesTable());
		proxyPanel.add(this.createProxyPropertiesTable());
		loginPanel.add(this.createLoginLogoutPropertiesTable());
		
		TabPanel categoryPanel = new TabPanel();
		categoryPanel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		categoryPanel.add(connectionPanel, "Connection Properties");
		categoryPanel.add(proxyPanel, "Proxy Properties");
		categoryPanel.add(loginPanel, "Login/Logout Properties");
		categoryPanel.selectTab(0);
		loadProperties();
		
		// bug if you uses FlexTable as main component
		super.remove(super.getWidget());
		add(categoryPanel);
	}
	
	/**
	 * This method creates a {@link FlexTable} with the
	 * general connection properties of the {@link HttpResource}.
	 * 
	 * @return FlexTable
	 */
	private FlexTable createConnectionPropertiesTable(){
		FlexTable connectionPropertiesTable = new FlexTable();
		connectionPropertiesTable.setCellPadding(5);
		connectionPropertiesTable.setHTML(0, 0, "Request Host Name: <font color=\"red\"><b>*</b></font>");
		connectionPropertiesTable.setWidget(0, 1, requestHostName);
		connectionPropertiesTable.setHTML(0, 2, "The Host Name of the Request URL");
	    
		connectionPropertiesTable.setHTML(1, 0, "Request Port");
		connectionPropertiesTable.setWidget(1, 1, requestPort);
		connectionPropertiesTable.setHTML(1, 2, "The optional Port of the Request URL.");
	    
		connectionPropertiesTable.setHTML(2, 0, "Protocol Type");
		connectionPropertiesTable.setWidget(2, 1, protocolType);
		connectionPropertiesTable.setHTML(2, 2, "The Protocol Type. The default value is <code>HTTP</code>.");
	    protocolType.addItem(HttpResource.HTTP_PROTOCOL, HttpResource.HTTP_PROTOCOL);
	    protocolType.addItem(HttpResource.HTTPS_PROTOCOL, HttpResource.HTTPS_PROTOCOL);
	    protocolType.setSelectedIndex(0);

	    
	    connectionPropertiesTable.setHTML(3, 0, "<font color=\"red\"><b>*</b></font> Mandatory.");
	    
	    FlexCellFormatter cf = connectionPropertiesTable.getFlexCellFormatter();
	    cf.setWordWrap(0, 0, false);
	    cf.setWordWrap(1, 0, false);
	    cf.setWordWrap(2, 0, false);
	    
	    requestHostName.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_HOST_NAME, requestHostName.getText());
			}
		});
	    requestHostName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_HOST_NAME, requestHostName.getText());
			}
	    });

	    requestPort.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_PORT, requestPort.getText());
			}
		});
	    requestPort.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_PORT, requestPort.getText());
			}
	    });

	    protocolType.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setPropertyValue(HttpResource.PROTOCOL_TYPE, protocolType.getValue(protocolType.getSelectedIndex()));
			}
		});
	    return connectionPropertiesTable;
	}
	
	/**
	 * This method creates a {@link FlexTable} with the
	 * Proxy properties of the {@link HttpResource}.
	 * 
	 * @return FlexTable
	 */
	private FlexTable createProxyPropertiesTable(){
		FlexTable proxyPropertiesTable = new FlexTable();
		proxyPropertiesTable.setCellPadding(5);
		proxyPropertiesTable.setHTML(0, 0, "Proxy URL");
		proxyPropertiesTable.setWidget(0, 1, proxyUrl);
		proxyPropertiesTable.setHTML(0, 2, "The URL of the optional proxy.");
	    
		proxyPropertiesTable.setHTML(1, 0, "Proxy Protocol Type");
		proxyPropertiesTable.setWidget(1, 1, proxyProtocol);
		proxyPropertiesTable.setHTML(1, 2, "The Proxy Protocol Type. The default value is <code>HTTP</code>.");
		proxyProtocol.addItem(HttpResource.HTTP_PROTOCOL, HttpResource.HTTP_PROTOCOL);
		proxyProtocol.addItem(HttpResource.HTTPS_PROTOCOL, HttpResource.HTTPS_PROTOCOL);
		proxyProtocol.setSelectedIndex(0);
	    
		proxyPropertiesTable.setHTML(2, 0, "Proxy Port");
		proxyPropertiesTable.setWidget(2, 1, proxyPort);
		proxyPropertiesTable.setHTML(2, 2, "The Port of the proxy.");

		proxyPropertiesTable.setHTML(3, 0, "Proxy User Id:");
		proxyPropertiesTable.setWidget(3, 1, userid);
		proxyPropertiesTable.setHTML(3, 2, " The Proxy User Id if proxy authorization is need.");	    

		proxyPropertiesTable.setHTML(4, 0, "Proxy Password:");
		proxyPropertiesTable.setWidget(4, 1, password);
		proxyPropertiesTable.setHTML(4, 2, " The Proxy Password if proxy authorization is need.");
		
	    FlexCellFormatter cf = proxyPropertiesTable.getFlexCellFormatter();
	    cf.setWordWrap(0, 0, false);
	    cf.setWordWrap(1, 0, false);
	    cf.setWordWrap(2, 0, false);
	    cf.setWordWrap(3, 0, false);
	    cf.setWordWrap(4, 0, false);
	    
	    proxyUrl.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.PROXY_URL, proxyUrl.getText());
			}
	    });
	    proxyUrl.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.PROXY_URL, proxyUrl.getText());
			}
	    });
	    
	    proxyProtocol.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setPropertyValue(HttpResource.PROXY_PROTOCOL, proxyProtocol.getValue(proxyProtocol.getSelectedIndex()));
			}
		});
	    
	    proxyPort.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.PROXY_PORT, proxyPort.getText());
			}
		});
	    proxyPort.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.PROXY_PORT, proxyPort.getText());
			}
	    });
	    
	    userid.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.USERID, userid.getText());
			}
		});
	    userid.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.USERID, userid.getText());
			}
	    });
	    
	    password.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.PASSWORD, password.getText()).setVisible(false);
			}
		});
	    password.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.PASSWORD, password.getText()).setVisible(false);
			}
	    });
	    return proxyPropertiesTable;
	}
	
	/**
	 * This method creates a {@link FlexTable} with the
	 * Login / Logout properties of the {@link HttpResource}.
	 * 
	 * @return FlexTable
	 */
	private FlexTable createLoginLogoutPropertiesTable(){
		FlexTable loginLogoutPropertiesTable = new FlexTable();
		loginLogoutPropertiesTable.setCellPadding(5);
		
		loginLogoutPropertiesTable.setHTML(0, 0, "Request Login Query String:");
		loginLogoutPropertiesTable.setWidget(0, 1, requestLoginQueryString);
		loginLogoutPropertiesTable.setHTML(0, 2, " The Query String of the optional Login Request.");	    
	    
		loginLogoutPropertiesTable.setHTML(1, 0, "Request Login User Id:");
		loginLogoutPropertiesTable.setWidget(1, 1, requestLoginUserId);
		loginLogoutPropertiesTable.setHTML(1, 2, " The Login User Id if different from proxy User Id.");	    

		loginLogoutPropertiesTable.setHTML(2, 0, "Request Login Password:");
		loginLogoutPropertiesTable.setWidget(2, 1, requestLoginPassword);
		loginLogoutPropertiesTable.setHTML(2, 2, " The Login Password if different from proxy Password.");		    
	    
		loginLogoutPropertiesTable.setHTML(3, 0, "Request Login Parameter User Id:");
		loginLogoutPropertiesTable.setWidget(3, 1, requestLoginParamUserId);
		loginLogoutPropertiesTable.setHTML(3, 2, " The Parameter name of the User Id for Login Request.");		
	    
		loginLogoutPropertiesTable.setHTML(4, 0, "Request Login Parameter Password:");
		loginLogoutPropertiesTable.setWidget(4, 1, requestLoginParamPassword);
		loginLogoutPropertiesTable.setHTML(4, 2, " The Parameter name of the Password for Login Request.");		 
	    
		loginLogoutPropertiesTable.setHTML(5, 0, "Request Logout Query String:");
		loginLogoutPropertiesTable.setWidget(5, 1, requestLogoutQueryString);
		loginLogoutPropertiesTable.setHTML(5, 2, " The Query String of the optional Logout Request.");	
		
	    FlexCellFormatter cf = loginLogoutPropertiesTable.getFlexCellFormatter();
	    cf.setWordWrap(0, 0, false);
	    cf.setWordWrap(1, 0, false);
	    cf.setWordWrap(2, 0, false);
	    cf.setWordWrap(3, 0, false);
	    cf.setWordWrap(4, 0, false);
	    cf.setWordWrap(5, 0, false);
	    
	    requestLoginQueryString.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_QUERY_STRING, requestLoginQueryString.getText());
			}
		});
	    requestLoginQueryString.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_QUERY_STRING, requestLoginQueryString.getText());
			}
	    });
	    
	    requestLoginUserId.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_USERID, requestLoginUserId.getText());
			}
		});
	    requestLoginUserId.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_USERID, requestLoginUserId.getText());
			}
	    });
	    
	    requestLoginPassword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_PASSWORD, requestLoginPassword.getText()).setVisible(false);
			}
		});
	    requestLoginPassword.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_PASSWORD, requestLoginPassword.getText()).setVisible(false);
			}
	    });
	    
	    requestLoginParamUserId.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_PARAM_USERID, requestLoginParamUserId.getText());
			}
		});
	    requestLoginParamUserId.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_PARAM_USERID, requestLoginParamUserId.getText());
			}
	    });
	    
	    requestLoginParamPassword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_PARAM_PASSWORD, requestLoginParamPassword.getText());
			}
		});
	    requestLoginParamPassword.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_LOGIN_PARAM_PASSWORD, requestLoginParamPassword.getText());
			}
	    });
	    
	    requestLogoutQueryString.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setPropertyValue(HttpResource.REQUEST_LOGOUT_QUERY_STRING, requestLogoutQueryString.getText());
			}
		});
	    requestLogoutQueryString.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setPropertyValue(HttpResource.REQUEST_LOGOUT_QUERY_STRING, requestLogoutQueryString.getText());
			}
	    });
	    return loginLogoutPropertiesTable;
	}
	
	
	/**
	 * This method gets the property values from the 
	 * {@link HttpResource} and puts them in the form fields inside this
	 * panel.
	 */
	public void loadProperties() {
		for (ResourceProperty property : getResource().getProperties().values()) {
			if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_HOST_NAME)) {
				requestHostName.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_PORT)) {
				requestPort.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.PROTOCOL_TYPE)) {
				setSelectedIndex(protocolType, property);
			} else if (property.getName().equalsIgnoreCase(HttpResource.PROXY_URL)) {
				proxyUrl.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.PROXY_PROTOCOL)) {
				setSelectedIndex(proxyProtocol, property);
			} else if (property.getName().equalsIgnoreCase(HttpResource.PROXY_PORT)) {
				proxyPort.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.USERID)) {
				userid.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.PASSWORD)) {
				password.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_LOGIN_QUERY_STRING)) {
				requestLoginQueryString.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_LOGIN_USERID)) {
				requestLoginUserId.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_LOGIN_PASSWORD)) {
				requestLoginPassword.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_LOGIN_PARAM_USERID)) {
				requestLoginParamUserId.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_LOGIN_PARAM_PASSWORD)) {
				requestLoginParamPassword.setText(property.getValue());
			} else if (property.getName().equalsIgnoreCase(HttpResource.REQUEST_LOGOUT_QUERY_STRING)) {
				requestLogoutQueryString.setText(property.getValue());
			}
		}
	}
	
	/**
	 * Selected the index of list baesd on property set
	 * @param list list of values of properties
	 * @param property property set
	 */
	private void setSelectedIndex(ListBox list, ResourceProperty property){
		for (int i = 0; i < list.getItemCount(); i++) {
			if (list.getValue(i).equalsIgnoreCase(property.getValue())) {
				list.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * Check for mandatory attributes.
	 */
	public boolean checkMandatory() {
		String description = "";
		if (null == requestHostName.getText() || "".equals(requestHostName.getText().trim())) {
			description = "<li>" + NO_REQUEST_HOST_NAME_DESCRIPTION + "</li>";
		}

		if (!"".equals(description)) {
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
	 * Initialize the {@link ResourcesPropertiesPanel#getResource()} in case of
	 * creation (not editing) of a new {@link HttpResource}.
	 */
	public void initializeResource() {
		// is NEW?
		getResource().setType(HttpResource.TYPE);
		setPropertyValue(HttpResource.PROTOCOL_TYPE, HttpResource.HTTP_PROTOCOL);
		setPropertyValue(HttpResource.PROXY_PROTOCOL, HttpResource.HTTP_PROTOCOL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
	@Override
	public void onResize(int availableWidth, int availableHeight) {
		super.onResize(availableWidth, availableHeight);

		int tabPanelItemHeight = availableHeight - Sizes.TABBAR_HEIGHT_PX - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_PADDING_BOTTOM - 
				Sizes.MAIN_TAB_PANEL_BORDER;

		int tabPanelItemWidth = availableWidth - Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_BORDER - 
				Sizes.MAIN_TAB_PANEL_BORDER;

		connectionPanel.setSize(Sizes.toString(tabPanelItemWidth), Sizes.toString(tabPanelItemHeight));
		proxyPanel.setSize(Sizes.toString(tabPanelItemWidth), Sizes.toString(tabPanelItemHeight));
		loginPanel.setSize(Sizes.toString(tabPanelItemWidth), Sizes.toString(tabPanelItemHeight));

	}
}