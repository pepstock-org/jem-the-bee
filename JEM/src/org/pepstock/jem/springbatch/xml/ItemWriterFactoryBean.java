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

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.springbatch.items.DataDescriptionItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;

/**
 * Factory bean for complex XML element for item <itemWriter>.
 * <br>
 * It uses the extensions XML authoring of SprigBatch.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class ItemWriterFactoryBean extends ItemFactoryBean {
	
	private DataDescriptionItemWriter<?> itemWriter = null;
	
	private ResourceAwareItemWriterItemStream<?> delegate = null;

	/**
	 * @return the itemWriter
	 */
	public DataDescriptionItemWriter<?> getItemWriter() {
		return itemWriter;
	}

	/**
	 * @param itemWriter the itemWriter to set
	 */
	public void setItemWriter(DataDescriptionItemWriter<?> itemWriter) {
		this.itemWriter = itemWriter;
	}
	
	
	/**
	 * @return the delegate
	 */
	public ResourceAwareItemWriterItemStream<?> getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(ResourceAwareItemWriterItemStream<?> delegate) {
		this.delegate = delegate;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getObject() throws Exception {
		// checks if data descriptions are set
	    if (getDataDescriptions() != null && getDataDescriptions().size()==1) {
	    	itemWriter.setDataDescription(getDataDescriptions().get(0));
	    } else {
	    	// only 1 data description is allowed
	    	throw new JemException("Invalid data description definition: elements "+((getDataDescriptions() == null) ? "0" : getDataDescriptions().size()));
	    }
	    // checks if there is a delegate (must be present see XML schema)
	    if (this.delegate != null){
	    	itemWriter.setDelegate((ResourceAwareItemWriterItemStream) delegate);
	    }
		// checks if datasources are set
	    if (this.getDataSources() != null && !this.getDataSources().isEmpty()) {
	    	itemWriter.setDataSources(getDataSources());
	    }
		// checks if locks are set
		if (getLocks() != null && !getLocks().isEmpty()) {
	    	itemWriter.setLocks(getLocks());
	    }
		return itemWriter;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Class<DataDescriptionItemWriter> getObjectType() {
		return DataDescriptionItemWriter.class;
	}
}