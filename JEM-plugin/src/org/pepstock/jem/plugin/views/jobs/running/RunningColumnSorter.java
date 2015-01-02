/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs.running;


import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.views.jobs.JobColumnSorter;

/**
 * It provides column sorter for a table viewer for RUNNING job queue.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class RunningColumnSorter extends JobColumnSorter {

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
			// step
			break;
		case 4: 
			// sorts by domain
			diff = o1.getJcl().getDomain().compareTo(o2.getJcl().getDomain());
			break;
		case 5: 
			// sorts by affinity
			diff = o1.getJcl().getAffinity().compareTo(o2.getJcl().getAffinity());
			break;
		case 6: 
			// sorts by started time (inverted because running time is displayed instead of start time)
			diff = o2.getStartedTime().compareTo(o1.getStartedTime());
			break;
		case 7: 
			// sorts by node 
			diff = o1.getJcl().getMemory() - o2.getJcl().getMemory();
			break;
		case 8: 
			// sorts by node 
			diff = o1.getMemberLabel().compareTo(o2.getMemberLabel());
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
