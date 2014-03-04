/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco Cuccato
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


/**
 * Add to SearcherWidget search listener capability
 * @author Marco Cuccato
 */
public abstract class SearcherListenerWidget extends SearcherWidget {

	private SearchListener listener = null;
	
	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param minChar the minimun lenght of the search string
	 * @param initialValue the default search string value
	 * @param preferenceKey preference key, used for history
	 */
	public SearcherListenerWidget(String labelValue, int minChar, String initialValue, String preferenceKey) {
		super(labelValue, minChar, initialValue, preferenceKey);
	}
	
	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param minChar the minimun lenght of the search string
	 * @param initialValue the default search string value
	 */
	public SearcherListenerWidget(String labelValue, int minChar, String initialValue) {
		super(labelValue, minChar, initialValue);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param minChar the minimun lenght of the search string
	 */
	public SearcherListenerWidget(String labelValue, int minChar) {
		super(labelValue, minChar);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 * @param initialValue the default search string value
	 */
	public SearcherListenerWidget(String labelValue, String initialValue) {
		super(labelValue, initialValue);
	}

	/**
	 * Builds the searcher
	 * @param labelValue the label value, displayed before text imput
	 */
	public SearcherListenerWidget(String labelValue) {
		super(labelValue);
	}

	/**
	 * @return the listener
	 */
	public SearchListener getSearchListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setSearchListener(SearchListener listener) {
		this.listener = listener;
	}

	/**
	 * @param textToSearch filter for job name
	 */
	@Override
	public void onSearch(String textToSearch){
		if (listener != null){
			listener.search(textToSearch);
		}
	}

}