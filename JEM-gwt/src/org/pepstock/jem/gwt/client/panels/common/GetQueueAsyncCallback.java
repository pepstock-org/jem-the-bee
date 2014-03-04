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
