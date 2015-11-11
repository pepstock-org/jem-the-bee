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
package org.pepstock.jem.gwt.client.panels.jobs.output;

import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.SearcherListenerWidget;
import org.pepstock.jem.gwt.client.commons.SharedObjects;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.common.GetQueueAsyncCallback;
import org.pepstock.jem.log.MessageLevel;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class OutputGetJobsAsyncCallback extends GetQueueAsyncCallback<Job> {
	
	private static final int NOTIFYCATION_INTERVAL = 20;

	private boolean hasHistory = false;
	
	private int countOfSearchWithoutHistory = 0;
	
	/**
	 * Build a callback handler for get queues
	 * @param table the table in which the result will be rendered
	 * @param searcher the searcher that begins this search
	 * @param hasHistory searched on history
	 * @param countOfSearchWithoutHistory amount of attempts to search without history
	 */
	public OutputGetJobsAsyncCallback(AbstractTable<Job> table, SearcherListenerWidget searcher, boolean hasHistory, int countOfSearchWithoutHistory) {
		super(table, searcher);
		this.hasHistory = hasHistory;
		this.countOfSearchWithoutHistory = countOfSearchWithoutHistory;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback#onJemSuccess(java.lang.Object)
	 */
	@Override
	public void onJemSuccess(Collection<Job> result) {
		super.onJemSuccess(result);
		if (SharedObjects.hasEviction() && !(this.hasHistory) && (countOfSearchWithoutHistory % NOTIFYCATION_INTERVAL == 1)){
			new Toast(MessageLevel.WARNING, "Be aware that the query has been performed on queue where not all data are available because the queue has been evicted! <br/>To have a consistent result, use the 'history' search.", 
					"Search without history").show();
		}
		if (this.hasHistory){
			countOfSearchWithoutHistory++;
		}
	}

}
