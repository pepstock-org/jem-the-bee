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
package org.pepstock.catalog;

import java.io.Serializable;

/**
 * Contains all constants of different kinds of a dataset.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public final class DataSetType implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant that represents a normal file
	 */
	public static final int FILE = 0;

	/**
	 * Constant that represents a temporary file
	 */
	public static final int TEMPORARY = 1;

	/**
	 * Constant that represents a stream where its content is defined inside of
	 * JCL
	 */
	public static final int INLINE = 2;

	/**
	 * Constant that represents a reference to another data description, defined
	 * in a previous step
	 */
	public static final int REFERENCE = 3;

	/**
	 * Constant that represents a GDG (generation data group) file
	 */
	public static final int GDG = 4;

	/**
	 * Constant that represents a stream where its content will be written
	 * together with sysout of job
	 */
	public static final int SYSOUT = 5;

	/**
	 * Constant that represents a file to access by a defined common resource
	 */
	public static final int RESOURCE = 6;

	/**
	 * To avoid any instantiation
	 */
	private DataSetType() {
	}
}