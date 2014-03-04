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
package org.pepstock.jem.gwt.client.panels.administration.certificates;

import java.util.Collection;

import org.pepstock.jem.gwt.client.commons.ConfirmMessageBox;
import org.pepstock.jem.gwt.client.commons.HideHandler;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.PreferredButton;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.panels.components.AbstractActionsButtonPanel;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.CertificateEntry;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * Component with buttons to perform actions on selected certificates.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CertificateEntryActions extends AbstractActionsButtonPanel<CertificateEntry> {
	
	/**
	 * Initializes buttons for actions
	 */
    public CertificateEntryActions() {
	    super();
	    init();
    }

	@Override
	protected void initButtons() {
		addAddButton();
		addRemoveButton();
	}

	/**
	 * Adds "ADD" buttonh
	 */
	private void addAddButton() {
		// checks if there is the authorization for that
		if (ClientPermissions.isAuthorized(Permissions.CERTIFICATES, Permissions.CERTIFICATES_CREATE)){
			Button addButton = new Button("Add", new AddButtonClickHandler());
			add(addButton);
			addButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(addButton, "Add a new certificate");
		}
	}

	private class AddButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// clears any selections before show popup for adding
			@SuppressWarnings("unchecked")
            MultiSelectionModel<CertificateEntry> selectionModel = (MultiSelectionModel<CertificateEntry>) getUnderlyingTable().getTable().getSelectionModel();
			selectionModel.clear();
			// do!
			// creates the inspector and shows it
			CertificateAdder inspector = new CertificateAdder();
			// be carefully about the HEIGHT and WIDTH 
			// they are FIXED in this case
			inspector.setWidth("600px");
			inspector.setHeight("240px");
			inspector.setModal(true);
			inspector.setTitle("Add a new certificate");
			inspector.center();

			// adds for closing
			// refreshes the list of certificates
			inspector.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> arg0) {
					if (getSearcher() != null) {
						getSearcher().refresh();
					}
				}
			});
		}
	}
	
	
	/**
	 * add "REMOVE" button
	 */
	private void addRemoveButton() {
		// checks if there is the authorization for that
		if (ClientPermissions.isAuthorized(Permissions.CERTIFICATES, Permissions.CERTIFICATES_DELETE)) {
			Button removeButton = new Button("Remove", new RemoveButtonClickHandler());
			add(removeButton);
			removeButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(removeButton, "Delete certificates");
		}
	}

	private class RemoveButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// checks if any certificates is selected
			// if no selection, send a warning
			@SuppressWarnings("unchecked")
			final MultiSelectionModel<CertificateEntry> selectionModel = (MultiSelectionModel<CertificateEntry>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No certificate is selected and it's not possible to perform REMOVE command.", "No Certificate selected!").show();
				return;
			}
	
			// asks for confirmation before remove a certificate
			ConfirmMessageBox cd = new ConfirmMessageBox("Confirm REMOVE", "Are you sure you want to remove the selected certificates?");
	        cd.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton button) {
			        if (button.getAction() == PreferredButton.YES_ACTION){
						// do!
						remove(selectionModel.getSelectedSet());
						// clear selection
						selectionModel.clear();
			        }
				}
			});
			cd.open();
		}
	}
	
	/**
	 * @param entries collection of certificates to remove
	 */
	private void remove(final Collection<CertificateEntry> entries) {
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.CERTIFICATES_MANAGER.removeCertificates(entries, new RemoveCertificatesAsyncCallback());
			}
	    });
	}
	
	private class RemoveCertificatesAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// if has success, refresh the data, to do not see in table that they are removed
			if (getSearcher() != null) {
				getSearcher().refresh();
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Remove certificates command error!").show();
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
}