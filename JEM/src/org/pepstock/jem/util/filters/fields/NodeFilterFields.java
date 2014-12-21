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

import org.pepstock.jem.NodeInfoBean;

/**
 * Collect all {@link NodeInfoBean} filterable fields.
 * <br>
 * @author Marco "Cuc" Cuccato
 * @version 1.4
 */
@SuppressWarnings("javadoc")
public enum NodeFilterFields implements JemFilterFields<NodeInfoBean> {
	
	NAME("name"),
	HOSTNAME("hostname"),
	DOMAIN("domain"),
	STATIC_AFFINITIES("staticaffinities"),
	DYNAMIC_AFFINITIES("dynamicaffinities"),
	STATUS("status"),
	OS("os"),
	MEMORY("memory"),
	PARALLEL_JOBS("parallelJobs"),
	CURRENT_JOB("currentjob"),
	ENVIRONMENT("environment");
	
	private String name = null;
	
	private String pattern = null;
	
	/**
	 * Constructor which use the name of the field of node 
	 * @param name name of the field of node
	 */
	private NodeFilterFields(String name) {
		this(name, null);
	}
	
	/**
	 * Constructor which use the name of the field of node and 
	 * the pattern
	 * @param name name of the field of node
	 * @param pattern pattern of filter field
	 */
	private NodeFilterFields(String name, String pattern) {
		this.name = name;
		this.pattern = pattern;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.fields.JemFilterFields#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.fields.JemFilterFields#hasPattern()
	 */
	@Override
	public boolean hasPattern() {
		return pattern != null;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.fields.JemFilterFields#getPattern()
	 */
	@Override
	public String getPattern() {
		return pattern;
	}
	
	/**
	 * Utility method to get a filter fields by field name
	 * 
	 * @param name the name of the {@link NodeFilterFields}
	 * @return the {@link NodeFilterFields} associated with provided name
	 */
	public static NodeFilterFields getByName(String name) {
		// scans all values
		for (NodeFilterFields jff : values()) {
			// checks ignoring case if the name of filed is the same
			// with the parameter
			if (jff.getName().equalsIgnoreCase(name)) {
				return jff;
			}
		}
		return null;
	}
}