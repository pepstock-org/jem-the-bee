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
package org.pepstock.jem.gwt.client.commons;

/**
 * A reload message box, to confirm action. Occurs when there a Exception connecting app server
 * @author Andrea "Stock" Stocchero
 */
public class ReloadConfirmMessageBox extends MessageBox {

	
	/**
	 * Constructs object using the title and description to show 
	 * @param title title of message to show
	 * @param description description of message to show
	 */
	public ReloadConfirmMessageBox(String title, String description) {
		super(Images.INSTANCE.error(), title, description);
	}

	/**
	 * Returns a list of buttons to add in the message box. Buttons is only one, Reload.
	 * 
	 * @see MessageBox#getPreferredButtons()
	 */
	@Override
	public PreferredButton[] getPreferredButtons() {
		return new PreferredButton[] { PreferredButton.RELOAD };
	}

	/**
	 * Returns the default button. It is RELOAD!
	 * @see MessageBox#getDefaultButton()
	 */
	@Override
	public PreferredButton getDefaultButton() {
		return PreferredButton.RELOAD;
	}
	
}