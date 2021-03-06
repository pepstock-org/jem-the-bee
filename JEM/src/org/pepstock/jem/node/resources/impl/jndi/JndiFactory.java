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
import org.pepstock.jem.util.Parser;

/**
 * Custom JNDI Factory, uses the connection properties to create a InitialContext.
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
		// creates the environment for JNDI object
		Hashtable<String, String> newEnv = new Hashtable<String, String>();
		
		boolean readOnly = false;
		// loads all properties defined for this resource
		Properties props = loadProperties(object, JndiResourceKeys.PROPERTIES_ALL);
		for (Entry<Object, Object> entry : props.entrySet()){
			// Doesn't add the readOnly property to the environment
			// but sets readonly
			if (entry.getKey().toString().equalsIgnoreCase(JndiResourceKeys.READONLY)){
				// gets if is read only registry. Default is false
				readOnly = Parser.parseBoolean(props.getProperty(JndiResourceKeys.READONLY, "false"), false);
			} else {
				newEnv.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		// get the resource custom properties
		RefAddr ra = ref.get(CommonKeys.RESOURCE_CUSTOM_PROPERTIES);
		if (ra != null) {
			// loads environments
			String propertyValue = ra.getContent().toString();
			// parses the custom properties
			// format: key equals value semi-colon
			String[] keys = propertyValue.split(";");
			for (int i=0; i<keys.length; i++){
				String[] values = keys[i].split("=");
				// loads the new environment
				newEnv.put(values[0], values[1]);
			}
		}
		// return initial context
		return new ContextWrapper(new InitialContext(newEnv), readOnly);
	}
}