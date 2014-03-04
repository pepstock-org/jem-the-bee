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
package org.pepstock.jem.gwt.client.security;

import java.io.Serializable;

/**
 * Abstract entity object used for user and ou entities.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class Subject implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;

	private String id = null;
	private String name = null;

	/**
	 * Empty constructor
	 */
	public Subject() {

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * String representation of object
	 */
	@Override
	public String toString() {
		return "[id:" + id + ", name:" + name + "]";
	}


	/**
	 * Checks if the IDs are equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		if (obj instanceof Subject) {
			Subject subject = (Subject) obj;
			return getId().equalsIgnoreCase(subject.getId());
		}
		return obj.toString().equalsIgnoreCase(toString());
	}

	@Override
	public int hashCode() {
		if (id != null){
			return id.hashCode(); 
		} else { 
			return super.hashCode();
		}
	}
	/**
	 * Compare method to sort by ID eventually
	 */
	@Override
	public int compareTo(Object obj) {
		if (obj instanceof Subject) {
			Subject subject = (Subject) obj;
			return getId().compareTo(subject.getId());
		} else {
			return -1;
		}
	}
}