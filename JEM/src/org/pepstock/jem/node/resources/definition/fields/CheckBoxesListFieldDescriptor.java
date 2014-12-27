/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.node.resources.definition.fields;

import com.google.gwt.user.client.ui.CheckBox;

/**
 * The most simple implementation of {@link MultiSelectableListFieldDescriptor}, rendered as a {@link CheckBox}es  
 * @author Marco "Fuzzo" Cuccato
 */
public class CheckBoxesListFieldDescriptor extends MultiSelectableListFieldDescriptor {

	private static final long serialVersionUID = -3414197323940075115L;

	/**
	 * Builds the field
	 */
	public CheckBoxesListFieldDescriptor() {
	}

	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public CheckBoxesListFieldDescriptor(String key, String label) {
		super(key, label);
	}

	@Override
	public String toString() {
		return "CheckBoxesListFieldDescriptor [toString()=" + super.toString() + "]";
	}

}
