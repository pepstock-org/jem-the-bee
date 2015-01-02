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
package org.pepstock.jem.node.resources.impl.jppf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This resource object represents a JPPF description to create a JPPF properties.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
@SuppressWarnings("javadoc")
public final class JppfResourceKeys {

	public static final String ADDRESSES = "addresses";

	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(ADDRESSES));

	/**
	 * To avoid any instantiation
	 */
	private JppfResourceKeys() {
	}
}