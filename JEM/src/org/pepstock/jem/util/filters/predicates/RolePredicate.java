/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.util.filters.predicates;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.RoleFilterFields;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

/**
 * This predicate is used to filter the roles to extract distributing all searches on all nodes of JEM.
 * <br> 
 * The {@link Predicate} of a {@link Role}
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public class RolePredicate extends JemFilterPredicate<Role> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty contructor
	 */
	public RolePredicate() {
	}
	
	/**
	 * Constructs the object saving the filter to use to extract the roles
	 * from Hazelcast map
	 *  
	 * @see JemFilterPredicate
	 * @param filter String filter 
	 */
	public RolePredicate(Filter filter) {
		super(filter);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.predicates.JemFilterPredicate#apply(com.hazelcast.core.MapEntry)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(MapEntry entry) {
		// casts the object to a Role 
		Role role = (Role)entry.getValue();
		boolean includeThis = true;
		// gets all tokens of filter
		FilterToken[] tokens = getFilter().toTokenArray();
		// scans all tokens
		for (int i=0; i<tokens.length && includeThis; i++) {
			FilterToken token = tokens[i];
			// gets name and value
			// remember that filters are built:
			// -[name] [value]
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			// gets the filter field for roles by name
			RoleFilterFields field = RoleFilterFields.getByName(tokenName);
			// if field is not present,
			// used NAME as default
			if (field == null) {
				field = RoleFilterFields.NAME;
			}
			
			// based on name of field, it will check
			// different attributes 
			// all matches are in AND
			switch (field) {
			case NAME:
				// checks name of ROLE
				includeThis &= checkName(tokenValue, role.getName());
				break;
			case REMOVABLE:
				// checks removable attribute of ROLE
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(role.isRemovable()), tokenValue);
				break;
			case PERMISSIONS:
				// skipped permission isEmpty check
				includeThis &= StringUtils.containsIgnoreCase(role.getPermissions().toString(), tokenValue);
				break;
			case USERS:
				// skipped users isEmpty check
				includeThis &= StringUtils.containsIgnoreCase(role.getUsers().toString(), tokenValue);
				break;
			case MODIFIED:
				// checks modified time of ROLE
				includeThis &= checkTime(tokenValue, role.getLastModified());
				break;
			case MODIFIED_BY:
				// checks who changed the role
				includeThis &= StringUtils.containsIgnoreCase(role.getUser(), tokenValue);
				break;
			default:
				// otherwise it uses a wrong filter name
				throw new JemRuntimeException("Unrecognized Role filter field: " + field);
			}
		}
		return includeThis;
	}
}