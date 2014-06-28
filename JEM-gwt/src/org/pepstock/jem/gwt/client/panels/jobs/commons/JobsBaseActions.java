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
package org.pepstock.jem.gwt.client.panels.jobs.commons;

import java.util.Collection;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.ConfirmMessageBox;
import org.pepstock.jem.gwt.client.commons.HideHandler;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.PreferredButton;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.panels.jobs.input.LegacySubmitter;
import org.pepstock.jem.gwt.client.panels.jobs.input.MultiDragAndDropSubmitter;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * Component with buttons to perform actions on selected jobs.
 * @author Andrea "Stock" Stocchero
 */
public class JobsBaseActions extends AbstractJobsActions {

	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private String queueName = null;

	/**
	 * Constructs all UI, adding buttons
	 * @param queueName jobs queue name to show 
	 */
	public JobsBaseActions(String queueName) {
		this.queueName = queueName;
		init();
	}
	
	@Override
	protected void initButtons() {
		addPurgeButton();
		addHoldButton();
		addReleaseButton();
		if (queueName != null && queueName.equalsIgnoreCase(Queues.INPUT_QUEUE)) {
			addSubmitButton();
		}
	}

	private void addReleaseButton() {
		// checks if user has the permission to RELEASE job 
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_RELEASE)){
			Button releaseButton = new Button("Release", new Release());
			add(releaseButton);
			releaseButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(releaseButton, "Make the jobs runnable");
		}
	}
	
	private void addHoldButton() {
		// checks if user has the permission to HOLD job 
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_HOLD)){
			Button holdButton = new Button("Hold", new Hold());
			add(holdButton);
			holdButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(holdButton, "Prevents the jobs to run");
		}
	}
	
	private void addPurgeButton() {
		// checks if user has the permission to PURGE job 
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_PURGE)){
			Button purgeButton = new Button("Purge", new Purge());
			add(purgeButton);
			purgeButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(purgeButton, "Delete jobs from list");
		}
	}
	
	private void addSubmitButton() {
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_SUBMIT)){
			// ALL users can submit jobs 
			Button submitButton = new Button("Submit...", new Submit());
			add(submitButton);
			submitButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(submitButton, "Sumbit new jobs");
		}
	}


	/**
	 * @param jobs collection of jobs to hold
	 */
	private void hold(final Collection<Job> jobs) {
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.hold(jobs, queueName, new ActionAsyncCall("Hold"));
			}
	    });

		
	}
	
	/**
	 * @param jobs collections of jobs to release (if they are in hold)
	 */
	private void release(final Collection<Job> jobs) {
		Loading.startProcessing();
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.release(jobs, queueName, new ActionAsyncCall("Release"));
			}
	    });

		
	}
	
	/**
	 * @param jobs collections of jobs to purge
	 */
	private void purge(final Collection<Job> jobs) {
		Loading.startProcessing();
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.purge(jobs, queueName, new ActionAsyncCall("Purge"));
			}
	    });
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class Release implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			MultiSelectionModel<Job> selectionModel = (MultiSelectionModel<Job>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No job is selected and it's not possible to perform RELEASE command.", "No job selected!").show();
				return;
			}
			// do!
			release(selectionModel.getSelectedSet());
			// clear selection
			selectionModel.clear();
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class Hold implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			MultiSelectionModel<Job> selectionModel = (MultiSelectionModel<Job>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No job is selected and it's not possible to perform HOLD command.", "No job selected!").show();
				return;
			}
			// do!
			hold(selectionModel.getSelectedSet());
			// clear selection
			selectionModel.clear();
		}
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class Purge implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			final MultiSelectionModel<Job> selectionModel = (MultiSelectionModel<Job>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No job is selected and it's not possible to perform PURGE command.", "No job selected!").show();
				return;
			}
			
			ConfirmMessageBox cd = new ConfirmMessageBox("Confirm PURGE", "Are you sure you want to purge the selected jobs?");
	        cd.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton button) {
			        if (button.getAction() == PreferredButton.YES_ACTION){
						// do!
						purge(selectionModel.getSelectedSet());
						// clear selection
						selectionModel.clear();
			        }
				}
			});
			cd.open();
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	static class Submit implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// if client browser supports it, propose the multi-file drag&drop uploader, otherwhise the legacy one
			if (Uploader.isAjaxUploadWithProgressEventsSupported()) {
				openSubmitter(false);
			} else {
				openSubmitter(true);
			}
		}
	}

	protected static void openSubmitter(boolean legacy) {
		PopupPanel submitter;
		if (legacy) {
			submitter = new LegacySubmitter();
			((LegacySubmitter)submitter).setWidth(600);
			((LegacySubmitter)submitter).setHeight(240);
		} else {
			submitter = new MultiDragAndDropSubmitter();
			((MultiDragAndDropSubmitter)submitter).setWidth(700);
			((MultiDragAndDropSubmitter)submitter).setHeight(340);
		}
		// be carefully about the HEIGHT and WIDTH calculation
		submitter.setModal(true);
		submitter.center();
	}


	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class ActionAsyncCall extends ServiceAsyncCallback<Boolean> {
		
		private String action = null;
		
		/**
		 * @param action
		 */
        private ActionAsyncCall(String action) {
	        super();
	        this.action = action;
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
			new Toast(MessageLevel.ERROR, caught.getMessage(), action+" command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
}