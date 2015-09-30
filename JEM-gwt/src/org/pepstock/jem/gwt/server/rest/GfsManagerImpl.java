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
package org.pepstock.jem.gwt.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.protocol.HTTP;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.GfsManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.paths.GfsManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Rest service to manage gfs>.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
@Singleton
@Path(GfsManagerPaths.MAIN)
public class GfsManagerImpl extends DefaultServerResource {

	private GfsManager gfsManager = null;

	/**
	 * REST service which returns all files on GFS for a specific path. It
	 * checks also if the user has got the authorization to read or write that
	 * folders.
	 * 
	 * @param type
	 *            GFS type where to perform the query
	 * @param item
	 *            item to search
	 * @param pathName
	 *            when the type is DATA, is the path name
	 * @see GfsFile
	 * @see GfsFileType
	 * @return collections of files
	 */
	@GET
	@Path(GfsManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilesList(@PathParam(GfsManagerPaths.TYPE) String type, @QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item, @QueryParam(GfsManagerPaths.PATH_NAME_QUERY_STRING) String pathName) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// get the type of GFS in integer format
				int typeInt = GfsFileType.getType(type);
				// if it's not correct, bad request
				if (typeInt == GfsFileType.NO_TYPE) {
					return ResponseBuilder.JSON.badRequest(GfsManagerPaths.TYPE);
				}
				// if item is missing, bad request
				if (item == null) {
					return ResponseBuilder.JSON.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				// returns the list of files
				return ResponseBuilder.JSON.ok(gfsManager.getFilesList(typeInt, item, pathName));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which reads on GFS and returns the file
	 * 
	 * @param type
	 *            GFS type where to perform the query
	 * @param item
	 *            item to search
	 * @param pathName
	 *            when the type is DATA, is the path name
	 * @see GfsFile
	 * @see GfsFileType
	 * @return content of file
	 */
	@GET
	@Path(GfsManagerPaths.GET)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam(GfsManagerPaths.TYPE) String type, @QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item, @QueryParam(GfsManagerPaths.PATH_NAME_QUERY_STRING) String pathName) {
		// it uses OCTETSTREAM response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.OCTET_STREAM);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// get the type of GFS in integer format
				int typeInt = GfsFileType.getType(type);
				// if it's not correct, bad request
				if (typeInt == GfsFileType.NO_TYPE) {
					return ResponseBuilder.OCTET_STREAM.badRequest(GfsManagerPaths.TYPE);
				}
				// if item is missing, bad request
				if (item == null) {
					return ResponseBuilder.OCTET_STREAM.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				// returns the content of file
				return ResponseBuilder.OCTET_STREAM.ok(gfsManager.getFile(typeInt, item, pathName));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.OCTET_STREAM.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Uploads a file by chunks, therefore multiple calls could be performed.
	 * 
	 * @param type
	 *            GFS type where to perform the query
	 * @param fileCode
	 *            it's an integer (usually generated random) to use to create a
	 *            temporaru file
	 * @param item
	 *            item where storing the content of REST call
	 * @param lastUpdate
	 *            last modified timestamp (in long format, optional)
	 * @param transferComplete
	 *            <code>true</code> means it's last call, otherwise false.
	 * @param length
	 *            reads from HTTP headers the length of the body
	 * @param content
	 *            bytes of the content
	 * @return <code>true</code> if ended correctly otherwise false
	 */
	@POST
	@Path(GfsManagerPaths.PUT)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	public Response putFile(@PathParam(GfsManagerPaths.TYPE) String type, @PathParam(GfsManagerPaths.FILE_CODE) int fileCode, @QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item,
	        @DefaultValue("-1") @QueryParam(GfsManagerPaths.LAST_UPDATE_QUERY_STRING) long lastUpdate, @QueryParam(GfsManagerPaths.COMPLETED_QUERY_STRING) boolean transferComplete, @HeaderParam(HTTP.CONTENT_LEN) int length, byte[] content) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// get the type of GFS in integer format
				int typeInt = GfsFileType.getType(type);
				// if it's not correct, bad request
				if (typeInt == GfsFileType.NO_TYPE) {
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.TYPE);
				}
				// if item is missing, bad request
				if (item == null) {
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				if (length == 0){
					return ResponseBuilder.PLAIN.noContent();
				}
				// creates a class with all parameters
				UploadedGfsChunkFile chunkFile = new UploadedGfsChunkFile();
				// sets the data
				chunkFile.setChunk(content);
				// sets unique code
				chunkFile.setFileCode(fileCode);
				// relative path must be used
				// when you want to upload the file
				// maintaining a relative path of source file
				chunkFile.setFilePath(item);
				// sets transfer is not the LAST
				chunkFile.setTransferComplete(transferComplete);
				// number of bytes
				chunkFile.setNumByteToWrite(length);
				// where to put the file (GFS type)
				chunkFile.setType(typeInt);
				// sets the update time
				chunkFile.setLastUpdate((lastUpdate < 0) ? System.currentTimeMillis() : lastUpdate);
				// return true if OK
				return ResponseBuilder.PLAIN.ok(gfsManager.uploadChunk(chunkFile).toString());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().emit(NodeMessage.JEMC265E, item, e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which delete on GFS file
	 * 
	 * @param type
	 *            GFS type where to perform the query
	 * @param item
	 *            item to search
	 * @param pathName
	 *            when the type is DATA, is the path name
	 * @see GfsFile
	 * @see GfsFileType
	 * @return <code>true</code> if ended correctly
	 */
	@DELETE
	@Path(GfsManagerPaths.DELETE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteFile(@PathParam(GfsManagerPaths.TYPE) String type, @QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item, @QueryParam(GfsManagerPaths.PATH_NAME_QUERY_STRING) String pathName) {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// get the type of GFS in integer format
				int typeInt = GfsFileType.getType(type);
				// if it's not correct, bad request
				if (typeInt == GfsFileType.NO_TYPE) {
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.TYPE);
				}
				// if item is missing, bad request
				if (item == null) {
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				// returns true if OK
				return ResponseBuilder.PLAIN.ok(gfsManager.deleteFile(typeInt, item, pathName).toString());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#initManager()
	 */
	@Override
	boolean init() throws Exception {
		if (gfsManager == null) {
			gfsManager = new GfsManager();
		}
		return true;
	}
}