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

import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pepstock.jem.util.InternalAbstractPredicate;

/**
 * Is a custom predicate (used by Hazelcast to filter object from maps) to extract from roles queue only roles which matches with user or orgUnit.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class RolesQueuePredicate extends InternalAbstractPredicate<User> {

	private static final long serialVersionUID = 1L;

	/**
	 * Checks the job passed by Hazelcast matches with Execution Environment of node. <br>
	 * This method is called on all nodes of cluster, where a piece of map is present
	 * 
	 * @see com.hazelcast.query.Predicate#apply(com.hazelcast.core.MapEntry)
	 */
	@Override
	public boolean apply(@SuppressWarnings("rawtypes") Entry arg0) {
		User user = getObject();
		// gets role instance
		Role role = (Role) arg0.getValue();
		// gets all users list
		List<String> users = role.getUsers();
		for (String userRegEx: users){
			Pattern pattern = Pattern.compile(userRegEx, Pattern.CASE_INSENSITIVE);
			// if matches with user id return true!
			// otherwise checks if matches with orgUnit id
			Matcher matcher = pattern.matcher(user.getId());
			
			if (matcher.matches()){
				return true;
			} else if (user.getOrgUnitId() != null){
				// checks if matches with orgUnit if not null
				Matcher matcherOu = pattern.matcher(user.getOrgUnitId());
				if (matcherOu.matches()){
					return true;
				}
			}
		}
		return false;
	}
}