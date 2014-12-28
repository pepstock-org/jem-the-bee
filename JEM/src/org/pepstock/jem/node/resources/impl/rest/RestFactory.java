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
package org.pepstock.jem.node.resources.impl.rest;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.impl.AbstractObjectFactory;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.tasks.jndi.JNDIException;
import org.pepstock.jem.rest.HTTPBaseAuthRestClient;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.SingleRestClient;
import org.pepstock.jem.util.Parser;

/**
 * Custom JBDC Factory, which extends APACHE DB pool, to set the connection properties.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RestFactory extends AbstractObjectFactory {

	/* (non-Javadoc)
	 * @see org.apache.commons.dbcp.BasicDataSourceFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, @SuppressWarnings("rawtypes") Hashtable env) throws Exception {
		// checks if the object is null or not a reference
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		// creates the rest client
		return createRestClient(loadProperties(object, RestResourceKeys.PROPERTIES_ALL));
	}
	
	/**
	 * Creates and configures a REST client instance based on the
	 * given properties.
	 * 
	 * @param properties the REST client configuration properties
	 * @return a REST instance to access to a REST endpoint
	 * @throws JNDIException if an error occurs creating the rmi client
	 */
	public Object createRestClient(Properties properties) throws JNDIException {
		// get URL
		// is mandatory
		String baseUri = properties.getProperty(CommonKeys.URL);
		if (baseUri == null){
			throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.URL);
		}
		// checks if a HTTP Basci authentication is required
		boolean basicAuth = Parser.parseBoolean(properties.getProperty(RestResourceKeys.HTTP_BASIC_AUTHENTICATION, "false"), false);
		
		RestClient client = null;
		// if basci authentication is required
		if (basicAuth){
			// User id is mandatory
			String username = properties.getProperty(CommonKeys.USERID);
			if (username == null){
				throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.USERID);
			}
			// password is mandatory
			String password = properties.getProperty(CommonKeys.PASSWORD);
			if (password == null){
				throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.PASSWORD);
			}
			// creates a REST client with special configuration
			// for HTTP basic authentication
			client = new HTTPBaseAuthRestClient(baseUri, username, password);
		} else {
			// otherwise a single REST client
			client = new SingleRestClient(baseUri);
		}
		return client;
	}
}