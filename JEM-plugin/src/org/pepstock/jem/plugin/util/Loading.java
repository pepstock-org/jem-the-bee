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
package org.pepstock.jem.plugin.util;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Abstract implementation to show busy indicator during the search and calling JEM REST services.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class Loading {
	
	/**
	 * Executes the loading 
	 */
	public final void run(){
		// shows busy indicator
		BusyIndicator.showWhile(getDisplay(), new Runnable() {

			@Override
			public void run() {
				try {
					// executes REST service
					execute();	
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// nop
				} finally {
					// nop
				}
			}
		});
	}

	/**
	 * Returns the display component
	 * @return the display component
	 */
	public abstract Display getDisplay();
	
	/**
	 * Executes all logic, calling REST services 
	 * @throws JemException if any error occurs
	 */
	protected abstract void execute() throws Exception;

}
