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
package org.pepstock.jem.node.resources.impl.jem;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.xml.bind.JAXBContext;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.tasks.jndi.JNDIException;
import org.pepstock.jem.rest.ResourceRestClient;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.services.LoginManager;

/**
 * JNDI factory to create object for JAVA batches. It returns a RestClient.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class JemFactory implements ObjectFactory {

	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JNDIException {
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		Reference ref = (Reference) object;
		Properties properties = new Properties();
		for (int i = 0; i < JemResourceKeys.PROPERTIES_ALL.size(); i++) {
			String propertyName = JemResourceKeys.PROPERTIES_ALL.get(i);
			RefAddr ra = ref.get(propertyName);
			if (ra != null) {
				String propertyValue = ra.getContent().toString();
				properties.setProperty(propertyName, propertyValue);
			}
		}
		return createRestClient(properties);
	}

	/**
	 * Creates and configures a RestClient instance based on the
	 * given properties.
	 * 
	 * @param properties the REST client configuration properties
	 * @return a RestClient instance to access to JEM
	 * @throws JNDIException if an error occurs creating the rest client
	 */
	public static Object createRestClient(Properties properties) throws JNDIException {
		String urlString = properties.getProperty(JemResourceKeys.URL);
		if (urlString == null){
			throw new JNDIException(NodeMessage.JEMC136E, JemResourceKeys.URL);
		}

		String username = properties.getProperty(CommonKeys.USERID);
		if (username == null){
			throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.USERID);
		}

		String password = properties.getProperty(CommonKeys.PASSWORD);
		if (password == null){
			throw new JNDIException(NodeMessage.JEMC136E, CommonKeys.PASSWORD);
		}

		// sets JAXB version 2, otherwise can't find the class
		System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "com.sun.xml.bind.v2.ContextFactory");
		
		DelegateRestClient client = new DelegateRestClient(urlString);
		LoginManager manager = new LoginManager(client);
		client.setManager(manager);
		try {
			LoggedUser user = manager.getUser();
			if (user == null) {
				// creates a Account object
				Account account = new Account();
				account.setUserId(username);
				account.setPassword(password);
				// log in
				manager.login(account);
			}

		} catch (JemException e) {
			throw new JNDIException(NodeMessage.JEMC269E, e);
		} 
		return client;
	}
	
	/**
	 * Delegate REST client, with close connection
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	static final class DelegateRestClient extends ResourceRestClient{

		private LoginManager manager = null;
		
		/**
		 * Creates the Rest client calling super class
		 * @param uriString uri of REST instance
		 */
		public DelegateRestClient(String uriString) {
			super(uriString);
		}


		/**
		 * @return the manager
		 */
		LoginManager getManager() {
			return manager;
		}

		/**
		 * @param manager the manager to set
		 */
		void setManager(LoginManager manager) {
			this.manager = manager;
		}

		/* (non-Javadoc)
		 * @see java.io.Closeable#close()
		 */
		@Override
		public void close() throws IOException {
			if (manager != null){
				try {
					manager.logoff();
				} catch (JemException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
}