/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels.jobs.running;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.ConfirmMessageBox;
import org.pepstock.jem.gwt.client.commons.HideHandler;
import org.pepstock.jem.gwt.client.commons.PreferredButton;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.panels.jobs.commons.AbstractJobsActions;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.view.client.MultiSelectionModel;
/**
 * Component with buttons to perform actions on selected jobs in execution.
 * @author Andrea "Stock" Stocchero
 *
 */
public class RunningActions extends AbstractJobsActions {

	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 *  Constructs all UI, adding buttons
	 */
	public RunningActions() {
		init();
	}

	@Override
	protected void initButtons() {
		addCancelButton();
		addForceButton();
	}

	private void addCancelButton() {
		// checks if user has the permission to CANCEL job
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_CANCEL)){
			Button cancelButton = new Button("Cancel", new CancelButtonClickHandler());
			add(cancelButton);
			cancelButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(cancelButton, "Stops the jobs");
		}
	}

	private class CancelButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			final MultiSelectionModel<Job> selectionModel = (MultiSelectionModel<Job>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				// do nothing
				new Toast(MessageLevel.WARNING, "No job is selected and it's not possible to perform CANCEL command.", "No job selected!").show();
				return;
			}
			
			ConfirmMessageBox cd = new ConfirmMessageBox("Confirm CANCEL", "Are you sure you want to cancel the selected jobs?");
	        cd.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton button) {
			        if (button.getAction() == PreferredButton.YES_ACTION){
						// do!
						cancel(selectionModel.getSelectedSet(), false);
						// clear selection
						selectionModel.clear();
			        }
				}
			});
			cd.open();
		}
	}
	
	private void addForceButton() {
		// checks if user has the permission to CANCEL job
		if (ClientPermissions.isAuthorized(Permissions.JOBS, Permissions.JOBS_KILL)){
			Button forceButton = new Button("Kill", new ForceButtonClickHandler());
			add(forceButton);
			forceButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(forceButton, "Force kill the jobs");
		}
	}

	private class ForceButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			final MultiSelectionModel<Job> selectionModel = (MultiSelectionModel<Job>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				// do nothing
				new Toast(MessageLevel.WARNING, "No job is selected and it's not possible to perform FORCE command.", "No job selected!").show();
				return;
			}
			
			ConfirmMessageBox cd = new ConfirmMessageBox("Confirm FORCE", "Are you sure you want to cancel with FORCE the selected jobs?");
	        cd.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton button) {
			        if (button.getAction() == PreferredButton.YES_ACTION){
						// do!
						cancel(selectionModel.getSelectedSet(), true);
						// clear selection
						selectionModel.clear();
			        }
				}
			});
			cd.open();
		}
	}
	
}