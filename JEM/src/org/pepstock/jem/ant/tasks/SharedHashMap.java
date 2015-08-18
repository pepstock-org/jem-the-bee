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
package org.pepstock.jem.ant.tasks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.pepstock.jem.util.ReverseURLClassLoader;

/**
 * With this HashMap you can share the data of map across hierarchy of Classloaders.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class SharedHashMap extends HashMap<String, Object> implements Map<String, Object>{

	private static final long serialVersionUID = 1L;

	//  Common instance shared across a proxy
    private static Map<String, Object> instance = null;
    
    /**
     * Empty and private constructor
     */
    private SharedHashMap() {
    }

    /**
     * This is a singleton. If instance is not null, means that local map is already loaded.
     * If null and classload is ANT classloader, then uses the proxy to load the instance from parent classloader, 
     * otherwise it creates a new instance.
     * @return shared HashMap.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getInstance() {
        ClassLoader myClassLoader = SharedHashMap.class.getClassLoader();
        if (instance==null) {
            // The root classloader is sun.misc.Launcher package. If we are not in a sun package,
            // we need to get hold of the instance of ourself from the class in the root classloader.
        	// checks is ANT classloader
            if (myClassLoader.getClass().getName().startsWith("org.apache.tools.ant.loader.AntClassLoader") ||
            		myClassLoader.getClass().getName().startsWith(ReverseURLClassLoader.class.getName())) {
                    try {
						// So we find our parent classloader
						ClassLoader parentClassLoader = myClassLoader.getParent();
						// And get the other version of our current class
						Class otherClassInstance = parentClassLoader.loadClass(SharedHashMap.class.getName());
						// And call its getInstance method - this gives the correct instance of ourself
						Method getInstanceMethod = otherClassInstance.getDeclaredMethod("getInstance", new Class[] {});
						Object otherAbsoluteSingleton = getInstanceMethod.invoke(null, new Object[] { } );
						// But, we can't cast it to our own interface directly because classes loaded from
						// different classloaders implement different versions of an interface.
						// So instead, we use java.lang.reflect.Proxy to wrap it in an object that
						// supports our interface, and the proxy will use reflection to pass through all calls
						// to the object.
						instance = (Map<String, Object>) Proxy.newProxyInstance(myClassLoader,
						                                     new Class[] { Map.class },
						                                     new DelegateInvocationHandler(otherAbsoluteSingleton));
					} catch (SecurityException e) {
						e.printStackTrace();
//						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
//						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
//						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
//						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
//						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
//						LogAppl.getInstance().debug(e.getMessage(), e);
					}
            // We're in the root classloader, so the instance we have here is the correct one
            } else {
                instance = new SharedHashMap();
            }
        }
        return instance;
    }
}