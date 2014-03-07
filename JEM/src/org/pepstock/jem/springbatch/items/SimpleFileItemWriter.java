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
package org.pepstock.jem.springbatch.items;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.DataSet;
import org.pepstock.jem.util.CharSet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

/**
 * Simple implementation of delegated writer.<br>
 * Simply writes records to a file as String.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SimpleFileItemWriter implements ResourceAwareItemWriterItemStream<String> {

	private Resource resource;

	@SuppressWarnings("unused")
	private int lineCount = 0;

	private boolean noOutput = false;

	private PrintWriter writer = null;

	/**
	 * Empty constructor
	 */
	public SimpleFileItemWriter() {
	}

	/**
	 * Set line count to zero and calls close method of Writer.
	 * 
	 * @see org.springframework.batch.item.ItemStream#close()
	 * @throws ItemStreamException if IO error occurs
	 */
	@Override
	public void close() throws ItemStreamException {
		lineCount = 0;
		if (writer != null) {
			writer.close();
		}
	}

	/**
	 * Checks the resource is set and is a Dataset and then open the reader. if
	 * no, an exception occurs.
	 * 
	 * @see org.springframework.batch.item.ItemStream#open(org.springframework.batch.item.ExecutionContext)
	 * @throws ItemStreamException if IO error occurs
	 */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		// set to true to avoid write nothing in other methods
		noOutput = true;

		if (resource == null) {
			throw new ItemStreamException(SpringBatchMessage.JEMS033E.toMessage().getFormattedMessage());
		} else if (resource instanceof DataSet) {
			DataSet ds = (DataSet) resource;
			try {
				writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ds.getDataSetImpl().getRealFile()), CharSet.DEFAULT));
			} catch (IOException e) {
				throw new ItemStreamException(e);
			}
		} else {
			throw new ItemStreamException(SpringBatchMessage.JEMS033E.toMessage().getFormattedMessage());
		}
		// set to false because the writer is open
		noOutput = false;
	}

	/**
	 * Do nothing.
	 * 
	 * @see org.springframework.batch.item.ItemStream#update(org.springframework.batch.item.ExecutionContext)
	 */
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// do nothing
	}

	/**
	 * writes records in String format
	 * 
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 * @throws Exception if IO errors occurs
	 */
	@Override
	public void write(List<? extends String> records) {
		// if is not false, return! Probably the writer is not open or is close
		if (noOutput){
			return;
		}

		// for all records write and increment line count!!
		for (String record : records) {
			writer.println(record);
			lineCount++;
		}
		writer.flush();
	}

	/**
	 * Set resource for output
	 * 
	 * @param resource resource used to write records
	 */
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

}