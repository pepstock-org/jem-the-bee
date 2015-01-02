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
package org.pepstock.jem.jppf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jppf.server.protocol.JPPFTask;
import org.jppf.utils.JPPFCallable;
import org.pepstock.jem.io.BytesArray;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;

/**
 * Allows to write a file remotely using the JPPF capabilities to execute a callable task on the client.
 * The client is job running under the control of JEM. This output stream allows to write all the data of
 * GFS without mounting the GFS on JPPF grid.<br>
 * <b>Note:</b> before to exit from application, perform always flush or close methods to download the buffer.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class RemoteOutputStream extends OutputStream {
	
	private boolean appendDueToBuffer = false;
	
	private File file = null;
	
	private boolean append = false;
	
	private static final int BUFFER_SIZE = (int)ChunksFactory.DEFAULT_BUFFER_SIZE;
	
	private BytesArray buffer = new BytesArray(BUFFER_SIZE);
	
	private JPPFTask task = null;

	/**
	 * Constructor the output stream with JPPF task (necessary to execute
	 * callable object on client), the file to write and if is in append mode
	 * 
	 * @param task JPPF task, necessary to execute the callable on client
	 * @param file file to write
	 * @param append if open the file in append mode
	 * 
	 */
	public RemoteOutputStream(JPPFTask task, File file, boolean append) {
		this.task = task;
		this.file = file;
		this.append = append;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the append
	 */
	public boolean isAppend() {
		return append;
	}

	/**
	 * @param append the append to set
	 */
	public void setAppend(boolean append) {
		this.append = append;
	}

	/**
	 * @return the task
	 */
	public JPPFTask getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(JPPFTask task) {
		this.task = task;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		// if buffer size is 0 return!
		// nothing to download
		if (buffer.size() == 0){
			return;
		}
		// execute a task on client to write the buffer
		// The first time it must write the not in append and
		// afterwards ALWAYS in append to download the buffer
		try {
			task.compute(new WriteBytesOutput(file, append, buffer, appendDueToBuffer));
		} catch (Exception e) {
			throw new IOException(e);
		} 
		// always in append!!
		appendDueToBuffer = true; 
		// new buffer
		buffer = new BytesArray(BUFFER_SIZE);
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void close() throws IOException {
		// flush at all close!
		flush();
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		write(new byte[]{(byte)b}, 0, 1);
	}
	
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte,int,int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		// if buffer size is already full, 
		// flush!
		if ((buffer.size() + len) > BUFFER_SIZE){
			flush();
		}
		buffer.write(b, off, len);
	}
	
	/**
	 * A callable that writes the buffer in the file on the client side.
	 */
	public static class WriteBytesOutput implements JPPFCallable<Boolean> {

		private static final long serialVersionUID = 1L;

		private File file = null;
		
		private boolean append = false; 
		
		private BytesArray buffer = null;
		
		private boolean appendDueToBuffer = false;
		
		/**
		 * Constructs object
		 * 
		 * @param file file where to write data
		 * @param append <code>true</code> if writes in append mode
		 * @param buffer buffer to write
		 * @param appendDueToBuffer if must be open in append open however, due to writes many buffers 
		 */
		public WriteBytesOutput(File file, boolean append, BytesArray buffer, boolean appendDueToBuffer){
			this.buffer = buffer;
			this.file = file;
			this.append = append;
			this.appendDueToBuffer = appendDueToBuffer;
		}

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * @param file the file to set
		 */
		public void setFile(File file) {
			this.file = file;
		}

		/**
		 * @return the append
		 */
		public boolean isAppend() {
			return append;
		}

		/**
		 * @param append the append to set
		 */
		public void setAppend(boolean append) {
			this.append = append;
		}

		/**
		 * @return the buffer
		 */
		public BytesArray getBuffer() {
			return buffer;
		}

		/**
		 * @param buffer the buffer to set
		 */
		public void setBuffer(BytesArray buffer) {
			this.buffer = buffer;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public Boolean call() {
			OutputStream ostream = null;
			try {
				// if you want to write appending data,
				// access in append mode!
				// Happens when the data description has got disposition MOD
				if (append){
					ostream = new FileOutputStream(file, true);
				} else {
					// the first buffer is written without appending
					// all others buffers must be appended to file
					if (appendDueToBuffer) {
						ostream = new FileOutputStream(file, true);
					} else {
						ostream = new FileOutputStream(file);
					}
				}
				// writes buffer on output stream
				buffer.writeTo(ostream);
			} catch (IOException e) {
				throw new JemRuntimeException(e.getMessage(), e);
			} catch (Exception e) {
				throw new JemRuntimeException(e.getMessage(), e);
			} finally {
				if (ostream != null){
					try {
						ostream.close();
					} catch (IOException e) {
						// debug
						LogAppl.getInstance().debug(e.getMessage(), e);
					}
				}
			}
			return Boolean.TRUE;
		}
	}

}