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
package org.pepstock.jem.springbatch.items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.items.DataDescriptionItem;
import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSet;
import org.pepstock.jem.springbatch.tasks.DataSetManager;
import org.pepstock.jem.springbatch.tasks.DataSource;
import org.pepstock.jem.springbatch.tasks.Lock;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.core.io.Resource;

/**
 * Reads data description definition where all resources are defined. Then sets
 * all resource from multiple resources sequentially, the actual reading is
 * delegated to <code>setDelegate(ResourceAwareItemReaderItemStream)</code>.
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T> class of objects to read
 * 
 * @see StepExecutionListener
 * @see MultiResourceItemReader
 */
public class DataDescriptionItemReader<T> extends MultiResourceItemReader<T> implements StepExecutionListener, DataDescriptionItem {

	private DataDescription dataDescription = null;

	private DataDescriptionImpl dataDescriptionImpl = null;
	
	private List<DataSource> dataSources = new ArrayList<DataSource>();
	
	private List<Lock> locks = new ArrayList<Lock>(); 

	private String stepName = null;

	/**
	 * Empty constructor. It calls superclass constructor
	 */
	public DataDescriptionItemReader() {
		super();
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
	 * Sets the data description
	 * 
	 * @see DataDescription
	 * @see Disposition#SHR
	 * @param dataDescription data description instance
	 * @throws IllegalArgumentException if data description is not defined in
	 *             disposition SHR, an exception occurs
	 */
	public void setDataDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;
		// we're in a READER so data description must be set in SHR
		if (!dataDescription.getDisposition().equalsIgnoreCase(Disposition.SHR)){
			throw new IllegalArgumentException(SpringBatchMessage.JEMS006E.toMessage().getFormattedMessage(dataDescription.getName(), dataDescription.getDisposition()));
		}
	}
	
	/**
	 * Returns the list of data sources defined for this tasklet.
	 * 
	 * @return the dataSourceList
	 */
	@Override
	public List<DataSource> getDataSources() {
		return dataSources;
	}

	/**
	 * Sets the list of data sources
	 * 
	 * @param dataSourceList the dataSourceList to set
	 */
	public void setDataSources(List<DataSource> dataSourceList) {
		this.dataSources = dataSourceList;
	}

	/**
	 * @return the locks
	 */
	@Override
	public List<Lock> getLocks() {
		return locks;
	}

	/**
	 * @param locks the locks to set
	 */
	public void setLocks(List<Lock> locks) {
		this.locks = locks;
	}

	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}


	/**
	 * @return the dataDescriptionImpl
	 */
	public DataDescriptionImpl getDataDescriptionImpl() {
		return dataDescriptionImpl;
	}

	/**
	 * @param dataDescriptionImpl the dataDescriptionImpl to set
	 */
	public void setDataDescriptionImpl(DataDescriptionImpl dataDescriptionImpl) {
		this.dataDescriptionImpl = dataDescriptionImpl;
	}

	/**
	 * Reads the next item, jumping to next resource if necessary.
	 * 
	 * @return object has been read
	 */
	@Override
	public T read() throws JemException, UnexpectedInputException, ParseException {
		T item;
		try {
			item = super.read();
		} catch (UnexpectedInputException e) {
			throw e;
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new JemException(e);
		}
		return item;
	}

	/**
	 * Prepares resources to set to superclass.
	 * 
	 * @param resources input resources
	 */
	@Override
	public void setResources(Resource[] resources) {
		for (Resource resource : resources) {
			// if is not Dataset, skip it
			if (resource instanceof DataSet) {
				DataSet ds = (DataSet) resource;
				// check if there's already the data set implementation,
				// otherwise do!
				if (!ds.isImplemented()) {
					try {
						DataSetManager.createDataSetImpl(ds, getDataDescription().getDisposition());
					} catch (IOException e) {
						// ignore
						LogAppl.getInstance().emit(SpringBatchMessage.JEMS037W, e, ds.toString());
					} catch (SpringBatchException e) {
						LogAppl.getInstance().emit(SpringBatchMessage.JEMS037W, e, ds.toString());
					}
				}
			}
		}
		// call superclass
		super.setResources(resources);
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
	public final void beforeStep(StepExecution stepExecution) {
		// save step name before to execute it. mandatory to create data
		// description impl in open method
		this.stepName = stepExecution.getStepName();
	}
}