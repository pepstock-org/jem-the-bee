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

import org.pepstock.jem.plugin.util.Loading;

/**
 * Extends loading and it is used when it is downloading the list of files of a specific folder.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class FilesListLoading extends Loading {
	
	private String filter = null;
	
	private String pathName = null;

	/**
	 * Creates object with the folder path to search in JEM.
	 * @param filter the folder path to search in JEM.
	 * @param pathName data path name
	 */
    public FilesListLoading(String filter, String pathName) {
	    super();
	    this.filter = filter;
	    this.pathName = pathName;
    }

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @return the pathName
	 */
	public String getPathName() {
		return pathName;
	}

}