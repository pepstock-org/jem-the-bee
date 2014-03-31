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

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.pepstock.jem.gwt.server.rest.GfsManagerImpl;
import org.pepstock.jem.gwt.server.rest.entities.GfsFileList;
import org.pepstock.jem.gwt.server.rest.entities.GfsOutputContent;
import org.pepstock.jem.gwt.server.rest.entities.GfsRequest;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.AbstractRestManager;
import org.pepstock.jem.util.RestClient;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Enrico Frigo
 * 
 */
public class GfsManager extends AbstractRestManager {

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

}