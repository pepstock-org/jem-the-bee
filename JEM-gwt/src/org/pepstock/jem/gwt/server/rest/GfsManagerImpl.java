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

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.GfsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.entities.GfsRequest;
import org.pepstock.jem.rest.paths.GfsManagerPaths;

import com.sun.jersey.spi.resource.Singleton;


/**
 * Rest service to manage gfs.<br>
 * Pay attention to URL (@path) annotation
 * 
 * @author Enrico Frigo
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
	@POST
	@Path(GfsManagerPaths.FILE_LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilesList(GfsRequest request) {
		Response resp = check();
		if (resp == null){
			try {
				Collection<GfsFile> gfsList = gfsManager.getFilesList(request.getType(), request.getItem(), request.getPathName());
				return ok(gfsList);
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return severError(e);
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
	@POST
	@Path(GfsManagerPaths.OUTPUT_FILE_CONTENT_PATH)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFile(GfsRequest request) {
		Response resp = check();
		if (resp == null){
			try {
				String content = gfsManager.getFile(request.getType(), request.getItem(), request.getPathName());
				return ok(content);
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return severError(e);
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
	@POST
	@Path(GfsManagerPaths.FILE_UPLOAD)
//	@Consumes({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(UploadedGfsChunkFile chunk) {
		Response resp = check();
		if (resp == null){
			try {
				return ok(gfsManager.uploadChunk(chunk));
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC265E, chunk.getFilePath(), e);
				return severError(new MessageException(NodeMessage.JEMC265E, chunk.getFilePath()));
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
	@POST
	@Path(GfsManagerPaths.FILE_DELETE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFile(GfsRequest request) {
		Response resp = check();
		if (resp == null){
			try {
				return ok(gfsManager.deleteFile(request.getType(), request.getItem(), request.getPathName()));
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
				return severError(new MessageException(UserInterfaceMessage.JEMG045E, e.getMessage()));
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