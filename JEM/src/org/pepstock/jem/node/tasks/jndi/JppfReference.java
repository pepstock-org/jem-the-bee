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
package org.pepstock.jem.node.tasks.jndi;

import javax.naming.Reference;

import org.jppf.utils.TypedProperties;

/**
 * Sets constants for JNDI for JPPF typed properteis.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class JppfReference extends Reference {

	private static final long serialVersionUID = 1L;

	/**
	 * Apache DBCP JNDI factory
	 */
	public static final String FTP_FACTORY = JppfFactory.class.getName();
	
	/**
	 * Is DataSource object created when requested
	 */
	public static final String FTP_OBJECT = TypedProperties.class.getName();
	
	/**
	 * Creates a JNDI reference for JDBC purposes
	 */
	public JppfReference() {
		super(FTP_OBJECT, FTP_FACTORY, null);
	}

}