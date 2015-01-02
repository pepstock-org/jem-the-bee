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
package org.pepstock.jem.node.tasks.jndi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.pepstock.jem.node.NodeMessage;

/**
 * An invocation handler that calls directly the method of delegate object.
 * Works ONLY on public methods.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 */
class DelegateInvocationHandler implements InvocationHandler {
	
	private final Object delegate;

	/**
	 * Default constructor, saving the delegate instance 
	 * @param delegate delegated instance
	 */
	public DelegateInvocationHandler(Object delegate) {
		this.delegate = delegate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws JNDIException {
		try {
			// gets the delegated method using the info of the method 
			// passed by arguments of handler
			Method delegateMethod = delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
			// invokes the method and return the value
			return delegateMethod.invoke(delegate, args);
		} catch (SecurityException e) {
			throw new JNDIException(NodeMessage.JEMC237E, e);
		} catch (IllegalArgumentException e) {
			throw new JNDIException(NodeMessage.JEMC237E, e);
		} catch (NoSuchMethodException e) {
			 throw new JNDIException(NodeMessage.JEMC237E, e);
		} catch (IllegalAccessException e) {
			 throw new JNDIException(NodeMessage.JEMC237E, e);
		} catch (InvocationTargetException e) {
			 throw new JNDIException(NodeMessage.JEMC237E, e);
		}
	}
}