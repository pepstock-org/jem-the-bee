/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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

import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Common panel used to insert a string as a filter to search necessary data.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class SimpleFilter extends HorizontalPanel {

	private static final String DEFAULT_INITIAL_FILTER_VALUE = "";

	private final TextBox textBox = new TextBox();

	/**
	 * Constructs the panel, using label passed as argument. Uses the initial default value "*" as filter string.
	 * 
	 * @param labelValue label for text field
	 */
	public SimpleFilter(String labelValue) {
		this(labelValue, DEFAULT_INITIAL_FILTER_VALUE);
	}

	/**
	 * Constructs the panel, using label passed as argument and minimum number of characters to allow the submit and
	 * the initial value for text field
	 * @param labelValue label for text field
	 * @param minChar minimum number of characters
	 * @param initialValue initial value
	 */
	public SimpleFilter(String labelValue, String initialValue) {
		// sets fixed height
		setSize(Sizes.HUNDRED_PERCENT, Sizes.toString(Sizes.SEARCHER_WIDGET_HEIGHT));
		setSpacing(6);

		HorizontalPanel subcomponent = new HorizontalPanel();
		subcomponent.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		subcomponent.setSpacing(4);

		Label label = new Label(labelValue);

		if (initialValue != null) {
			textBox.setText(initialValue);
		}
		
		textBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				onSearch(textBox.getText());
			}
		});

		subcomponent.add(label);
		subcomponent.add(textBox);
		add(subcomponent);

		setCellHorizontalAlignment(subcomponent, HasHorizontalAlignment.ALIGN_LEFT);
	}

	/**
	 * Calls this method every time you click on button "search"
	 * @param textToSearch
	 */
	public abstract void onSearch(String textToSearch);

	/**
	 * Performs a new search, calling the listener with the data inserted
	 */
	public void refresh() {
		onSearch(textBox.getText());
	}

}