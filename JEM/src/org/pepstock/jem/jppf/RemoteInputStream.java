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
package org.pepstock.jem.jppf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.jppf.server.protocol.JPPFTask;
import org.jppf.utils.JPPFCallable;
import org.pepstock.jem.io.BytesArray;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;

/**
 * Allows to read a file remotely using the JPPF capabilities to execute a callable task on the client.
 * The client is job running under the control of JEM. This input stream allows to read all the data of
 * GFS without mounting the GFS on JPPF grid.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class RemoteInputStream extends InputStream {
	
	private File file = null;
	
	private BytesArray buffer = null;
	
	private JPPFTask task = null;
	
	private List<ChunkDefinition> chunks = null;
	
	private int chunkIndex = 0;
	
	private ChunkDefinition currentChunk = null;
	
	// position of read in FILE
	private int index = 0;
	
	// position of read in BUFFER	
	private int position = 0;
	
	private int size = 0;

	/**
	 * Constructor the input stream with JPPF task (necessary to execute
	 * callable object on client) and the file to read.<br>
	 * Calls JPPF client (on JEM job) to have all chunks 
	 * 
	 * @param task JPPF task, necessary to execute the callable on client
	 * @param file file to read
	 * @throws IOException 
	 * 
	 */
	public RemoteInputStream(JPPFTask task, File file) throws IOException {
		this.task = task;
		this.file = file;
		try {
			// gets chunks, calling a callable 
			this.chunks = task.compute(new GetChunks(file));
			// to calculate the size of input stream
			for (ChunkDefinition chunk : chunks){
				size+=chunk.getLength();
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
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
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		// if is at the beginning or at the end of buffer reading
		// read another buffer!!!
		if ((index == 0) || (currentChunk == null) || (index > currentChunk.getEnd())){
			
			// if chunk index is beyond of list size
			// file is ended
			if (chunkIndex == chunks.size()){
				return -1;
			}
			
			// gets current chunk
			currentChunk = chunks.get(chunkIndex);
			chunkIndex++;
			//reads remotely
			try {
				// computing a callable task on client
				// passing the current chunk and file itself
					buffer = task.compute(new ReadBytesOutput(file, currentChunk));
			} catch (Exception e) {
				throw new IOException(e);
			}
			// reset the position on buffer
			position = 0;
		}
		// reads from buffer
		byte data = buffer.read(position);
		// increments index and position
		index++;
		position++;
		return data;
	}
  
	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
    public int available() throws IOException {
    	return size;
    }
	
	/**
	 * A callable that reads the file content on the client side
	 * and returns the buffer of bytes to the node.
	 */
	public static class ReadBytesOutput implements JPPFCallable<BytesArray> {

		private static final long serialVersionUID = 1L;

		private File file = null;
		
		private ChunkDefinition chunk = null;
		
		/**
		 * Constructs the object saving file to read and chunk definition
		 * to know which part of file must be read.
		 * 
		 * @param file file to read
		 * @param chunk chunk information
		 */
		public ReadBytesOutput(File file, ChunkDefinition chunk){
			this.chunk = chunk;
			this.file = file;
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

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public BytesArray call() {
			BytesArray ba = new BytesArray(chunk.getLength());
		
			// uses random file in read.
			// because with random is faster skip bytes
			RandomAccessFile istream = null;
			try {
				istream = new RandomAccessFile(file, "r");
				// skips previous already read buffer
				istream.seek(chunk.getStart());
				
				// reads a buffer
				byte[] currBuffer = new byte[chunk.getLength()];
				istream.read(currBuffer);
				// if buffer is not empty, 
				// moves on bytes array for transportation
				ba.write(currBuffer, 0, chunk.getLength());
			} catch (IOException e) {
				throw new JemRuntimeException(e);
			} catch (Exception e) {
				throw new JemRuntimeException(e);
			} finally {
				if (istream != null){
					try {
						istream.close();
					} catch (IOException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
			return ba;
		}
	}
	
	/**
	 * A callable that reads the file length on the client side
	 * and returns a linked list of chunks.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4	
	 *
	 */
	public static class GetChunks implements JPPFCallable<List<ChunkDefinition>> {
		
		private static final long serialVersionUID = 1L;

		private File file = null;
		
		/**
		 * Constructs the object saving file to read.
		 * 
		 * @param file file to read
		 */
		public GetChunks(File file){
			this.file = file;
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

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public List<ChunkDefinition> call() throws IOException {
			// returns list of chunk
			return ChunksFactory.getChunks(file);
		}
		
	}
}