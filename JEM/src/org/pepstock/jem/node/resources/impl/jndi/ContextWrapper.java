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

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Is a JNDI context implementation, which wraps a InitialContext instance.
 * <br>
 * If configured in read only, the user can't bind, unbind or rebind any object or 
 * destroySubContext, createSubContext, addToEnv, removeFromEnv actions.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class ContextWrapper extends InitialContext {
	
	private static final String EXCEPTION_STRING = "InitialContext in readonly! It can't bind, unbind, rebind, rename  objects or perform destroySubContext, createSubContext, addToEnv, removeFromEnv actions.";

	private InitialContext delegate = null;
	
	private boolean readOnly = false;

	/**
	 * Creates a JNDI context, using an initial context (used as delegated) and if the access
	 * must be done in read only.
	 * 
	 * @param delegate Initial context 
	 * @param readOnly if some methods are not accessible
	 * @throws NamingException if any errors occurs
	 */
	ContextWrapper(InitialContext delegate, boolean readOnly) throws NamingException {
		super();
		this.delegate = delegate;
		this.readOnly = readOnly;
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#lookup(java.lang.String)
	 */
	@Override
	public Object lookup(String name) throws NamingException {
		return delegate.lookup(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#lookup(javax.naming.Name)
	 */
	@Override
	public Object lookup(Name name) throws NamingException {
		return delegate.lookup(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#bind(java.lang.String, java.lang.Object)
	 */
	@Override
	public void bind(String name, Object obj) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.bind(name, obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#bind(javax.naming.Name, java.lang.Object)
	 */
	@Override
	public void bind(Name name, Object obj) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.bind(name, obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#rebind(java.lang.String, java.lang.Object)
	 */
	@Override
	public void rebind(String name, Object obj) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.rebind(name, obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#rebind(javax.naming.Name, java.lang.Object)
	 */
	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.rebind(name, obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#unbind(java.lang.String)
	 */
	@Override
	public void unbind(String name) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.unbind(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#unbind(javax.naming.Name)
	 */
	@Override
	public void unbind(Name name) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.unbind(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#rename(java.lang.String, java.lang.String)
	 */
	@Override
	public void rename(String oldName, String newName) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.rename(oldName, newName);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#rename(javax.naming.Name, javax.naming.Name)
	 */
	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.rename(oldName, newName);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#list(java.lang.String)
	 */
	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		return delegate.list(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#list(javax.naming.Name)
	 */
	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		return delegate.list(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#listBindings(java.lang.String)
	 */
	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		return delegate.listBindings(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#listBindings(javax.naming.Name)
	 */
	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		return delegate.listBindings(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#destroySubcontext(java.lang.String)
	 */
	@Override
	public void destroySubcontext(String name) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.destroySubcontext(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#destroySubcontext(javax.naming.Name)
	 */
	@Override
	public void destroySubcontext(Name name) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		delegate.destroySubcontext(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#createSubcontext(java.lang.String)
	 */
	@Override
	public Context createSubcontext(String name) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		return delegate.createSubcontext(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#createSubcontext(javax.naming.Name)
	 */
	@Override
	public Context createSubcontext(Name name) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		return delegate.createSubcontext(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#lookupLink(java.lang.String)
	 */
	@Override
	public Object lookupLink(String name) throws NamingException {
		return delegate.lookupLink(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#lookupLink(javax.naming.Name)
	 */
	@Override
	public Object lookupLink(Name name) throws NamingException {
		return delegate.lookupLink(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#getNameParser(java.lang.String)
	 */
	@Override
	public NameParser getNameParser(String name) throws NamingException {
		return delegate.getNameParser(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#getNameParser(javax.naming.Name)
	 */
	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		return delegate.getNameParser(name);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#composeName(java.lang.String, java.lang.String)
	 */
	@Override
	public String composeName(String name, String prefix) throws NamingException {
		return delegate.composeName(name, prefix);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#composeName(javax.naming.Name, javax.naming.Name)
	 */
	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		return delegate.composeName(name, prefix);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#addToEnvironment(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		return delegate.addToEnvironment(propName, propVal);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#removeFromEnvironment(java.lang.String)
	 */
	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		if (readOnly){
			throw new NamingException(EXCEPTION_STRING);
		}
		return delegate.removeFromEnvironment(propName);
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#getEnvironment()
	 */
	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return delegate.getEnvironment();
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#close()
	 */
	@Override
	public void close() throws NamingException {
		delegate.close();
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#getNameInNamespace()
	 */
	@Override
	public String getNameInNamespace() throws NamingException {
		return delegate.getNameInNamespace();
	}
}
