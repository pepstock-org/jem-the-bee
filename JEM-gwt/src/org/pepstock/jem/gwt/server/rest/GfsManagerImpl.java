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
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.paths.GfsManagerPaths;

import com.sun.jersey.spi.resource.Singleton;


/**
 * Rest service to manage gfs.<br>
 * Pay attention to URL (@path) annotation
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
	 * REST service which returns all read files on GFS. It checks also if the
	 * user has got the authorization to read or write that folders.
	 * 
	 * @param type
	 *            could a integer value
	 * @see GfsFile
	 * @param request
	 *            the folder (relative to type of GFS) to use to read files and
	 *            directories and data path name of folder
	 * @return collections of files
	 * @throws JemException
	 *             if any error occurs
	 */
	@GET
	@Path(GfsManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilesList(@PathParam(GfsManagerPaths.TYPE) String type, 
			@QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item, 
			@QueryParam(GfsManagerPaths.PATH_NAME_QUERY_STRING) String pathName) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try {
				int typeInt = GfsFileType.getType(type);
				if (typeInt == GfsFileType.NO_TYPE){
					return ResponseBuilder.JSON.badRequest(GfsManagerPaths.TYPE);
				}
				if (item == null){
					return ResponseBuilder.JSON.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				return ResponseBuilder.JSON.ok(gfsManager.getFilesList(typeInt, item, pathName));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}


	/**
	 * REST service which reads on GFS and returns the file
	 * 
	 * @param type
	 *            could a integer value
	 * @param request
	 *            path where get the file from and data path name of file
	 * @see GfsFile
	 * @return content of file
	 * @throws JemException
	 *             if any error occurs
	 */
	@GET
	@Path(GfsManagerPaths.GET)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam(GfsManagerPaths.TYPE) String type, 
			@QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item, 
			@QueryParam(GfsManagerPaths.PATH_NAME_QUERY_STRING) String pathName) {
		Response resp = check(ResponseBuilder.OCTET_STREAM);
		if (resp == null){
			try {
				int typeInt = GfsFileType.getType(type);
				if (typeInt == GfsFileType.NO_TYPE){
					return ResponseBuilder.OCTET_STREAM.badRequest(GfsManagerPaths.TYPE);
				}
				if (item == null){
					return ResponseBuilder.OCTET_STREAM.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				return ResponseBuilder.OCTET_STREAM.ok(gfsManager.getFile(typeInt, item, pathName));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.OCTET_STREAM.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * Uploads a file. THIS IS STILL UNDER CONSTRUCTION 
	 * @param chunk chunk file to upload
	 * @return
	 */
	// FIXME
	@POST
	@Path(GfsManagerPaths.PUT)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
//	public Response uploadFile(UploadedGfsChunkFile chunk) {
	public Response uploadFile(@PathParam(GfsManagerPaths.TYPE) String type, 
			@PathParam(GfsManagerPaths.FILE_CODE) int fileCode,
			@QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item,
			@DefaultValue("-1") @QueryParam(GfsManagerPaths.LAST_UPDATE_QUERY_STRING) long lastUpdate,
			@QueryParam(GfsManagerPaths.COMPLETED_QUERY_STRING) boolean transferComplete,
			@HeaderParam(HTTP.CONTENT_LEN) int length,
			byte[] content) {
		
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				int typeInt = GfsFileType.getType(type);
				if (typeInt == GfsFileType.NO_TYPE){
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.TYPE);
				}
				if (item == null){
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				
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
				return ResponseBuilder.PLAIN.ok(gfsManager.uploadChunk(chunkFile).toString());
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC265E, item, e);
				return ResponseBuilder.JSON.severError(new MessageException(NodeMessage.JEMC265E, item));
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which delete on GFS 
	 * 
	 * @param type
	 *            could a integer value
	 * @param request
	 *            path where delete the file from and data path name of file
	 * @see GfsFile
	 * @return <code>true</code> if delete
	 * @throws JemException
	 *             if any error occurs
	 */
	@DELETE
	@Path(GfsManagerPaths.DELETE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteFile(@PathParam(GfsManagerPaths.TYPE) String type, 
			@QueryParam(GfsManagerPaths.ITEM_QUERY_STRING) String item, 
			@QueryParam(GfsManagerPaths.PATH_NAME_QUERY_STRING) String pathName) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				int typeInt = GfsFileType.getType(type);
				if (typeInt == GfsFileType.NO_TYPE){
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.TYPE);
				}
				if (item == null){
					return ResponseBuilder.PLAIN.badRequest(GfsManagerPaths.ITEM_QUERY_STRING);
				}
				return ResponseBuilder.PLAIN.ok(gfsManager.deleteFile(typeInt, item, pathName).toString());
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
				return ResponseBuilder.PLAIN.severError(new MessageException(UserInterfaceMessage.JEMG045E, e.getMessage()));
			}
		} else {
			return resp;
		}
	}
	
	/* (non-Javadoc)
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