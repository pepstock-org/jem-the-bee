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
package org.pepstock.jem.plugin.views.jobs.inspector;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.util.ShellLoading;
import org.pepstock.jem.plugin.views.jobs.inspector.model.Category;
import org.pepstock.jem.plugin.views.jobs.inspector.model.ProducedOutput;

/**
 * Extends loading and it is used when it is downloading a output file of a job from JEM.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class FileLoading extends ShellLoading {

	private Category category = null;
	
	private ProducedOutput output = null;
	
	/**
	 * Creates object using a category
	 * @param shell Eclipse shell
	 * @param category folder on output directory of job
	 */
    public FileLoading(Shell shell, Category category) {
	    super(shell);
	    this.category = category;
    }

	/**
	 * Creates object using a output file 
	 * @param shell Eclipse shell
	 * @param output file in output directory of job
	 */
    public FileLoading(Shell shell, ProducedOutput output) {
	    super(shell);
	    this.output = output;
    }

	/**
	 * @return the output
	 */
	public ProducedOutput getOutput() {
		return output;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}
}