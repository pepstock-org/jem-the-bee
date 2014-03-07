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

import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.JobStatus;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.components.AbstractActionsButtonPanel;
import org.pepstock.jem.gwt.client.panels.components.CommandPanel;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspector;
import org.pepstock.jem.gwt.client.panels.jobs.input.InputTable;
import org.pepstock.jem.gwt.client.panels.jobs.output.OutputTable;
import org.pepstock.jem.gwt.client.panels.jobs.routing.RoutingTable;
import org.pepstock.jem.gwt.client.panels.jobs.running.RunningTable;
import org.pepstock.jem.gwt.client.panels.status.Header;
import org.pepstock.jem.gwt.client.panels.status.JobsSearcher;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.Queues;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main panel of job queue manager. Shows the list of jobs with the possibilities to act on them. 
 * Furthermore allows to inspect the job to see JCL, general information and complete output.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Status extends VerticalPanel implements ResizeCapable,  SearchListener{
	
	private CommandPanel<Job> commandPanel = null;
	
    // Create a new stack layout panel.
    private StackLayoutPanel stackPanel = new StackLayoutPanel(Unit.PX);
    
    private TableContainer<Job> input = new TableContainer<Job>(new InputTable(false));
    
    private TableContainer<Job> running = new TableContainer<Job>(new RunningTable(false));
    
    private TableContainer<Job> output = new TableContainer<Job>(new OutputTable(false));
    
    private TableContainer<Job> routing = new TableContainer<Job>(new RoutingTable(false));
	
	/**
	 * Constructs all UI 
	 */
	public Status() {
		
		input.getUnderlyingTable().setInspectListener(new InspectListener<Job>() {

			@Override
            public void inspect(Job job) {
				Status.this.inspect(job, Queues.INPUT_QUEUE, false);
            }
		});
		running.getUnderlyingTable().setInspectListener(new InspectListener<Job>() {

			@Override
            public void inspect(Job job) {
				Status.this.inspect(job, Queues.RUNNING_QUEUE, true);
            }
		});

		output.getUnderlyingTable().setInspectListener(new InspectListener<Job>() {

			@Override
            public void inspect(Job job) {
	            Status.this.inspect(job, Queues.OUTPUT_QUEUE, true);
            }
		});

		routing.getUnderlyingTable().setInspectListener(new InspectListener<Job>() {

			@Override
            public void inspect(Job job) {
				Status.this.inspect(job, Queues.ROUTING_QUEUE, false);
            }
		});

		
		
		commandPanel = new CommandPanel<Job>(new JobsSearcher(), new AbstractActionsButtonPanel<Job>() {
			@Override
			protected void initButtons() {
				// do nothing
			}
		}, 50);
		// sets listeners
		commandPanel.getSearcher().setSearchListener(this);
		
		stackPanel.add(input, createHeaderWidget("Input"), Sizes.STATUS_PANEL_HEADER_PX);
		stackPanel.add(running, createHeaderWidget("Running"), Sizes.STATUS_PANEL_HEADER_PX);
		stackPanel.add(output, createHeaderWidget("Output"), Sizes.STATUS_PANEL_HEADER_PX);
		stackPanel.add(routing, createHeaderWidget("Routing"), Sizes.STATUS_PANEL_HEADER_PX);
		
		add(commandPanel);
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
	}

	private Widget createHeaderWidget(String text) {
		return new Header(text);
	}

	
	/**
	 * @see test.client.main.JobsSearchListener#search(java.lang.String)
	 */
	@Override
	public void search(final String jobsFilter) {
		if (getWidgetCount() == 1) {
			add(stackPanel);
		}
		
		commandPanel.getSearcher().setEnabled(false);
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.getJobStatus(jobsFilter, new GetJobStatusAsyncCallback());
			}
		});
	}

	private class GetJobStatusAsyncCallback extends ServiceAsyncCallback<JobStatus> {
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get JobStatus error!").show();
			commandPanel.getSearcher().setFirstSearch(true);
		}

		@Override
		public void onJemSuccess(JobStatus result) {
			// sets data to table to show it
			commandPanel.getSearcher().setFirstSearch(false);
			
			int inputIX = changeHeader(result.getJobsInput(), 0);
			int runningIX = changeHeader(result.getJobsRunning(), 1);
			int outputIX = changeHeader(result.getJobsOutput(), 2);
			int routingIX = changeHeader(result.getJobsRouting(), 3);
			
			input.getUnderlyingTable().setRowData(result.getJobsInput());
			running.getUnderlyingTable().setRowData(result.getJobsRunning());
			output.getUnderlyingTable().setRowData(result.getJobsOutput());
			routing.getUnderlyingTable().setRowData(result.getJobsRouting());
			
			int showIndex = Math.min(Math.min(Math.min(inputIX, runningIX), outputIX), routingIX);
			stackPanel.showWidget((showIndex == Integer.MAX_VALUE) ? 0 : showIndex);			
			
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			commandPanel.getSearcher().setEnabled(true);
        }
	}
	
	/**
	 * Inspect in the job
	 * @param job job instance
	 * @param queueName queue name where to search for job
	 * @param getOuput if to get output or only JCL
	 * @see org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspectListener#inspect(org.pepstock.jem.Job)
	 */
	public final void inspect(final Job job, final String queueName, final boolean getOuput) {
		commandPanel.getSearcher().setEnabled(false);
		Loading.startProcessing();

		Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				// asks for JCL
				if (getOuput) {
					Services.QUEUES_MANAGER.getOutputTree(job, queueName, new GetOutputTreeAsyncCallback(job));
				} else {
					// asks for JCL
					Services.QUEUES_MANAGER.getJcl(job, queueName, new GetJclAsyncCallback(job));
				}
			
			}
	    });

	}

	private class GetOutputTreeAsyncCallback extends ServiceAsyncCallback<OutputTree> {

		private final Job job;
		
		public GetOutputTreeAsyncCallback(final Job job) {
			this.job = job;
		}
		

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get JCL error!").show();
		}

		@Override
		public void onJemSuccess(OutputTree result) {
			// sets JCL content
			job.getJcl().setContent(result.getJclContent());

			// creates the inspector and shows it
			JobInspector inspector = new JobInspector(job, result);
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
			commandPanel.getSearcher().setEnabled(true);
        }
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
			commandPanel.getSearcher().setEnabled(true);
        }
	}
	
	private int changeHeader(Collection<Job> collection, int index){
		Header header = (Header)stackPanel.getHeaderWidget(index);
		if (collection != null){
			header.setCount(collection.size());
			if (!collection.isEmpty()){
				return index;
			}
		} else { 
			header.setCount(0);
		}
		return Integer.MAX_VALUE;
	}
	
	@Override
	public void onResize(int availableWidth, int availableHeight) {
		int height = availableHeight - Sizes.SEARCHER_WIDGET_HEIGHT;
		stackPanel.setPixelSize(availableWidth, height);
	}
}