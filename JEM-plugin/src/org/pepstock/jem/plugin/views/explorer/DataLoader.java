/*******************************************************************************
* Copyright (c) 2012-2014 pepstock.org.
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

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.plugin.Client;

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
	 * @throws JemException if any error occurs
	 */
	public static Collection<GfsFile> loadData(int type, String longName, String pathName) throws JemException {
		if (Client.getInstance().isLogged()){
			Collection<GfsFile> filesList = null ;
			switch (type) {
			case GfsFile.DATA:
				filesList = Client.getInstance().getGfsFileListData(longName, pathName);
				break;
			case GfsFile.LIBRARY:
				filesList = Client.getInstance().getGfsFileListLibrary(longName, pathName);
				break;
			case GfsFile.SOURCE:
				filesList = Client.getInstance().getGfsFileListSource(longName, pathName);
				break;
			case GfsFile.CLASS:
				filesList = Client.getInstance().getGfsFileListClass(longName, pathName);
				break;
			case GfsFile.BINARY:
				filesList = Client.getInstance().getGfsFileListBinary(longName, pathName);
				break;
			default:
				// default load DATA
				filesList = Client.getInstance().getGfsFileListData(longName, pathName);
				break;
			}
			return filesList;
		}
		return new ArrayList<GfsFile>();
	}
}