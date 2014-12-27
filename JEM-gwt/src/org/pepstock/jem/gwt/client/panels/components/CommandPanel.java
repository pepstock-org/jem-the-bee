/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.SearcherListenerWidget;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Component which contains searcher and the actions 
 * 
 * @author Marco "Fuzzo" Cuccato
 * @param <T> 
 *
 */
public class CommandPanel<T> extends HorizontalPanel {

	private SearcherListenerWidget searcher = null;
	private AbstractActionsButtonPanel<T> actions = null;
	
	/**
	 * 
	 */
	public static final int DEFAULT_PERCENT_SEARCHER_WIDTH = 40;
	
	private int percentSearcherWidth = -1;
	
	/**
	 * Builds the command panel, composed by a {@link SearcherListenerWidget} and by a {@link AbstractActionsButtonPanel} 
	 * @param searcher the searcher, on left
	 * @param actions the actions, on right
	 */
	public CommandPanel(SearcherListenerWidget searcher, AbstractActionsButtonPanel<T> actions) {
		this(searcher, actions, DEFAULT_PERCENT_SEARCHER_WIDTH);
	}
	
	/**
	 * @param searcher
	 * @param actions
	 * @param percentSearcherWidth the with of searcher, in percent
	 */
	public CommandPanel(SearcherListenerWidget searcher, AbstractActionsButtonPanel<T> actions, int percentSearcherWidth) {
		setWidth(Sizes.HUNDRED_PERCENT);
		this.percentSearcherWidth = percentSearcherWidth;
		setSearcher(searcher);
		setActions(actions);
	}

	/**
	 * @return
	 */
	public SearcherListenerWidget getSearcher() {
		return searcher;
	}

	protected final void setSearcher(SearcherListenerWidget searcher) {
		if (searcher != null) {
			this.searcher = searcher;
			add(searcher);
			setCellHorizontalAlignment(searcher, HasHorizontalAlignment.ALIGN_LEFT);
			if (percentSearcherWidth > -1) {
				setCellWidth(searcher, this.percentSearcherWidth + "%");
			}
		}
	}

	/**
	 * @return
	 */
	public AbstractActionsButtonPanel<T> getActions() {
		return actions;
	}

	protected final void setActions(AbstractActionsButtonPanel<T> actions) {
		if (actions != null) {
			this.actions = actions;
			this.actions.setSearcher(this.searcher);
			add(actions);
			setCellHorizontalAlignment(actions, HasHorizontalAlignment.ALIGN_RIGHT);
			setCellWidth(actions, (100 - this.percentSearcherWidth) + "%");
		}
	}
	
}