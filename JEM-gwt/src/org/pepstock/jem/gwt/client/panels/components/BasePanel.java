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
package org.pepstock.jem.gwt.client.panels.components;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.SearcherListenerWidget;
import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.events.FilterEvent;
import org.pepstock.jem.gwt.client.events.FilterEventHandler;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Base Panel 
 * @author Marco "Fuzzo" Cuccato
 * @param <T> 
 */
public abstract class BasePanel<T> extends VerticalPanel implements ResizeCapable, SearchListener {
	
	private TableContainer<T> tableContainer = null;
	private CommandPanel<T> commandPanel = null;
	
	/**
	 * @param tableContainer
	 * @param commandPanel
	 */
	public BasePanel(TableContainer<T> tableContainer, CommandPanel<T> commandPanel) {
		this.tableContainer = tableContainer;
		this.commandPanel = commandPanel;
		add(commandPanel);
		add(tableContainer);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		// sets listeners
		getCommandPanel().getSearcher().setSearchListener(this);
		getCommandPanel().getActions().setUnderlyingTable(getTableContainer().getUnderlyingTable());

		// subscribe the basic filter event handler to eventbus
		EventBus.INSTANCE.addHandler(FilterEvent.TYPE, (FilterEventHandler)getCommandPanel().getSearcher());
	}

	/**
	 * @return
	 */
	public TableContainer<T> getTableContainer() {
		return tableContainer;
	}

	/**
	 * @return
	 */
	public CommandPanel<T> getCommandPanel() {
		return commandPanel;
	}
	
	/**
	 * Called when you select a different tab panel
	 */
	public void search(){
		SearcherListenerWidget searcher = getCommandPanel().getSearcher();
		if (searcher.isFirstSearch()){
			return;
		}
		searcher.refresh();
	}

	@Override
	public void onResize(int availableWidth, int availableHeight) {
		int height = availableHeight - Sizes.SEARCHER_WIDGET_HEIGHT;
		tableContainer.onResize(availableWidth, height);
	}
	
}