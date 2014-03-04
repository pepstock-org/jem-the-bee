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

import org.pepstock.jem.GfsFile;

/**
 * Utility that returns the path name based on data type of GFS folder.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class PathName {

	/**
	 *  Private constructor to avoid new instantiations 
	 */
	private PathName() {
	}
	
	/**
	 * Returns the name of path of GFS folder.
	 * @param type data type (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 * @return the name of path of GFS folder.
	 */
	public static final String getPathName(int type){
		String name = null;
		switch (type) {
		case GfsFile.DATA:
			name = "Data";
			break;
		case GfsFile.LIBRARY:
			name = "Library";
			break;
		case GfsFile.SOURCE:
			name = "Source";
			break;
		case GfsFile.CLASS:
			name = "Class";
			break;
		case GfsFile.BINARY:
			name = "Binary";
			break;
		default:
			name = "Data";
			break;
		}
		return name;
	}

}