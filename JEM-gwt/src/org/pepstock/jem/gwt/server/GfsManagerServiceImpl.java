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
package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gwt.client.services.GfsManagerService;
import org.pepstock.jem.gwt.server.services.GfsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.CharSet;

/**
 * Is GWT server service which can provide all methods to manage global file
 * system resources
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GfsManagerServiceImpl extends DefaultManager implements GfsManagerService {

	private static final long serialVersionUID = 1L;

	private transient GfsManager gfsManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.GfsManagerService#getFilesList(int,
	 * java.lang.String)
	 */
	@Override
	public Collection<GfsFile> getFilesList(int type, String path, String pathName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (gfsManager == null) {
			initManager();
		}
		try {
			return gfsManager.getFilesList(type, path, pathName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.GfsManagerService#getFile(int,
	 * java.lang.String)
	 */
	@Override
	public String getFile(int type, String file, String pathName) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (gfsManager == null) {
			initManager();
		}
		try {
			byte[] content = gfsManager.getFile(type, file, pathName);
			String result = null;
			if (content != null){
				result = new String(content, CharSet.DEFAULT);
			}
			return result;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/**
	 * Initializes a manager
	 * 
	 * @throws JemException
	 *             if any exception occurs
	 */
	private synchronized void initManager() throws JemException {
		if (gfsManager == null) {
			try {
				gfsManager = new GfsManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG045E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

}