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
package org.pepstock.jem.node.executors.gfs;

import java.io.File;
import java.io.FileOutputStream;

import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * The executor upload a chunk of a file.
 * 
 * @author Simone "Busy" Businaro
 * @version 1.2
 * 
 */
public class WriteChunk extends DefaultExecutor<Boolean> {

	private static final long serialVersionUID = 1L;

	private UploadedGfsChunkFile chunk;

	/**
	 * Write this chunk to the GFS
	 * 
	 * @param chunk
	 */
	public WriteChunk(UploadedGfsChunkFile chunk) {
		this.chunk = chunk;
	}

	/**
	 * @return the pathName
	 */
	public UploadedGfsChunkFile getChunk() {
		return chunk;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public final Boolean execute() throws ExecutorException {
		String parentPath = null;
		File file = null;
		// checks here the type of file-system to scan
		switch (chunk.getType()) {
			case GfsFileType.LIBRARY:
				parentPath = System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME);
				break;
			case GfsFileType.SOURCE:
				parentPath = System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME);
				break;
			case GfsFileType.CLASS:
				parentPath = System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME);
				break;
			case GfsFileType.BINARY:
				parentPath = System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME);
				break;
			default:
				throw new ExecutorException(NodeMessage.JEMC264E);
		}
		// the temporary file
		try {
			file = new File(parentPath, chunk.getFilePath() + "." + chunk.getFileCode());
			// if tmp file does not exists create it with all directory structure
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			// if the transferred is complete just rename the tmp file
			if (chunk.isTransferComplete()) {
				File finalFile = new File(parentPath, chunk.getFilePath());
				file.renameTo(finalFile);
				return true;
			}
			// write to the temporary file
			FileOutputStream output = new FileOutputStream(file.getAbsolutePath(), true);
			try {
				output.write(chunk.getChunk(), 0, chunk.getNumByteToWrite());
			} finally {
				output.close();
			}
		} catch (Exception e) {
			// upload get an exception so delete tmp file
			file.delete();
			throw new ExecutorException(NodeMessage.JEMC265E, file.getAbsolutePath(), e);
		}
		return true;
	}
}