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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.pepstock.jem.Result;
import org.pepstock.jem.annotations.ReturnCode;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.log.LogAppl;

/**
 * Is singleton object. Only one instance of this container must instantiated.<br>
 * Contains all return code for all step which are able to manage the RC.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public final class ReturnCodesContainer {

	private static final String LEVEL_SEPARATOR = ".";
	
	private static final int LEVEL_WITHOUT_ID = 2;
	
	private static final int LEVEL_WITH_ID = 3;
	
	// format used for searching by target, task  and id
	private static final MessageFormat MESSAGE_FORMAT = new MessageFormat("{0}.{1}.{2}");

	private static final ReturnCodesContainer CONTAINER = new ReturnCodesContainer();

	private Map<String, Integer> mapReturnCodes = null;

	/**
	 * Private constructor which creates the map to save the references of data
	 * description
	 */
	private ReturnCodesContainer() {
		mapReturnCodes = new HashMap<String, Integer>();
	}
	
	/**
	 * Is singleton static method to retrieve the object. Only one instance of
	 * this container must instantiated.<br>
	 * If static instance is null, creates a new one.
	 * 
	 * @return container instance
	 */
	public static ReturnCodesContainer getInstance() {
		return CONTAINER;
	}


	/**
	 * Returns <code>true</code> if container contains the step, otherwise
	 * <code>false</code>.
	 * 
	 * @param step representation
	 * @return <code>true</code> if container contains the step
	 */
	boolean hasReturnCode(DataDescriptionStep step) {
		return hasReturnCode(createKey(step));
	}

	/**
	 * Returns return found by reference, otherwise <code>null</code>.
	 * 
	 * @param step step representation
	 * @return return code instance
	 */
	Integer getReturnCode(DataDescriptionStep step) {
		return getReturnCode(createKey(step));
	}

	/**
	 * Returns <code>true</code> if container contains the reference, otherwise
	 * <code>false</code>.
	 * 
	 * @param reference string reference representation
	 * @return <code>true</code> if container contains the reference
	 */
	boolean hasReturnCode(String reference) {
		String ref = normalizeReference(reference);
		return (ref != null) ? mapReturnCodes.containsKey(ref.toLowerCase()) : false;	
	}

	/**
	 * Returns return found by reference, otherwise <code>null</code>.
	 * 
	 * @param reference reference string representation
	 * @return return code instance
	 */
	Integer getReturnCode(String reference) {
		String ref = normalizeReference(reference);
		return (ref != null) ? mapReturnCodes.get(ref.toLowerCase()) : null;	
	}

	/**
	 * Adds new data description implementation, defined for passed target and
	 * task.
	 * 
	 * @param item task in executing
	 * @param rc return code to set
	 */
	void setReturnCode(DataDescriptionStep item, Integer rc) {
		setReturnCode(null, item, rc);
	}

	/**
	 * Adds new data description implementation, defined for passed target and
	 * task.
	 * 
	 * @param project ANT project
	 * @param item task in executing
	 * @param rc return code to set
	 */
	void setReturnCode(Project project, DataDescriptionStep item, Integer rc) {
		setReturnCode(project, item, null, rc);
	}

	/**
	 * Adds new data description implementation, defined for passed target and
	 * task.
	 * 
	 * @param project ANT project
	 * @param item task in executing
	 * @param rc return code to set
	 * @param property ANT property name to be set
	 */
	void setReturnCode(Project project, DataDescriptionStep item, String property, Integer rc) {
		// create a key using message format defined for reference
		String key = createKey(item);
		mapReturnCodes.put(key, rc);
		if (project != null){
			if (property == null) {
				PropertyHelper.getPropertyHelper(project).setNewProperty(key, rc);
			} else if (PropertyHelper.getPropertyHelper(project).getProperty(property) == null){
				PropertyHelper.getPropertyHelper(project).setNewProperty(property, rc);
			} else {
				PropertyHelper.getPropertyHelper(project).setProperty(property, rc, true);
			}
		}
	}


	/**
	 * Creates a key using the format defined for searching
	 * 
	 * @see ReturnCodesContainer#MESSAGE_FORMAT
	 * @param target target name
	 * @param task task name
	 * @return the key of map (always lower-case)
	 */
	String createKey(DataDescriptionStep item) {
		String key = MESSAGE_FORMAT.format(new Object[] { item.getTargetName(), item.getTaskName(), item.getId() }, new StringBuffer(), null).toString();
		return key.toLowerCase();
	}
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	private String normalizeReference(String reference){
		String[] levels = StringUtils.split(reference, LEVEL_SEPARATOR);
		if (levels != null){
			if (levels.length == LEVEL_WITH_ID){
				return reference;
			} else if (levels.length == LEVEL_WITHOUT_ID){
				String referenceNew = levels[0] + LEVEL_SEPARATOR + // target
						levels[1] + LEVEL_SEPARATOR + // task
						DataDescriptionStep.DEFAULT_ID; // id
				return referenceNew;		
			}
		}
		return null;
	}

	final int getReturnCode(Class<?> clazz){
		// scans all declared fields
		for (Field field : clazz.getDeclaredFields()){
			// if has got data description annotation
			if (field.isAnnotationPresent(ReturnCode.class)){
				// get the static field and nly if integer
				try {
					Object obj = null;
					if (Modifier.isStatic(field.getModifiers()) && (field.getType().equals(Integer.class) || field.getType().equals(int.class) )){
						obj = FieldUtils.readStaticField(field, true);
						return (Integer)obj;					
					} else {
						LogAppl.getInstance().emit(AntMessage.JEMA078E, field.getName());
					}
				} catch (IllegalAccessException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return Result.SUCCESS;
	}
	
	/**
	 * Returns the string representation of data description container (uses
	 * HaspMap to string method).
	 * 
	 * @see java.util.HashMap#toString()
	 * @return the string representation of data description container
	 */
	@Override
	public String toString() {
		return mapReturnCodes.toString();
	}

}