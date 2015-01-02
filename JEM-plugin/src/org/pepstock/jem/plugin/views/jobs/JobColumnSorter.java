/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs;

import java.util.Date;

import org.pepstock.jem.Job;
import org.pepstock.jem.plugin.commons.JemColumnSorter;

/**
 * It provider column sorter for a table viewer for jobs.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class JobColumnSorter extends JemColumnSorter<Job> {

	private static final long serialVersionUID = 1L;

	/**
	 * Compares the JCL type of jobs
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
	 */
	public final int getComparedType(Job o1, Job o2) {
		// sorts by JCL type
		String label11 = (o1.getJcl().getType() == null) ? "" : o1.getJcl().getType();
		String label22 = (o2.getJcl().getType() == null) ? "" : o2.getJcl().getType();
		return label11.compareTo(label22);
	}

	/**
	 * Compares the users of jobs
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
     */
	public final int getComparedUser(Job o1, Job o2) {
		// sorts by user
		String userO1 = (o1.isUserSurrogated()) ? o1.getJcl().getUser() : o1.getUser();
		String userO2 = (o2.isUserSurrogated()) ? o2.getJcl().getUser() : o2.getUser();
		return userO1.compareTo(userO2);
	}

	/**
	 * Compares the routing status of jobs
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
     */
	public final int getComparedRoutingStatus(Job o1, Job o2) {
		int diff = 0;
		// route status
		Boolean committed1 = o1.getRoutingInfo().isRoutingCommitted();
		Boolean committed2 = o2.getRoutingInfo().isRoutingCommitted();
		// could be true, false or NULL
		if (committed1 != null && committed2 != null) {
			diff = committed1.compareTo(committed2);
		} else if (committed1 != null && committed2 == null) {
			diff = 1;
		} else if (committed1 == null && committed2 != null) {
			diff = -1;
		} else {
			diff = 0;
		}
		return diff;
	}

	/**
	 * Compares the rotuing time of jobs
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
     */
	public final int getComparedRoutingTime(Job o1, Job o2) {
		int diff = 0;
		// routed
		Date routedTime1 = o1.getRoutingInfo().getRoutedTime();
		Date routedTime2 = o2.getRoutingInfo().getRoutedTime();
		Boolean routed1 = routedTime1 != null;
		Boolean routed2 = routedTime2 != null;
		if (routed1 && routed2) {
			diff = routedTime1.compareTo(routedTime2);
		} else if (routed1 && !routed2) {
			diff = 1;
		} else if (!routed1 && routed2) {
			diff = -1;
		} else {
			diff = 0;
		}
		return diff;
	}

	/**
	 * Compares the member label of jobs
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
     */
	public final int getComparedMember(Job o1, Job o2) {
		// sorts by node member
		String label1 = (o1.getMemberLabel() == null) ? "" : o1.getMemberLabel();
		String label2 = (o2.getMemberLabel() == null) ? "" : o2.getMemberLabel();
		return label1.compareTo(label2);
	}
}