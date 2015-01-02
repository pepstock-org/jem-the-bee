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
		case 0:
			// job name
			return job.getName();
		case 1:
			// jcl type
			return job.getJcl().getType();
		case 2:
			// user
			return job.isUserSurrogated() ? job.getJcl().getUser() : job.getUser();
		case 3:
			// env
			return job.getJcl().getEnvironment();
		case 4:
			// domain
			return job.getJcl().getDomain();
		case 5:
			// affinity
			return job.getJcl().getAffinity();
		case 6:
			// submitted time
			return getDateFormatter().format(job.getSubmittedTime());
		case 7:
			// routing status 
			return getRoutingStatus(job);
		case 8:
			// priority
			return String.valueOf(job.getJcl().getPriority());
		case 9:// hold
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
