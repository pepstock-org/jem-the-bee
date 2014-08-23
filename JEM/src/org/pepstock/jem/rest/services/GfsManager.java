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
 * @author Enrico Frigo
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
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<GfsOutputContent>> generic = new GenericType<JAXBElement<GfsOutputContent>>() {
		};
		try {
			JAXBElement<GfsOutputContent> jaxbContact = resource.path(GfsManagerPaths.MAIN).path(GfsManagerPaths.OUTPUT_FILE_CONTENT_PATH).accept(MediaType.APPLICATION_XML).post(generic, request);
			GfsOutputContent object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object.getContent();
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
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
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<GfsFileList>> generic = new GenericType<JAXBElement<GfsFileList>>() {
		};
		try {
			JAXBElement<GfsFileList> jaxbContact = resource.path(GfsManagerPaths.MAIN).path(GfsManagerPaths.FILE_LIST).accept(MediaType.APPLICATION_XML).post(generic, request);
			GfsFileList object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
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
		WebResource resource = getClient().getBaseWebResource().path(GfsManagerPaths.MAIN).path(GfsManagerPaths.FILE_UPLOAD);
		ClientResponse response = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file.getUploadedFile());
			byte[] chunk = new byte[UploadedGfsChunkFile.MAX_CHUNK_SIZE];
			Random random = new Random();
			int randomNumber = random.nextInt(Integer.MAX_VALUE);
			// read and write file in chunks
			int readNum = 0;
			for (; (readNum = fis.read(chunk)) != -1;) {
				UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
				chunkFile.setChunk(chunk);
				chunkFile.setFileCode(randomNumber);
				chunkFile.setFilePath(file.getGfsPath()+file.getUploadedFile().getName());
				chunkFile.setTransferComplete(false);
				chunkFile.setNumByteToWrite(readNum);
				chunkFile.setType(file.getType());
				chunkFile.setLastUpdate(file.getUploadedFile().lastModified());
				
				response =  resource.accept(MediaType.APPLICATION_OCTET_STREAM).post(ClientResponse.class, chunkFile);
				if (response.getStatus() != HttpStatus.SC_OK) {
					throw new JemException(NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
				}
				response.close();
				fireUploadListeners(readNum);
			}
			// the last chunk was read
			UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
			chunkFile.setChunk(chunk);
			chunkFile.setFileCode(randomNumber);
			chunkFile.setFilePath(file.getGfsPath()+file.getUploadedFile().getName());
			chunkFile.setTransferComplete(true);
			chunkFile.setType(file.getType());
			chunkFile.setLastUpdate(file.getUploadedFile().lastModified());
			response =  resource.accept(MediaType.APPLICATION_OCTET_STREAM).post(ClientResponse.class, chunkFile);		
			if (response.getStatus() != HttpStatus.SC_OK) {
				throw new JemException(NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
			}
			fireUploadListeners(readNum);
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
	 * notifies upload listener
	 * @param units number of byte uploaded
	 */
	private void fireUploadListeners(int units){
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
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<ReturnedObject>> generic = new GenericType<JAXBElement<ReturnedObject>>() {
		};
		try {
			JAXBElement<ReturnedObject> jaxbContact = resource.path(GfsManagerPaths.MAIN).path(GfsManagerPaths.FILE_DELETE).accept(MediaType.APPLICATION_XML).post(generic, request);
			ReturnedObject object = jaxbContact.getValue();
			if (object.hasException()) {
				throw new JemException(object.getExceptionMessage());
			}
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class GfsPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public GfsPostService(String subService) {
			super(GfsManager.this.getClient(), GfsManagerPaths.MAIN, subService);
		}

	}
}