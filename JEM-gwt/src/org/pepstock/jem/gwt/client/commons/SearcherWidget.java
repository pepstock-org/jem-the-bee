/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Common panel used to insert a string as a filter to search necessary data.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class SearcherWidget extends HorizontalPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	/**
	 * Default initial filter
	 */
	public static final String DEFAULT_INITIAL_FILTER_VALUE = "*";

	private final Button search = new Button("Search");
	
	private SearcherHistoryTextBox textBox = new SearcherHistoryTextBox();
	
	private boolean firstSearch = true;
	
	private boolean enabled = true;
	
	private String preferenceKey = null;

	/**
	 * Constructs the panel, using label passed as argument. Uses the initial default value as filter string.
	 * 
	 * @param labelValue label for text field
	 */
	public SearcherWidget(String labelValue) {
		this(labelValue, 0, DEFAULT_INITIAL_FILTER_VALUE);
	}

	/**
	 * Constructs the panel, using label passed as argument and the initial value for text field
	 * @param labelValue label for text field
	 * @param initialValue initial value
	 */
	public SearcherWidget(String labelValue, String initialValue) {
		this(labelValue, 0, initialValue);
	}

	/**
	 * Constructs the panel, using label passed as argument and minimum number of characters to allow the submit
	 * @param labelValue label for text field
	 * @param minChar minimum number of characters
	 */
	public SearcherWidget(String labelValue, final int minChar) {
		this(labelValue, minChar, DEFAULT_INITIAL_FILTER_VALUE);
	}

	/**
	 * Constructs the panel, using label passed as argument and minimum number of characters to allow the submit and
	 * the initial value for text field
	 * @param labelValue label for text field
	 * @param minChar minimum number of characters
	 * @param initialValue initial value
	 */
	public SearcherWidget(String labelValue, final int minChar, String initialValue) {
		this(labelValue, minChar, initialValue, null);
	}
	
	/**
	 * Constructs the panel, using label passed as argument and minimum number of characters to allow the submit and
	 * the initial value for text field
	 * @param labelValue label for text field
	 * @param minChar minimum number of characters
	 * @param initialValue initial value
	 * @param preferenceKey preference Key to extract value from logged user
	 */
	public SearcherWidget(String labelValue, final int minChar, String initialValue, String preferenceKey) {
		this.preferenceKey = preferenceKey;
		// sets fixed height
		setSize(Sizes.HUNDRED_PERCENT, Sizes.toString(Sizes.SEARCHER_WIDGET_HEIGHT));
		addStyleName(Styles.INSTANCE.common().noWrap());
		
		setSpacing(4);
		setHorizontalAlignment(ALIGN_LEFT);
		setVerticalAlignment(ALIGN_MIDDLE);

		Label label = new Label(labelValue);
		
		if (initialValue != null)  {
			textBox.setText(initialValue);
		}

		if (preferenceKey != null){
			textBox.setPreferenceKey(preferenceKey);
			List<String> list = CurrentUser.getInstance().getListPreference(preferenceKey);
			if (!list.isEmpty()){
				textBox.setText(list.get(0));
			}
		} 
		search.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		
		if (textBox.getText().length() <= minChar) {
			search.setEnabled(false);
		} else {
			search.setEnabled(true);
		}

		textBox.setSearchButton(search);
		
		textBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				// button search is enable only if text length is greater
				// than minimum characters set in the constructor
				if (textBox.getText().length() <= minChar) {
					search.setEnabled(false);
				} else {
					search.setEnabled(true);
				}
				textBox.hide();
			}
		});

		// handles the ENTER press
		textBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && search.isEnabled()){
					search.click();
					savePreference();
				}
			}
		});

		search.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				textBox.hide();
				onSearch(textBox.getText());
				savePreference();
			}
		});

		add(label);
		
		add(textBox);
		add(search);
		setCellWidth(textBox, Sizes.HUNDRED_PERCENT);
	}
	
	/**
	 * Saves user preference
	 */
	private void savePreference() {
		String preferenceKey = getPreferenceKey();
		if (preferenceKey != null) {
			List<String> list = CurrentUser.getInstance().getListPreference(preferenceKey);
			if (list.isEmpty()) {
				CurrentUser.getInstance().setListPreference(preferenceKey, list);
			}
			if (!list.contains(textBox.getText())) {
				list.add(0, textBox.getText());
				if (list.size() > PreferencesKeys.DEFAULT_MAXIMUM_PREFERENCES) {
					list.remove(list.size() - 1);
				}
				CurrentUser.getInstance().setLastUpdateTime();
			} else {
				list.remove(textBox.getText());
				list.add(0, textBox.getText());
				CurrentUser.getInstance().setLastUpdateTime();
			}
		}
	}
	
	/**
	 * @return the firstSearch
	 */
	public boolean isFirstSearch() {
		return firstSearch;
	}

	
	/**
	 * @return the preferenceKey
	 */
	public String getPreferenceKey() {
		return preferenceKey;
	}

	/**
	 * @param preferenceKey the preferenceKey to set
	 */
	public void setPreferenceKey(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}

	/**
	 * @param firstSearch the firstSearch to set
	 */
	public void setFirstSearch(boolean firstSearch) {
		this.firstSearch = firstSearch;
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

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		textBox.setReadOnly(!enabled);
		textBox.setEnabled(enabled);
		search.setEnabled(enabled);
	}
	
	/**
	 * Updates the search text to the given value
	 * @param textToSearch the text to search
	 */
	public void setSearchText(String textToSearch) {
		textBox.setText(textToSearch);
	}

	/**
	 * @return the text contained in the input box
	 */
	public String getSearchText() {
		return textBox.getText();
	}
	
	/**
	 * Set focus on input box
	 */
	public void setFocusOnTextBox() {
		textBox.setFocus(true);
	}
	
	/**
	 * Set focus on search button
	 */
	public void setFocusOnButton() {
		search.setFocus(true);
	}
}