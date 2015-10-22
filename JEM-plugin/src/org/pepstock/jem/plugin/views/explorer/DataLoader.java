/*******************************************************************************
* Copyright (C) 2012-2015 pepstock.org.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     Andrea "Stock" Stocchero
******************************************************************************/
package org.pepstock.jem.plugin.views.explorer;

import java.util.ArrayList;
import java.util.Collection;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.rest.RestException;

/**
 * Utility to load collection of files from JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class DataLoader {

	/**
	 * Private constructor to avoid new instantiations
	 */
	private DataLoader() {
	}
	
	/**
	 * Loads data from JEM for a specific data type (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 * 
	 * @param type data type (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 * @param longName long name of folder of GFS 
	 * @param pathName data path name
	 * @return colletion of files of folder requested
	 * @throws RestException if any error occurs
	 */
	public static Collection<GfsFile> loadData(int type, String longName, String pathName) throws RestException {
		if (Client.getInstance().isLogged()){
			return Client.getInstance().getGfsFileList(type, longName, pathName);
		}
		return new ArrayList<GfsFile>();
	}
}