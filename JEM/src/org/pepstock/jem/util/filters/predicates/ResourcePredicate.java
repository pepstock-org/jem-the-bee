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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.util.TimeUtils;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.ResourceFilterFields;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;

/**
 * The {@link Predicate} of a {@link Resource}
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 */
public class ResourcePredicate extends JemFilterPredicate<Resource> implements Serializable {

	private static final long serialVersionUID = 8087227037699399624L;

	/**
	 * Empty contructor
	 */
	public ResourcePredicate() {
	}
	
	/**
	 * @see JemFilterPredicate
	 * @param filter 
	 */
	public ResourcePredicate(Filter filter) {
		super(filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(Map.Entry entry) {
		Resource resource = (Resource)entry.getValue();
		boolean includeThis = true;
		FilterToken[] tokens = getFilter().toTokenArray();

		for (int i=0; i<tokens.length && includeThis; i++) {
			FilterToken token = tokens[i];
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			ResourceFilterFields field = ResourceFilterFields.getByName(tokenName);
			if (field == null) {
				field = ResourceFilterFields.NAME;
			}
			
			switch (field) {
			case NAME:
				includeThis &= checkName(tokenValue, resource);
				break;
			case TYPE:
				includeThis &= StringUtils.containsIgnoreCase(resource.getType(), tokenValue);
				break;
			case PROPERTIES:
				includeThis &= checkProperties(tokenValue, resource);
				break;
			case MODIFIED:
				includeThis &= checkTime(tokenValue, resource.getLastModified());
				break;
			case MODIFIED_BY:
				includeThis &= StringUtils.containsIgnoreCase(resource.getUser(), tokenValue);
				break;
			default:
				throw new JemRuntimeException("Unrecognized Resource filter field: " + field);
			}
		}
		return includeThis;
	}

	/**
	 * Checks name of resource
	 * @param tokenValue filter of resource
	 * @param resource resource instance
	 * @return true if matches
	 */
	private boolean checkName(String tokenValue, Resource resource){
		// is able to manage for label the * wildcard
		if ("*".equalsIgnoreCase(tokenValue)) {
			return true;
		} else {
			String newTokenValue = tokenValue;
			if (tokenValue.endsWith("*")){
				newTokenValue = StringUtils.substringBeforeLast(tokenValue, "*");
			}
			return StringUtils.containsIgnoreCase(resource.getName(), newTokenValue);
		}
	}
	
	/**
	 * Checks properties values of resource
	 * @param tokenValue filter of resource property
	 * @param resource resources to check
	 * @return true if matches
	 */
	private boolean checkProperties(String tokenValue, Resource resource){
		int count = 0;
		String value = null;
		for (ResourceProperty property : resource.getProperties().values()){
			if (count == 0){
				value = property.getName() + " = " + (property.isVisible() ? property.getValue() : ResourceProperty.MASK_FOR_NO_VISIBLE_PROPERTY);
			} else {
				value = value +", " + property.getName() + " = " + (property.isVisible() ? property.getValue() : ResourceProperty.MASK_FOR_NO_VISIBLE_PROPERTY);	
			}
			count++;
		}
		return StringUtils.containsIgnoreCase(value, tokenValue);
	}
	
	/**
	 * Checks date of resource update
	 * @param time date of resource update 
	 * @param tokenValue filter to check
	 * @return true if matches
	 */
	private boolean checkTime(String tokenValue, Date time){
		long now = System.currentTimeMillis();
		try {
			// parse the date value based on pattern
			long inputTime = TimeUtils.parseDuration(tokenValue);
			long resourceTime = now-time.getTime();
			return resourceTime <= inputTime;
		} catch (Exception e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// cannot parse the date, exclude this entry by default!
			return false;
		}		
	}

	@Override
	public Set<QueryableEntry> filter(QueryContext arg0) {
		return null;
	}
}