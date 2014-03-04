/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Cuc" Cuccato
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

import org.pepstock.jem.node.resources.Resource;

/**
 * Collect all {@link Resource} filterable fields
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 */

@SuppressWarnings("javadoc")
public enum ResourceFilterFields implements JemFilterFields<Resource> {
	
	NAME("name", "name"),
	TYPE("type", "type"),
	PROPERTIES("properties"),
	MODIFIED("modified", DURATION_PATTERN_DESCRIPTION),
	MODIFIED_BY("modifiedby");

	private String name = null;
	private String pattern = null;
	
	private ResourceFilterFields(String name) {
		this(name, null);
	}
	
	private ResourceFilterFields(String name, String pattern) {
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
	 * @param name the name of the {@link ResourceFilterFields}
	 * @return the {@link ResourceFilterFields} associated with provided name
	 */
	public static ResourceFilterFields getByName(String name) {
		for (ResourceFilterFields jff : values()) {
			if (jff.getName().equals(name)) {
				return jff;
			}
		}
		return null;
	}

}