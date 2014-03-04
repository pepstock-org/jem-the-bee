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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.DataSet;
import org.pepstock.jem.util.CharSet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

/**
 * Simple implementation of delegated reader.<br>
 * Simply reads records from a file as String.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SimpleFileItemReader implements ResourceAwareItemReaderItemStream<String> {

	private Resource resource;

	@SuppressWarnings("unused")
	private int lineCount = 0;

	private boolean noInput = false;

	private LineNumberReader reader = null;

	/**
	 * Empty constructor
	 */
	public SimpleFileItemReader() {
	}

	/**
	 * Set resource for input
	 * 
	 * @param resource resource used to read records
	 */
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Set line count to zero and calls close method of Reader.
	 * 
	 * @see org.springframework.batch.item.ItemStream#close()
	 * @throws ItemStreamException if IO error occurs
	 */
	@Override
	public void close() throws ItemStreamException {
		lineCount = 0;
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				throw new ItemStreamException(e);
			}
		}
	}

	/**
	 * Checks the resource is set, exists, is readable and is a Dataset and then
	 * open the reader. if no, an exception occurs.
	 * 
	 * @see org.springframework.batch.item.ItemStream#open(org.springframework.batch.item.ExecutionContext)
	 * @throws ItemStreamException if IO error occurs
	 */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		// set to true to avoid read nothing in other methods
		noInput = true;
		if (resource == null) {
			throw new ItemStreamException(SpringBatchMessage.JEMS033E.toMessage().getFormattedMessage());
		} else if (!resource.exists()) {
			throw new ItemStreamException(SpringBatchMessage.JEMS033E.toMessage().getFormattedMessage());
		} else if (!resource.isReadable()) {
			throw new ItemStreamException(SpringBatchMessage.JEMS033E.toMessage().getFormattedMessage());
		} else if (resource instanceof DataSet) {
			DataSet ds = (DataSet) resource;
			try {
				reader = new LineNumberReader(new InputStreamReader(new FileInputStream(ds.getDataSetImpl().getRealFile()), CharSet.DEFAULT));
			} catch (FileNotFoundException e) {
				throw new ItemStreamException(e);
			}
		} else {
			throw new ItemStreamException(SpringBatchMessage.JEMS033E.toMessage().getFormattedMessage());
		}
		// set to false because the reader is open
		noInput = false;
	}

	/**
	 * Do nothing
	 * 
	 * @see org.springframework.batch.item.ItemStream#update(org.springframework.batch.item.ExecutionContext)
	 * @param arg0 execution context instance
	 */
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// do nothing
	}

	/**
	 * Reads a record in String format
	 * 
	 * @see org.springframework.batch.item.ItemReader#read()
	 * @return log record in a string
	 * @throws IOException 
	 */
	@Override
	public String read() throws IOException  {
		// if is not false, return! Probably the reader is not open or is close
		if (noInput) {
			return null;
		}

		// if reader is null, exception!
		if (reader == null) {
			throw new UnexpectedInputException(SpringBatchMessage.JEMS034E.toMessage().getFormattedMessage());
		}

		// reads teh line and increment linecount
		String line = null;
		try {
			line = this.reader.readLine();
			if (line == null) {
				return null;
			}
			lineCount++;
		} catch (IOException e) {
			// set true so I don't read again for nothing because there's a
			// error
			noInput = true;
			throw e;
		}
		return line;
	}
}