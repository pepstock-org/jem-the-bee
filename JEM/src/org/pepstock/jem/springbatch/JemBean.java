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
package org.pepstock.jem.springbatch;

import org.pepstock.jem.AbstractJcl;
import org.pepstock.jem.springbatch.tasks.managers.DefinitionsLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Contains all attributes to assign to job. it's a bean so you could add to
 * SpringBatch code.<br>
 * Only JobName is mandatory. All others are optional, using a default value.<br>
 * Implements ApplicationContextAware to set to job and steps JEM StepListener
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class JemBean extends AbstractJcl implements ApplicationContextAware{

	private static final long serialVersionUID = 1L;

	private String classPath = null;
	
	private String lockingScope = SpringBatchKeys.JOB_SCOPE;
	
	private String options= null;
	
	private String parameters = null;
	
	/**
	 * Empty constructor
	 */
	public JemBean() {
		
	}

	/**
	 * Returns the class path necessary to Springbatch to execute job.
	 * This is not the System class path.
	 * 
	 * @return the classPath
	 */
	public String getClassPath() {
		return classPath;
	}

	/**
	 * Sets the class path necessary to Springbatch to execute job.
	 * This is not the System class path.
	 * 
	 * @param classPath the classPath to set
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * @return the lockingScope
	 */
	public String getLockingScope() {
		return lockingScope;
	}

	/**
	 * @param lockingScope the lockingScope to set
	 */
	public void setLockingScope(String lockingScope) {
		this.lockingScope = lockingScope;
	}
	
	/**
	 * @return the options
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		DefinitionsLoader.getInstance().setContext((ConfigurableApplicationContext)context);
	}
}