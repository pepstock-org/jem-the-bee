/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.event;

/**
 * Interface of environment listeners which will be engaged when environment coordinate will be added, removed or updated. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface PreferencesEnvironmentEventListener {
	
	/**
	 * Called when the plugin will add a new environment coordinate
	 * 
	 * @param event event with coordinate 
	 */
	void environmentAdded(EnvironmentEvent event);
	
	/**
	 * Called when the plugin will remvoe a new environment coordinate
	 * 
	 * @param event event with coordinate 
	 */
	void environmentRemoved(EnvironmentEvent event);
	
	/**
	 * Called when the plugin will update a new environment coordinate
	 * 
	 * @param event event with coordinate 
	 */
	void environmentUpdated(EnvironmentEvent event);
}
