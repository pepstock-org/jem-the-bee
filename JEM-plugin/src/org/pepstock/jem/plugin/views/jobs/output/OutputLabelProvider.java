/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.output;

import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.commons.JemConstants;
import org.pepstock.jem.plugin.views.jobs.JobLabelProvider;

/**
 * Provides the labels to use inside the OUTPUT jobs table, for each job.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class OutputLabelProvider extends JobLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.plugin.views.jobs.JobLabelProvider#getColumnText(java
	 * .lang.Object, int)
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
			// routed info
			return job.getRoutingInfo().getRoutedTime() != null ? JemConstants.YES : "";
		case 5:
			// domain
			return job.getJcl().getDomain();
		case 6:
			// affinity
			return job.getJcl().getAffinity();
		case 7:
			// ended time
			return getDateFormatter().format(job.getEndedTime());
		case 8:
			// result
			return String.valueOf(job.getResult().getReturnCode());
		case 9:
			// hold
			return job.getJcl().isHold() ? "hold" : "";
		case 10:
			// node member
			return job.getMemberLabel();
		default:
			// default null
			return null;
		}
	}
}
