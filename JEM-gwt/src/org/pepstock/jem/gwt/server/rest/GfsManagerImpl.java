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
package org.pepstock.jem.gwt.server.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.services.GfsManager;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.rest.entities.GfsFileList;
import org.pepstock.jem.rest.entities.GfsOutputContent;
import org.pepstock.jem.rest.entities.GfsRequest;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.paths.GfsManagerPaths;


/**
 * Rest service to manage gfs.<br>
 * Pay attention to URL (@path) annotation
 * 
 * @author Enrico Frigo
 * 
 */
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
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public GfsFileList getFilesList(GfsRequest request) throws JemException {
		GfsFileList gfsContainer = new GfsFileList();
		if (isEnable()) {
			if (gfsManager == null) {
				initManager();
			}
			Collection<GfsFile> gfsList;
			try {
				gfsList = gfsManager.getFilesList(request.getType(), request.getItem(), request.getPathName());
				gfsContainer.setGfsFiles(gfsList);
				gfsContainer.setPath(request.getItem());
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
				gfsContainer.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(gfsContainer);
		}
		return gfsContainer;
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
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public GfsOutputContent getFile(GfsRequest request) throws JemException {
		GfsFileList gfsContainer = new GfsFileList();
		GfsOutputContent gfsFile = new GfsOutputContent();
		if (isEnable()) {
			if (gfsManager == null) {
				initManager();
			}

			try {
				gfsFile.setContent(gfsManager.getFile(request.getType(), request.getItem(), request.getPathName()));
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
				gfsContainer.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(gfsContainer);
		}
		return gfsFile;
	}

	/**
	 * Uploads a file. THIS IS STILL UNDER CONSTRUCTION 
	 * @param chunk chunk file to upload
	 * @return
	 */
	@POST
	@Path(GfsManagerPaths.FILE_UPLOAD)
	@Consumes({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response uploadFile(UploadedGfsChunkFile chunk) {
		if (isEnable()) {
			if (gfsManager == null) {
				initManager();
			}
			try {
				gfsManager.uploadChunk(chunk);
			} catch (ServiceMessageException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC265E, chunk.getFilePath(),e);
				String msg = NodeMessage.JEMC265E.toMessage().getFormattedMessage(chunk.getFilePath());
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
			}
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			return Response.status(HttpStatus.SC_SERVICE_UNAVAILABLE).entity(msg).build();
		}		
		return Response.status(HttpStatus.SC_OK).entity("OK").build();
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
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ReturnedObject deleteFile(GfsRequest request) throws JemException {
		ReturnedObject object = new ReturnedObject();
		if (isEnable()) {
			if (gfsManager == null) {
				initManager();
			}
			try {
				if (!gfsManager.deleteFile(request.getType(), request.getItem(), request.getPathName())){
					throw new JemException("Unable to delete "+request.getItem());
				}
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
				object.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(object);
		}
		return object;
	}
	
	/**
	 * Initializes a jobs manager
	 */
	private synchronized void initManager() {
		if (gfsManager == null) {
			gfsManager = new GfsManager();
		}
	}
}