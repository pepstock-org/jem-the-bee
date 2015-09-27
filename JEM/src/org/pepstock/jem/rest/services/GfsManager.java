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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.GfsManagerPaths;

import com.sun.jersey.api.client.ClientResponse;

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
	public byte[] getFile(String type, String item, String pathName) throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.APPLICATION_OCTET_STREAM);
		String path = PathReplacer.path(GfsManagerPaths.GET).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).build();
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, item);
		if (pathName != null){
			queryParams.put(GfsManagerPaths.PATH_NAME_QUERY_STRING, pathName);
		}
		// creates the returned object
		ClientResponse response = builder.query(queryParams).get(path);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(byte[].class);
		} else {
			throw new RestException(response.getStatus(), response.getEntity(String.class));
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
	public Collection<GfsFile> getFilesList(String type, String item, String pathName) throws RestException {
		RequestBuilder builder = RequestBuilder.media(this);
		String path = PathReplacer.path(GfsManagerPaths.LIST).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).build();
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, item);
		if (pathName != null){
			queryParams.put(GfsManagerPaths.PATH_NAME_QUERY_STRING, pathName);
		}		
	    try {
			// creates the returned object
			ClientResponse response = builder.query(queryParams).get(path);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<GfsFile>)JsonUtil.getInstance().deserializeList(response, GfsFile.class);
			} else {
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
	    } catch (IOException e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Uploads a file via rest service
	 * @param file internal file to upload the file
	 * @return <code>true</code> if everything went OK, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean putFile(UploadedGfsFile file) throws RestException{
		return putFile(GfsFileType.getName(file.getType()), file.getGfsPath(), file.getUploadedFile(), file.getRelativePath());
	}
	
	/**
	 * Uploads a file via rest service
	 * @param type GFS type where storing the file
	 * @param gfsPath the path inside of GFS type where storing the file
	 * @param file file to be uploaded
	 * @return <code>true</code> if everything went OK, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean putFile(String type, String gfsPath, File file) throws RestException{
		return putFile(type, gfsPath, file, null);
	}
	
	/**
	 * Uploads a file via rest service
	 * @param type GFS type where storing the file
	 * @param gfsPath the path inside of GFS type where storing the file
	 * @param file file to be uploaded
	 * @param filePathAndName the new file name where store the file
	 * @return <code>true</code> if everything went OK, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean putFile(String type, String gfsPath, File file, String filePathAndName) throws RestException{
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM);
		
		// creates a random code, to identify the chunk
		Random random = new Random();
		int randomNumber = random.nextInt(Integer.MAX_VALUE);
		
		String path = PathReplacer.path(GfsManagerPaths.PUT).replace(GfsManagerPaths.TYPE_PATH_PARAM, type)
				.replace(GfsManagerPaths.FILE_CODE_PATH_PARAM, String.valueOf(randomNumber)).build();
		
		// relative path must be used
		// when you want to upload the file
		// maintaining a relative path of source file
		String destFile = null;
		if (filePathAndName != null){
			destFile = gfsPath + filePathAndName;
		} else {
			destFile = gfsPath + file.getName();
		}

		// prepares the query params of REST call
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, destFile);
		queryParams.put(GfsManagerPaths.LAST_UPDATE_QUERY_STRING, String.valueOf(file.lastModified()));
		
		FileInputStream fis = null;
		try {
			// reads file 
			fis = new FileInputStream(file);
			FileChannel ch = fis.getChannel( );
			ByteBuffer bb = ByteBuffer.allocate(UploadedGfsChunkFile.MAX_CHUNK_SIZE);
			int nRead;
			long bytesToTransfer = ch.size();
			while ( (nRead=ch.read(bb)) != -1 )
			{
			    if ( nRead == 0 )
			        continue;
			    bb.position(0);
			    bb.limit(nRead);
			    
			    bytesToTransfer = bytesToTransfer - nRead;
			    if (bytesToTransfer == 0){
			    	// last chunk. It's last call
			    	queryParams.put(GfsManagerPaths.COMPLETED_QUERY_STRING, Boolean.TRUE.toString());
			    } else {
			    	// It's an intermediate call
			    	queryParams.put(GfsManagerPaths.COMPLETED_QUERY_STRING, Boolean.FALSE.toString());
			    }
			    byte[] bytes = new byte[nRead];
			    System.arraycopy(bb.array(), 0, bytes, 0, nRead);
			    // creates the returned object
			    ClientResponse response = builder.query(queryParams).post(path, bytes);
			    String result = response.getEntity(String.class);
			    if (response.getStatus() == Status.OK.getStatusCode()){
			    	if (!Boolean.parseBoolean(result)){
			    		return false;
			    	}
			    } else {
			    	throw new RestException(response.getStatus(), result);
			    }
			    fireUploadListeners(nRead);
			    bb.clear( );
			}
			return true;
		} catch (IOException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getAbsolutePath(), e.getMessage()));
		} finally {
			if (fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					 LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

//	/**
//	 * Uploads a file via rest service
//	 * @param file
//	 * @return response status
//	 * @throws JemException
//	 */
//	public int upload(UploadedGfsFile file) throws RestException {
//		// gets the web resource
//		// adding paths for upload
//		ClientResponse response = null;
//		FileInputStream fis = null;
//		try {
//			// file input stream to read 
//			fis = new FileInputStream(file.getUploadedFile());
//			// creates a chunk in bytes
//			byte[] chunk = new byte[UploadedGfsChunkFile.MAX_CHUNK_SIZE];
//			// creates a random code, to identify the chunk
//			Random random = new Random();
//			int randomNumber = random.nextInt(Integer.MAX_VALUE);
//			// read and write file in chunks
//			int readNum = 0;
//			// reads the file with the buffer 
//			// the last send will be done out of this FOR
//			for (; (readNum = fis.read(chunk)) != -1;) {
//				// creates a chunk object to serialize and send by REST
//				UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
//				// sets the data
//				chunkFile.setChunk(chunk);
//				// sets unique code
//				chunkFile.setFileCode(randomNumber);
//				// relative path must be used
//				// when you want to upload the file
//				// maintaining a relative path of source file
//				if (file.getRelativePath() != null){
//					chunkFile.setFilePath(file.getGfsPath()+file.getRelativePath());
//				} else {
//					chunkFile.setFilePath(file.getGfsPath()+file.getUploadedFile().getName());
//				}
//				// sets transfer is not the LAST
//				chunkFile.setTransferComplete(false);
//				// number of bytes
//				chunkFile.setNumByteToWrite(readNum);
//				// where to put the file (GFS type)
//				chunkFile.setType(file.getType());
//				// sets the update time 
//				chunkFile.setLastUpdate(file.getUploadedFile().lastModified());
//				
//				// creates and performs post HTTP
//				
//				response = post(GfsManagerPaths.UPLOAD, chunkFile);
//				
//				// checks if everything went OK!
//				if (response.getStatus() != Status.OK.getStatusCode()) {
//					throw new RestException(response.getStatus(),NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
//				}
//				// close the response and activate the listeners
//				response.close();
//				fireUploadListeners(readNum);
//			}
//			// the last chunk was read
//			UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
//			// adds the last chunk
//			chunkFile.setChunk(chunk);
//			// sets random unique code
//			chunkFile.setFileCode(randomNumber);
//			// relative path must be used
//			// when you want to upload the file
//			// maintaining a relative path of source file
//			if (file.getRelativePath() != null){
//				chunkFile.setFilePath(file.getGfsPath()+file.getRelativePath());
//			} else {
//				chunkFile.setFilePath(file.getGfsPath()+file.getUploadedFile().getName());
//			}
//			// is the LAST call
//			chunkFile.setTransferComplete(true);
//			// where to put the file (GFS type)
//			chunkFile.setType(file.getType());
//			// sets the update time
//			chunkFile.setLastUpdate(file.getUploadedFile().lastModified());
//			
//			// creates and performs post HTTP
//			response = post(GfsManagerPaths.UPLOAD, chunkFile);
//			// checks if everything went OK!
//			if (response.getStatus() != Status.OK.getStatusCode()) {
//				throw new RestException(response.getStatus(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), response.getEntity(String.class)));
//			}
//			// activate the listeners
//			fireUploadListeners(readNum);
//			// doesn't close here the response. see finally
//			return response.getStatus();
//		} catch (UniformInterfaceException e) {
//			LogAppl.getInstance().debug(e.getMessage(), e);
//			if (e.getResponse().getStatus() != 204) {
//				throw new RestException(e.getResponse().getStatus(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), e.getMessage()));
//			}
//			return e.getResponse().getStatus();
//		} catch (IOException e) {
//			LogAppl.getInstance().debug(e.getMessage(), e);
//			throw new RestException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getUploadedFile().getAbsolutePath(), e.getMessage()));
//		} finally {
//			// if file inut stream
//			// is open, it closes
//			if (fis != null){
//				try {
//	                fis.close();
//                } catch (IOException e) {
//	                LogAppl.getInstance().ignore(e.getMessage(), e);
//                }
//			}
//			// important to CLOSE ALWAYS the response
//			if (response != null){
//				response.close();
//			}
//		}
//	}
	
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
	public boolean delete(String type, String item, String pathName) throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		String path = PathReplacer.path(GfsManagerPaths.DELETE).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).build();
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, item);
		if (pathName != null){
			queryParams.put(GfsManagerPaths.PATH_NAME_QUERY_STRING, pathName);
		}		
		// creates the returned object
		ClientResponse response = builder.query(queryParams).delete(path);
		String result = response.getEntity(String.class);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else {
			throw new RestException(response.getStatus(), result);
		}
	}
}