/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.springbatch.items;

import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

/**
 * Reads data description definition where all resources are defined. Then sets
 * the resource, the actual writing is delegated to
 * <code>setDelegate(ResourceAwareItemReaderItemStream)</code>.
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T> class of objects to write
 * 
 */
public class DataDescriptionItemWriter<T> implements ResourceAwareItemWriterItemStream<T>, StepExecutionListener, DataDescriptionItem {

	private DataDescription dataDescription = null;

	private DataDescriptionImpl dataDescriptionImpl = null;

	private Resource resource = null;

	private String stepName = null;

	private ResourceAwareItemWriterItemStream<? super T> delegate;

	/**
	 * Empty constructor
	 */
	public DataDescriptionItemWriter() {
	}

	/**
	 * Returns the data description object. <code>null</code> if it's not set.
	 * 
	 * @see DataDescription
	 * @return data description instance
	 */
	public DataDescription getDataDescription() {
		return dataDescription;
	}

	/**
	 * Sets the data description. Disposition don't must be SHR and multiple
	 * datasets are not allowed in writing mode.
	 * 
	 * @see DataDescription
	 * @see Disposition#SHR
	 * @param dataDescription data description instance
	 * @throws IllegalArgumentException if data description is defined in disposition
	 *             SHR or with multiple dataset, an exception occurs
	 */
	public void setDataDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;
		// we're in a WRITER so data description don't must be SHR
		if (dataDescription.getDisposition().equalsIgnoreCase(Disposition.SHR)) {
			throw new IllegalArgumentException(SpringBatchMessage.JEMS030E.toMessage().getFormattedMessage(dataDescription.getName(), dataDescription.getDisposition()));
		} else if (dataDescription.isMultiDataset()) {
			// multiple datasets are not allowed
			throw new IllegalArgumentException(SpringBatchMessage.JEMS004E.toMessage().getFormattedMessage(dataDescription.getName(), dataDescription.getDisposition()));
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.items.DataDescriptionItem#getStepName()
	 */
	@Override
	public String getStepName() {
		return stepName;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.items.DataDescriptionItem#getDataDescriptionImpl()
	 */
	@Override
	public DataDescriptionImpl getDataDescriptionImpl() {
		return dataDescriptionImpl;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.items.DataDescriptionItem#setDataDescriptionImpl(org.pepstock.catalog.DataDescriptionImpl)
	 */
	@Override
	public void setDataDescriptionImpl(DataDescriptionImpl dataDescriptionImpl) {
		this.dataDescriptionImpl = dataDescriptionImpl;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.items.DataDescriptionItem#setResources(org.springframework.core.io.Resource[])
	 */
	@Override
	public void setResources(Resource[] resources) {
		if (resources != null){
			if (resources.length == 1){
				setResource(resources[0]);
			} else {
				throw new IllegalArgumentException(SpringBatchMessage.JEMS031E.toMessage().getFormattedMessage(resources.length));
			}
		} else {
			throw new IllegalArgumentException(SpringBatchMessage.JEMS031E.toMessage().getFormattedMessage(0));
		}
	}
	/**
	 * Delegate used for actual writing of the output.
	 * 
	 * @param delegate delegate instance
	 */
	public void setDelegate(ResourceAwareItemWriterItemStream<? super T> delegate) {
		this.delegate = delegate;
	}

	/**
	 * Calls close method of delegate.<br>
	 * Commits all modification about GDG allocated and checks if we have
	 * created locks for our resources. If yes, unlocks them.
	 * 
	 * @see org.springframework.batch.item.ItemStream#close()
	 * @throws ItemStreamException if RMI error, an exception occurs
	 */
	@Override
	public void close() throws ItemStreamException {
		// call close method of delegate
		delegate.close();

	}

	/**
	 * Initializes RMI connect with JEM node to have the Resource locker.<br>
	 * It creates data description implementation from data description for
	 * locking the resources.<br>
	 * It reads and prepares the GDG resources and finally it sets all resources
	 * for writing.<br>
	 * Then set resources to delegate and open method of it.
	 * 
	 * @see org.springframework.batch.item.ItemStream#open(org.springframework.batch.item.ExecutionContext)
	 */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		// if delegate is not defined, throw a exception
		if (delegate == null) {
			throw new ItemStreamException(SpringBatchMessage.JEMS032E.toMessage().getFormattedMessage());
		}


		// call delegate to write
		delegate.setResource(this.resource);
		delegate.open(executionContext);
	}

	/**
	 * Calls close method of delegate.
	 * 
	 * @see org.springframework.batch.item.ItemStream#update(org.springframework.batch.item.ExecutionContext)
	 */
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	/**
	 * Writes teh objects, calling <code>write</code> method of delegate
	 * 
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 * @param item list of object to write
	 * @throws Exception if any exception occurs
	 */
	@Override
	public void write(List<? extends T> item) throws JemException {
		try {
			delegate.write(item);
		} catch (Exception e) {
			throw new JemException(e);
		}
	}

	/**
	 * Sets the resource to use to writes objects
	 * 
	 * @see org.springframework.batch.item.file.ResourceAwareItemWriterItemStream#setResource(org.springframework.core.io.Resource)
	 */
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Do nothing. is a method of StepListener interface. Implemented because
	 * it's necessary to know which step is executed to extract stepname.<br>
	 * Return <code>null</code>.
	 * 
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 * @param stepExecution step execution instance
	 * @return <code>null</code> always
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

	/**
	 * Save the current step name, form step execution context.
	 * 
	 * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
	 * @param stepExecution step execution instance
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepName = stepExecution.getStepName();
	}


}