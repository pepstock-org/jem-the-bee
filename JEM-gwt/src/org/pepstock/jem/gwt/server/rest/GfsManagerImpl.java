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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.rest.entities.GfsFileList;
import org.pepstock.jem.gwt.server.rest.entities.GfsOutputContent;
import org.pepstock.jem.gwt.server.services.GfsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Rest service to manage gfs.<br>
 * Pay attention to URL (@path) annotation
 * 
 * @author Enrico Frigo
 *
 */
@Path("/"+GfsManagerImpl.GFS_MANAGER_PATH)
public class GfsManagerImpl extends DefaultServerResource  {

	/**
	 * Key to define the path to bind this services
	 */
	public static final String GFS_MANAGER_PATH = "gfs";

	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String GFS_MANAGER_FILE_LIST = "ls";

	/**
	 * "data" parameter on url
	 */
	public static final String GFS_MANAGER_FILE_DATA = "data";

	/**
	 * "lib" parameter on url
	 */
	public static final String GFS_MANAGER_FILE_LIBRARY = "lib";
	
	/**
	 * "src" parameter on url
	 */
	public static final String GFS_MANAGER_FILE_SOURCE = "src";
	
	/**
	 * "class" parameter on url
	 */
	public static final String GFS_MANAGER_FILE_CLASS = "class";
	
	/**
	 * "bin" parameter on url
	 */
	public static final String GFS_MANAGER_FILE_BINARY = "bin";
	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH = "cat";

	private GfsManager gfsManager = null;
	
    /**
	 * REST service which returns all read DATA files on GFS.
	 * It checks also if the user has got the authorization to read or write that folders.
     * 
     * @param type could a integer value
	 * @see GfsFile
     * @param path the folder (relative to type of GFS) to use to read files and directories
     * @return collections of files
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_FILE_LIST+"/"+GFS_MANAGER_FILE_DATA)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsFileList getFilesListData(String path) throws JemException  {
		return getFilesList(GfsFile.DATA,path);
	}

    /**
	 * REST service which returns all read LIBRARY files on GFS.
	 * It checks also if the user has got the authorization to read or write that folders.
     * 
     * @param type could a integer value
	 * @see GfsFile
     * @param path the folder (relative to type of GFS) to use to read files and directories
     * @return collections of files
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_FILE_LIST+"/"+GFS_MANAGER_FILE_LIBRARY)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsFileList getFilesListLibrary(String path) throws JemException  {
		return getFilesList(GfsFile.LIBRARY,path);
	}
	
    /**
	 * REST service which returns all read SOURCE files on GFS.
	 * It checks also if the user has got the authorization to read or write that folders.
     * 
     * @param type could a integer value
	 * @see GfsFile
     * @param path the folder (relative to type of GFS) to use to read files and directories
     * @return collections of files
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_FILE_LIST+"/"+GFS_MANAGER_FILE_SOURCE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsFileList getFilesListSource(String path) throws JemException  {
		return getFilesList(GfsFile.SOURCE,path);
	}
	
    /**
	 * REST service which returns all read CLASS files on GFS.
	 * It checks also if the user has got the authorization to read or write that folders.
     * 
     * @param type could a integer value
	 * @see GfsFile
     * @param path the folder (relative to type of GFS) to use to read files and directories
     * @return collections of files
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_FILE_LIST+"/"+GFS_MANAGER_FILE_CLASS)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsFileList getFilesListClass(String path) throws JemException  {
		return getFilesList(GfsFile.CLASS,path);
	}
	
    /**
	 * REST service which returns all read BINARY files on GFS.
	 * It checks also if the user has got the authorization to read or write that folders.
     * 
     * @param type could a integer value
	 * @see GfsFile
     * @param path the folder (relative to type of GFS) to use to read files and directories
     * @return collections of files
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_FILE_LIST+"/"+GFS_MANAGER_FILE_BINARY)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsFileList getFilesListBinary(String path) throws JemException  {
		return getFilesList(GfsFile.BINARY,path);
	}
    /**
	 * REST service which returns all read files on GFS.
	 * It checks also if the user has got the authorization to read or write that folders.
     * 
     * @param type could a integer value
	 * @see GfsFile
     * @param path the folder (relative to type of GFS) to use to read files and directories
     * @return collections of files
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_FILE_LIST)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsFileList getFilesList(int type, String path) throws JemException{
		GfsFileList gfsContainer = new GfsFileList();
		if (isEnable()){
			if (gfsManager == null){
				initManager();
			}

			Collection<GfsFile> gfsList;
            try {
            	gfsList = gfsManager.getFilesList(type,path);
				gfsContainer.setGfsFiles(gfsList);
				gfsContainer.setPath(path);	            
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
            	gfsContainer.setExceptionMessage(e.getMessage());
            }				
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			gfsContainer.setExceptionMessage(msg);
		}
		return gfsContainer;
	}

	/**
	 * Return the content of the files requested, search on the DATA path.
	 * @param path path where get the file from
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	@POST
	@Path("/"+GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH+"/"+GFS_MANAGER_FILE_DATA)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsOutputContent getFileData(String path) throws JemException {
		return getFile(GfsFile.DATA,path);
	}

	/**
	 * Return the content of the files requested, search on the LIBRARY path.
	 * @param path path where get the file from
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	@POST
	@Path("/"+GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH+"/"+GFS_MANAGER_FILE_LIBRARY)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsOutputContent getFileLibrary(String path) throws JemException {
		return getFile(GfsFile.LIBRARY,path);
	}

	/**
	 * Return the content of the files requested, search on the SOURCE path.
	 * @param path path where get the file from
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	@POST
	@Path("/"+GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH+"/"+GFS_MANAGER_FILE_SOURCE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsOutputContent getFileSource(String path) throws JemException {
		return getFile(GfsFile.SOURCE,path);
	}
	
	/**
	 * Return the content of the files requested, search on the CLASS path.
	 * @param path path where get the file from
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	@POST
	@Path("/"+GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH+"/"+GFS_MANAGER_FILE_CLASS)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsOutputContent getFileClass(String path) throws JemException {
		return getFile(GfsFile.CLASS,path);
	}
	
	/**
	 * Return the content of the files requested, search on the BINARY path.
	 * @param path path where get the file from
	 * @return content of file
	 * @throws JemException if any exception occurs
	 */
	@POST
	@Path("/"+GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH+"/"+GFS_MANAGER_FILE_BINARY)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public GfsOutputContent getFileBinary(String path) throws JemException {
		return getFile(GfsFile.BINARY,path);
	}

	/**
	 * REST service which reads on GFS and returns the file
	 * 
     * @param type could a integer value
	 * @see GfsFile
     * @param file the file name to retrieve
     * @return content of file
     * @throws JemException if any error occurs
     */
	@POST
	@Path("/"+GFS_MANAGER_OUTPUT_FILE_CONTENT_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public GfsOutputContent getFile(int type, String file) throws JemException {
		GfsFileList gfsContainer = new GfsFileList();
		GfsOutputContent gfsFile = new GfsOutputContent();
		if (isEnable()){
			if (gfsManager == null){
				initManager();
			}
			
            try {
            	gfsFile.setContent(gfsManager.getFile(type, file));
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, e, e.getMessage());
            	gfsContainer.setExceptionMessage(e.getMessage());
            }				
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			gfsContainer.setExceptionMessage(msg);
		}
		return gfsFile;
	}


	/**
	 * Initializes a jobs manager
	 */
	private synchronized void initManager(){
		if (gfsManager == null){
			gfsManager = new GfsManager();
		}
	}
}