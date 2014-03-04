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

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.util.ShellLoading;

/**
 * Extends loading and it is used when it is looking for jobs from JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class JobSearchLoading extends ShellLoading {
	
	private String filter = null;

	/**
	 * Creates object using the filter for searching
	 * @param shell Eclipse shell
	 * @param filter filter for searching
	 */
	public JobSearchLoading(Shell shell, String filter) {
		super(shell);
		this.filter = filter;
	}
	
	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

}