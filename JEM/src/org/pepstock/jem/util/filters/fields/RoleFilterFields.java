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
package org.pepstock.jem.util.filters.fields;

import org.pepstock.jem.node.security.Role;

/**
 * Collect all {@link Role} filterable fields
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 */

@SuppressWarnings("javadoc")
public enum RoleFilterFields implements JemFilterFields<Role> {
	
	NAME("name"),
	REMOVABLE("removable"),
	PERMISSIONS("permissions"),
	USERS("users"),
	MODIFIED("modified", DURATION_PATTERN_DESCRIPTION),
	MODIFIED_BY("modifiedby");

	private String name = null;
	private String pattern = null;
	
	private RoleFilterFields(String name) {
		this(name, null);
	}
	
	private RoleFilterFields(String name, String pattern) {
		this.name = name;
		this.pattern = pattern;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasPattern() {
		return pattern != null;
	}
	
	@Override
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param name the name of the {@link Role}
	 * @return the {@link Role} associated with provided name
	 */
	public static RoleFilterFields getByName(String name) {
		for (RoleFilterFields jff : values()) {
			if (jff.getName().equals(name)) {
				return jff;
			}
		}
		return null;
	}

}