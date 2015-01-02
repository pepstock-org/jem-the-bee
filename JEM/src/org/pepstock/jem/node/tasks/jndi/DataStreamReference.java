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
package org.pepstock.jem.node.tasks.jndi;

import javax.naming.Reference;

/**
 * Sets constants for JNDI. Both class name and factory name.<br>
 * This reference is used to create an object (inputStream or 
 * outputStream, depending on data description SHR).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataStreamReference extends Reference {

	private static final long serialVersionUID = 1L;

	private static final String FACTORY = DataStreamFactory.class.getName();

	private static final String CLASSNAME = Object.class.getName();

	/**
	 * Empty constructor called by JNDI engine
	 */
	public DataStreamReference() {
		super(CLASSNAME, FACTORY, null);
	}
}