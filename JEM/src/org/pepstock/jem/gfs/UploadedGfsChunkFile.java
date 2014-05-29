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
package org.pepstock.jem.gfs;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.io.BytesArray;

/**
 * 
 * This class represents a chunk of file that is being uploaded. We use chunk so
 * to be able to upload also relative big file keeping the jvm memory low
 * 
 * @author Simone "Busy" Businaro
 * 
 */
@XmlRootElement
public class UploadedGfsChunkFile implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * is the max size of a chunk in byte
	 */
	public static final int MAX_CHUNK_SIZE = 262144;

//	private byte[] chunk;
	
	private BytesArray chunk;

	private int type;

	private String filePath;

	private boolean transferComplete = false;

	private int fileCode;

	private int numByteToWrite;

//	/**
//	 * 
//	 * @return the chunk of the file that is currently been uploaded
//	 */
//	public byte[] getChunk() {
//		return chunk;
//	}
//
//	/**
//	 * 
//	 * @param chunk of the file that is currently been uploaded
//	 */
//	public void setChunk(byte[] chunk) {
//		this.chunk = chunk;
//	}

	
	
	/**
	 * 
	 * @return an integer that identifies the folder of the GFS.
	 * @see {@link GfsFileType}
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the chunk
	 */
	public BytesArray getChunk() {
		return chunk;
	}

	/**
	 * @param chunk the chunk to set
	 */
	public void setChunk(BytesArray chunk) {
		this.chunk = chunk;
	}

	/**
	 * 
	 * @param type an integer that identifies the folder of the GFS.
	 * @see {@link GfsFileType}
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 
	 * @return the path of the file, included the file name, relative to one of
	 *         the GFS folder specified by the type
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 
	 * @param filePath the path of the file, included the name relative to one
	 *            of the GFS folder specified by the type
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 
	 * @return true if the current chunk is the last chunk of the file that is
	 *         being uploaded false otherwise
	 */
	public boolean isTransferComplete() {
		return transferComplete;
	}

	/**
	 * 
	 * @param transferComplete is a flag that indicates if the current chunk is
	 *            the last chunk of the file that is being uploaded or not
	 */
	public void setTransferComplete(boolean transferComplete) {
		this.transferComplete = transferComplete;
	}

	/**
	 * 
	 * @return a file code associated with the uploaded file. This code will be
	 *         used for the name of the temporary uploaded file
	 */
	public int getFileCode() {
		return fileCode;
	}

	/**
	 * 
	 * @param fileCode associated with the uploaded file. This code will be used
	 *            for the name of the temporary uploaded file. All the chunk of
	 *            a same file must have the same fileCode
	 */
	public void setFileCode(int fileCode) {
		this.fileCode = fileCode;
	}

	/**
	 * 
	 * @return the number of byte read from input stream to write to the output
	 *         stream
	 */
	public int getNumByteToWrite() {
		return numByteToWrite;
	}

	/**
	 * 
	 * @param numByteToWrite the number of byte read from input stream to write
	 *            to the output stream
	 */
	public void setNumByteToWrite(int numByteToWrite) {
		this.numByteToWrite = numByteToWrite;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UploadedGfsChunkFile [type=" + type + ", filePath=" + filePath + ", fileCode=" + fileCode + "]";
	}
	
}
