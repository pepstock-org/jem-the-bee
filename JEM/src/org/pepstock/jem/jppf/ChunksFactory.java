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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.log.LogAppl;

/**
 * Creates a list of chunk reading files length or subset of bytes (if a delimiter is specified)
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class ChunksFactory {
	
	/**
	 * Default BUFFER size, used for read and write operation as well
	 */
	public static final long DEFAULT_BUFFER_SIZE = 4096L;
	
	private static final long MINIMUM_BUFFER_SIZE = 1024L;

	/**
	 * To avoid any instantiation
	 */
	private ChunksFactory() {
	}
	
	/**
	 * Returns a linked list of chunk definitions, reading length of file.<br>
	 * Buffer size is omitted and it uses the default one. 
	 * @param file file to calculate chunks 
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunks(File file) throws IOException{
		return getChunks(file, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Returns a linked list of chunk definitions, reading length of file.<br>
	 * Buffer size is an argument to create chunk with right dimension  
	 * @param file file to calculate chunks
	 * @param bufferSizeParm buffer size used to create chunk with right dimension 
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunks(File file, long bufferSizeParm) throws IOException{
		List<ChunkDefinition> list = new LinkedList<ChunkDefinition>();
		// checks if buffer is too small
		long bufferSize = Math.max(bufferSizeParm, MINIMUM_BUFFER_SIZE);
		
		// starts from 0
		long maxLen = file.length() - 1;
		long chunkStart = 0;
		long chunkEnd = 0;
		long skip = 0;

		// scans for possible chunks but -1 , because at least 1 is necessary
		// and the last one must have remaining bytes  
		while(chunkStart < maxLen){
			skip += bufferSize;
			skip = Math.min(skip, maxLen);
			chunkEnd = skip;
			list.add(new ChunkDefinition(chunkStart, chunkEnd));
			// next starting point
			chunkStart = chunkEnd + 1;
		}
		return list;
	}

	/**
	 * Returns a linked list of chunk definitions, reading a chunk definition.<br>
	 * Buffer size is omitted and it uses the default one. 
	 * @param chunk chunk used to calculate chunks 
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunksByChunk(ChunkDefinition chunk) throws IOException{
		return getChunksByChunk(chunk, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Returns a linked list of chunk definitions, reading a chunk definition.<br>
	 * Buffer size is an argument to create chunk with right dimension  
	 * @param chunk chunk used to calculate chunks 
	 * @param bufferSizeParm buffer size used to create chunk with right dimension 
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunksByChunk(ChunkDefinition chunk, long bufferSizeParm) throws IOException{
		List<ChunkDefinition> list = new LinkedList<ChunkDefinition>();
		// checks if buffer is too small
		long bufferSize = Math.max(bufferSizeParm, MINIMUM_BUFFER_SIZE);
		
		long chunkStart = chunk.getStart();
		long chunkEnd = 0;
		long skip = chunk.getStart()-1;

		// scans for possible chunks but -1 , because at least 1 is necessary
		// and the last one must have remaining bytes  
		while(chunkStart < chunk.getEnd()){
			skip += bufferSize;
			skip = Math.min(skip, chunk.getEnd());
			chunkEnd = skip;
			list.add(new ChunkDefinition(chunkStart, chunkEnd));
			// next starting point
			chunkStart = chunkEnd + 1;
		}
		return list;
	}

	/**
	 * Returns a linked list of chunk definitions, reading length of file.<br>
	 * Buffer size is omitted and it uses the default one.<br>
	 * Delimiter is omitted and it uses <code>System.getProperty("line.separator")</code>.
	 * @param file file to calculate chunks 
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunksByDelimiter(File file) throws IOException{
		return getChunksByDelimiter(file, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Returns a linked list of chunk definitions, reading length of file.<br>
	 * Buffer size is an argument to create chunk with right dimension.<br>
	 * Delimiter is omitted and it uses <code>System.getProperty("line.separator")</code>.
	 * @param file file to calculate chunks
	 * @param bufferSize buffer size used to create chunk with right dimension 
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunksByDelimiter(File file, long bufferSize) throws IOException{
		return getChunksByDelimiter(file, bufferSize, System.getProperty("line.separator").charAt(0));
	}

	/**
	 * Returns a linked list of chunk definitions, reading length of file.<br>
	 * Buffer size is an argument to create chunk with right dimension.<br>
	 * Delimiter is a char used to split file.
	 * @param file file to calculate chunks
	 * @param bufferSizeParm buffer size used to create chunk with right dimension 
	 * @param delimiter a char used to split file
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunksByDelimiter(File file, long bufferSizeParm, char delimiter) throws IOException{
		List<ChunkDefinition> list = new LinkedList<ChunkDefinition>();
		// checks if buffer is too small
		long bufferSize = Math.max(bufferSizeParm, MINIMUM_BUFFER_SIZE);
		// calculate possible chunks number
		long possibleChunksCount = file.length()/bufferSize;

		// uses RandomAccessFile so it can really seeks the position
		// without reading all byte (skip method of inputstream).
		// this is faster
		RandomAccessFile istream = null;
		try {
			// access in READ
			istream = new RandomAccessFile(file, "r");
			
			long chunkStart = 0;
			long chunkEnd = 0;
			long skip = 0;

			// scans for possible chunks but -1 , because at least 1 is necessary
			// and the last one must have remaining bytes
			for (int i=0; i<possibleChunksCount - 1; i++){
				// calculate skip
				skip = (i+1)*bufferSize;
				// skip could be lesser than chunk start
				// because it scans bytes to find the delimiter
				// and the scan could go beyond to chunk start
				// of next chunk
				if (skip > chunkStart){
					// seeks ending offset (fast)
					istream.seek(skip);
					// reads all bytes searching for delimiter
					while(true){
						int	read = istream.read();
						
						// if finds delimiter or is end of file
						// ends cycle
						if ((read == delimiter) || (read == -1)){
							break;
						}
					}
					
					// takes file pointer (-1 because is already to new byte) as end of chunk
					// could be end of file or delimiter position
					chunkEnd = istream.getFilePointer() - 1;
					list.add(new ChunkDefinition(chunkStart, chunkEnd));
					// new start
					chunkStart = chunkEnd + 1;
				}
			}
			// chunk start is beyond file length
			// do not create chunk. 
			// could happen because it scans bytes to find the delimiter
			// and the scan could go to end of file
			if (chunkStart < file.length()){
				// subtracts 1 because index starts from 0
				list.add(new ChunkDefinition(chunkStart, file.length()-1));
			}
		} finally {
			// close Random file
			if (istream != null){
				try {
					istream.close();
				} catch (IOException e) {
					// debug
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return list;
	}

	/**
	 * Returns a linked list of chunk definitions, reading length of file.<br>
	 * Buffer size is an argument to create chunk with right dimension.<br>
	 * Delimiter is a String used to split file.
	 * @param file file to calculate chunks
	 * @param bufferSizeParm buffer size used to create chunk with right dimension 
	 * @param delimiter string used to split file
	 * @return list of chunks
	 * @throws IOException if any IO exception occurs
	 */
	public static final List<ChunkDefinition> getChunksByDelimiter(File file, long bufferSizeParm, String delimiter) throws IOException{
		List<ChunkDefinition> list = new LinkedList<ChunkDefinition>();

		// checks if buffer is too small
		long bufferSize = Math.max(bufferSizeParm, MINIMUM_BUFFER_SIZE);
		
		// calculate possible chunks number
		long possibleChunksCount = file.length()/bufferSize;

		// uses RandomAccessFile so it can really seeks the position
		// without reading all byte (skip method of inputstream).
		// this is faster
		RandomAccessFile istream = null;
		try {
			// access in READ
			istream = new RandomAccessFile(file, "r");
			
			long chunkStart = 0;
			long chunkEnd = 0;
			long skip = 0;

			// scans for possible chunks but -1 , because at least 1 is necessary
			// and the last one must have remaining bytes
			for (int i=0; i<possibleChunksCount - 1; i++){
				// calculate skip
				skip = (i+1)*bufferSize;
				// skip could be lesser than chunk start
				// because it scans bytes to find the delimiter
				// and the scan could go beyond to chunk start
				// of next chunk
				if (skip > chunkStart){
					// seeks ending offset (fast)
					istream.seek(skip);
					// reads all bytes searching for delimiter
					int delimiterOffset = 0;
					
					while(true){
						boolean mustEnd = false;
						int	read = istream.read();
						// if finds delimiter or is end of file
						// ends cycle
						if (read != -1){
							if (read == delimiter.charAt(delimiterOffset)){
								delimiterOffset++;
								if (delimiterOffset == delimiter.length()){
									mustEnd = true;
								}
							} else {
								delimiterOffset = 0;
							}
						} else {
							mustEnd = true;
						}
						if (mustEnd){
							break;
						}
					}
					
					// takes file pointer (-1 because is already to new byte) as end of chunk
					// could be end of file or delimiter position
					chunkEnd = istream.getFilePointer() - 1;
					list.add(new ChunkDefinition(chunkStart, chunkEnd));
					// new start
					chunkStart = chunkEnd + 1;
				}
			}
			// chunk start is beyond file length
			// do not create chunk. 
			// could happen because it scans bytes to find the delimiter
			// and the scan could go to end of file
			if (chunkStart < file.length()){
				// subtracts 1 because index starts from 0
				list.add(new ChunkDefinition(chunkStart, file.length()-1));
			}
		} finally {
			// close Random file
			if (istream != null){
				try {
					istream.close();
				} catch (IOException e) {
					// debug
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return list;
	}
}
