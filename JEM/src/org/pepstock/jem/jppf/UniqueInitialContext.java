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
package org.pepstock.jem.jppf;

import javax.naming.InitialContext;

/**
 * Uses a ThreadLocal to isolate JNDI initial context in multi-threading environment as 
 * JPPF node is. This avoids conflicts on different contexts with different references.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class UniqueInitialContext extends ThreadLocal<InitialContext>{

	private static final ThreadLocal<InitialContext> CONTEXT = new ThreadLocal<InitialContext>();
	
	/**
	 * Sets JNDI context
	 * @param context JNDI context
	 */
	public static final void setContext(InitialContext context){
		CONTEXT.set(context);
	}

	/**
	 * Returns JNDI context
	 * @return JNDI context
	 */
	public static final InitialContext getContext(){
		return CONTEXT.get();
	}

}