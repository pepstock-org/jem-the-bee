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
package org.pepstock.jem.rest.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.apache.http.HttpStatus;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.GfsFileList;
import org.pepstock.jem.rest.entities.GfsOutputContent;
import org.pepstock.jem.rest.entities.GfsRequest;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.paths.GfsManagerPaths;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

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
		super(restClient);
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
	 * @throws JemException if any exception occurs
	 */
	public String getFile(GfsRequest request) throws JemException {
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the returned object
		GenericType<JAXBElement<GfsOutputContent>> generic = new GenericType<JAXBElement<GfsOutputContent>>() {
		};
		try {
			// creates the complete path of REST service, setting also the output format (XML)
			JAXBElement<GfsOutputContent> jaxbContact = resource.path(GfsManagerPaths.MAIN).path(GfsManagerPaths.OUTPUT_FILE_CONTENT_PATH).accept(MediaType.APPLICATION_XML).post(generic, request);
			// gets the returned object
			GfsOutputContent object = jaxbContact.getValue();
	    	// checks if has got any exception
	    	// Exception must be saved as attribute of returned object
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object.getContent();
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// checks http status 
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * Returns the list of file from a specific folder
	 * @param type type of files
	 * @param request specific folder
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	public GfsFileList getFilesList(GfsRequest request) throws JemException {
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the returned object
		GenericType<JAXBElement<GfsFileList>> generic = new GenericType<JAXBElement<GfsFileList>>() {
		};
		try {
			// creates the complete path of REST service, setting also the output format (XML)
			JAXBElement<GfsFileList> jaxbContact = resource.path(GfsManagerPaths.MAIN).path(GfsManagerPaths.FILE_LIST).accept(MediaType.APPLICATION_XML).post(generic, request);
			// gets the returned object
			GfsFileList object = jaxbContact.getValue();
	    	// checks if has got any exception
	    	// Exception must be saved as attribute of returned object
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// checks http status 
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * Uploads a file via rest service
	 * @param file
	 * @return response status
	 * @throws JemException
	 */
	public int upload(UploadedGfsFile file) throws JemException {
		// gets the web resource
		// adding paths for upload
		WebResource resource = getClient().getBaseWebResource().path(GfsManagerPaths.MAIN).path(GfsManagerPaths.FILE_UPLOAD);
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
				response =  resource.accept(MediaType.APPLICATION_OCTET_STREAM).post(ClientResponse.class, chunkFile);
				// checks if everything went OK!
				if (response.getStatus() != HttpStatus.SC_OK) {
					throw new JemException(NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
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
			response =  resource.accept(MediaType.APPLICATION_OCTET_STREAM).post(ClientResponse.class, chunkFile);	
			// checks if everything went OK!
			if (response.getStatus() != HttpStatus.SC_OK) {
				throw new JemException(NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
			}
			// activate the listeners
			fireUploadListeners(readNum);
			// doesn't close here the response. see finally
			return response.getStatus();
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return e.getResponse().getStatus();
		} catch (IOException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new JemException(e.getMessage(), e);
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
	 * delete a file of a specific type
	 * @param type type of file
	 * @param request file name
	 * @throws JemException if any exception occurs
	 */
	public void delete(GfsRequest request) throws JemException {
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the returned object
		GenericType<JAXBElement<ReturnedObject>> generic = new GenericType<JAXBElement<ReturnedObject>>() {
		};
		try {
			// creates the complete path of REST service, setting also the output format (XML)
			JAXBElement<ReturnedObject> jaxbContact = resource.path(GfsManagerPaths.MAIN).path(GfsManagerPaths.FILE_DELETE).accept(MediaType.APPLICATION_XML).post(generic, request);
			// gets the returned object
			ReturnedObject object = jaxbContact.getValue();
	    	// checks if has got any exception
	    	// Exception must be saved as attribute of returned object
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// checks http status 
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Inner service, which extends post the default post service.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class GfsPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * Constructs the REST service, using HTTP client and service and subservice paths, passed as argument
		 * 
		 * @param subService subservice path
		 * 
		 */
		public GfsPostService(String subService) {
			super(GfsManager.this.getClient(), GfsManagerPaths.MAIN, subService);
		}
	}
}