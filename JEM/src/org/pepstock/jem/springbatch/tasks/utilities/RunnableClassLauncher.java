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
package org.pepstock.jem.springbatch.tasks.utilities;

import org.pepstock.jem.annotations.SetFields;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.node.tasks.jndi.AbsoluteHashMap;

/**
 * Is a wrapper to execute Runnable, applying all annotations that runnbale has dfined inside itself.
 * It must be load from the classloader of the custom runnable.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RunnableClassLauncher{
	
	/**
	 * Execute the runnable set on the method as parameter.
	 * @param runnable class to be executed
	 */
	public void run(Runnable runnable) {
		// saves the current context classloader
		ClassLoader savedContext = Thread.currentThread().getContextClassLoader();
		try {
			// sets new classloader for JNDI because it uses the context classloader
			Thread.currentThread().setContextClassLoader(AbsoluteHashMap.class.getClassLoader());
			// apply annotations
			SetFields.applyByAnnotation(runnable);
			runnable.run();
		} catch (Exception e) {
			throw new JemRuntimeException(e.getCause().getMessage(), e.getCause());
		} finally {
			// set again teh original context classloader
			Thread.currentThread().setContextClassLoader(savedContext);

		}
	}
}
