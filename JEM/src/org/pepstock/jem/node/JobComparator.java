/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.Comparator;

import org.pepstock.jem.Job;

/**
 * Is used to sort the list of jobs from input queue, so you can start with jobs
 * with highest priority
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobComparator implements Comparator<Job>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a comparator and does nothing
	 */
	public JobComparator() {
	}

	/**
	 * Compares the object with job. Used to sort, returns 1 if priority of
	 * object parameter is less then this job priority, -1 if viceversa. If
	 * priorities are equals, compares started time and return result of
	 * "compareTo" of Date object. If object parameter is not a job, return 0
	 * 
	 * @see org.pepstock.jem.Job#compareTo(Object)
	 * @param job0 the first object to be compared.
	 * @param job1 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 */
	@Override
	public int compare(Job job0, Job job1) {
		return job0.compareTo(job1);
	}

}