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
package org.pepstock.jem.node.resources.impl.jdbc;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.pepstock.jem.node.resources.impl.CommonKeys;

/**
 * Custom JBDC Factory, which extends APACHE DB pool, to set the connection properties 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JdbcFactory extends BasicDataSourceFactory {

	/* (non-Javadoc)
	 * @see org.apache.commons.dbcp.BasicDataSourceFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, @SuppressWarnings("rawtypes") Hashtable env) throws Exception {
		// reads the reference
		Reference ref = (Reference) object;
		
		// get the resource custom properties
		// if exits, adds to CONNECTION properties,
		// which is usd by Apache DB pool to set connection properties
		RefAddr ra = ref.get(CommonKeys.RESOURCE_CUSTOM_PROPERTIES);
		if (ra != null) {
			String propertyValue = ra.getContent().toString();
			ref.add(new StringRefAddr(JdbcResourceKeys.PROP_CONNECTIONPROPERTIES, propertyValue));	
		}
		// DBPool want USERNAME instead of USERID
		RefAddr raUser = ref.get(CommonKeys.USERID);
		if (raUser != null) {
			String propertyValue = raUser.getContent().toString();
			ref.add(new StringRefAddr(JdbcResourceKeys.PROP_USERNAME, propertyValue));	
		}
		// call super
		return super.getObjectInstance(ref, name, ctx, env);
	}

	
}
