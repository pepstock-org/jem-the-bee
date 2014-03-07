/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Cuc" Cuccato
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
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.util.TimeUtils;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.RoleFilterFields;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

/**
 * The {@link Predicate} of a {@link Role}
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 */
public class RolePredicate extends JemFilterPredicate<Role> implements Serializable {

	private static final long serialVersionUID = -1410063063130458355L;

	/**
	 * Empty contructor
	 */
	public RolePredicate() {
	}
	
	/**
	 * @see JemFilterPredicate
	 * @param filter 
	 */
	public RolePredicate(Filter filter) {
		super(filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(MapEntry entry) {
		Role role = (Role)entry.getValue();
		boolean includeThis = true;
		FilterToken[] tokens = getFilter().toTokenArray();
		for (int i=0; i<tokens.length && includeThis; i++) {
			FilterToken token = tokens[i];
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			RoleFilterFields field = RoleFilterFields.getByName(tokenName);
			if (field == null) {
				field = RoleFilterFields.NAME;
			}
			
			switch (field) {
			case NAME:
				includeThis &= checkName(tokenValue, role);
				break;
			case REMOVABLE:
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
				includeThis &= checkTime(tokenValue, role.getLastModified());
				break;
			case MODIFIED_BY:
				includeThis &= StringUtils.containsIgnoreCase(role.getUser(), tokenValue);
				break;
			default:
				throw new JemRuntimeException("Unrecognized Role filter field: " + field);
			}
		}
		return includeThis;
	}
	
	/**
	 * Checks the filter name
	 * @param tokenValue filter passed
	 * @param job job instance
	 * @return true if matches
	 */
	private boolean checkName(String tokenValue, Role role){
		// is able to manage for label the * wildcard
		if ("*".equalsIgnoreCase(tokenValue)) {
			return true;
		} else {
			String newTokenValue = tokenValue;
			if (tokenValue.endsWith("*")){
				newTokenValue = StringUtils.substringBeforeLast(tokenValue, "*");
			}
			return StringUtils.containsIgnoreCase(role.getName(), newTokenValue);
		}		
	}
	
	/**
	 * Checks date of role update
	 * @param time date of role
	 * @param tokenValue filter to check
	 * @return true if matches
	 */
	private boolean checkTime(String tokenValue, Date time){
		long now = System.currentTimeMillis();
		try {
			// parse the date value based on pattern
			long inputTime = TimeUtils.parseDuration(tokenValue);
			long jobTime = now-time.getTime();
			return jobTime <= inputTime;
		} catch (Exception e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// cannot parse the date, exclude this entry by default!
			return false;
		}		
	}
}