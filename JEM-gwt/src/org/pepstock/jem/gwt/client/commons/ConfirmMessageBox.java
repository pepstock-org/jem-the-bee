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

/**
 * A yes/no message box, to confirm actions
 * @author Andrea "Stock" Stocchero
 */
public class ConfirmMessageBox extends MessageBox {

	
	/**
	 * Constructs object using the title and description to show 
	 * @param title title of message to show
	 * @param description description of message to show
	 */
	public ConfirmMessageBox(String title, String description) {
		super(Images.INSTANCE.question(), title, description);
	}

	/**
	 * Returns a list of buttons to add in the messgae box. Buttons are YES and NO.
	 * 
	 * @see MessageBox#getPreferredButtons()
	 */
	@Override
	public PreferredButton[] getPreferredButtons() {
		return new PreferredButton[] { PreferredButton.YES, PreferredButton.NO };
	}

	/**
	 * Returns the default button. It is NO!
	 * @see MessageBox#getDefaultButton()
	 */
	@Override
	public PreferredButton getDefaultButton() {
		return PreferredButton.NO;
	}
	
}