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
import java.util.Date;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.InternalAbstractPredicate;
import org.pepstock.jem.util.TimeUtils;
import org.pepstock.jem.util.filters.Filter;

import com.hazelcast.query.Predicate;

/**
 * Root class of Hazelcast custom {@link Predicate}s. It uses as abstract class for predicates on JEM entities.
 * The predicate are used by Hazelcast to filter queries in distributed way.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public abstract class JemFilterPredicate extends InternalAbstractPredicate<Filter> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public JemFilterPredicate() {
	}
	
	/**
	 * Build a {@link JemFilterPredicate} using and saving a filter.
	 * @param filter the {@link Filter} associated with this {@link Predicate}
	 */
	public JemFilterPredicate(Filter filter) {
		this.setObject(filter);
	}
	
	/* (non-Javadoc)
	 * @see com.hazelcast.query.Predicate#apply(com.hazelcast.core.MapEntry)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public abstract boolean apply(Entry entry);
	
	/**
	 * Checks name of object with the filter value
	 * @param tokenValue filter of resource
	 * @param name name of instance to be checked
	 * @return true if matches
	 */
	protected boolean checkName(String tokenValue, String name){
		// is able to manage for label the * wildcard
		// matches ALWAYS if has got the star only
		if ("*".equalsIgnoreCase(tokenValue)) {
			return true;
		} else {
			String newTokenValue = tokenValue;
			// checks if ends with wildcard
			if (tokenValue.endsWith("*")){
				// if yes, remove the stars
				newTokenValue = StringUtils.substringBeforeLast(tokenValue, "*");
			}
			// checks if contains the string
			return StringUtils.containsIgnoreCase(name, newTokenValue);
		}
	}
	
	/**
	 * Checks date/time with date attribute of entity
	 * @param time date to be checked
	 * @param tokenValue filter to check
	 * @return true if matches
	 */
	protected boolean checkTime(String tokenValue, Date time){
		// checks if time is ok
		if (time == null){
			return false;
		}
		// gets current time
		// used to subtract the filter value
		long now = System.currentTimeMillis();
		try {
			// parse the date value based on pattern
			long inputTime = TimeUtils.parseDuration(tokenValue);
			// calculates the range of time
			long entityTime = now-time.getTime();
			// checks if time is in the range
			return entityTime <= inputTime;
		} catch (Exception e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// cannot parse the date, exclude this entry by default!
			return false;
		}		
	}
}