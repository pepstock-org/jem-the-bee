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
package org.pepstock.jem.springbatch.xml;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.springbatch.items.DataDescriptionItemReader;
import org.pepstock.jem.springbatch.xml.ItemFactoryBean;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;

/**
 * Factory bean for complex XML element for item reader <itemReader>.
 * <br>
 * It uses the extensions XML authoring of SprigBatch.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class ItemReaderFactoryBean extends ItemFactoryBean {

	private DataDescriptionItemReader<?> itemReader = null;
	
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
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getObject() throws Exception {
		// checks if datadescriptions are set
	    if (getDataDescriptions() != null && getDataDescriptions().size()==1) {
	    	itemReader.setDataDescription(getDataDescriptions().get(0));
	    } else {
	    	// only 1 data description is allowed
	    	throw new JemException("Invalid data description definition: elements "+((getDataDescriptions() == null) ? "0" : getDataDescriptions().size()));
	    }
	    // checks if there is a delegate (must be present see XML schema)
	    if (this.delegate != null){
	    	itemReader.setDelegate((ResourceAwareItemReaderItemStream) delegate);
	    }
	    
		// checks if datasources are set
	    if (getDataSources() != null && !getDataSources().isEmpty()) {
	    	itemReader.setDataSources(getDataSources());
	    }
		// checks if locks are set
		if (getLocks() != null && !getLocks().isEmpty()) {
	    	itemReader.setLocks(getLocks());
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
}