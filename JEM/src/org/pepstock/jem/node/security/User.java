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
package org.pepstock.jem.node.security;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.authz.Permission;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id = null;
	
	private String name = null;
	
	private String orgUnitId = null;
	
	private String orgUnitName = null;
	
	private Collection<PrincipalAttribute> attributes = null;
	
	private Collection<Permission> permissions = null;

	/**
	 * @param id 
	 * 
	 */
	public User(String id) {
		this.id = id;
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
	 * @return the organizationalUnitId
	 */
	public String getOrgUnitId() {
		return orgUnitId;
	}

	/**
	 * @param orgUnitId the organizationalUnitId to set
	 */
	public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
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
	 * @return the orgUnitName
	 */
	public String getOrgUnitName() {
		return orgUnitName;
	}

	/**
	 * @param orgUnitName the orgUnitName to set
	 */
	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}

	/**
	 * @return the attributes
	 */
	public Collection<PrincipalAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Collection<PrincipalAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the permissions
	 */
	public Collection<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Collection<Permission> permissions) {
		this.permissions = permissions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + id + "]";
	}

}