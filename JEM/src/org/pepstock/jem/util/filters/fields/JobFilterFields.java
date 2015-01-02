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
package org.pepstock.jem.util.filters.fields;

import org.pepstock.jem.Job;

/**
 * Collect all {@link Job} filterable fields.
 * <br>
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.4	
 */
@SuppressWarnings("javadoc")
public enum JobFilterFields implements JemFilterFields<Job> {
	
	NAME("name"),
	TYPE("type"),
	USER("user"),
	ENVIRONMENT("environment"),
	ROUTED("routed", YES_NO_PATTERN_DESCRIPTION),
	DOMAIN("domain"),
	AFFINITY("affinity"),
	ENDED_TIME("endedtime", DURATION_PATTERN_DESCRIPTION),
	RETURN_CODE("returncode"),
	MEMBER("member"),
	SUBMITTED_TIME("submittedtime", DURATION_PATTERN_DESCRIPTION),
	PRIORITY("priority"),
	MEMORY("memory"),
	STEP("step"),
	RUNNING_TIME("runningtime", DURATION_PATTERN_DESCRIPTION),
	ID("id");

	public static final JobFilterFields[] DEFAULTS = new JobFilterFields[] {NAME};
	
	private String name = null;
	
	private String pattern = null;
	
	/**
	 * Constructor which use the name of the field of job 
	 * @param name name of the field of job
	 */
	private JobFilterFields(String name) {
		this(name, null);
	}
	
	/**
	 * Constructor which use the name of the field of job and 
	 * the pattern
	 * @param name name of the field of job
	 * @param pattern pattern of filter field
	 */
	private JobFilterFields(String name, String pattern) {
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
	 * @param name the name of the {@link JobFilterFields}
	 * @return the {@link JobFilterFields} associated with provided name
	 */
	public static JobFilterFields getByName(String name) {
		// scans all values
		for (JobFilterFields jff : values()) {
			// checks ignoring case if the name of filed is the same
			// with the parameter
			if (jff.getName().equalsIgnoreCase(name)) {
				return jff;
			}
		}
		return null;
	}
}