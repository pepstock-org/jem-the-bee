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
package org.pepstock.jem.plugin.views.jobs;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface used by table container to refresh the table content after searching 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface Refresher {
	
	/**
	 * Returns parent composite 
	 * @return parent composite 
	 */
	Composite getComposite();
	
	/**
	 * Performs call by REST to load data
	 * @param filter searching string 
	 */
	void refresh(String filter);

}
