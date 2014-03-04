/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Cuc" Cuccato
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

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.HasAbstractTable;
import org.pepstock.jem.gwt.client.commons.SearcherWidget;
import org.pepstock.jem.gwt.client.panels.jobs.running.HasSearcher;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Marco "Cuc" Cuccato
 *
 * @param <T>
 */
public abstract class AbstractActionsButtonPanel<T> extends HorizontalPanel implements HasAbstractTable<T>, HasSearcher {

	/**
	 * 
	 */
	public static final int SPACING_DEFAULT = 4;

	private AbstractTable<T> table = null;
	private SearcherWidget searcher = null;

	/**
	 * 
	 */
	public AbstractActionsButtonPanel() {
		setHorizontalAlignment(ALIGN_LEFT);
		setSpacing(SPACING_DEFAULT);
	}
	
	/**
	 * 
	 */
	public void init(){
		initButtons();
		adjustVisibility();
	}
	
	/**
	 * This is called by contructor
	 * @param buttons
	 */
	protected abstract void initButtons();
	
	protected void adjustVisibility() {
		if (getWidgetCount() < 1) {
			setVisible(false);
		}
	}
	
	@Override
	public AbstractTable<T> getUnderlyingTable() {
		return table;
	}

	@Override
	public void setUnderlyingTable(AbstractTable<T> table)
			throws UnsupportedOperationException {
		this.table = table;
	}

	/**
	 * @return the searcher
	 */
	@Override
	public SearcherWidget getSearcher() {
		return searcher;
	}

	/**
	 * @param searcher the searcher to set
	 */
	@Override
	public void setSearcher(SearcherWidget searcher) {
		this.searcher = searcher;
	}

}