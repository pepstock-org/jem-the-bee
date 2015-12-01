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
package org.pepstock.jem.springbatch.tasks.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.naming.NamingException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.pepstock.jem.annotations.AssignChunkContext;
import org.pepstock.jem.annotations.AssignStepContribution;
import org.pepstock.jem.annotations.SetFields;
import org.pepstock.jem.annotations.ToBeExecuted;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Is a JemTasklet which is able to execute a runnable or another bean loaded in sprinbatch. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class LauncherTasklet extends JemTasklet {

	private Object object = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.springbatch4.tasks.JemTasklet#run(org.springframework
	 * .batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution, ChunkContext chunkContext) throws TaskletException {
		if (object == null){
			LogAppl.getInstance().emit(SpringBatchMessage.JEMS051E, "object");
			throw new TaskletException(SpringBatchMessage.JEMS051E.toMessage().getFormattedMessage("object"));
		}

		// load by Class.forName
		Class<?> clazz = object.getClass();
		// executes beans
		try {
			// applies all annotations
			SetFields.applyByAnnotation(object);
			// applies annotation with step contribution and chunk
			applyByAnnotation(clazz, object, stepContribution, chunkContext);

			// check if it's a Tasklet and not JemTasklet. if not,
			// exception occurs.
			if (object instanceof Runnable) {
				run((Runnable) object);
			} else {
				// scans the method to see if there is any method to be executed
				Method method = null;
				for (Method m : clazz.getDeclaredMethods()){
					// checks if there is the annotation
					// and not already set
					if (m.isAnnotationPresent(ToBeExecuted.class) && method == null){
						//set method name
						method = m;
					}
				}
				// if method is null, means that it doesn't know which method must be executed
				if (method == null){
					LogAppl.getInstance().emit(SpringBatchMessage.JEMS050E, clazz.getName());
					throw new TaskletException(SpringBatchMessage.JEMS050E.toMessage().getFormattedMessage(clazz.getName()));
				}
				// loads parameters. 
				// if methods doesn't have any parameter, parms to null
				// otherwise step contribution and chunk context must be passed to the method
				Object[] parms;
				if (method.getParameterTypes().length == 0){
					// if parms not null, will pass the map to the method
					parms = null;
				} else {
					parms = new Object[]{stepContribution, chunkContext};
				}
				// invoke
				method.invoke(object, parms);
			}
		} catch (SecurityException e) {
			throw new TaskletException(e);
		} catch (NamingException e) {
			throw new TaskletException(e);
		} catch (IllegalAccessException e) {
			throw new TaskletException(e);
		} catch (IllegalArgumentException e) {
			throw new TaskletException(e);
		} catch (InvocationTargetException e) {
			throw new TaskletException(e);
		}
		return RepeatStatus.FINISHED;
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}
	
	/**
	 * Runs the object instance
	 * @param runnable object runnable instance of jcl, to be execute
	 * @throws TaskletException if any error occurs during the runnable execution
	 */
	private void run(Runnable runnable) throws TaskletException{
		try {
			runnable.run();
		} catch (Exception e) {
			throw new TaskletException(e);
		}
	}
	
	/**
	 * Assigns the value of step contribution and chunk context 
	 * @param clazz class for reflection
	 * @param stepContribution step contribution, passed by SpringBatch core
	 * @param chunkContext chunk context, passed by SpringBatch core
	 * @throws IllegalAccessException if any error occurs
	 */
	private void applyByAnnotation(Class<?> clazz, Object instance, StepContribution stepContribution, ChunkContext chunkContext) throws IllegalAccessException {
		// scans all declared fields
		for (Field field : clazz.getDeclaredFields()){
			// if has got data description annotation
			if (field.isAnnotationPresent(AssignStepContribution.class)){
				 if (Modifier.isStatic(field.getModifiers())){
					 FieldUtils.writeStaticField(field, stepContribution, true);
				 } else {
					 FieldUtils.writeField(field, instance, stepContribution, true);
				 }
			} else if (field.isAnnotationPresent(AssignChunkContext.class)){
				 if (Modifier.isStatic(field.getModifiers())){
					 FieldUtils.writeStaticField(field, chunkContext, true);
				 } else {
					 FieldUtils.writeField(field, instance, chunkContext, true);
				 }
			}
		}
	}
}
