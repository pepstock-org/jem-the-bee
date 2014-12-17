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

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.springbatch.items.DataDescriptionItemReader;
import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSource;
import org.pepstock.jem.springbatch.tasks.Lock;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for complex XML element for item reader <itemReader>.<br>
 * It uses the extensions XML authoring of SprigBatch.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class ItemReaderFactoryBean implements FactoryBean<Object> {
	
	static final String DELEGATE = "delegate";
	
	static final String DATA_DESCRIPTIONS = "dataDescriptions";
	
	static final String DATA_SOURCES = "dataSources";
	
	static final String LOCKS = "locks";
	
	private DataDescriptionItemReader<?> itemReader = null;
	
	private List<DataDescription> dataDescriptions = null;
	
	private List<DataSource> dataSources = null;
	
	private List<Lock> locks = null;
	
	private ResourceAwareItemReaderItemStream<?> delegate = null;

	/**
	 * @return the itemReader
	 */
	public DataDescriptionItemReader<?> getItemReader() {
		return itemReader;
	}

	/**
	 * @param itemReader the itemReader to set
	 */
	public void setItemReader(DataDescriptionItemReader<?> itemReader) {
		this.itemReader = itemReader;
	}
	
	/**
	 * @return the delegate
	 */
	public ResourceAwareItemReaderItemStream<?> getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(ResourceAwareItemReaderItemStream<?> delegate) {
		this.delegate = delegate;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getObject() throws Exception {
		// checks if datadescriptions are set
	    if (this.dataDescriptions != null && this.dataDescriptions.size()==1) {
	    	itemReader.setDataDescription(dataDescriptions.get(0));
	    } else {
	    	// only 1 data description is allowed
	    	throw new JemException("Invalid data description definition: elements "+((this.dataDescriptions == null) ? "0" : this.dataDescriptions.size()));
	    }
	    // checks if there is a delegate (must be present see XML schema)
	    if (this.delegate != null){
	    	itemReader.setDelegate((ResourceAwareItemReaderItemStream) delegate);
	    }
	    
		// checks if datasources are set
	    if (this.dataSources != null && !this.dataSources.isEmpty()) {
	    	itemReader.setDataSources(dataSources);
	    }
		// checks if locks are set
		if (this.locks != null && !this.locks.isEmpty()) {
	    	itemReader.setLocks(locks);
	    }
		return itemReader;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Class<DataDescriptionItemReader> getObjectType() {
		return DataDescriptionItemReader.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}
}