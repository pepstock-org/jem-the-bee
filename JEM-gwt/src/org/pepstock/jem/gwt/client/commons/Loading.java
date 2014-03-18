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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;

/**
 * It's the "loading" popup panel which is showing a animated gif.
 *     
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class Loading {

	/**
	 * No delay
	 */
	public static final int NO_DELAY = -1;

	static {
		Styles.INSTANCE.loading().ensureInjected();
	}

	private static final DelayedPopupPanel POPUP = new DelayedPopupPanel(true, 100) {
		// no code
	};
	
	static {
		POPUP.setGlassEnabled(true);
		POPUP.setAnimationEnabled(false);
		// animated image
		final Image ajaxImage = new Image(Images.INSTANCE.loading());
		final Grid grid = new Grid(1, 2);
		grid.setWidget(0, 0, ajaxImage);
		grid.setText(0, 1, "Loading...");
		final FlowPanel container = new FlowPanel();
		container.add(grid);
		POPUP.add(container);
	}

	/**
	 * To avoid any instantiation
	 */
	private Loading() {
	}
	
	/**
	 * Called when the remote call is ended. Sets show to false, because if the timer is still running,
	 * timer doesn't show the panel. 
	 */
	public static synchronized void stopProcessing() {
		if (POPUP.getDelay() != DelayedPopupPanel.NO_DELAY && POPUP.getTimer() != null){
			POPUP.getTimer().cancel();
			POPUP.setTimer(null);
		}
		POPUP.hide();
	}

	/**
	 * Called before the remote call starts. Start a timer to avoid to show itself for quick requests.
	 */
	public static synchronized void startProcessing() {
		if (POPUP.getDelay() != DelayedPopupPanel.NO_DELAY){
			// fixes 5 seconds to wait
			POPUP.setTimer(new Timer() {
				@Override
				public void run() {
					POPUP.center();
				}
			});		
			POPUP.getTimer().schedule(POPUP.getDelay());
		} else {
			POPUP.center();			
		}
	}
	
	/**
	 * Called before the remote call starts. Start a timer to avoid to show itself for quick requests.
	 * @param customDelay overrides the default delay for quick requests
	 */
	public static synchronized void startProcessing(int customDelay) {
		POPUP.setDelay(customDelay);
		startProcessing();
		POPUP.setDelay(POPUP.getDefaultDelay());
	}

	/**
	 * Called before the remote call starts, without delayed start
	 */
	public static synchronized void startProcessingNoDelay() {
		startProcessing(DelayedPopupPanel.NO_DELAY);
	}
	
}