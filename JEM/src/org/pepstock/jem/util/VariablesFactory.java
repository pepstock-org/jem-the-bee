/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.pepstock.jem.Job;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.LogAppl;

/**
 * <code>Factory</code> class useful to build {@link Properties} containing all
 * field values of <b>Jem</b> classes, like {@link Job} and {@link NodeInfoBean}
 * . <br>
 * In particular <code>VariablesFactory</code> build a <code>Properties</code>
 * table so done: <br>
 * - the keys of the table are the fields so encoded: <dd>
 * <b>jem.classType.fieldName</b> or <b>jem.classType.fieldName.subFieldName</b>
 * (recursive) where <br> <dd>- <b>jem</b> is the
 * {@link VariablesFactory#JEM_VARIABLE_PREFIX}, the Jem variable prefix <br>
 * <dd>- <b>.</b> is the {@link VariablesFactory#JEM_VARIABLES_SEPARATOR}, the
 * Jem variable separator <br> <dd>- <b>classType</b> is the a named used to
 * identify a class type (for example
 * {@link VariablesFactory#JOB_VARIABLE_PREFIX} or
 * {@link VariablesFactory#NODE_INFO_VARIABLE_PREFIX}) <br> <dd>-
 * <b>fieldName</b> the java class field name <br> <dd>- <b>subFieldName</b> the
 * java class field name of the previous field (<code>fieldName</code>) if
 * <code>fieldName</code> is an <code>Object</code> class (this done
 * recursively) <br>
 * - the values in the table are the toString() values of the associated key
 * fields.
 * 
 * 
 * @author Alessandro Zambrini
 * @version 1.0
 * 
 */
public class VariablesFactory {

	/**
	 * It is the separator used inside the Jem variables (the keys of the
	 * {@link Properties} created)
	 */
	public static final String JEM_VARIABLES_SEPARATOR = ".";

	/**
	 * It is prefix used for the Jem variables (the keys of the
	 * {@link Properties} created)
	 */
	public static final String JEM_VARIABLE_PREFIX = "jem" + JEM_VARIABLES_SEPARATOR;

	/**
	 * It is the a named used to identify the {@link Job} class type inside the
	 * Jem variables (the keys of the {@link Properties} created)
	 */
	public static final String JOB_VARIABLE_PREFIX = JEM_VARIABLE_PREFIX + "job" + JEM_VARIABLES_SEPARATOR;

	/**
	 * It is the a named used to identify the {@link NodeInfoBean} class type
	 * inside the Jem variables (the keys of the {@link Properties} created)
	 */
	public static final String NODE_INFO_VARIABLE_PREFIX = JEM_VARIABLE_PREFIX + "nodeInfo" + JEM_VARIABLES_SEPARATOR;

	/**
	 * It is the default value of a field that is <code>null</code>.
	 */
	public static final String VARIABLE_NO_VALUE = "N/A (null)";
	
	/**
	 * To avoid any instantiation
	 */
	private VariablesFactory() {
		
	}

	/**
	 * This methods build a {@link Properties} containing all field values of a
	 * {@link Job} and of a {@link NodeInfoBean}: <br>
	 * - the keys of the table are the fields of the <code>Job</code> and of the
	 * <code>NodeInfoBean</code>. For example:<br>
	 * <dd><b>jem.job.name</b> or <b>jem.job.result.returnCode</b> or
	 * <b>jem.nodeInfo.ipaddress</b> <br> <dd>the string <b>jem.job.</b> is
	 * {@link VariablesFactory#JOB_VARIABLE_PREFIX} <br> <dd>the string
	 * <b>jem.nodeInfo.</b> is
	 * {@link VariablesFactory#NODE_INFO_VARIABLE_PREFIX} <br>
	 * - the values of the table are the fields values (toString()). For example
	 * <dd>the value of the field <b>name</b> of the <code>Job</code> <dd>the
	 * value of the field <b>returnCode</b> of the field <b>result</b> of the
	 * <code>Job</code> <dd>the value of the field <b>ipaddress</b> of the
	 * <code>NodeInfoBean</code>
	 * 
	 * @param job the <code>Job</code> from which extract field keys and field
	 *            values.
	 * @param nodeInfo the <code>NodeInfoBean</code> from which extract field
	 *            keys and field values.
	 * @return the <code>Properties</code> created.
	 * @see Properties
	 * @throws VariablesFactoryException throwed if an exception occurs.
	 */
	public static Properties createVariables(Job job, NodeInfoBean nodeInfo) throws VariablesFactoryException {
		Properties properties = new Properties();
		Properties jobVariables = createVariables(job);
		Properties nodeInfoVariables = createVariables(nodeInfo);
		properties.putAll(jobVariables);
		properties.putAll(nodeInfoVariables);
		return properties;
	}

	/**
	 * This methods build a {@link Properties} containing all field values of a
	 * {@link Job}: <br>
	 * - the keys of the table are the fields of the <code>Job</code>. For
	 * example: <br>
	 * <dd><b>jem.job.name</b> or <b>jem.job.result.returnCode</b> <br> <dd>the
	 * string <b>jem.job.</b> is {@link VariablesFactory#JOB_VARIABLE_PREFIX} <br>
	 * - the values of the table are the fields values (toString()). For
	 * example: <br> <dd>the value of the field <b>name</b> of the
	 * <code>Job</code>. <br> <dd>the value of the field <b>returnCode</b> of
	 * the field <b>result</b> of the <code>Job</code>.
	 * 
	 * @param job the <code>Job</code> from which extract field keys and field
	 *            values.
	 * @return the <code>Properties</code> created.
	 * @see Properties
	 * @throws VariablesFactoryException throwed if an exception occurs.
	 */
	public static Properties createVariables(Job job) throws VariablesFactoryException {
		try {
			Properties properties = new Properties();
			Field[] declaredFields = Job.class.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				if (!Modifier.isStatic(field.getModifiers())) {
					boolean isAccessible = field.isAccessible();
					if (!isAccessible) {
						field.setAccessible(true);
					}
					addObjectFieldVariable(field, job, JOB_VARIABLE_PREFIX, properties);
					Object fieldValue = field.get(job);
					addObjectFieldVariables(fieldValue, field.getType(), JOB_VARIABLE_PREFIX + field.getName() + JEM_VARIABLES_SEPARATOR, properties);
					if (!isAccessible) {
						field.setAccessible(false);
					}
				}
			}
			return properties;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UtilMessage.JEMB004E, ex, job);
			throw new VariablesFactoryException(UtilMessage.JEMB004E.toMessage().getFormattedMessage(job));
		}
	}

	/**
	 * This methods build a {@link Properties} containing all field values of a
	 * {@link NodeInfoBean}: <br>
	 * - the keys of the table are the fields of the <code>NodeInfoBean</code>.
	 * For example: <br>
	 * <dd><b>jem.nodeInfo.ipaddress</b> <dd>the string <b>jem.nodeInfo.</b> is
	 * {@link VariablesFactory#NODE_INFO_VARIABLE_PREFIX} <br>
	 * - the values of the table are the fields values (toString()). For
	 * example: <br> <dd>the value of the field <b>ipaddress</b> of the
	 * <code>NodeInfoBean</code>.
	 * 
	 * @param nodeInfo the <code>NodeInfoBean</code> from which extract field
	 *            keys and field values.
	 * @return the <code>Properties</code> created
	 * @see Properties
	 * @throws VariablesFactoryException throwed if an exception occurs.
	 * @see VariablesFactoryException
	 */
	public static Properties createVariables(NodeInfoBean nodeInfo) throws VariablesFactoryException {
		try {
			Properties properties = new Properties();
			Field[] declaredFields = NodeInfoBean.class.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				if (!Modifier.isStatic(field.getModifiers())) {
					boolean isAccessible = field.isAccessible();
					if (!isAccessible) {
						field.setAccessible(true);
					}
					addObjectFieldVariable(field, nodeInfo, NODE_INFO_VARIABLE_PREFIX, properties);
					Object fieldValue = field.get(nodeInfo);
					addObjectFieldVariables(fieldValue, field.getType(), NODE_INFO_VARIABLE_PREFIX + field.getName() + JEM_VARIABLES_SEPARATOR, properties);
					if (!isAccessible) {
						field.setAccessible(false);
					}
				}
			}
			return properties;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UtilMessage.JEMB004E, ex, nodeInfo);
			throw new VariablesFactoryException(UtilMessage.JEMB004E.toMessage().getFormattedMessage(nodeInfo));
		}
	}

	/**
	 * This method add to a parameter {@link Properties} all the field values of
	 * an <code>Object</code> class. <br>
	 * - the keys inserted in the table are the fields so encoded: <br>
	 * <dd><b>variablePrefix.fieldName</b> (recursive) where <br> <dd>-
	 * <b>variablePrefix.</b> is the variable prefix used for the keys. It may
	 * contains more object field names because it works recursively. <br> <dd>-
	 * <b>fieldName</b> the java class field name of the parameter
	 * <code>fieldObject</code> <br>
	 * - the values inserted in the table are the toString() values of the
	 * associated key fields.
	 * 
	 * @param fieldObject the <code>Object</code> class from which get field
	 *            keys and their values.
	 * @param fieldClass the <code>Class</code> type of the parameter
	 *            <code>fieldObject</code>.
	 * @param variablePrefix the prefix used in the keys.
	 * @param properties the <code>Properties</code> in which the method adds
	 *            keys and values.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @see Properties
	 * @throws Exception throwed if an exception occurs.
	 */
	@SuppressWarnings("rawtypes")
	private static void addObjectFieldVariables(Object fieldObject, Class fieldClass, String variablePrefix, Properties properties) throws IllegalArgumentException, IllegalAccessException  {
		// I inspect only Jem the bee classes
		if (!fieldClass.isPrimitive() && fieldClass.getClassLoader() != String.class.getClassLoader()) {
			Field[] declaredFields = fieldClass.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				if (!Modifier.isStatic(field.getModifiers())) {
					boolean isAccessible = field.isAccessible();
					if (!isAccessible) {
						field.setAccessible(true);
					}
					addObjectFieldVariable(field, fieldObject, variablePrefix, properties);
					Object fieldValue = null;
					if (null != fieldObject) {
						fieldValue = field.get(fieldObject);
					}
					addObjectFieldVariables(fieldValue, field.getType(), variablePrefix + field.getName() + JEM_VARIABLES_SEPARATOR, properties);
					if (!isAccessible) {
						field.setAccessible(false);
					}
				}
			}
		}
	}

	/**
	 * This method add to a parameter {@link Properties} a field value of an
	 * <code>Object</code> class. <br>
	 * - the keys inserted in the table are the fields so encoded: <br>
	 * <dd><b>variablePrefix.fieldName</b> (recursive) where <dd>-
	 * <b>variablePrefix.</b> is the variable prefix used for the key. It may
	 * contains <code>Object</code> field names because it works recursively.
	 * <dd>- <b>fieldName</b> the java class field name of the field parameter
	 * <code>fieldOwner</code> <br>
	 * - the value inserted in the table is the toString() value of the
	 * associated field of the <code>Object</code> class.
	 * 
	 * @param field the field from which extract the value
	 * @param fieldOwner the <code>Object</code> from which is extracted the
	 *            field value
	 * @param variablePrefix the prefix used in the key.
	 * @param properties the <code>Properties</code> in which the method adds
	 *            the key and the value.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @see Properties
	 * @throws Exception throwed if an exception occurs.
	 */
	private static void addObjectFieldVariable(Field field, Object fieldOwner, String variablePrefix, Properties properties) throws IllegalArgumentException, IllegalAccessException {
		String fieldName = field.getName();
		Object fieldValue = null;
		if (null != fieldOwner) {
			fieldValue = field.get(fieldOwner);
		}
		if (null != fieldValue) {
			properties.put(variablePrefix + fieldName, fieldValue.toString());
			LogAppl.getInstance().emit(UtilMessage.JEMB005I, new Object[] { variablePrefix + fieldName, fieldValue });
		} else {
			properties.put(variablePrefix + fieldName, VARIABLE_NO_VALUE);
			LogAppl.getInstance().emit(UtilMessage.JEMB005I, new Object[] { variablePrefix + fieldName, VARIABLE_NO_VALUE });
		}
	}
}