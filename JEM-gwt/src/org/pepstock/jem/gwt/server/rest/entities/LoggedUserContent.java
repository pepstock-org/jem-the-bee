/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.server.rest.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.gwt.client.security.LoggedUser;

/**
 * Represents a wrapper to LoggedUser, necessary for REST
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@XmlRootElement
public class LoggedUserContent extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private LoggedUser loggedUser = null;

	/**
	 * Empty constructor
	 */
	public LoggedUserContent() {
	}

	/**
	 * Returns logged user
	 * 
	 * @return the loggedUser
	 */
	public LoggedUser getLoggedUser() {
		return loggedUser;
	}

	/**
	 * Sets logged user
	 * 
	 * @param loggedUser the loggedUser to set
	 */
	public void setLoggedUser(LoggedUser loggedUser) {
		this.loggedUser = loggedUser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "LoggedUserContent [loggedUser=" + loggedUser + "]";
    }
}