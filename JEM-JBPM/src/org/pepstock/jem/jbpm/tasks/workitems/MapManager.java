/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.jbpm.tasks.workitems;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.pepstock.jem.jbpm.JBpmKeys;
import org.pepstock.jem.jbpm.tasks.JemWorkItem;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * Loads a map into another map, putting the instance of data description and data sources
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class MapManager implements JemWorkItem {

	/**
	 * Loads a map into another map, putting the instance of data description and data sources
	 * @param parameters map of parameters prepared by JBPM
	 * @return copy of the map but with the instances of data description and data sources
	 */
	final Map<String, Object> loadParameters(Map<String, Object> parameters){
		// gets JNDI context
		InitialContext ic =  null;
		try {
	        ic = ContextUtils.getContext();
        } catch (NamingException e) {
	       LogAppl.getInstance().ignore(e.getMessage(), e);
        }
		
		// creates new map
		Map<String, Object> result = new HashMap<String, Object>();
		// scans all parameters
		for (Entry<String, Object> entry : parameters.entrySet()){
			try {
				// gets key
	            String name = entry.getKey();
	            // is DD?
	            if (name.startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX)){
	            	// gets data description name
	            	String ddn = StringUtils.substringAfter(name, JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX);
	            	// if context is not null, lookup and stores on the map
	            	if (ic != null){
	            		result.put(name, ic.lookup(ddn));
	            	}
	            } else if (name.startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX)){
	            	// is Data source?
	            	// gets datasource name
	            	String dds = StringUtils.substringAfter(name, JBpmKeys.JBPM_DATA_SOURCE_PREFIX);
	            	// if context is not null, lookup and stores on the map
	            	if (ic != null){
	            		result.put(name, ic.lookup(dds));
	            	}
	            } else if (!name.equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY)){
	            	// is Lock? IGNORE
	            	result.put(name, entry.getValue());
	            }
            } catch (NamingException e) {
            	// ignore every naming exception
            	LogAppl.getInstance().ignore(e.getMessage(), e);
            }
		}
		return result;
	}

}
