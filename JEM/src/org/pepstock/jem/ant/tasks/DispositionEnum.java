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
package org.pepstock.jem.ant.tasks;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.pepstock.catalog.Disposition;

/**
 * Implements ANT interface with the allowed values for <code>disposition</code>
 * attribute.
 * 
 * @see org.pepstock.catalog.Disposition
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DispositionEnum extends EnumeratedAttribute {

	private static String[] VALUES = (String[]) Disposition.VALUES.toArray(new String[0]);

	/**
	 * Empty constructor
	 */
	public DispositionEnum() {
	}

	/**
	 * Returns the enumeration with allowed disposition strings.
	 * 
	 * @return enumeration with allowed disposition strings.
	 */
	@Override
	public String[] getValues() {
		// for FINDBUGS, this is "May expose internal representation by returning reference to mutable object"
		// no change is possible override ANT
		return VALUES;
	}

}