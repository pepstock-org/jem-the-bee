/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.node.tasks.jndi;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.JmsResource;

/**
 * Factory useful to create a {@link ConnectionFactory}, that is the
 * <code>JMS</code> datasource object to be used inside the java programs. <br>
 * It uses a <code>JmsReference</code>, containing the properties of the
 * <code>JMS</code> datasource object. <br>
 * The <code>JMS</code> datasource object allows to connect to a
 * <code>JMS</code> provider, and write or read <code>Queues</code> or
 * <code>Topics</code>. <br>
 * It implements the <code>ObjectFactory</code>.
 * 
 * @see JmsReference
 * @see ObjectFactory
 * @author Alessandro Zambrini
 * @version 1.0
 * 
 */
public class JmsFactory implements ObjectFactory {
	
	/**
	 * This method creates a {@link ConnectionFactory} for <code>JMS</code>
	 * purposes, starting from the {@link JmsReference} in the parameter
	 * <code>obj</code>. <br>
	 * The <code>ConnectionFactory</code> allows to connect to a
	 * <code>JMS</code> provider, and write or read <code>Queues</code> or
	 * <code>Topics</code>. <br>
	 * 
	 * @see ObjectFactory
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws JNDIException {
		if ((obj == null) || !(obj instanceof Reference)) {
			return null;
		}
		Reference ref = (Reference) obj;
		try {
			addJmsEnvironmentParams(ref, nameCtx);
			String connectionFactoryName = getReferencePropertyValue(ref, JmsResource.CONNECTION_FACTORY_NAME, true);
			return (ConnectionFactory) nameCtx.lookup(connectionFactoryName);
		} catch (NamingException e) {
			throw new JNDIException(NodeMessage.JEMC235E, e);
		}
	}

	/**
	 * Adds in the <code>Environment</code> of <code>nameCtx</code> parameter
	 * all the properties useful to lookup the <code>ConnectionFactory</code> by
	 * name. <br>
	 * The properties are taken from the <code>Reference</code> paramer, a
	 * <code>JmsReference</code>.
	 * 
	 * @param reference the {@link JmsReference} object containing the
	 *            properties (names and values) of a <code>JMS</code> object.
	 * @param nameCtx the {@link Context} in which to set the <code>JMS</code>
	 *            properties and used to lookup the
	 *            <code>ConnectionFactory</code> by name.
	 * @throws NamingException if a error occurs
	 * @throws JNDIException if a error occurs
	 * 
	 * @see Context
	 * @see Reference
	 */
	private void addJmsEnvironmentParams(Reference reference, Context nameCtx) throws NamingException, JNDIException {
		String providerUrl = getReferencePropertyValue(reference, JmsResource.PROVIDER_URL, true);
		nameCtx.addToEnvironment(JmsResource.PROVIDER_URL, providerUrl);
		String initialContextFactor = getReferencePropertyValue(reference, JmsResource.INITIAL_CONTEXT_FACTORY, true);
		nameCtx.addToEnvironment(JmsResource.INITIAL_CONTEXT_FACTORY, initialContextFactor);
		String principal = getReferencePropertyValue(reference, JmsResource.USERID, false);
		String credentials = getReferencePropertyValue(reference, JmsResource.PASSWORD, false);
		if (null != principal && null != credentials) {
			nameCtx.addToEnvironment(Context.SECURITY_PRINCIPAL, principal);
			nameCtx.addToEnvironment(Context.SECURITY_CREDENTIALS, credentials);
		}
	}

	/**
	 * This method search inside a {@link JmsReference} object the value of a
	 * property. <br>
	 * If the property is mandatory, and is not found, the method throws an
	 * <code>Exception</code>.
	 * 
	 * @param reference the {@link JmsReference} object containing the
	 *            properties (names and values) of a <code>JMS</code> object.
	 * @param propertyName the name of the property searched.
	 * @param mandatory <code>true</code> if the property is mandatory and must
	 *            be found, <code>false</code> otherwise.
	 * @return the value of the property if found.
	 * 
	 * @throws Exception if the property searched is mandatory and it is not
	 *             found.
	 * 
	 * @see Reference
	 */
	private String getReferencePropertyValue(Reference reference, String propertyName, boolean mandatory) throws JNDIException {
		RefAddr ra = reference.get(propertyName);
		if (null == ra || null == ra.getContent() || "".equalsIgnoreCase(ra.getContent().toString().trim())) {
			if (mandatory) {
				throw new JNDIException(NodeMessage.JEMC136E, propertyName);
			} else {
				return null;
			}
		} else {
			return ra.getContent().toString();
		}

	}
}