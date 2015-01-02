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
 * Interface of environment listeners which will be engaged when a connection or a disconnection will occur.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface EnvironmentEventListener {
	
	/**
	 * Called when the plugin will connect to JEM
	 * 
	 * @param event event with coordinate 
	 */
	void environmentConnected(EnvironmentEvent event);

	/**
	 * Called when the plugin will disconnect to JEM
	 * 
	 * @param event event with coordinate 
	 */
	void environmentDisconnected(EnvironmentEvent event);

}
