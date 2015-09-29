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
 * REST service to manage GFS. <br>
 * It can manages all the actions of global file system, by REST.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class GfsManager extends AbstractRestManager {

	private List<UploadListener> listeners = new LinkedList<UploadListener>();

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient REST client instance
	 */
	public GfsManager(RestClient restClient) {
		super(restClient, GfsManagerPaths.MAIN);
	}

	/**
	 * Adds a upload listener. Used for upload
	 * 
	 * @param listener upload listeners
	 */
	public void addUploadListener(UploadListener listener) {
		// adds listener if not already added
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a upload listener. Used for upload
	 * 
	 * @param listener upload listeners
	 */
	public void removeUploadListener(UploadListener listener) {
		// removes listener if exists in the list
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	/**
	 * Returns a content file of a specific type
	 * 
	 * @param type type of file on GFS
	 * @param item file name on GFS
	 * @param pathName for GFS DATA type, is the path name of datasets rules
	 *            (optional)
	 * @return content of file
	 * @throws RestException if any exception occurs
	 */
	public byte[] getFile(String type, String item, String pathName) throws RestException {
		// creates a request builder with the APPLICATION/OCTET-STREAM media
		// type as accept type
		// because it receives the content of the file in the HTTP body in
		// byte[] format
		RequestBuilder builder = RequestBuilder.media(this, MediaType.APPLICATION_OCTET_STREAM);
		// replaces to the path the GFS type information
		String path = PathReplacer.path(GfsManagerPaths.GET).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).build();
		// adds item to search and data path name (if there is)
		// as HTTP query parameters
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, item);
		if (pathName != null) {
			queryParams.put(GfsManagerPaths.PATH_NAME_QUERY_STRING, pathName);
		}
		// performs the request adding the query parameters
		ClientResponse response = builder.query(queryParams).get(path);
		// if HTTP status code is OK, return the byte array of file
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(byte[].class);
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), response.getEntity(String.class));
		}
	}

	/**
	 * Returns the list of file from a specific folder
	 * 
	 * @param type type of file on GFS
	 * @param item file name on GFS
	 * @param pathName for GFS DATA type, is the path name of datasets rules
	 *            (optional)
	 * @return list of files
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<GfsFile> getFilesList(String type, String item, String pathName) throws RestException {
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// replaces to the path the GFS type information
		String path = PathReplacer.path(GfsManagerPaths.LIST).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).build();
		// adds item to search and data path name (if there is)
		// as HTTP query parameters
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, item);
		if (pathName != null) {
			queryParams.put(GfsManagerPaths.PATH_NAME_QUERY_STRING, pathName);
		}
		try {
			// performs the request adding the query parameters
			ClientResponse response = builder.query(queryParams).get(path);
			// if HTTP status code is OK, return list of files
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (List<GfsFile>) JsonUtil.getInstance().deserializeList(response, GfsFile.class);
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			// throw an exception of JSON parsing
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}

	/**
	 * Uploads a file via rest service
	 * 
	 * @param file internal file to upload the file
	 * @return <code>true</code> if everything went OK, otherwise
	 *         <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean putFile(UploadedGfsFile file) throws RestException {
		return putFile(GfsFileType.getName(file.getType()), file.getGfsPath(), file.getUploadedFile(), file.getRelativePath());
	}

	/**
	 * Uploads a file via rest service
	 * 
	 * @param type GFS type where storing the file
	 * @param gfsPath the path inside of GFS type where storing the file
	 * @param file file to be uploaded
	 * @return <code>true</code> if everything went OK, otherwise
	 *         <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean putFile(String type, String gfsPath, File file) throws RestException {
		return putFile(type, gfsPath, file, null);
	}

	/**
	 * Uploads a file via rest service
	 * 
	 * @param type GFS type where storing the file
	 * @param gfsPath the path inside of GFS type where storing the file
	 * @param file file to be uploaded
	 * @param filePathAndName the new file name where store the file
	 * @return <code>true</code> if everything went OK, otherwise
	 *         <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean putFile(String type, String gfsPath, File file, String filePathAndName) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type and APPLICATION/OCTET_STREAM as content type
		// because it sends the certificate in bytes inside the HTTP body
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM);
		// creates a random code, to identify the chunk
		Random random = new Random();
		int randomNumber = random.nextInt(Integer.MAX_VALUE);
		// replaces to the path the GFS type information and file code, passed
		// as path parameter
		String path = PathReplacer.path(GfsManagerPaths.PUT).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).replace(GfsManagerPaths.FILE_CODE_PATH_PARAM, String.valueOf(randomNumber)).build();

		// relative path must be used
		// when you want to upload the file
		// maintaining a relative path of source file
		String destFile = null;
		if (filePathAndName != null) {
			destFile = gfsPath + filePathAndName;
		} else {
			destFile = gfsPath + file.getName();
		}

		// adds item to search and last update of file
		// as HTTP query parameters
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, destFile);
		queryParams.put(GfsManagerPaths.LAST_UPDATE_QUERY_STRING, String.valueOf(file.lastModified()));

		// cretaes input stream to read the file
		FileInputStream fis = null;
		try {
			// reads file using NIO and channel
			fis = new FileInputStream(file);
			FileChannel ch = fis.getChannel();
			// creates bytes buffer
			ByteBuffer bb = ByteBuffer.allocate(UploadedGfsChunkFile.MAX_CHUNK_SIZE);
			int nRead;
			// sets the total amount of bytes to transfer
			long bytesToTransfer = ch.size();
			// cycle to read all bytes
			while ((nRead = ch.read(bb)) != -1) {
				// if read 0 byte, is ended
				if (nRead == 0)
					continue;
				// sets buffer attributes
				bb.position(0);
				bb.limit(nRead);

				// reduces the amount of bytes to transfer
				bytesToTransfer = bytesToTransfer - nRead;
				// if 0, is the last call
				// and then sets the transfer completed query parameter
				// setting to true
				if (bytesToTransfer == 0) {
					// last chunk. It's last call
					queryParams.put(GfsManagerPaths.COMPLETED_QUERY_STRING, Boolean.TRUE.toString());
				} else {
					// It's an intermediate call
					queryParams.put(GfsManagerPaths.COMPLETED_QUERY_STRING, Boolean.FALSE.toString());
				}
				// copies the bytes in a array
				byte[] bytes = new byte[nRead];
				System.arraycopy(bb.array(), 0, bytes, 0, nRead);
				// performs the rest call
				// setting query parameters and bytes to transfer
				ClientResponse response = builder.query(queryParams).post(path, bytes);
				// because of the accept type is always TEXT/PLAIN
				// it gets the string
				String result = response.getEntity(String.class);
				// if HTTP status code is OK, parse to boolean
				if (response.getStatus() == Status.OK.getStatusCode()) {
					// the transfer returns false
					// there is an error
					// and close the cycle here
					if (!Boolean.parseBoolean(result)) {
						return false;
					}
				} else {
					// otherwise throws the exception using the
					// body of response as message of exception
					throw new RestException(response.getStatus(), result);
				}
				// notifies all listeners
				fireUploadListeners(nRead);
				// clear the buffer for next cycle
				bb.clear();
			}
			// here returns ALWAYS true
			// transfer completed
			return true;
		} catch (IOException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), NodeMessage.JEMC265E.toMessage().getFormattedMessage(file.getAbsolutePath(), e.getMessage()));
		} finally {
			// close finally always the file stream
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Notifies upload listener
	 * 
	 * @param units number of byte uploaded
	 */
	private void fireUploadListeners(int units) {
		// if not empty,
		// scans all listeners calling
		// the method passing the amount of bytes
		// transferred to JEM
		if (!listeners.isEmpty()) {
			for (UploadListener listener : listeners) {
				listener.setUnitsDone(units);
			}
		}
	}

	/**
	 * Delete a file of a specific type
	 * 
	 * @param type type of file on GFS
	 * @param item file name on GFS
	 * @param pathName for GFS DATA type, is the path name of datasets rules
	 *            (optional)
	 * @return <code>true</code> if the file has been deleted otherwise
	 *         <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public boolean delete(String type, String item, String pathName) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces to the path the GFS type information
		String path = PathReplacer.path(GfsManagerPaths.DELETE).replace(GfsManagerPaths.TYPE_PATH_PARAM, type).build();
		// adds item to search and data path name (if there is)
		// as HTTP query parameters
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put(GfsManagerPaths.ITEM_QUERY_STRING, item);
		if (pathName != null) {
			queryParams.put(GfsManagerPaths.PATH_NAME_QUERY_STRING, pathName);
		}
		// performs the rest call
		// setting query parameters
		ClientResponse response = builder.query(queryParams).delete(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is OK, parse to boolean
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return Boolean.parseBoolean(result);
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}
}