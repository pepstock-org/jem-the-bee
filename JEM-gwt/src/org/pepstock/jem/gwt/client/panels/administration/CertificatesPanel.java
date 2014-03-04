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
package org.pepstock.jem.gwt.client.panels.administration;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.administration.certificates.CertificatesTableContainerPanel;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main panel of certificate manager. 
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class CertificatesPanel extends VerticalPanel implements ResizeCapable {
	
	private CertificatesTableContainerPanel tableContainer = null;

	/**
	 * Constructs all UI 
	 */
	public CertificatesPanel() {
		setSpacing(4);
		tableContainer = new CertificatesTableContainerPanel();
		add(tableContainer);
	}
    
	@Override
	public void onResize(int availableWidth, int availableHeight) {
		// spacing on all parts around table container
		int w = availableWidth - getSpacing() * 2;
		int h = availableHeight - getSpacing() * 2;
		tableContainer.setSize(Sizes.toString(w), Sizes.toString(h));
		tableContainer.onResize(w, h);
	}
}