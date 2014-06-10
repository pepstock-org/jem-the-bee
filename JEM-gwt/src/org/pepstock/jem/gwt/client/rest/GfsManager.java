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
package org.pepstock.jem.gwt.client.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.apache.http.HttpStatus;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.gwt.server.rest.GfsManagerImpl;
import org.pepstock.jem.gwt.server.rest.entities.GfsFileList;
import org.pepstock.jem.gwt.server.rest.entities.GfsOutputContent;
import org.pepstock.jem.gwt.server.rest.entities.GfsRequest;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.util.AbstractRestManager;
import org.pepstock.jem.util.RestClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Enrico Frigo
 * 
 */
public class GfsManager extends AbstractRestManager {
	
	private LinkedList<UploadListener> listeners = new LinkedList<UploadListener>();

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
	 * Returns the content file in DATA
	 * @param request data path
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	public String getFileData(GfsRequest request) throws JemException {
		return getFile(GfsManagerImpl.GFS_MANAGER_FILE_DATA + "", request);
	}

	/**
	 * Returns the content file in LIBRARY
	 * @param request library path
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	public String getFileLibrary(GfsRequest request) throws JemException {
		return getFile(GfsManagerImpl.GFS_MANAGER_FILE_LIBRARY + "", request);
	}

	/**
	 * Returns the content file in CLASS
	 * @param request classes path
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	public String getFileClass(GfsRequest request) throws JemException {
		return getFile(GfsManagerImpl.GFS_MANAGER_FILE_CLASS + "", request);
	}

	/**
	 * Returns the content file in SOURCE
	 * @param request source path
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	public String getFileSource(GfsRequest request) throws JemException {
		return getFile(GfsManagerImpl.GFS_MANAGER_FILE_SOURCE + "", request);
	}

	/**
	 * Returns the content file in BINARY
	 * @param request binary path
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	public String getFileBinary(GfsRequest request) throws JemException {
		return getFile(GfsManagerImpl.GFS_MANAGER_FILE_BINARY + "", request);
	}

	/**
	 * Returns a content file of a specific type
	 * @param type type of file
	 * @param request file name
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	public String getFile(String type, GfsRequest request) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<GfsOutputContent>> generic = new GenericType<JAXBElement<GfsOutputContent>>() {
		};
		try {
			JAXBElement<GfsOutputContent> jaxbContact = resource.path(GfsManagerImpl.GFS_MANAGER_PATH).path(GfsManagerImpl.GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH).path(type).accept(MediaType.APPLICATION_XML).post(generic, request);
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
	 * Returns the list of files from data directory
	 * @param request data directory 
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	public GfsFileList getFilesListData(GfsRequest request) throws JemException {
		return getFilesList(GfsManagerImpl.GFS_MANAGER_FILE_DATA + "", request);
	}

	/**
	 * Returns the list of files from library directory
	 * @param request library directory 
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	public GfsFileList getFilesListLibrary(GfsRequest request) throws JemException {
		return getFilesList(GfsManagerImpl.GFS_MANAGER_FILE_LIBRARY + "", request);
	}

	/**
	 * Returns the list of files from classes directory
	 * @param request classes directory 
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	public GfsFileList getFilesListClass(GfsRequest request) throws JemException {
		return getFilesList(GfsManagerImpl.GFS_MANAGER_FILE_CLASS + "", request);
	}

	/**
	 * Returns the list of files from source directory
	 * @param request source directory 
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	public GfsFileList getFilesListSource(GfsRequest request) throws JemException {
		return getFilesList(GfsManagerImpl.GFS_MANAGER_FILE_SOURCE + "", request);
	}

	/**
	 * Returns the list of files from binary directory
	 * @param request binary directory 
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	public GfsFileList getFilesListBinary(GfsRequest request) throws JemException {
		return getFilesList(GfsManagerImpl.GFS_MANAGER_FILE_BINARY + "", request);
	}

	/**
	 * Returns the list of file from a specific folder
	 * @param type type of files
	 * @param request specific folder
	 * @return list of files
	 * @throws JemException if any exception occurs
	 */
	private GfsFileList getFilesList(String type, GfsRequest request) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<GfsFileList>> generic = new GenericType<JAXBElement<GfsFileList>>() {
		};
		try {
			JAXBElement<GfsFileList> jaxbContact = resource.path(GfsManagerImpl.GFS_MANAGER_PATH).path(GfsManagerImpl.GFS_MANAGER_FILE_LIST).path(type).accept(MediaType.APPLICATION_XML).post(generic, request);
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
		WebResource resource = getClient().getBaseWebResource().path(GfsManagerImpl.GFS_MANAGER_PATH).path(GfsManagerImpl.GFS_MANAGER_FILE_UPLOAD);
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
		for (UploadListener listener : listeners){
			listener.setUnitsDone(units);
		}
	}
}