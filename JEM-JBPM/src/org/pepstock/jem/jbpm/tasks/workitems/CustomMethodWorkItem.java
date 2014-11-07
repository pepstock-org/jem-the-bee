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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.pepstock.jem.Result;
import org.pepstock.jem.jbpm.tasks.JemWorkItem;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.SetFields;

/**
 * Is a Jem WorkItem which wraps whatever class calling a specific method.<br>
 * Is able to call follwoing methods:<br>
 * <ul>
 * <li> int <code>method</>();
 * <li> int <code>method</>(Map<String, Object> parms);
 * <li> <code>any-result</code> <code>method</>();
 * <li> <code>any-result</code> <code>method</>(Map<String, Object> parms);
 * </ul>
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class CustomMethodWorkItem implements JemWorkItem {

	private Object instance = null;
	
	private String methodName = null;
	
	private Class<?> clazz = null;

	/**
	 * Construct the object storing the class and the method to call
	 * @param clazz custom class to call
	 * @param methodName method of the class to call
	 * @throws IllegalAccessException if any error occurs during the class invocation
	 * @throws InstantiationException if any error occurs during the class invocation 
	 */
    public CustomMethodWorkItem(Class<?> clazz, String methodName) throws InstantiationException, IllegalAccessException {
	    this.clazz = clazz;
	    this.methodName = methodName;
	    this.instance = clazz.newInstance();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.jbpm.tasks.JemWorkItem#execute(java.util.Map)
	 */
	@Override
	public int execute(Map<String, Object> parameters) throws Exception {
		// sets Fields if they are using annotations
		SetFields.applyByAnnotation(instance);	
		Method method = null;
		try {
			// before try to get the method without parms
			method = clazz.getMethod(methodName);
			return executeMethod(method, null);
		} catch (NoSuchMethodException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// if not finds the method without parms, try with a MAP as parm
			method = clazz.getMethod(methodName, Map.class);
			return executeMethod(method, parameters);
		}
	}
	
	/**
	 * Executes the method extracts from class, passing the right parameters and getting the return code, if there is
	 * @param method method to execute
	 * @param parameters map of parameters of JBPM work item
	 * @return return code of execution
	 * @throws JemException if any error occurs
	 * @throws IllegalArgumentException if any error occurs during the method invocation
	 * @throws IllegalAccessException if any error occurs during the method invocation
	 * @throws InvocationTargetException if any error occurs during the method invocation
	 */
	private int executeMethod(Method method, Map<String, Object> parameters) throws JemException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		// gets return type
		Class<?> result = method.getReturnType();
		// if parms not null, will pass the map to the method
		Object[] parms;
		if (parameters == null || parameters.isEmpty()){
			parms = null;
		} else {
			parms = new Object[]{parameters};
		}
		// if return class is a integer, means returns a integer
		// returns the integer
		if (result.equals(int.class)){
			return (Integer) method.invoke(instance, parms);
		} else {
			// invoke method without taking the result
			method.invoke(instance, parms);
			// returns always 0
			return Result.SUCCESS;
		}
	}

}
