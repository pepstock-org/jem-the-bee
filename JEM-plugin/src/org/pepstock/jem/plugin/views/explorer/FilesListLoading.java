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

import org.pepstock.jem.plugin.util.Loading;

/**
 * Extends loading and it is used when it is downloading the list of files of a specific folder.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class FilesListLoading extends Loading {
	
	private String filter = null;

	/**
	 * Creates object with the folder path to search in JEM.
	 * @param filter the folder path to search in JEM.
	 */
    public FilesListLoading(String filter) {
	    super();
	    this.filter = filter;
    }

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

}