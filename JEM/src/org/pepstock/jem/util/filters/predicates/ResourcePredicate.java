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
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.ResourceFilterFields;

import com.hazelcast.query.Predicate;

/**
 * This predicate is used to filter the resources to extract distributing all searches on all nodes of JEM.
 * <br>
 * The {@link Predicate} of a {@link Resource}
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public class ResourcePredicate extends JemFilterPredicate implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public ResourcePredicate() {
	}
	
	/**
	 * Constructs the object saving the filter to use to extract the resources
	 * from Hazelcast map
	 * @see JemFilterPredicate
	 * @param filter String filter
	 */
	public ResourcePredicate(Filter filter) {
		super(filter);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.predicates.JemFilterPredicate#apply(com.hazelcast.core.MapEntry)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(Entry entry) {
		// casts the object to a Resource 
		Resource resource = (Resource)entry.getValue();
		boolean includeThis = true;
		// gets all tokens of filter
		if (!getObject().isEmpty()){
			// iterate over all filter tokens
			Iterator<FilterToken> iterator = getObject().values().iterator();
			// exit if tokens already processed OR if i can immediate exclude this
			while(iterator.hasNext() && includeThis) {
				FilterToken token = iterator.next();
				// gets name and value
				// remember that filters are built:
				// -[name] [value]
				String tokenName = token.getName();
				String tokenValue = token.getValue();
				// gets the filter field for resources by name
				ResourceFilterFields field = ResourceFilterFields.getByName(tokenName);
				// if field is not present,
				// used NAME as default
				if (field == null) {
					field = ResourceFilterFields.NAME;
				}
				boolean match = true;
				// based on name of field, it will check
				// different attributes 
				// all matches are in AND
				switch (field) {
					case NAME:
						// checks name of RESOURCE
						match = checkName(tokenValue, resource.getName());
						break;
					case TYPE:
						// checks type of RESOURCE
						match = StringUtils.containsIgnoreCase(resource.getType(), tokenValue);
						break;
					case PROPERTIES:
						// checks properties of RESOURCE
						match = checkProperties(tokenValue, resource);
						break;
					case MODIFIED:
						// checks modified time of ROLE
						match = checkTime(tokenValue, resource.getLastModified());
						break;
					case MODIFIED_BY:
						// checks who changed the resource
						match = StringUtils.containsIgnoreCase(resource.getUser(), tokenValue);
						break;
					default:
						// otherwise it uses a wrong filter name
						throw new JemRuntimeException("Unrecognized Resource filter field: " + field);
				}
				includeThis &= (token.isNot()) ? !match : match;
			}
		}
		return includeThis;
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
		// scans all resource properties
		// to create a string to check if contains a string
		for (ResourceProperty property : resource.getProperties().values()){
			// if is starting point
			// doesn't add any previous value
			if (count == 0){
				// it builds the string without property NOT visible
				value = property.getName() + " = " + (property.isVisible() ? property.getValue() : ResourceProperty.MASK_FOR_NO_VISIBLE_PROPERTY);
			} else {
				// it builds the string without property NOT visible adding previous value
				value = value +", " + property.getName() + " = " + (property.isVisible() ? property.getValue() : ResourceProperty.MASK_FOR_NO_VISIBLE_PROPERTY);	
			}
			// increments count
			count++;
		}
		// here checks if the filter value is in the string built
		// with resource properties
		return StringUtils.containsIgnoreCase(value, tokenValue);
	}
}