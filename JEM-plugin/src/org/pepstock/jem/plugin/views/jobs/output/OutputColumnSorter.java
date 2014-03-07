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
import org.pepstock.jem.plugin.views.jobs.JobColumnSorter;
/**
 * It provides column sorter for a table viewer for OUTPUT job queue.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class OutputColumnSorter extends JobColumnSorter {

    private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Job o1, Job o2) {
		int diff = 0;
		switch(getIndex()){
		case 0: 
			// sorts by jobname
			diff = o1.getName().compareTo(o2.getName());
			break;
		case 1: 
			// sort jcl type
			diff = getComparedType(o1, o2);
			break;
		case 2:
			// sorts by user
			diff = getComparedUser(o1, o2);
			break;
		case 3:
			// environment
			diff = o1.getJcl().getEnvironment().compareTo(o2.getJcl().getEnvironment());
			break;
		case 4:
			// routed
			diff = getComparedRoutingTime(o1, o2);
			break;
		case 5: 
			// sorts by domain
			diff = o1.getJcl().getDomain().compareTo(o2.getJcl().getDomain());
			break;
		case 6: 
			// sorts by affinity
			diff = o1.getJcl().getAffinity().compareTo(o2.getJcl().getAffinity());
			break;
		case 7: 
			// sorts by ended time
			diff = o1.getEndedTime().compareTo(o2.getEndedTime());
			break;
		case 8: 
			// sorts by return code
			diff = o1.getResult().getReturnCode() - o2.getResult().getReturnCode();
			break;
		case 9:
			// sorts by hold
			diff = (o1.getJcl().isHold() ? 1 : 0) - (o2.getJcl().isHold() ? 1 : 0);
			break;
		case 10:
			// sorts by node member
			diff = getComparedMember(o1, o2);
			break;
		default:
			// sorts by jobname
			diff = o1.getName().compareTo(o2.getName());
			break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}

}
