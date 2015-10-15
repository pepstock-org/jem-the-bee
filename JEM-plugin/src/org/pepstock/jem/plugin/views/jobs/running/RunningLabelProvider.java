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
package org.pepstock.jem.plugin.views.jobs.running;


import java.util.Date;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.util.TimeDisplayUtils;
import org.pepstock.jem.util.ColumnIndex;
/**
 * Provides the labels to use inside the RUNNING jobs table, for each job.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class RunningLabelProvider extends ColumnLabelProvider {

	/**
	 * Returns the text of column, using job and column index
	 * @param obj job instance
	 * @param index column index
	 * @return text of column
	 */
	public String getColumnText(Object obj, int index) {
		Job job = (Job) obj;
		// gets running time
		Date startedTime = job.getStartedTime();
		String rt = TimeDisplayUtils.getReadableTimeDiff(startedTime);
		switch (index) {
		case ColumnIndex.COLUMN_1:
			// job name
			return job.getName();
		case ColumnIndex.COLUMN_2:
			// jcl type
			return job.getJcl().getType();
		case ColumnIndex.COLUMN_3:
			// user
			return (job.isUserSurrogated()) ? job.getJcl().getUser() : job.getUser();
		case ColumnIndex.COLUMN_4:
			// step
			return job.getCurrentStep().getName();
		case ColumnIndex.COLUMN_5:
			// domain
			return job.getJcl().getDomain();
		case ColumnIndex.COLUMN_6:
			// affinity
			return job.getJcl().getAffinity();
		case ColumnIndex.COLUMN_7:
			// running time
			return rt;
		case ColumnIndex.COLUMN_8:
			// memory
			return String.valueOf(job.getJcl().getMemory());
		case ColumnIndex.COLUMN_9:
			// node member
			return job.getMemberLabel();
		default:
			// default null
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
	 */
    @Override
    public void update(ViewerCell cell) {
	    cell.setText(getColumnText(cell.getElement(), cell.getColumnIndex()));
	    Job job = (Job)cell.getElement();
	    // if job is waiting, sets gray color
	    if (job.getRunningStatus() == Job.WAITING_FOR_RESOURCES){
			Display display = Display.getCurrent();
			Color gray = display.getSystemColor(SWT.COLOR_GRAY);
	    	cell.setForeground(gray);
	    } else {
			Display display = Display.getCurrent();
			Color gray = display.getSystemColor(SWT.COLOR_BLACK);
	    	cell.setForeground(gray);
	    }
    }
}
