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

/**
 * This class represents a password field, with optional regular expression validator.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.3
 */
public class PasswordFieldDescriptor extends TextFieldDescriptor {

	private static final long serialVersionUID = 0L;

	/**
	 * Empty constructor to be serialized
	 */
	public PasswordFieldDescriptor() {
		super();
	}

	/**
	 * Constructs the filed using key and label 
	 * @param key property key
	 * @param label label to be showed on UI
	 */
	public PasswordFieldDescriptor(String key, String label) {
		super(key, label);
		super.setVisible(false);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.resources.definition.fields.TextFieldDescriptor#toString()
	 */
	@Override
	public String toString() {
		return "PasswordFieldDescriptor [toString()=" + super.toString() + "]";
	}
}
