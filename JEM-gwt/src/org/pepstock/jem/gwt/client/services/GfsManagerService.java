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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.log.JemException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
@RemoteServiceRelativePath(Services.GFS)
public interface GfsManagerService extends RemoteService {

	/**
	 * @param type 
	 * @param path 
	 * @param pathName 
	 * @return
	 * @throws JemException
	 */
	Collection<GfsFile> getFilesList(int type, String path, String pathName) throws JemException;

	/**
	 * 
	 * @param type
	 * @param file
	 * @param pathName 
	 * @return
	 * @throws JemException
	 */
	String getFile(int type, String file, String pathName) throws JemException;

}