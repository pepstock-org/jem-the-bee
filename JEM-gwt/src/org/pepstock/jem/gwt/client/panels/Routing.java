/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.UpdateListener;
import org.pepstock.jem.gwt.client.panels.common.GetQueueAsyncCallback;
import org.pepstock.jem.gwt.client.panels.components.BasePanel;
import org.pepstock.jem.gwt.client.panels.components.CommandPanel;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspector;
import org.pepstock.jem.gwt.client.panels.jobs.commons.JobsSearcher;
import org.pepstock.jem.gwt.client.panels.jobs.routing.RoutingActions;
import org.pepstock.jem.gwt.client.panels.jobs.routing.RoutingTable;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.Queues;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Main panel of job ROUTING queue manager. Shows the list of jobs in routing with the possibilities to act on them. 
 * Furthermore allows to inspect the job to see JCL and general information.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Routing extends BasePanel<Job> implements SearchListener, UpdateListener<Job> {

	/**
	 * Constructs all UI 
	 */
	public Routing() {
		super(new TableContainer<Job>(new RoutingTable(true)),
				new CommandPanel<Job>(new JobsSearcher(PreferencesKeys.JOB_SEARCH_ROUTING), new RoutingActions()));
		getTableContainer().getUnderlyingTable().setInspectListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see test.client.main.JobsSearchListener#search(java.lang.String)
	 */
	@Override
	public void search(final String jobsFilter) {
		getCommandPanel().getSearcher().setEnabled(false);
		Loading.startProcessing();

	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.getRoutingQueue(jobsFilter, 
						new GetQueueAsyncCallback<Job>(getTableContainer().getUnderlyingTable(), getCommandPanel().getSearcher()));
			}
	    });
	}
	
	/**
	 * @see org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspectListener#inspect(org.pepstock.jem.Job)
	 */
	@Override
	public void inspect(final Job job) {
		getCommandPanel().getSearcher().setEnabled(false);
		Loading.startProcessing();

	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				// asks for JCL
				Services.QUEUES_MANAGER.getJcl(job, Queues.ROUTING_QUEUE, new GetJclAsyncCallback(job));
			}
	    });
	}

	private class GetJclAsyncCallback extends ServiceAsyncCallback<String> {
		
		private final Job job;
		
		public GetJclAsyncCallback(final Job job) {
			this.job = job;
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get JCL error!").show();
		}

		@Override
		public void onJemSuccess(String result) {
			// sets JCL content
			job.getJcl().setContent(result);
			
			// creates the inspector and shows it
			JobInspector inspector = new JobInspector(job);
			inspector.setModal(true);
			inspector.setTitle(job.getName());
			inspector.center();
			
			// adds for closing
			inspector.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> arg0) {
					// ignore
				}
			});
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			getCommandPanel().getSearcher().setEnabled(true);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspectListener#update(org.pepstock.jem.Job)
	 */
    @Override
    public void update(Job object) {
	    // ignore
    }

}