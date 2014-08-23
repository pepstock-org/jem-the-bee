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
package org.pepstock.jem.node.security;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.pepstock.jem.rest.maps.UserPreferencesMapAdapter;


/**
 * Entity class which represents the current logged user. It contains all authorizations
 * to use inside the application. This object is created server side.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
// uses Accessory Type to avoid to have REST error serializing upser preferences
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement
public class LoggedUser extends Subject {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * (none) 
	 */
	private static final String NONE_BRACKETS = "(none)";
	
	private Map<String, Boolean> authorized = new HashMap<String, Boolean>();
	
	// PAY ATTENTION: HashMap are not supported by REST. For this reason there is a specific adapter
	@XmlJavaTypeAdapter(UserPreferencesMapAdapter.class)
	private Map<String, UserPreference> preferences = new HashMap<String, UserPreference>();
	
	private OrganizationalUnit organizationalUnit = null;

	/**
	 * Constructs a empty object
	 */
	public LoggedUser() {
		super();
	}
	
	/**
	 * Returns the organizational unit of user 
	 * @return the group
	 */
	public OrganizationalUnit getOrganizationalUnit() {
		return organizationalUnit;
	}

	/**
	 * Sets the organizational unit of user
	 * @param group the group to set
	 */
	public void setOrganizationalUnit(OrganizationalUnit group) {
		this.organizationalUnit = group;
	}
	
    
	/**
	 * @param key 
	 * @param value 
	 */
	public void addAuthorized(String key, boolean value) {
		this.authorized.put(key, value);
	}	
	
	/**
	 * @return the authorized
	 */
	public Map<String, Boolean> getAuthorized() {
		return authorized;
	}

	/**
	 * @param authorized the authorized to set
	 */
	public void setAuthorized(Map<String, Boolean> authorized) {
		this.authorized = authorized;
	}	

	/**
	 * @param key
	 * @return
	 */
	public Boolean isAuthorized(String key) {
		Boolean result = authorized.get(key);
		return result == null ? Boolean.FALSE : result;
	}
	
	/**
	 * @return the preferences
	 */
	public Map<String, UserPreference> getPreferences() {
		return preferences;
	}

	/**
	 * @param preferences the preferences to set
	 */
	public void setPreferences(Map<String, UserPreference> preferences) {
		this.preferences = preferences;
	}

	/**
	 * Returns a strign representation of object
	 */
	@Override
	public String toString() {
		return "Id=" + getId() + "; OrgUnit=" + (getOrganizationalUnit() != null ? getOrganizationalUnit().getId() : NONE_BRACKETS) + "; FullName=" + getName(); 
	}
}