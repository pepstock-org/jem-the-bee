/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.Date;

/**
 * Is common class with name, last update time and user used for entities which can be updated.
 * 
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public abstract class UpdateableItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name = null;

	private String user = null;
	
	private Date lastModified = null;

	/**
	 * Empty constructor
	 */
	public UpdateableItem() {
	}

	/**
	 * Returns the name (key) for this resource
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name (key) for this resource
	 * 
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the user
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public final void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the lastModified
	 */
	public final Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public final void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}