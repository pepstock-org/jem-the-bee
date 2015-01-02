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
package org.pepstock.jem.springbatch.tasks;

import java.util.Map;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.springbatch.JemBean;
import org.pepstock.jem.springbatch.SpringBatchKeys;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.items.DataDescriptionItem;
import org.springframework.batch.core.Step;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Singleton which loads all steps, both tasklet and chunks, defined in JCL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class DefinitionsLoader {

	// this constant is not visible in SpringBatch
	// in this class
	// org.springframework.batch.core.configuration.xml.AbstractStepParser
	// you could find the property name
	private static final String TASKLETS = "tasklet";

	// this constant is not defined as constant in SpringBatch.
	// in this class
	// org.springframework.batch.core.configuration.xml.ChunkElementParser
	// there is the statement to set the property.
	private static final String CHUNKS = "hasChunkElement";

	// this constant is not defined as constant in SpringBatch.
	// in this class
	// org.springframework.batch.core.configuration.xml.ChunkElementParser
	// there is the statement to set the property.
	private static final String ITEM_READER = "itemReader";

	// this constant is not defined as constant in SpringBatch.
	// in this class
	// org.springframework.batch.core.configuration.xml.ChunkElementParser
	// there is the statement to set the property.
	private static final String ITEM_WRITER = "itemWriter";

	private static final DefinitionsLoader INSTANCE = new DefinitionsLoader();

	protected ConfigurableApplicationContext context = null;

	private String lockingScope = SpringBatchKeys.JOB_SCOPE;
	
	/**
	 * To avoid any instantiation
	 */
	private DefinitionsLoader() {
		
	}

	/**
	 * @return to get Loader instance in sigleton way
	 */
	public static DefinitionsLoader getInstance() {
		return INSTANCE;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(ConfigurableApplicationContext context) {
		this.context = context;
		JemBean bean = (JemBean) context.getBean(SpringBatchKeys.BEAN_ID);
		if (bean.getLockingScope() != null) {
			if (!bean.getLockingScope().equalsIgnoreCase(SpringBatchKeys.JOB_SCOPE) && !bean.getLockingScope().equalsIgnoreCase(SpringBatchKeys.STEP_SCOPE)) {
				throw new MessageRuntimeException(SpringBatchMessage.JEMS021E, bean.getLockingScope());
			}
			lockingScope = bean.getLockingScope();
		} else {
			lockingScope = SpringBatchKeys.JOB_SCOPE;
		}
		LogAppl.getInstance().emit(SpringBatchMessage.JEMS022I, bean.getJobName(), lockingScope);
	}

	/**
	 * @return true if locking scope is set to JOB
	 */
	public boolean isJobLockingScope() {
		return lockingScope.equalsIgnoreCase(SpringBatchKeys.JOB_SCOPE);
	}

	/**
	 * @return true if locking scope is set to STEP
	 */
	public boolean isStepLockingScope() {
		return lockingScope.equalsIgnoreCase(SpringBatchKeys.STEP_SCOPE);
	}

	/**
	 * Loads all definitions from JCL starting for all steps
	 */
	void loadForLock() {
		@SuppressWarnings("rawtypes")
		Map map = context.getBeansOfType(Step.class);
		if (!map.isEmpty()) {
			for (Object keyObject : map.keySet()) {
				String step = keyObject.toString();
				loadForLock(step);
			}
		}
	}

	/**
	 * Loads definitions from JCL starting for a step
	 * 
	 * @param step step name
	 */
	void loadForLock(String step) {
		// gets step bean definition
		BeanDefinition bDef = context.getBeanFactory().getBeanDefinition(step);
		// checks if you are using tasklets
		if (bDef.getPropertyValues().contains(TASKLETS)) {
			PropertyValue property = bDef.getPropertyValues().getPropertyValue(TASKLETS);
			if (property.getValue() instanceof RuntimeBeanReference) {
				RuntimeBeanReference task = (RuntimeBeanReference) property.getValue();
				Object object = context.getBean(task.getBeanName());
				// checks if tasklet is a JEm one,
				// with all datadescriptions
				if (object instanceof JemTasklet) {
					JemTasklet tasklet = (JemTasklet) object;
					// creates a definition
					Definition tasn = new Definition();
					tasn.setStepName(step);
					tasn.setObject(tasklet);
					DefinitionsContainer.getInstance().getObjects().add(tasn);
				}
			}
			// checks if chunks are used
		} else if (bDef.getPropertyValues().contains(CHUNKS)) {
			// checks for itemReader instance
			if (bDef.getPropertyValues().contains(ITEM_READER)) {
				PropertyValue property = bDef.getPropertyValues().getPropertyValue(ITEM_READER);
				if (property.getValue() instanceof RuntimeBeanReference) {
					RuntimeBeanReference task = (RuntimeBeanReference) property.getValue();
					Object object = context.getBean(task.getBeanName());
					// checks if is a DataDescription
					if (object instanceof DataDescriptionItem) {
						DataDescriptionItem item = (DataDescriptionItem) object;
						// creates a definition
						Definition tasn = new Definition();
						tasn.setStepName(step);
						tasn.setObject(item);
						DefinitionsContainer.getInstance().getObjects().add(tasn);
					}
				}
			}
			// checks for itemWriter instance
			if (bDef.getPropertyValues().contains(ITEM_WRITER)) {
				PropertyValue property = bDef.getPropertyValues().getPropertyValue(ITEM_WRITER);

				if (property.getValue() instanceof RuntimeBeanReference) {
					RuntimeBeanReference task = (RuntimeBeanReference) property.getValue();
					Object object = context.getBean(task.getBeanName());
					// checks if is a DataDescription
					if (object instanceof DataDescriptionItem) {
						DataDescriptionItem item = (DataDescriptionItem) object;
						// creates a definition
						Definition tasn = new Definition();
						tasn.setStepName(step);
						tasn.setObject(item);
						DefinitionsContainer.getInstance().getObjects().add(tasn);
					}
				}
			}
		}
	}
}