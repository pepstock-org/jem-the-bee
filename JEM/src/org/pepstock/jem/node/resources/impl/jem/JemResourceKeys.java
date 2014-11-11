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
package org.pepstock.jem.node.resources.impl.jem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.node.resources.impl.CommonKeys;

/**
 * Contains all information necessary to create a REST client for JEM by JNDI.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@SuppressWarnings("javadoc")
public class JemResourceKeys {

	public static final String URL = "url";
	
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(CommonKeys.USERID, CommonKeys.PASSWORD, URL));

	/**
	 * To avoid any instantiation
	 */
	private JemResourceKeys() {
	}
}