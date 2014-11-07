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
package org.pepstock.jem.jbpm.tasks.workitems;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pepstock.jem.jbpm.JBpmKeys;
import org.pepstock.jem.jbpm.tasks.JemWorkItem;
import org.pepstock.jem.util.SetFields;

/**
 * Is a wrapper to another JemWorkItem, preparing a new map to pass as argument
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class DelegatedWorkItem implements JemWorkItem {
	
	private Object instance = null;

	/**
	 * Stores the object instance to call
	 * 
	 * @param instance JemWorkItem instance to call
	 */
    public DelegatedWorkItem(Object instance) {
	    this.instance = instance;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.jbpm.tasks.JemWorkItem#execute(java.util.Map)
	 */
	@Override
	public int execute(Map<String, Object> parameters) throws Exception {
		// sets Fields if they are using annotations
		SetFields.applyByAnnotation(instance);
		
		JemWorkItem jemWorkItem = (JemWorkItem) instance;
		
		// loads all parameters but not the "jem.*" ones
		Map<String, Object> parms = new HashMap<String, Object>();
		for (Entry<String, Object> entry : parms.entrySet()){
			String name = entry.getKey();
			if (!name.startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX) && 
					!name.startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX) && 
					!name.equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY)){
				parms.put(name, entry.getValue());
			}
		}// executes the workItem
		return jemWorkItem.execute(parameters);
	}

}
