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
package org.pepstock.jem.rest.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.GfsRequest;
import org.pepstock.jem.rest.paths.GfsManagerPaths;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * REST service to manage GFS.
 * <br>
 * It can manages all the actions of global file system, by REST.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class GfsManager extends AbstractRestManager {
	
	private List<UploadListener> listeners = new LinkedList<UploadListener>();

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient
	 *            REST client instance
	 */
	public GfsManager(RestClient restClient) {
		super(restClient, GfsManagerPaths.MAIN);
	}
	
	/**
	 * Adds a upload listener. Used for upload
	 * 
	 * @param listener upload listeners
	 */
	public void addUploadListener(UploadListener listener){
		// adds listener if not already added
		if (!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	/**
	 * Removes a upload listener. Used for upload
	 * 
	 * @param listener upload listeners
	 */
	public void removeUploadListener(UploadListener listener){
		// removes listener if exists in the list
		if (listeners.contains(listener)){
			listeners.remove(listener);
		}
	}

	/**
	 * Returns a content file of a specific type
	 * @param type type of file
	 * @param request file name
	 * @return content of file
	 * @throws RestException if any exception occurs
	 */
	public String getFile(GfsRequest request) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = post(GfsManagerPaths.OUTPUT_FILE_CONTENT_PATH, request);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(String.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Returns the list of file from a specific folder
	 * @param type type of files
	 * @param request specific folder
	 * @return list of files
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<GfsFile> getFilesList(GfsRequest request) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = post(GfsManagerPaths.FILE_LIST, request);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<GfsFile>)JsonUtil.getInstance().deserializeList(response, GfsFile.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Uploads a file via rest service
	 * @param file
	 * @return response status
	 * @throws JemException
	 */
	public int upload(UploadedGfsFile file) throws RestException {
		// gets the web resource
		// adding paths for upload
		ClientResponse response = null;
		FileInputStream fis = null;
		try {
			// file input stream to read 
			fis = new FileInputStream(file.getUploadedFile());
			// creates a chunk in bytes
			byte[] chunk = new byte[UploadedGfsChunkFile.MAX_CHUNK_SIZE];
			// creates a random code, to identify the chunk
			Random random = new Random();
			int randomNumber = random.nextInt(Integer.MAX_VALUE);
			// read and write file in chunks
			int readNum = 0;
			// reads the file with the buffer 
			// the last send will be done out of this FOR
			for (; (readNum = fis.read(chunk)) != -1;) {
				// creates a chunk object to serialize and send by REST
				UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
				// sets the data
				chunkFile.setChunk(chunk);
				// sets unique code
				chunkFile.setFileCode(randomNumber);
				// relative path must be used
				// when you want to upload the file
				// maintaining a relative path of source file
				if (file.getRelativePath() != null){
					chunkFile.setFilePath(file.getGfsPath()+file.getRelativePath());
				} else {
					chunkFile.setFilePath(file.getGfsPath()+file.getUploadedFile().getName());
				}
				// sets transfer is not the LAST
				chunkFile.setTransferComplete(false);
				// number of bytes
				chunkFile.setNumByteToWrite(readNum);
				// where to put the file (GFS type)
				chunkFile.setType(file.getType());
				// sets the update time 
				chunkFile.setLastUpdate(file.getUploadedFile().lastModified());
				
				// creates and performs post HTTP
				
				response = post(GfsManagerPaths.FILE_UPLOAD, chunkFile);
				
				// checks if everything went OK!
				if (response.getStatus() != Status.OK.getStatusCode()) {
					throw new RestException(response.getStatus(),NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
				}
				// close the response and activate the listeners
				response.close();
				fireUploadListeners(readNum);
			}
			// the last chunk was read
			UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
			// adds the last chunk
			chunkFile.setChunk(chunk);
			// sets random unique code
			chunkFile.setFileCode(randomNumber);
			// relative path must be used
			// when you want to upload the file
			// maintaining a relative path of source file
			if (file.getRelativePath() != null){
				chunkFile.setFilePath(file.getGfsPath()+file.getRelativePath());
			} else {
				chunkFile.setFilePath(file.getGfsPath()+file.getUploadedFile().getName());
			}
			// is the LAST call
			chunkFile.setTransferComplete(true);
			// where to put the file (GFS type)
			chunkFile.setType(file.getType());
			// sets the update time
			chunkFile.setLastUpdate(file.getUploadedFile().lastModified());
			
			// creates and performs post HTTP
			response = post(GfsManagerPaths.FILE_UPLOAD, chunkFile);
			// checks if everything went OK!
			if (response.getStatus() != Status.OK.getStatusCode()) {
				throw new RestException(response.getStatus(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
			}
			// activate the listeners
			fireUploadListeners(readNum);
			// doesn't close here the response. see finally
			return response.getStatus();
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new RestException(e.getResponse().getStatus(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), e.getMessage()));
			}
			return e.getResponse().getStatus();
		} catch (IOException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), e.getMessage()));
		} finally {
			// if file inut stream
			// is open, it closes
			if (fis != null){
				try {
	                fis.close();
                } catch (IOException e) {
	                LogAppl.getInstance().ignore(e.getMessage(), e);
                }
			}
			// important to CLOSE ALWAYS the response
			if (response != null){
				response.close();
			}
		}
	}
	
	/**
	 * Notifies upload listener
	 * @param units number of byte uploaded
	 */
	private void fireUploadListeners(int units){
		// if not empty, 
		// scans all listeners calling
		// the method passing the amount of bytes
		// transferred to JEM
		if (!listeners.isEmpty()){
			for (UploadListener listener : listeners){
				listener.setUnitsDone(units);
			}
		}
	}
	
	/**
	 * Delete a file of a specific type
	 * @param request file name
	 * @return <code>true</code> if the file has been deleted otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean delete(GfsRequest request) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = post(GfsManagerPaths.FILE_DELETE, request);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(Boolean.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
}