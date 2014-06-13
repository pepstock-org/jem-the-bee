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

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.notify.MessagesCollection;
import org.pepstock.jem.gwt.client.notify.ToastMessage;
import org.pepstock.jem.log.MessageLevel;

import com.google.common.base.Objects;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;



/**
 * An android-style Toast message
 * @author Andrea "Stock" Stocchero
 * @author Marco "Fuzzo" Cuccato
 */
public class Toast extends PopupPanel {
	
	static {
		Styles.INSTANCE.toast().ensureInjected();
	}
	
	private static final int SEPARATOR = 10;
	
	private static final List<Toast> ACTIVE_TOASTS = new LinkedList<Toast>();
	
	private String message;
	
	private String title;
	
	private MessageLevel level;
	
	private boolean timerStopped = false;
	
	/**
	 * @param level {@link MessageLevel}
	 * @param message the message description
	 * @param title the message box title
	 */
	public Toast(MessageLevel level, String message, String title) {
		this.level = level;
		this.message = message;
		this.title = title;
		// build the panel
		build();
		// set the style
		setStyle();
		// add this toast to message collection
		addToMessageCollection();
	}

	protected void build() {
		setStyleName(Styles.INSTANCE.toast().main());
		final VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(2);

		if (title != null) {
			final Label t = new Label(title);
			t.addStyleName(Styles.INSTANCE.toast().title());
			panel.add(t);
		}

		if (message != null) {
			final HTML m = new HTML(message);
			m.addStyleName(Styles.INSTANCE.toast().message());
			panel.add(m);
		}
		int maxWidth = Window.getClientWidth() * 75 / 100;
		getElement().getStyle().setPropertyPx("maxWidth", maxWidth);

 		setWidget(panel);
	}
	
	protected void setStyle() {
		String theStyle;
		switch (level) {
		case WARNING:
			theStyle = Styles.INSTANCE.toast().yellow();
			break;
		case ERROR:
			theStyle = Styles.INSTANCE.toast().red();
			break;
		default:
			theStyle = Styles.INSTANCE.toast().lightGreen();
			break;
		}
		addStyleName(theStyle);
	}
	
	protected void addToMessageCollection() {
		ToastMessage tMessage = new ToastMessage();
		tMessage.setLevel(level);
		tMessage.setMessage(message);
		tMessage.setTitle(title);
		MessagesCollection.add(tMessage);
	}
	
	protected static synchronized int getAvailableTop(Toast t) {
		int lastTop = 0;
		if (ACTIVE_TOASTS.isEmpty()) {
			lastTop = Sizes.HEADER + (Sizes.TABBAR_HEIGHT_PX + Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT) /2 - t.getOffsetHeight();
		} else {
			Toast last = ((LinkedList<Toast>) ACTIVE_TOASTS).getLast();
			lastTop = last.getAbsoluteTop() + last.getOffsetHeight() + SEPARATOR;  
		}
		ACTIVE_TOASTS.add(t);
		return lastTop;
	}
	
	protected static synchronized void onToastHide(Toast t) {
		ACTIVE_TOASTS.remove(t);
	}


	@Override
	public final void show() {
		if (ACTIVE_TOASTS.contains(this)) {
			System.out.println("An identic Toast is already showed, aborting...");
			return;
		}
		setVisible(false);
		super.show();
		// needs deferred task 
		// otherwsie browser don't apply styles
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ToastShower());
	}
	
	private class ToastShower implements ScheduledCommand {
		@Override
        public void execute() {
			// position calculation
			int left = (Window.getClientWidth() - getOffsetWidth())/2;
			int top = getAvailableTop(Toast.this); 
			
			setPopupPosition(left, top);
			setVisible(true);

			Timer t = new Timer() {
				@Override
				public void run() {
					if (!timerStopped){
						Toast.this.hide();
					}
				}
			};

			// Schedule the timer to close the popup in 3 seconds.
			t.schedule(3000);
        }
	}
	
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#hide()
	 */
    @Override
    public void hide() {
	    super.hide();
	    onToastHide(Toast.this);
    }

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
			onKeyDown(event);
			break;
		case Event.ONMOUSEOVER:
			onMouseOver(event);
			break;
		case Event.ONMOUSEOUT:
			onMouseOut(event);
			break;
		default:
			break;
		}
	}
	
	private void onKeyDown(NativePreviewEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE){
			hide();
		}
	}
	
	private void onMouseOut(NativePreviewEvent event) {
		if (isShowing() && timerStopped) {
			boolean insidePopup = Sizes.isEventInsideWidget(event.getNativeEvent(), this);
			// if inside of popup, ignore the event (generates by label of message and title)
			if (!insidePopup){
				hide();
			}
		}
	}
	
	private void onMouseOver(NativePreviewEvent event) {
		if (isShowing()) {
			boolean insidePopup = Sizes.isEventInsideWidget(event.getNativeEvent(), this);
			if (insidePopup){
				timerStopped = true;
			}
		}
	}

	@Override
	public String toString() {
		return "Toast [message=" + message + ", title=" + title + ", level="
				+ level + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Toast) {
			return equals((Toast)obj);
		}
		return false;
	}
	
	/**
	 * Determine if this {@link Toast} is equal to another one
	 * @param other the other {@link Toast}
	 * @return <code>true</code> if and only if level, message and title of both Toast are equal
	 */
	public boolean equals(Toast other) {
		return Objects.equal(level, other.level) && 
			Objects.equal(message, other.message) &&
			Objects.equal(title, other.title);
	}
	
}
