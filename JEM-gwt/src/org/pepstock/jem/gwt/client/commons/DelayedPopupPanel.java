/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This is a popup panel with a {@link Timer} that should be used to manage it's visualization
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class DelayedPopupPanel extends PopupPanel {

	/**
	 * No delay
	 */
	public static final int NO_DELAY = -1;

	private final int defaultDelay;
	
	private int delay;
	
	private Timer timer;
	
	/**
	 * Builds a delayed popup panel
	 * @param autoHide <code>true</code> if the popup should be automatically hidden when the user clicks outside of it
	 * @param defaultDelay the default delay of this panel, in milliseconds
	 */
	public DelayedPopupPanel(boolean autoHide, final int defaultDelay) {
		this(autoHide, false, defaultDelay);
	}
	
	/**
	 * Builds a delayed popup panel 
	 * @param autoHide <code>true</code> if the popup should be automatically hidden when the user clicks outside of it
	 * @param modal if <code>true</code>, the keyboard and mouse event that do not target the DelayedPopupPanel will be ignored
	 * @param defaultDelay the default delay of this panel, in milliseconds
	 */
	public DelayedPopupPanel(boolean autoHide, boolean modal, final int defaultDelay) {
		super(autoHide, modal);
		this.defaultDelay = defaultDelay;
		this.delay = this.defaultDelay;
	}

	/**
	 * Returns the delay to appear 
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Sets the delay to appear
	 * @param delay the delay to set
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * @return a {@link Timer} object
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * Set the timer object 
	 * @param timer
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	/**
	 * @return the default delay interval, provided in contructor
	 */
	public int getDefaultDelay() {
		return defaultDelay;
	}
	
	
}
