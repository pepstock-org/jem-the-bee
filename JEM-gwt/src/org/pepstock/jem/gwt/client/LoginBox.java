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
package org.pepstock.jem.gwt.client;

import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.LoggedListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.security.LoggedUser;
import org.pepstock.jem.gwt.client.services.InfoService;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Is Login component, where is asking userid and password to log in JEM.
 * When receives teh logged user from server, calls teh listener that the user is logged in.
 *  
 * @author Andrea "Stock" Stocchero
 */
public class LoginBox extends VerticalPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.loginBox().ensureInjected();
	}

	private LoggedListener listener = null;

	private final TextBox useridInputBox = new TextBox();
	
	private final PasswordTextBox passwordInputBox= new PasswordTextBox();
	
	private final HorizontalPanel buttonAndEnvironment = new HorizontalPanel();
	
	private final Button loginButton = new Button("Login");
	
	private final InlineHTML environmentLabel = new InlineHTML();
	
	private final Label exceptionMessage = new Label();

	/**
	 * Constructs all components
	 */
	public LoginBox() {
		setSpacing(10);
		addStyleName(Styles.INSTANCE.loginBox().grid());
		
		// sets fix WIDTH for better rendering
		useridInputBox.setWidth("95%");
		passwordInputBox.setWidth("95%");

		// configure button+env label
		buttonAndEnvironment.getElement().setId("buttonAndEnvironment");
		buttonAndEnvironment.setHorizontalAlignment(ALIGN_LEFT);
		buttonAndEnvironment.setVerticalAlignment(ALIGN_MIDDLE);

		//set button style
		loginButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		
		// add button on button+environment label container
		buttonAndEnvironment.add(loginButton);
		buttonAndEnvironment.add(environmentLabel);
		environmentLabel.setVisible(false);
		buttonAndEnvironment.setCellWidth(environmentLabel, Sizes.HUNDRED_PERCENT);
		
		Services.INFO_SERVICE.getEnvironmentInformation(new ServiceAsyncCallback<String[]>() {

			@Override
			public void onJemFailure(Throwable caught) {
				//do nothing...
			}

			@Override
			public void onJemSuccess(String[] result) {
				String envName = result[InfoService.Indexes.NAME.getIndex()];
				environmentLabel.setHTML("&nbsp;&nbsp;on environment <b>" + envName + "</b>");
				environmentLabel.setVisible(true);
			}
			
			@Override
            public void onJemExecuted() {
				//do nothing...
            }
			
		});
		
		// for exception, sets empty string and red color, to outline error
		exceptionMessage.setText(" ");
		
		// handles when the ENTER is pressed to perform login 
		useridInputBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					loginButton.click();
				} else {
					clearExceptionMessage();
				}
			}
		});
			
		// handles when the ENTER is pressed to perform login 
		passwordInputBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					loginButton.click();
				} else {
					clearExceptionMessage();
				}
			}
		});

		loginButton.addClickHandler(new LoginButtonClickHandler());
		
		add(new HTML("Please insert your User ID and Password"));
		HTML userIdLabel = new HTML("User ID");
		userIdLabel.addStyleName(Styles.INSTANCE.common().bold());
		add(userIdLabel);
		add(useridInputBox);
		HTML passwordLabel = new HTML("Password");
		passwordLabel.addStyleName(Styles.INSTANCE.common().bold());
		add(passwordLabel);
		add(passwordInputBox);
		add(exceptionMessage);
		add(buttonAndEnvironment);
		setCellVerticalAlignment(buttonAndEnvironment, ALIGN_MIDDLE);
	}
	
	/**
	 * @return the listener
	 */
    public final LoggedListener getListener() {
	    return listener;
    }

	/**
	 * @param listener the listener to set
	 */
    public void setListener(LoggedListener listener) {
	    this.listener = listener;
    }

    /**
     * Checks if text of userid input field and password one are not empty.
     * Clear the exception message too.
     */
	private boolean checkUserIdAndPassword() {
		if (useridInputBox.getText().isEmpty() || passwordInputBox.getText().isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Clear the exception message
	 */
	private void clearExceptionMessage() {
		if (!exceptionMessage.getText().trim().isEmpty()) {
			exceptionMessage.setText(" ");
		}
	}
	
	private class LoginButtonClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			// check user and password fields
			if (!checkUserIdAndPassword()) {
				exceptionMessage.setText("Please insert User ID and Password");
				exceptionMessage.addStyleName(Styles.INSTANCE.common().red());
				return;
			}

			// show wait poup panel
			Loading.startProcessingNoDelay();

			Services.LOGIN_MANAGER.login(useridInputBox.getText(), passwordInputBox.getText(), new LoginAsyncCallback());
		}
	}
	
	private class LoginAsyncCallback extends ServiceAsyncCallback<LoggedUser> {
		
		@Override
		public void onJemFailure(Throwable caught) {
			// shows the exception
			exceptionMessage.setText(caught.getMessage());
			exceptionMessage.addStyleName(Styles.INSTANCE.common().red());
			passwordInputBox.setText("");
			useridInputBox.setSelectionRange(0, useridInputBox.getText().length());
		}

		@Override
		public void onJemSuccess(LoggedUser user) {
			if (user == null){
				new Toast(MessageLevel.ERROR, "JEM returns a user null.<br> Please have a look into JEM log file to find the internal error.", "User null!").show();
			} else {
				// calls listener the user is logged
				getListener().logged(user);
			}
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}

}