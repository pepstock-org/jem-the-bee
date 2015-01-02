/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.common;

import java.util.Collection;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearcherListenerWidget;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.log.MessageLevel;

/**
 * Utility callback class used for queue gets
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <T> the type of objects in queue
 */
public class GetQueueAsyncCallback<T> extends ServiceAsyncCallback<Collection<T>> {
	
	private AbstractTable<T> table;
	private SearcherListenerWidget searcher;
	
	/**
	 * Build a callback handler for get queues
	 * @param table the table in which the result will be rendered
	 * @param searcher the searcher that begins this search
	 */
	public GetQueueAsyncCallback(AbstractTable<T> table, SearcherListenerWidget searcher) {
		this.table = table;
		this.searcher = searcher;
	}
	
	@Override
	public void onJemSuccess(Collection<T> result) {
		// sets data to table to show it
		table.setRowData(result);
		searcher.setFirstSearch(false);
	}
	
	@Override
	public void onJemFailure(Throwable caught) {
		// show an error toast
		new Toast(MessageLevel.ERROR, caught.getMessage(), "Search error!").show();
		searcher.setFirstSearch(true);
	}
	
	@Override
	public void onJemExecuted() {
		// hide loading panel
		Loading.stopProcessing();
		searcher.setEnabled(true);
	}

}
