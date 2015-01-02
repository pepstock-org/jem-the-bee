/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.preferences;

import java.io.Serializable;

/**
 * Entity with all information to connect to JEM environment by REST.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class Coordinate implements Serializable{
	
    private static final long serialVersionUID = 1L;

	/**
	 * Default REST web context
	 */
	public static final String DEFAULT_REST_WEB_CONTEXT = "rest";

	private String host = null;
	
	private String name = null;
	
	private String userId = null;
	
	private String password = null;
	
	private String restContext = DEFAULT_REST_WEB_CONTEXT; 

	private boolean isDefault = false;

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		return isDefault;
	}
	/**
	 * @param isDefault the isDefault to set
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	/**
	 * @return the restContext
	 */
	public String getRestContext() {
		return restContext;
	}
	/**
	 * @param restContext the restContext to set
	 */
	public void setRestContext(String restContext) {
		this.restContext = restContext;
	}
	
	/**
	 * Returns a clone of object
	 * @return a clone of object
	 */
	public Coordinate getClone(){
		// creates a new coordinate
		Coordinate coordinate = new Coordinate();
		// sets all attributes to new coordinate
		coordinate.setHost(host);
		coordinate.setName(name);
		coordinate.setUserId(userId);
		coordinate.setPassword(password);
		coordinate.setRestContext(restContext);
		coordinate.setDefault(isDefault);
		return coordinate;
	}
	
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "Coordinate [host=" + host + ", name=" + name + ", userid=" + userId + ", restContext=" + restContext + ", isDefault=" + isDefault + "]";
    }

}
