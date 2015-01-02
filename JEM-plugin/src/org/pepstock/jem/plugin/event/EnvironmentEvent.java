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

import java.util.EventObject;

import org.pepstock.jem.plugin.preferences.Coordinate;

/**
 * Represents event when environment updates will be generated.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class EnvironmentEvent extends EventObject {
	
    private static final long serialVersionUID = 1L;

    private Coordinate coordinate = null;

	/**
	 * Creates object with source and coordinate of updated environment 
	 * @param source source of event
	 * @param coordinate coordinate of updated environment
	 */
	public EnvironmentEvent(Object source, Coordinate coordinate) {
		super(source);
		this.coordinate = coordinate;
	}

	/**
	 * @return the coordinate
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "EnvironmentEvent [coordinate=" + coordinate + "]";
    }
}
