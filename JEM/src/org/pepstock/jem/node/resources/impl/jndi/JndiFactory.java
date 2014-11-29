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
package org.pepstock.jem.node.resources.impl.jndi;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;

import org.pepstock.jem.node.resources.impl.AbstractObjectFactory;
import org.pepstock.jem.node.resources.impl.CommonKeys;

/**
 * Custom JNDI Factory, uses the connection properties to create a InitialContext 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JndiFactory extends AbstractObjectFactory {

	/* (non-Javadoc)
	 * @see org.apache.commons.dbcp.BasicDataSourceFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, @SuppressWarnings("rawtypes") Hashtable env) throws Exception {
		// reads the reference
		Reference ref = (Reference) object;
		Hashtable<String, String> newEnv = new Hashtable<String, String>();
		
		// loads all properties defined for this resource
		Properties props = loadProperties(object, JndiResourceKeys.PROPERTIES_ALL);
		for (Entry<Object, Object> entry : props.entrySet()){
			newEnv.put(entry.getKey().toString(), entry.getValue().toString());
		}
		
		// get the resource custom properties
		RefAddr ra = ref.get(CommonKeys.RESOURCE_CUSTOM_PROPERTIES);
		if (ra != null) {
			// loads environments
			String propertyValue = ra.getContent().toString();
			String[] keys = propertyValue.split(";");
			for (int i=0; i<keys.length; i++){
				String[] values = keys[i].split("=");
				newEnv.put(values[0], values[1]);
			}
		}
		// return initial context
		return new InitialContext(newEnv);
	}
}
