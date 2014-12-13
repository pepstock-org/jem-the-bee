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
package org.pepstock.jem.springbatch.xml;

import java.util.List;

import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSource;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.Lock;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for complex XML element for tasklet <tasklet>.<br>
 * It uses the extensions XML authoring of SprigBatch.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class TaskletFactoryBean implements FactoryBean<Object> {
	
	static final String DATA_DESCRIPTIONS = "dataDescriptions";
	
	static final String DATA_SOURCES = "dataSources";
	
	static final String LOCKS = "locks";
	
	private JemTasklet tasklet = null;
	
	private List<DataDescription> dataDescriptions = null;
	
	private List<DataSource> dataSources = null;
	
	private List<Lock> locks = null;

	/**
	 * @return the tasklet
	 */
	public JemTasklet getTasklet() {
		return tasklet;
	}

	/**
	 * @param tasklet the tasklet to set
	 */
	public void setTasklet(JemTasklet tasklet) {
		this.tasklet = tasklet;
	}

	/**
	 * @return the dataDescriptions
	 */
	public List<DataDescription> getDataDescriptions() {
		return dataDescriptions;
	}

	/**
	 * @param dataDescriptions the dataDescriptions to set
	 */
	public void setDataDescriptions(List<DataDescription> dataDescriptions) {
		this.dataDescriptions = dataDescriptions;
	}

	/**
	 * @return the dataSources
	 */
	public List<DataSource> getDataSources() {
		return dataSources;
	}

	/**
	 * @param dataSources the dataSources to set
	 */
	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	/**
	 * @return the locks
	 */
	public List<Lock> getLocks() {
		return locks;
	}

	/**
	 * @param locks the locks to set
	 */
	public void setLocks(List<Lock> locks) {
		this.locks = locks;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		// checks if datadescriptions are set
	    if (this.dataDescriptions != null && !this.dataDescriptions.isEmpty()) {
	    	tasklet.setDataDescriptionList(dataDescriptions);
	    }
		// checks if datasources are set
	    if (this.dataSources != null && !this.dataSources.isEmpty()) {
	    	tasklet.setDataSourceList(dataSources);
	    }
		// checks if locks are set
		if (this.locks != null && !this.locks.isEmpty()) {
	    	tasklet.setLocks(locks);
	    }
		return tasklet;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<JemTasklet> getObjectType() {
		return JemTasklet.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}
}