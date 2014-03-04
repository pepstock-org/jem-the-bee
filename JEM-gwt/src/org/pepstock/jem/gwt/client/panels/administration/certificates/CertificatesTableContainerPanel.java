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

import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.events.FilterEvent;
import org.pepstock.jem.gwt.client.events.FilterEventHandler;
import org.pepstock.jem.gwt.client.panels.components.BasePanel;
import org.pepstock.jem.gwt.client.panels.components.CommandPanel;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.CertificateEntry;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Main panel of certificates manager.<br>Shows the list of uploaded certificates. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class CertificatesTableContainerPanel extends BasePanel<CertificateEntry> implements SearchListener {
	
	/**
	 * Constructs all UI 
	 */
	public CertificatesTableContainerPanel() {
		super(new TableContainer<CertificateEntry>(new CertificateEntriesTable()),
			new CommandPanel<CertificateEntry>(new CertificateEntrySearcher(), new CertificateEntryActions()));

		
		// sets itself as listener for searching
		getCommandPanel().getSearcher().setSearchListener(this);
		getCommandPanel().getActions().setUnderlyingTable(getTableContainer().getUnderlyingTable());
		
		// subscribe the basic filter event handler to eventbus
		EventBus.INSTANCE.addHandler(FilterEvent.TYPE, (FilterEventHandler)getCommandPanel().getSearcher());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see test.client.main.JobsSearchListener#search(java.lang.String)
	 */
	@Override
	public void search(final String filter) {
		// check permission to read certificates
		if (ClientPermissions.isAuthorized(Permissions.CERTIFICATES, Permissions.CERTIFICATES_READ)) {
			getCommandPanel().getSearcher().setEnabled(false);
			Loading.startProcessing();
		    Scheduler scheduler = Scheduler.get();
		    scheduler.scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					Services.CERTIFICATES_MANAGER.getCertificates(filter, new GetCertificatesAsyncCallback());
				}
		    });
		}
	}
	
	private class GetCertificatesAsyncCallback extends ServiceAsyncCallback<Collection<CertificateEntry>> {

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Search error!").show();
			getCommandPanel().getSearcher().setFirstSearch(true);
		}

		@Override
		public void onJemSuccess(Collection<CertificateEntry> result) {
			// sets data to table to show it
			getTableContainer().getUnderlyingTable().setRowData(result);
			getCommandPanel().getSearcher().setFirstSearch(false);
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			getCommandPanel().getSearcher().setEnabled(true);
        }
		
	}
	
}