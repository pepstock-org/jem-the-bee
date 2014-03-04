/*******************************************************************************
 * Copyright (c) 2012-2013 pepstock.org.
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
 * Entity with all information to connet to JEM environment by REST.
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
	
	private String userid = null;
	
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
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
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
		Coordinate coordinate = new Coordinate();
		coordinate.setHost(host);
		coordinate.setName(name);
		coordinate.setUserid(userid);
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
	    return "Coordinate [host=" + host + ", name=" + name + ", userid=" + userid + ", restContext=" + restContext + ", isDefault=" + isDefault + "]";
    }

}
