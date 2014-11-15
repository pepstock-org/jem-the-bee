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
package org.pepstock.jem.gwt.client;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.SharedObjects;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.services.InfoService;
import org.pepstock.jem.gwt.client.services.Services;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * This class manages the logo for specific company.<br>
 * It calls a RPC service to have the LOGO and LINK to put here.
 * If they're missing, a default logo and link is set.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class LoginImager extends AbsolutePanel {
	
	static {
		Styles.INSTANCE.loginBox().ensureInjected();
	}
	
	/**
	 * Default logo image
	 */
	private static final String DEFAULT_LOGO_FILE = "logo.png";
	
	/**
	 * Default link
	 */
	private static final String DEFAULT_LOGO_LINK = "http://www.pepstock.org";
	
	private final HTML license = new HTML(SharedObjects.LICENSE);
	
	private Image logo = new Image();
	
	private String link = null;
	
	/**
	 * 
	 */
	public LoginImager() {
		license.addStyleName(Styles.INSTANCE.loginBox().license());
		// add style to change cursor
		logo.addStyleName(Styles.INSTANCE.loginBox().logo());

		// add click handle to open in a new window
		logo.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (link == null){
					link = DEFAULT_LOGO_LINK;
				}
				Window.open(link, null, null);
			}
		});
		
		// catch errors (mainly 404) setting in this case the default image and link
		logo.addErrorHandler(new ErrorHandler() {
			
			@Override
			public void onError(ErrorEvent event) {
				logo.setUrl(Images.INSTANCE.pepstock().getSafeUri());
				link = DEFAULT_LOGO_LINK;
			}
		});

		// adds images and set the position of logo
		ImageResource jem = Images.INSTANCE.logoForLogin();
		add(new Image(jem));
		add(logo);
		add(license);
		setWidgetPosition(logo, 60, 0);

		
		// calls RPC servccie to have URL and link of logo
		Services.INFO_SERVICE.getLogoURL(new LogoURLAsyncCallback());
	}
	
	private class LogoURLAsyncCallback extends ServiceAsyncCallback<String[]> {
		@Override
		public void onJemSuccess(String[] result) {
			// if result is empty, it uses the default logo file
			if (result.length == 0){
				logo.setUrl(DEFAULT_LOGO_FILE);
			} else {
				logo.setUrl(result[InfoService.Indexes.URL.getIndex()]);
				link = result[InfoService.Indexes.LINK.getIndex()];
			}
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			// when failure occurs, it uses the default LOGO
			logo.setUrl(Images.INSTANCE.pepstock().getSafeUri());
			link = DEFAULT_LOGO_LINK;
		}

		@Override
        public void onJemExecuted() {
			// do nothing
        }
		
	}
}