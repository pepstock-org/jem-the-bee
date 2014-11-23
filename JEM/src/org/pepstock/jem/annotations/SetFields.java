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
package org.pepstock.jem.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.pepstock.jem.jppf.DataStreamNameClassPair;
import org.pepstock.jem.jppf.UniqueInitialContext;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * Utility to set method value using the annotations toassign data sources and data description
 * 
 * @see AssignDataDescription
 * @see AssignDataSource
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class SetFields {

	/**
	 * To avoid any instantiation
	 */
	private SetFields() {
		
	}

	/**
	 * Assigns the value of data description and data source starting from a class. This is used when you have to set fields of java main class.
	 * @param clazz java main class
	 * @throws NamingException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 */
	public static void applyByAnnotation(Class<?> clazz) throws NamingException, IllegalAccessException{
		applyByAnnotation(null, clazz,  false);
	}
	
	/**
	 * Assigns the value of data description and data source to a object.
	 * @param object instance of object to set fields
	 * @throws NamingException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 */
	public static void applyByAnnotation(Object object) throws NamingException, IllegalAccessException{
		applyByAnnotation(object, false);
	}
	
	/**
	 * Assigns the value of data description and data source to a object, checking if for JPPF integration or not
	 * @param object instance of object to set fields
	 * @param isUsingUniqueInitialContext if is JPPF integration
	 * @throws NamingException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 */
	public static void applyByAnnotation(Object object, boolean isUsingUniqueInitialContext) throws NamingException, IllegalAccessException{
		applyByAnnotation(object, object.getClass(), false);
	}
	
	/**
	 * Assigns the value of data description and data source to a object, checking if for JPPF integration or not
	 * @param object instance of object to set fields (can be null for java main class call)
	 * @param clazz class for reflection
	 * @param isUsingUniqueInitialContext if is JPPF integration
	 * @throws NamingException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 */
	private static void applyByAnnotation(Object object, Class<?> clazz, boolean isUsingUniqueInitialContext) throws NamingException, IllegalAccessException{
		// new initial context to access by JNDI to COMMAND DataDescription
		InitialContext ic = null;

		// scans all declared fields
		for (Field field : clazz.getDeclaredFields()){
			// if has got data description annotation
			if (field.isAnnotationPresent(AssignDataDescription.class)){
				// check initial context to instantiate once
				if (ic == null){
					// checks if is JPPF integration which used another context
					ic = (isUsingUniqueInitialContext) ? UniqueInitialContext.getContext() : ContextUtils.getContext();
				}
				// gets annotation
				AssignDataDescription annotation = (AssignDataDescription)field.getAnnotation(AssignDataDescription.class);
				
				checkIfIsRightObject(annotation.value(), true);

				// sets field
				setFieldByAnnotation(ic, object, annotation.value(), field);
			} else if (field.isAnnotationPresent(AssignDataSource.class)){
				// check initial context to instantiate once
				if (ic == null){
					// checks if is JPPF integration which used another context
					ic = (isUsingUniqueInitialContext) ? UniqueInitialContext.getContext() : ContextUtils.getContext();
				}
				// gets annotation
				AssignDataSource annotation = (AssignDataSource)field.getAnnotation(AssignDataSource.class);
				
				checkIfIsRightObject(annotation.value(), false);

				// sets field
				setFieldByAnnotation(ic, object, annotation.value(), field);
			}
		}
	}
	
	private static void setFieldByAnnotation(InitialContext ic, Object object, String name, Field field) throws NamingException, IllegalAccessException{
		// gets object via JNDI
		Object objectJNDI = (Object) ic.lookup(name);
		
		// if is static or it's a java main class (object = null) sets statically
		if (Modifier.isStatic(field.getModifiers()) || object == null){
			FieldUtils.writeStaticField(field, objectJNDI, true);
		} else {
			// sets field
			FieldUtils.writeField(field, object, objectJNDI, true);
		}
	}
	
	private static void checkIfIsRightObject(String name, boolean isDD) throws NamingException{
		InitialContext ic = ContextUtils.getContext();
		NamingEnumeration<NameClassPair> list = ic.list(name);
		boolean isDataDescription = false;
		while(list.hasMore()){
			NameClassPair pair = list.next();
			// checks if is datastream
			// only datastreams are changed
			if (pair instanceof DataStreamNameClassPair){
				isDataDescription = true;
			}
		}
		if (isDD && !isDataDescription){
			throw new NamingException(name+" is not a data description");
		}
		if (!isDD && isDataDescription){
			throw new NamingException(name+" is not a data source");
		}
	}

}
