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

import javax.naming.NamingException;

import org.pepstock.jem.annotations.SetFields;
import org.pepstock.jem.log.JemException;

/**
 * Is a standard main class which calls another main class. This is used in ANT to use annotations on main class with classpath.
 * The last argument is the main class to execute.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JavaMainClassLauncher {
	
	private static final String MAIN_METHOD = "main";

	/**
	 * Empty constructor
	 */
	private JavaMainClassLauncher() {

	}

	/**
	 * Is a standard main class which calls another main class. This is used in ANT to use annotations on main class with classpath.
	 * The last argument is the main class to execute.
	 * @param args arguments. The last one is the main class to be executes
	 * @throws ClassNotFoundException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 * @throws NamingException if any error occurs
	 * @throws NoSuchMethodException if any error occurs
	 * @throws SecurityException if any error occurs
	 * @throws JemException 
	 * @throws InvocationTargetException if any error occurs
	 * @throws IllegalArgumentException if any error occurs
	 */
	public static void main(String[] args) throws SecurityException, NoSuchMethodException, NamingException, IllegalAccessException, ClassNotFoundException, JemException {
		// checks if has got argument. MUST have
		if (args != null){
			// init vars
			String className = null;
			String[] argsToBePassed;
			// if has got only 1 paramter, has got only main class
			if (args.length == 1){
				className = args[0];
				argsToBePassed = new String[0];
			} else {
				// more than 1 arg, therefore extract class name (last arg)
				className = args[args.length-1];
				// and copies all arguments to another array
				argsToBePassed = new String[args.length-1];
				System.arraycopy(args, 0, argsToBePassed, 0, args.length-1);
			}
			// reflection to get class
			Class<?> clazz = Class.forName(className);
			// apply annotations
			SetFields.applyByAnnotation(clazz);

			// sets Fields if they are using annotations
			Method main = clazz.getMethod(MAIN_METHOD, String[].class);
			// static method doesn't have an instance, for this reason the first parameter is null
			try {
				main.invoke(null, (Object) argsToBePassed);
			} catch (InvocationTargetException e) {
				throw new JemException(e.getCause().getMessage(), e);
			} 
		}
	}

}
