/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;


/**
 * With this HashMap you can share teh data of map across hierarchy of Classloaders.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */

public final class AbsoluteHashMap extends HashMap<String, Object> implements Map<String, Object>{

	private static final long serialVersionUID = 1L;
	/**
     *  Common instance shared across a proxy
     */
    private static Map<String, Object> instance = null;
    
    
    /**
     * Empty and private constructor
     */
    private AbsoluteHashMap() {
    }

    /**
     * This is a singleton. If instance is not null, means that local map is already loaded.
     * If null and classload is ANT classloader, then uses the proxy to load the instance from parent classloader, 
     * otherwise it creates a new instance.
     * @return shared HashMap.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static synchronized Map<String, Object> getInstance() {
        ClassLoader myClassLoader = AbsoluteHashMap.class.getClassLoader();
        if (instance==null) {
            // The root classloader is sun.misc.Launcher package. If we are not in a sun package,
            // we need to get hold of the instance of ourself from the class in the root classloader.
        	// checks is ANT classloader
            if (myClassLoader.getClass().getName().startsWith("org.apache.tools.ant.loader.AntClassLoader")) {
                    try {
						// So we find our parent classloader
						ClassLoader parentClassLoader = myClassLoader.getParent();
						// And get the other version of our current class
						Class otherClassInstance = parentClassLoader.loadClass(AbsoluteHashMap.class.getName());
						// And call its getInstance method - this gives the correct instance of ourself
						Method getInstanceMethod = otherClassInstance.getDeclaredMethod("getInstance", new Class[] { String.class });
						String internalKey = createKey();
						Object otherAbsoluteSingleton = getInstanceMethod.invoke(null, new Object[] { internalKey } );
						// But, we can't cast it to our own interface directly because classes loaded from
						// different classloaders implement different versions of an interface.
						// So instead, we use java.lang.reflect.Proxy to wrap it in an object that
						// supports our interface, and the proxy will use reflection to pass through all calls
						// to the object.
						instance = (Map<String, Object>) Proxy.newProxyInstance(myClassLoader,
						                                     new Class[] { Map.class },
						                                     new DelegateInvocationHandler(otherAbsoluteSingleton));
					} catch (SecurityException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (ClassNotFoundException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (NoSuchMethodException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (IOException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (IllegalAccessException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						LogAppl.getInstance().debug(e.getMessage(), e);
					}
            // We're in the root classloader, so the instance we have here is the correct one
            } else {
                instance = new AbsoluteHashMap();
            }
        }

        return instance;
    }
    
    /**
     * THis method is call by proxy to get list of JNDI resources. The argument is InternalKey object, not 
     * visible outside of package, serialized and base64. The internal key must contain the output path
     * so we're sure that if someone is able to read the list of byte, is not able to use because output path
     * is related to job.
     *    
     * @param internalKeyString InternalKey objct, serialized and base 64
     * @return list of JNDI resources
     */
    public static synchronized Map<String, Object> getInstance(String internalKeyString) {
    	try {
    		// decodes internalKey
    		byte[] internalKey = Base64.decodeBase64(internalKeyString);
    		ByteArrayInputStream bais = new ByteArrayInputStream(internalKey);
    		ObjectInputStream ois = new ObjectInputStream(bais);
    		InternalKey key = (InternalKey) ois.readObject();
    		// if keyValue must be the same of output path value
    		if (key.getValue() == null){
    			throw new MessageRuntimeException(NodeMessage.JEMC236E);
    		}
    		if (!key.getValue().equalsIgnoreCase(System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME))){
    			throw new MessageRuntimeException(NodeMessage.JEMC236E);
    		}
    		ois.close();
    	} catch (IOException e) {
    		throw new MessageRuntimeException(NodeMessage.JEMC236E, e);
    	} catch (ClassNotFoundException e) {
    		throw new MessageRuntimeException(NodeMessage.JEMC236E, e);
    	}
    	return instance;
    }
    
    /**
     * create a InternalKey object, set the output path value, serializes in bytes and encodes in base64
     * 
     * @return a string which represent a internalKey
     * @throws IOException if any exception occurs during the serialization
     */
    private static String createKey() throws IOException{
        InternalKey key = new InternalKey();
        key.setValue(System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(key);
        oos.close();
        byte[] b = baos.toByteArray();
        return Base64.encodeBase64String(b);
    }

}