/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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
package org.pepstock.jem.node.resources.impl.jms;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.impl.AbstractObjectFactory;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.tasks.jndi.JNDIException;

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
public class JmsFactory extends AbstractObjectFactory {
	
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
			Hashtable<String, String> env = new Hashtable<String, String>();
			String providerUrl = getReferencePropertyValue(ref, JmsResourceKeys.PROVIDER_URL, true);
			String initialContextFactor = getReferencePropertyValue(ref, JmsResourceKeys.INITIAL_CONTEXT_FACTORY, true);
			String principal = getReferencePropertyValue(ref, CommonKeys.USERID, false);
			String credentials = getReferencePropertyValue(ref, CommonKeys.PASSWORD, false);
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactor);
			env.put(Context.PROVIDER_URL, providerUrl);
			if (null != principal && null != credentials) {
				env.put(Context.SECURITY_PRINCIPAL, principal);
				env.put(Context.SECURITY_CREDENTIALS, credentials);				
			}
			return new InitialContext(env);
		} catch (NamingException e) {
			throw new JNDIException(NodeMessage.JEMC235E, e);
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