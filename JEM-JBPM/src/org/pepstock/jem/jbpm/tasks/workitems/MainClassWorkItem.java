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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.reflect.FieldUtils;
import org.pepstock.jem.Result;
import org.pepstock.jem.annotations.SetFields;
import org.pepstock.jem.jbpm.JBpmKeys;
import org.pepstock.jem.jbpm.annotations.AssignParameters;
import org.pepstock.jem.jbpm.tasks.JemWorkItem;
import org.pepstock.jem.log.LogAppl;

/**
 * Is a JemWorkItem which is able to call the <code>public static void main</code> method.<br>
 * All parameters, prepared by JBPM for workItem, will be passed as main arguments, with the format <code>key=value</code>.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class MainClassWorkItem implements JemWorkItem {
	
	private static final String MAIN_METHOD = "main";
	
	private Class<?> clazz = null;

	/**
	 * Constructor which stores the class to call
	 * 
	 * @param clazz the class to call
	 */
    public MainClassWorkItem(Class<?> clazz) {
	    this.clazz = clazz;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.jbpm.tasks.JemWorkItem#execute(java.util.Map)
	 */
	@Override
	public int execute(Map<String, Object> parameters) throws Exception {
		SetFields.applyByAnnotation(clazz);
		applyByAnnotation(clazz, parameters);

		// sets Fields if they are using annotations
		Method main = clazz.getMethod(MAIN_METHOD, String[].class);
		// init params accordingly
		String[] params = null; 
		if (!parameters.isEmpty()){
			// creates a list because not all values of parameters MAP will be passed
			List<String> paramsList = new LinkedList<String>();
			// scans all entrys removing all JEM.* parameters and parameters which has got a key with =
			for (Entry<String, Object> entry : parameters.entrySet()){
				String name = entry.getKey();
				if (!name.startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX) && 
						!name.startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX) && 
						!name.equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY) &&
						!name.contains("=")){
					// adds with format key=value
					paramsList.add(entry.getKey()+"="+entry.getValue().toString());
				}
			}
			if (!paramsList.isEmpty()){
				// gets in Array format
				params = paramsList.toArray(new String[0]);
			}
		}
		// static method doesn't have an instance, for this reason the first parameter is null
		main.invoke(null, (Object) params); 
		return Result.SUCCESS;
	}
	
	/**
	 * Is a static method which checks if the passed class has got a <code>main</code> method. 
	 * @param clazz class to be checked
	 * @return <code>true</code> if the class has got the main method
	 */
	public static final boolean hasMainMethod(Class<?> clazz){
		try {
			Method method = clazz.getMethod(MAIN_METHOD, String[].class);
			return Modifier.isStatic(method.getModifiers());
        } catch (Exception e) {
        	LogAppl.getInstance().ignore(e.getMessage(), e);
        	return false;
        }
	}
	
	/**
	 * Assigns the value of parameters 
	 * @param object instance of object
	 * @param parameters parameters to set
	 * @throws IllegalAccessException if any error occurs
	 */
	private void applyByAnnotation(Class<?> clazz, Map<String, Object> parameters) throws IllegalAccessException {
		// scans all declared fields
		for (Field field : clazz.getDeclaredFields()){
			// if has got data description annotation
			if (field.isAnnotationPresent(AssignParameters.class) && (Modifier.isStatic(field.getModifiers()))){
				FieldUtils.writeStaticField(field, parameters, true);	
			}
		}
	}
}
