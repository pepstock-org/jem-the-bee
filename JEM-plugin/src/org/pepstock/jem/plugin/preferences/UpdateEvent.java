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
package org.pepstock.jem.plugin.preferences;

/**
 * Event to wrap all updates of coordinates, in the preferences tables. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class UpdateEvent {

	/**
	 * ADD action
	 */
	public static final int ADD = 0;
	/**
	 * REMOVE action
	 */
	public static final int REMOVE = 1;
	/**
	 * UPATE action
	 */
	public static final int UPDATE = 2;
	
	private Coordinate coordinate = null;
	
	private int type = ADD;
	
	/**
	 * Creates event with coordinate and type
	 * @param coordinate coordinate updated
	 * @param type type of update
	 */
    public UpdateEvent(Coordinate coordinate, int type) {
	    super();
	    this.coordinate = coordinate;
	    this.type = type;
    }

	/**
	 * @return the coordinate
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "UpdateEvent [coordinate=" + coordinate + ", type=" + type + "]";
    }

	
}
