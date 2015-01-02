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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.pepstock.jem.jppf.DataStreamNameClassPair;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * JNDI context which contains all the resources references at runtime.
 * It is loaded of declared resources by ANT, SpringBatch and other JCL before to executes
 * business logic.<br>
 * It is built by JemContext factory. It uses a <code>AbsoluteHashMap</code> to share the references
 * JNDI cross classloader.
 * <br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class JemContext extends InitialContext implements Context {
	
	private Map<String, Object> bindings = AbsoluteHashMap.getInstance();
	
	@SuppressWarnings("rawtypes")
	private final Hashtable env = new Hashtable();

	/**
	 * Creates a JNDI context using the environment passed from JNDI engine.
	 * It saves the environment, without passing it to the parent.
	 * <br>
	 * Usually the environment is not used but it saved to return with <code>getEnvironment</code>
	 * method.
	 * 
	 * @param env Environment hash table
	 * @throws NamingException if an exception occurs
	 * 
	 */
	@SuppressWarnings("unchecked")
	public JemContext(Map<?,?> env) throws NamingException {
		super(true);
		if (env != null){
			// be aware that all
			// additional keys added after this constructor call,
			// will be ignored!
			this.env.putAll(env);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
	 */
	@Override
	public void bind(Name name, Object obj) throws NamingException {
		// name mustn't be empty
		if (name.isEmpty()) {
			throw new InvalidNameException(NodeMessage.JEMC139E.toMessage().getMessage());
		}

		// Extract name of reference
		String atom = name.get(0);
		// gets references from bindings
		Object inter = bindings.get(atom);
		// only one name must be specified
		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			if (inter != null) {
				throw new NameAlreadyBoundException(NodeMessage.JEMC102E.toMessage().getMessage());
			}

			// Call getStateToBind for using any state factories
			Object objNew = NamingManager.getStateToBind(obj, 
					new CompositeName().add(atom), 
					this, env);

			// Add object to internal data structure
			bindings.put(atom, objNew);
		} else {
			throw new NamingException(NodeMessage.JEMC101E.toMessage().getFormattedMessage(atom));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
	 */
	@Override
	public void bind(String name, Object obj) throws NamingException {
		bind(new CompositeName(name), obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookup(javax.naming.Name)
	 */
	@Override
	public Object lookup(Name name) throws NamingException {
		// name mustn't be empty
		if (name.isEmpty()) {
			throw new InvalidNameException(NodeMessage.JEMC139E.toMessage().getMessage());
		} 
		// Extract components that belong to this name space
		String atom = name.get(0);
		// gets reference
		Object inter = bindings.get(atom);
		// only one name must be specified
		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			if (inter == null) {
				throw new NameNotFoundException(NodeMessage.JEMC100E.toMessage().getFormattedMessage(name));
			}
			// Call getObjectInstance for using any object factories
			try {
				return NamingManager.getObjectInstance(inter, 
						new CompositeName().add(atom), 
						this, env);
			} catch (Exception e) {
				// ignore
				LogAppl.getInstance().ignore(e.getMessage(), e);
				// creates an naming exception
				NamingException ne = new NamingException(NodeMessage.JEMC140E.toMessage().getMessage());
				// setting the root cause
				ne.setRootCause(e);
				throw ne;
			}
		} else {
			// if here, name size is not = 1
			throw new NamingException(NodeMessage.JEMC101E.toMessage().getFormattedMessage(atom));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookup(java.lang.String)
	 */
	@Override
	public Object lookup(String name) throws NamingException {
		return lookup(new CompositeName(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
	 */
	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		// name mustn't be empty
		if (name.isEmpty()) {
			throw new InvalidNameException(NodeMessage.JEMC139E.toMessage().getMessage());
		}
		// Extract components that belong to this name space
		String atom = name.get(0);
		// only one name must be specified
		if (name.size() == 1) {
			// Call getStateToBind for using any state factories
			Object objNew = NamingManager.getStateToBind(obj, 
					new CompositeName().add(atom), 
					this, env);

			// Add object to internal data structure
			bindings.put(atom, objNew);
		} else {
			// if here, name size is not = 1
			throw new NamingException(NodeMessage.JEMC101E.toMessage().getFormattedMessage(atom));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
	 */
	@Override
	public void rebind(String name, Object obj) throws NamingException {
		rebind(new CompositeName(name), obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#unbind(javax.naming.Name)
	 */
	@Override
	public void unbind(Name name) throws NamingException {
		// name mustn't be empty
		if (name.isEmpty()) {
			throw new InvalidNameException(NodeMessage.JEMC139E.toMessage().getMessage());
		}
		// Extract components that belong to this name space
		String atom = name.get(0);
		// Remove object from internal data structure
		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			bindings.remove(atom);
		} else {
			// if here, name size is not = 1
			throw new NamingException(NodeMessage.JEMC101E.toMessage().getFormattedMessage(atom));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#unbind(java.lang.String)
	 */
	@Override
	public void unbind(String name) throws NamingException {
		unbind(new CompositeName(name));
	}
	
	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#getEnvironment()
	 */
	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return env;
	}

	/* (non-Javadoc)
	 * @see javax.naming.InitialContext#list(java.lang.String)
	 */
	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		// returns a list of data descriptor and ONLY data descriptors
		// creates a list, container of result
		List<NameClassPair> items = new ArrayList<NameClassPair>();
		// scans all keys set of binding
		for (String key : bindings.keySet()){
			// parameter is empty or
			// key starts with name
			if ("".equalsIgnoreCase(name) || key.startsWith(name)){
				// gets object
				Object value = bindings.get(key);
				// ONLY if reference is a DATASTREAM (and then data descriptor)
				if (value instanceof DataStreamReference){
					// creates a specific PAIR
					DataStreamNameClassPair pair = new DataStreamNameClassPair(key, value.getClass().getName(), false);
					pair.setObject(value);
					items.add(pair);
				} else if  (value.getClass().getName().equalsIgnoreCase(DataStreamReference.class.getName())){
					// if here means that you are on different classloader
					// creates a specific PAIR
					DataStreamNameClassPair pair = new DataStreamNameClassPair(key, value.getClass().getName(), false);
					pair.setObject(value);
					items.add(pair);
				} else {
					// otherwise it uses a normal pair 
					// without possibility to get any data
					NameClassPair pair = new NameClassPair(key, value.getClass().getName(), false);	
					items.add(pair);
				}
			}
		}
		// returns an enumeration usind the iterator of list
		// created as result 
		return new JemNamingEnumeration(items.iterator());
	}

	/**
	 * Special enumeration with all name-class-pairs to return after a <code>list</code> method call.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	static class JemNamingEnumeration implements NamingEnumeration<NameClassPair>{

		private Iterator<NameClassPair> iter = null;
		
		/**
		 * Creates an enumeration using the iterator of list to return as result
		 * @param iter terator of list to return as result
		 */
		public JemNamingEnumeration(Iterator<NameClassPair> iter) {
			this.iter = iter;
		}

		/* (non-Javadoc)
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		@Override
		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Enumeration#nextElement()
		 */
		@Override
		public NameClassPair nextElement() {
			return iter.next();
		}

		/* (non-Javadoc)
		 * @see javax.naming.NamingEnumeration#next()
		 */
		@Override
		public NameClassPair next() throws NamingException {
			return iter.next();
		}

		/* (non-Javadoc)
		 * @see javax.naming.NamingEnumeration#hasMore()
		 */
		@Override
		public boolean hasMore() throws NamingException {
			return iter.hasNext();
		}

		/* (non-Javadoc)
		 * @see javax.naming.NamingEnumeration#close()
		 */
		@Override
		public void close() throws NamingException {
			// nop
		}
	}
}