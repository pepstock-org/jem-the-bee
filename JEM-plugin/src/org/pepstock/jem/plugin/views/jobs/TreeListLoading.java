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
import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.util.ShellLoading;

/**
 * Extends loading and it is used when it gets the list of job output files from JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class TreeListLoading extends ShellLoading {
	
	private Job job = null;
	
	/**
	 * Creates the object with job instance
	 * @param shell Eclipse plugin
	 * @param job instance of job
	 */
    public TreeListLoading(Shell shell, Job job) {
    	super(shell);
	    this.job = job;
    }

	/**
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

}