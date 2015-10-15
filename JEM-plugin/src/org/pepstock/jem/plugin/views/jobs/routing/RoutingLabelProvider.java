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
package org.pepstock.jem.plugin.views.jobs.routing;


import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.commons.JemConstants;
import org.pepstock.jem.plugin.views.jobs.JobLabelProvider;
import org.pepstock.jem.util.ColumnIndex;
/**
 * Provides the labels to use inside the ROUTING jobs table, for each job.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class RoutingLabelProvider extends JobLabelProvider {

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.jobs.JobLabelProvider#getColumnText(java.lang.Object, int)
	 */
    @Override
	public String getColumnText(Object obj, int index) {
		Job job = (Job) obj;
		switch (index) {
		case ColumnIndex.COLUMN_1:
			// job name
			return job.getName();
		case ColumnIndex.COLUMN_2:
			// jcl type
			return job.getJcl().getType();
		case ColumnIndex.COLUMN_3:
			// user
			return job.isUserSurrogated() ? job.getJcl().getUser() : job.getUser();
		case ColumnIndex.COLUMN_4:
			// env
			return job.getJcl().getEnvironment();
		case ColumnIndex.COLUMN_5:
			// domain
			return job.getJcl().getDomain();
		case ColumnIndex.COLUMN_6:
			// affinity
			return job.getJcl().getAffinity();
		case ColumnIndex.COLUMN_7:
			// submitted time
			return getDateFormatter().format(job.getSubmittedTime());
		case ColumnIndex.COLUMN_8:
			// routing status 
			return getRoutingStatus(job);
		case ColumnIndex.COLUMN_9:
			// priority
			return String.valueOf(job.getJcl().getPriority());
		case ColumnIndex.COLUMN_10:// hold
			return job.getJcl().isHold() ? "hold" : "";
		default:
			// default null
			return null;
		}
	}
    
    /**
     * Returns the routing status in string format
     * @param job job used to get the routing status
     * @return routing status
     */
    private String getRoutingStatus(Job job){
		// routing status 
		String status = null;
		Boolean isRoutingCommited = job.getRoutingInfo().isRoutingCommitted();
		if (isRoutingCommited == null) {
			status = "to be routed";
		} else if (!isRoutingCommited) {
			status = "waiting confirm";
		} else if (isRoutingCommited) {
			status = "routed";
		} else {
			status = JemConstants.UNKNOWN_BRACKETS;
		}
		return status;
    }
}
