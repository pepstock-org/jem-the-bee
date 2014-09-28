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
package org.pepstock.jem.ant.tasks.utilities;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.pepstock.jem.gfs.GfsFileType;

/**
 * Implements ANT interface with the allowed values for <code>type</code>
 * attribute.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class GfsTypeEnum extends EnumeratedAttribute {

	private static String[] VALUES = (String[]) GfsFileType.VALUES.toArray(new String[0]);

	/**
	 * Empty constructor
	 */
	public GfsTypeEnum() {
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