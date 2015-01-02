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
package org.pepstock.jem.gwt.client.panels.status;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class Header extends SimplePanel {

	// common styles
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.stackpanelHeader().ensureInjected();
	}
	
	private HTML headerText = new HTML();
	
	private String initialText = null;
	
	/**
	 * @param initialText 
	 * 
	 */
	public Header(String initialText) {
		
		this.initialText = initialText;
	
		// Add the image and text to a horizontal panel
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setHeight(Sizes.HUNDRED_PERCENT);
		hPanel.setSpacing(0);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		headerText = new HTML(initialText);

		hPanel.add(headerText);
		add(hPanel);
	}
	
	/**
	 * Displays the count
	 * @param count
	 */
	public void setCount(int count){
		if (headerText.getStyleName().contains(Styles.INSTANCE.common().headerWithData())){
			headerText.removeStyleName(Styles.INSTANCE.common().headerWithData());
		}
		if (headerText.getStyleName().contains(Styles.INSTANCE.common().headerWithoutData())){
			headerText.removeStyleName(Styles.INSTANCE.common().headerWithoutData());
		}
		
		headerText.setText(initialText+" ["+count+"]");
		if (count > 0) {
			headerText.addStyleName(Styles.INSTANCE.common().headerWithData());
		} else  {
			headerText.addStyleName(Styles.INSTANCE.common().headerWithoutData());
		}
	}

}