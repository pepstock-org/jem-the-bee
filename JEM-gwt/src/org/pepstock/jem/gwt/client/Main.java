/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.gwt.client.commons.LoggedListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.i18n.I18N;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.node.security.LoggedUser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main JEM web application. It constructs all necessary objects.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Main implements EntryPoint, LoggedListener {

	// div to use for loading before to have login page
	private static final String LOADING_PURE_HTML_DIV_ID = "jemLoading";
	
	static {
		// separator
		GWT.create(I18N.class);
		
		// common styles
		Styles.INSTANCE.overrideDefaultTheme().ensureInjected();
		Styles.INSTANCE.common().ensureInjected();
	}
	
	// root hml panel
	private static final RootLayoutPanel ROOT_LAYOUT_PANEL = RootLayoutPanel.get();
	static {
		ROOT_LAYOUT_PANEL.getElement().setId("htmlPanel");
	}
	
	private static final VerticalPanel ROOT_TABLE_CENTER_PANEL = new VerticalPanel();
	static {
		ROOT_TABLE_CENTER_PANEL.getElement().setId("rootPanel");
		ROOT_TABLE_CENTER_PANEL.addStyleName(Styles.INSTANCE.common().autoMargin());
		ROOT_TABLE_CENTER_PANEL.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		ROOT_TABLE_CENTER_PANEL.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		ROOT_TABLE_CENTER_PANEL.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		// remove the static loading pure HTML panel
		DivElement jemLoading = DivElement.as(Document.get().getElementById(LOADING_PURE_HTML_DIV_ID));
		jemLoading.removeFromParent();
		
		// add the non pure HTML panel
		ROOT_LAYOUT_PANEL.add(ROOT_TABLE_CENTER_PANEL);
	}
	
	// login panel
	private static final HorizontalPanel LOGIN_APPLICATION_PANEL = new HorizontalPanel();
	static {
		LOGIN_APPLICATION_PANEL.getElement().setId("loginApplicationPanel");
		LOGIN_APPLICATION_PANEL.setSpacing(20);
		LOGIN_APPLICATION_PANEL.setVisible(false);
		LOGIN_APPLICATION_PANEL.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		LOGIN_APPLICATION_PANEL.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		LOGIN_APPLICATION_PANEL.add(new LoginImager());
	}
	
	// home (main) panel
	private MainVerticalPanel homeApplicationPanel = null;
	// the login box
	private final LoginBox loginBox = new LoginBox();
	
	/**
	 * @see EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {
		// setup login listener
		loginBox.setListener(this);
		LOGIN_APPLICATION_PANEL.add(loginBox);

		// login handle
		Services.LOGIN_MANAGER.getUser(new UserAsynchCallback());
	}

	private void showLogin() {
		if (ROOT_TABLE_CENTER_PANEL.getWidgetIndex(homeApplicationPanel) > -1) {
			ROOT_TABLE_CENTER_PANEL.remove(homeApplicationPanel);
		}
		if (ROOT_TABLE_CENTER_PANEL.getWidgetIndex(LOGIN_APPLICATION_PANEL) < 0) {
			ROOT_TABLE_CENTER_PANEL.add(LOGIN_APPLICATION_PANEL);
		}
		LOGIN_APPLICATION_PANEL.setVisible(true);
	}
	
	private void showHome() {
		if (ROOT_TABLE_CENTER_PANEL.getWidgetIndex(LOGIN_APPLICATION_PANEL) > -1) {
			ROOT_TABLE_CENTER_PANEL.remove(LOGIN_APPLICATION_PANEL);
		}
		if (ROOT_TABLE_CENTER_PANEL.getWidgetIndex(homeApplicationPanel) < 0) {
			ROOT_TABLE_CENTER_PANEL.add(homeApplicationPanel);
			homeApplicationPanel.onResize();
			
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					homeApplicationPanel.onResize(event.getWidth(), event.getHeight());
				}
			});
		}
		LOGIN_APPLICATION_PANEL.setVisible(false);
	}

	/**
	 * Set the logged user and show home panel
	 * @param user the logged user
	 */
	@Override
	public void logged(LoggedUser user) {
		// saves the user on common instance, usable everywhere
		CurrentUser.getInstance().setUser(user);
		
		homeApplicationPanel = new MainVerticalPanel();
		homeApplicationPanel.getElement().setId("homeApplicationPanel");
		homeApplicationPanel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);

		
		// remove login one and add application!
		showHome();
	}

	private class UserAsynchCallback extends ServiceAsyncCallback<LoggedUser> {
		/**
		 * Set the correct panel to show if the user is null (login panel) or if it exists (home panel)
		 * @param user
		 */
		@Override
		public void onJemSuccess(LoggedUser user) {
			// if don't receive the user, means that it's not autheticated so shows login panel
			if (user == null) {
				showLogin();
			// otherwhise user is already logged
			} else {
				logged(user);
			}
		}

		/**
		 * Show an error message
		 */
		@Override
		public void onJemFailure(Throwable caught) {
			// if we have an error to ask the logged user we're not able to go ahead, so login panel is removed (not necessary) and show the error panel
			final LoginErrorBox loginErrorBox = new LoginErrorBox();
			LOGIN_APPLICATION_PANEL.remove(loginBox);
			LOGIN_APPLICATION_PANEL.add(loginErrorBox);
			// this check because if we have a error on servlet loading, the message is formatted by servlet container and we don't like it.
			// If runtimeException, is JEM exception
			if (caught instanceof RuntimeException) {
				loginErrorBox.setException(caught.getMessage());
			} else {
				loginErrorBox.setException("Unknown exception: " + caught.getMessage());
			}
			showLogin();
		}

		@Override
        public void onJemExecuted() {
			// do nothing
        }
	}
	
	
}
