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
package org.pepstock.jem.jppf;

import java.io.IOException;
import java.io.OutputStream;

import org.jppf.server.protocol.JPPFTask;
import org.jppf.utils.JPPFCallable;

/**
 * Allows to write standard output and error remotely using the JPPF capabilities to execute a callable task on the client.
 * The client is job running under the control of JEM.<br>
 * <b>Note:</b> before to exit from application, perform always flush or close methods to download the buffer. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class RemoteStandardOutputStream extends OutputStream {
	
	private static final int BUFFER_SIZE = 512;
	
	private StringBuilder buffer = new StringBuilder();
	
	private JPPFTask task = null;

	/**
	 * Constructor the output stream with JPPF task (necessary to execute
	 * callable object on client)
	 * 
	 * @param task JPPF task, necessary to execute the callable on client
	 */
	public RemoteStandardOutputStream(JPPFTask task) {
		this.task = task;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		// calls a task on client to download the buffer
		try {
			task.compute(new WriteOutput(buffer));
		} catch (Exception e) {
			throw new IOException(e);
		}  
		// clears buffer
		buffer = new StringBuilder();
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void close() throws IOException {
		flush();
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		// writes on buffer.
		// if is beyond the buffer limit,
		// downloads on client
		buffer.append((char)b);
		if (buffer.length() > BUFFER_SIZE){
			flush();
		}
	}
	
	/**
	 * A callable that writes the buffer in standard output on the client side.
	 */
	public static class WriteOutput implements JPPFCallable<Boolean> {

		private static final long serialVersionUID = 1L;

		private StringBuilder buffer = null;
		
		/**
		 * Construct the task with buffer to write
		 *  
		 * @param buffer string buffer to write
		 */
		public WriteOutput(StringBuilder buffer){
			this.buffer = buffer;
		}
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public Boolean call() {
			// writes buffer on System.out
			System.out.print(buffer.toString());
			return true;
		}
	}

}