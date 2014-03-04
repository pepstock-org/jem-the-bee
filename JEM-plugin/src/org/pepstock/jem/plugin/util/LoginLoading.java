/*******************************************************************************
 * Copyright (c) 2012-2014 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.util;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.preferences.Coordinate;

/**
 * Extends loading and it is used when the view part must connect JEM environment.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class LoginLoading extends ShellLoading {

	private Coordinate coordinate = null;

	/**
	 * Creates the object with coordinate to use to login
	 * @param shell Eclipse shell
	 * @param coordinate coordinate of JEM
	 */
    public LoginLoading(Shell shell, Coordinate coordinate) {
	    super(shell);
	    this.coordinate = coordinate;
    }

	/**
	 * @return the coordinate
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}
}
