/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.commons;

import org.pepstock.jem.gwt.client.events.FilterEvent;
import org.pepstock.jem.gwt.client.events.FilterEventHandler;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.ParseException;

/**
 * Add {@link FilterEvent} handling capability to {@link SearcherListenerWidget}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public abstract class SearcherFilterableListenerWidget extends SearcherListenerWidget implements FilterEventHandler {

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param minChar the minimun lenght of the search string
	 * @param initialValue the default search string value
	 * @param preferenceKey preference key, used for history
	 */
	public SearcherFilterableListenerWidget(String labelValue, int minChar, String initialValue, String preferenceKey) {
		super(labelValue, minChar, initialValue, preferenceKey);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param minChar the minimun lenght of the search string
	 * @param initialValue the default search string value
	 */
	public SearcherFilterableListenerWidget(String labelValue, int minChar, String initialValue) {
		super(labelValue, minChar, initialValue);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param minChar the minimun lenght of the search string
	 */
	public SearcherFilterableListenerWidget(String labelValue, int minChar) {
		super(labelValue, minChar);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param initialValue the default search string value
	 */
	public SearcherFilterableListenerWidget(String labelValue,
			String initialValue) {
		super(labelValue, initialValue);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 */
	public SearcherFilterableListenerWidget(String labelValue) {
		super(labelValue);
	}

	/**
	 * Handle {@link FilterEvent} and adjust the search string according
	 */
	@Override
	public void onFilter(FilterEvent event) {
		if (UITools.isInForegroundVisible(this)) {
			Filter filter;
			try {
				filter = Filter.parse(getSearchText().trim());
			} catch (ParseException pe) {
				filter = new Filter();
			}
			filter.add(event.getFilterToken());
			
			setSearchText(filter.toSearchString().trim());
			setEnabled(true);
		}
	}

}