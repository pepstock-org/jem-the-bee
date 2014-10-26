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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class LauncherTasklet extends JemTasklet {
	
	private static final String MAIN_METHOD = "main";
	
	private String className = null;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution, ChunkContext chuckContext) throws TaskletException {
		if (className != null){
			// load by Class.forName
			Class<?> clazz;
	        try {
		        clazz = Class.forName(className);
	        } catch (ClassNotFoundException e) {
	        	LogAppl.getInstance().emit(SpringBatchMessage.JEMS052E, e, className);
				throw new TaskletException(SpringBatchMessage.JEMS052E.toMessage().getFormattedMessage(className), e);
	        }
	        
	        if (hasMainMethod(clazz)){
	        	 try {
					Method main = clazz.getMethod(MAIN_METHOD, String[].class);
					 main.invoke(null, (Object) null);
					 return RepeatStatus.FINISHED;
				} catch (SecurityException e) {
					throw new TaskletException(e);
				} catch (IllegalArgumentException e) {
					throw new TaskletException(e);
				} catch (NoSuchMethodException e) {
					throw new TaskletException(e);
				} catch (IllegalAccessException e) {
					throw new TaskletException(e);
				} catch (InvocationTargetException e) {
					throw new TaskletException(e);
				}
	        } else {
	        	Object instance;
	        	try {
	        		instance = Class.forName(className).newInstance();
	        	} catch (InstantiationException e) {
	        		throw new TaskletException(e);
	        	} catch (IllegalAccessException e) {
	        		throw new TaskletException(e);
	        	} catch (ClassNotFoundException e) {
	        		throw new TaskletException(e);
	        	}
	        	// check if it's a JemWorkItem. if not,
	        	// exception occurs. 
	        	if (instance instanceof Tasklet) {
	        		Tasklet customtasklet = (Tasklet) instance;
	        		try {
	        			return customtasklet.execute(stepContribution, chuckContext);
	        		} catch (Exception e) {
	        			throw new TaskletException(e);
	        		}
	        	} else {
	        		LogAppl.getInstance().emit(SpringBatchMessage.JEMS050E, className);
	        		throw new TaskletException(SpringBatchMessage.JEMS050E.toMessage().getFormattedMessage(className));
	        	}
	        }
		}
		LogAppl.getInstance().emit(SpringBatchMessage.JEMS051E, "className");
		throw new TaskletException(SpringBatchMessage.JEMS051E.toMessage().getFormattedMessage("className"));
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/**
	 * Is a static method which checks if the passed class has got a <code>main</code> method. 
	 * @param clazz class to be checked
	 * @return <code>true</code> if the class has got the main method
	 */
	private boolean hasMainMethod(Class<?> clazz){
		try {
	        clazz.getMethod(MAIN_METHOD, String[].class);
	        return true;
        } catch (Exception e) {
        	return false;
        }
	}
	
}
