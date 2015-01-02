/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Enrico Frigo - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.pepstock.jem.plugin.util.TimeDisplayUtils;

/**
 * Provides the labels to use inside the jobs table for each job.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class JobLabelProvider extends LabelProvider implements ITableLabelProvider {

	private SimpleDateFormat dateFormatter;

	/**
	 * Creates a date formatter
	 */
	public JobLabelProvider() {
		dateFormatter = new SimpleDateFormat(TimeDisplayUtils.TIMESTAMP_FORMAT);
	}

	/**
	 * @return the dateFormatter
	 */
	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
    @Override
    public final Image getColumnImage(Object obj, int index) {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
    @Override
    public abstract String getColumnText(Object obj, int index);

}
