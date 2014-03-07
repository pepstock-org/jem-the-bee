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
package org.pepstock.jem.gwt.client.commons;

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Abstratc popup panel which represents a message box (like a javascript Window.alert).
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class MessageBox extends PopupPanel {

	
	static {
		Styles.INSTANCE.messageBox().ensureInjected();
	}
	
	private HideHandler hideHandler = null;

	/**
	 * A generic message box, like a javascript Window.alert
	 * 
	 * @param image image to show on west
	 * @param title title of message to show
	 * @param message content of message to show
	 * 
	 */
	public MessageBox(ImageResource image, String title, String message) {
		super(false, true);
		setAnimationEnabled(true);
		setGlassEnabled(true);

		setStyleName(Styles.INSTANCE.messageBox().main());
		
		// sets message
		final HTML msg = new HTML(message);
		msg.addStyleName(Styles.INSTANCE.messageBox().message());

		// sets Max Width!
		int maxWidth = Window.getClientWidth() * 75 / 100;
		getElement().getStyle().setPropertyPx("maxWidth", maxWidth);

		// dialog box content
		DockPanel dock = new DockPanel();
		dock.setSpacing(5);
		final HorizontalPanel flow = new HorizontalPanel();
		flow.setSpacing(4);
		// add buttons to box
		if (getPreferredButtons() != null) {
			for (int i = 0; i < getPreferredButtons().length; i++) {
				PreferredButton button = getPreferredButtons()[i];
				button.setMessageBox(this);
				flow.add(button);
			}
		}

		// sets default button
		final int defaultButtonIndex = flow.getWidgetIndex(getDefaultButton());
		((PreferredButton) flow.getWidget(defaultButtonIndex))
				.addStyleName(Styles.INSTANCE.common().defaultActionButton());

		dock.add(flow, DockPanel.SOUTH);
		dock.setCellHorizontalAlignment(flow, DockPanel.ALIGN_CENTER);

		// adds title
		if (title != null) {
			final Label t = new Label(title);
			t.addStyleName(Styles.INSTANCE.messageBox().title());
			dock.add(t, DockPanel.NORTH);
			dock.setCellHorizontalAlignment(t, DockPanel.ALIGN_CENTER);

		}
		
		dock.add(msg, DockPanel.CENTER);
		dock.setCellVerticalAlignment(msg, DockPanel.ALIGN_MIDDLE);
		
		// adds image
		dock.add(new Image((image == null) ? Images.INSTANCE.info() : image),
				DockPanel.WEST);

		dock.setWidth(Sizes.HUNDRED_PERCENT);
		setWidget(dock);

		// handle default button selection (deferred)
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				((PreferredButton) flow.getWidget(defaultButtonIndex)).setFocus(true);
			}
		});

	}

	/**
	 * Returns a list of buttons to add in the messgae box.
	 * @return a {@link PreferredButton}[]
	 */
	public abstract PreferredButton[] getPreferredButtons();

	/**
	 * Returns the default button.
	 * @return the {@link PreferredButton} which will be selected by default
	 */
	public abstract PreferredButton getDefaultButton();

	/**
	 * Gets hide handler
	 * @return the hideHandler
	 */
	public HideHandler getHideHandler() {
		return hideHandler;
	}

	/**
	 * Sets Hide handler
	 * @param hideHandler
	 *            the hideHandler to set
	 */
	public void setHideHandler(HideHandler hideHandler) {
		this.hideHandler = hideHandler;
	}

	/**
	 * Overridden so the Box is always centered
	 */
	public void open() {
		center();
	}

}