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
package org.pepstock.jem.plugin.views;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all view part opened. Necessary to know when the last view part is closed, to logoff from JEM
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JemBroker {
	
	private static final List<String> VIEW_PARTS = new ArrayList<String>();

	/**
	 * Private constructor to avoid new instantiations
	 */
	private JemBroker() {
	}
	
	/**
	 * Adds the ID of a view part to list
	 * @param id view part ID
	 */
	public static void addViewPartID(String id){
		VIEW_PARTS.add(id);
	}
	
	/**
	 * Removes a view part from list by ID
	 * @param id view part ID
	 */
	public static void removeViewPartID(String id){
		VIEW_PARTS.remove(id);
	}
	
	/**
	 * Returns <code>true</code> is view part list is empty
	 * @return <code>true</code> is view part list is empty
	 */
	public static boolean isLastViewPart(){
		return VIEW_PARTS.isEmpty();
	}

}