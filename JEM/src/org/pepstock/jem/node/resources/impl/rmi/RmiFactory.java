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
package org.pepstock.jem.node.resources.impl.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.definition.ResourceMessage;
import org.pepstock.jem.node.resources.impl.AbstractObjectFactory;
import org.pepstock.jem.node.tasks.jndi.JNDIException;
import org.pepstock.jem.util.Parser;

/**
 * JNDI factory to create object for JAVA batches
 * <br>
 * It returns a RMI registry.
 * 
 * @see Registry
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class RmiFactory  extends AbstractObjectFactory {

	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JNDIException {
		// checks if object is null
		// or is not a reference
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		// creates RMI registry
		return createRmiClient(loadProperties(object, RmiResourceKeys.PROPERTIES_ALL));
	}

	/**
	 * Creates and configures a RMI registry instance based on the
	 * given properties.
	 * 
	 * @param properties the RMI client configuration properties
	 * @return a RMI instance to access to a RMI registry
	 * @throws JNDIException if an error occurs creating the rmi client
	 */
	public Object createRmiClient(Properties properties) throws JNDIException {
		// host name is mandatory
		String hostname = properties.getProperty(RmiResourceKeys.HOSTNAME);
		// if null, EXCEPTION!
		if (hostname == null){
			throw new JNDIException(NodeMessage.JEMC136E, RmiResourceKeys.HOSTNAME);
		}
		// gets registry port. if null, uses the default, 1099
		int port = Parser.parseInt(properties.getProperty(RmiResourceKeys.PORT, "1099"), 1099);
		// gets if SSL connection is required. Default is false
		boolean ssl = Parser.parseBoolean(properties.getProperty(RmiResourceKeys.SSL, "false"), false);
		
		Registry registry;
        try {
	        registry = null;
	        // if SSl, uses
	        // a socket factory SSL
	        if (ssl){
	        	registry = LocateRegistry.getRegistry(hostname, port, new SslRMIClientSocketFactory());
	        } else {
	        	// otherwise a normal registry
	        	registry = LocateRegistry.getRegistry(hostname, port);
	        }
        } catch (RemoteException e) {
	        throw new JNDIException(ResourceMessage.JEMR028E, e, hostname, port);
        }
		return registry;
	}
}