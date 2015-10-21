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
package org.pepstock.jem.gwt.client.panels.jobs.routing;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.panels.jobs.commons.JobFieldsComparator;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.util.ColumnIndex;

/**
 *  Is the column comparator to sort cell table for table with job in routing queue
 * @author Andrea "Stock" Stocchero
 *
 */
@SuppressWarnings("serial")
public class RoutingJobComparator extends IndexedColumnComparator<Job> {

	/**
	 * Constructs the comparator, saving the index of column, chosen for sorting
	 * @param index index of column, chosen for sorting
	 */
	public RoutingJobComparator(int index) {
		super(index, PreferencesKeys.JOB_SORT_ROUTING);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Job o1, Job o2) {
		int diff = 0;
		switch(getIndex()){
			case ColumnIndex.COLUMN_2: 
				// sorts by jobname
				diff = o1.getName().compareTo(o2.getName());
				break;
			case ColumnIndex.COLUMN_3: 
				// sorts by JCL type
				diff = o1.getJcl().getType().compareTo(o2.getJcl().getType());
				break;
			case ColumnIndex.COLUMN_4:
				// sorts by user
				diff = JobFieldsComparator.sortByUser(o1, o2);
				break;
			case ColumnIndex.COLUMN_5: 
				// sorts by environment
				diff = o1.getJcl().getEnvironment().compareTo(o2.getJcl().getEnvironment());
				break;
			case ColumnIndex.COLUMN_6: 
				// sorts by domain
				diff = o1.getJcl().getDomain().compareTo(o2.getJcl().getDomain());
				break;
			case ColumnIndex.COLUMN_7: 
				// sorts by affinity
				diff = o1.getJcl().getAffinity().compareTo(o2.getJcl().getAffinity());
				break;
			case ColumnIndex.COLUMN_8: 
				// sorts by submitted time
				diff = o1.getSubmittedTime().compareTo(o2.getSubmittedTime());
				break;
			case ColumnIndex.COLUMN_9:
				// route status
				diff = JobFieldsComparator.sortByRouteStatus(o1, o2);
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
