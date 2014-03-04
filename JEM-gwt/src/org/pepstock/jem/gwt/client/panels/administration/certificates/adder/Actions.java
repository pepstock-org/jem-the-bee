/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels.administration.certificates.adder;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.administration.certificates.CertificateAdder;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ActionsButtonPanel that user can do after a certificate is adding. At the moment we have 2 actions:<br>
 * <br>
 * <ul>
 * <li><b>Add<b>: to add a certificate</li>
 * <li><b>Cancel<b>: to discard the adding</li>
 * </ul>
 * 
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public final class Actions extends HorizontalPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private CertificateAdder adder = null;

	/**
	 * Creates the UI using certificate adder manager
	 * 
	 * @param adder manager to add new certificate
	 * 
	 */
	public Actions(CertificateAdder adder) {
		setSpacing(Sizes.SPACING);
		this.adder = adder;

		// adds button for adding and its handler
		Button saveButton = new Button("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				// submits forms of adder
				getAdder().submit();
			}
		});
		saveButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		add(saveButton);

		// adds button for cancelling and its handler
		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				getAdder().close();
			}
		});
		add(cancelButton);
	}

	/**
	 * Returns "adder" manager
	 * @return the adder
	 */
	public CertificateAdder getAdder() {
		return adder;
	}

	/**
	 * Sets "adder" manager
	 * @param adder the adder to set
	 */
	public void setAdder(CertificateAdder adder) {
		this.adder = adder;
	}
}