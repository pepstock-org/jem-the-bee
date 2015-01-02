/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.security;

import java.io.Serializable;

/**
 * Abstract entity object used for user and organizational unit entities.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// if the obj passed as argument if null
		// return false
		if (obj == null){
			return false;
		}
		// checks if is a subject
		if (obj instanceof Subject) {
			Subject subject = (Subject) obj;
			// if subject, checks the ID
			return getId().equalsIgnoreCase(subject.getId());
		}
		// otherwise compare the string of tostring method
		return obj.toString().equalsIgnoreCase(toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// uses the id has hashcode
		if (id != null){
			return id.hashCode(); 
		} else { 
			return super.hashCode();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		// to sort, uses the user id by default
		if (o instanceof Subject) {
			Subject subject = (Subject) o;
			return getId().compareTo(subject.getId());
		} else {
			// otherwise always less than 0
			return -1;
		}
	}

	/**
	 * String representation of object
	 */
	@Override
	public String toString() {
		return "[id:" + id + ", name:" + name + "]";
	}
}