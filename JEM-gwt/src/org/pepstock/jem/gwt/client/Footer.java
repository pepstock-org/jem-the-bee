/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.gwt.client.about.AboutPanel;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.SharedObjects;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.notify.NotifyPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.About;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * This is the footer element of Jem home screen
 * @author Marco "Fuzzo" Cuccato
 */
public class Footer extends HorizontalPanel implements ResizeCapable{

	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	
	private final HTML license = new HTML(SharedObjects.LICENSE);

	// 16px of images plus 6 for padding
	private static final int NOTIFY_IMAGE_SIZE = 22; 
	
	/**
	 *
	 */
	public Footer() {
		license.addStyleName(Styles.INSTANCE.common().footer());
		license.addClickHandler(new FooterClickHandler());
		
		Image image = new Image(Images.INSTANCE.notifySmall());
		image.setTitle("Messages history");
		image.addStyleName(Styles.INSTANCE.common().notifyImage());
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// sets data to table to show it
				NotifyPanel p = new NotifyPanel();
				p.center();
			}
		});
		
		add(license);
		add(image);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
	   license.setSize(Sizes.toString(availableWidth - NOTIFY_IMAGE_SIZE), Sizes.toString(availableHeight));
    }

    private static class FooterClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
		    Scheduler scheduler = Scheduler.get();
		    scheduler.scheduleDeferred(new AboutCommand());
		}
		
		private static class AboutCommand implements ScheduledCommand {

			@Override
			public void execute() {
				Services.INFO_SERVICE.getAbout(new ServiceAsyncCallback<About>() {

					@Override
					public void onJemFailure(Throwable caught) {
						new Toast(MessageLevel.ERROR, caught.getMessage(), "Search error!").show();
					}

					@Override
					public void onJemSuccess(About result) {
						// sets data to table to show it
						AboutPanel p = new AboutPanel(result);
						p.center();
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