/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Cuc" Cuccato
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
package org.pepstock.jem.gwt.client.panels.jobs.commons;

import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.components.AbstractActionsButtonPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author Marco "Cuc" Cuccato
 *
 */
public abstract class AbstractJobsActions extends AbstractActionsButtonPanel<Job>  {
	
	/**
	 * @param jobs collections of jobs to cancel
	 * @param force if <code>true</code>, uses cancel with force parameter
	 */
	public void cancel(final Collection<Job> jobs, final boolean force){
		Loading.startProcessing();
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.cancel(jobs, force, new CancelAsyncCallback(force));
			}
	    });

	}
	
	private class CancelAsyncCallback extends ServiceAsyncCallback<Boolean> {
		
		private final boolean force;
		
		public CancelAsyncCallback(final boolean force) {
			this.force = force;
		}
		
		@Override
		public void onJemSuccess(Boolean result) {
			// if has success, refresh the data, to see in table that they are not in hold
			if (getSearcher() != null) {
				getSearcher().refresh();
			}
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), (force ? "Cancel" : "Force") + " command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
}