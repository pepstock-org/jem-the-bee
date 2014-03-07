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
package org.pepstock.jem.ant;

import org.pepstock.jem.Jcl;

/**
 * This is the JCL created for Ant. This has an additional attribute
 * which represents the classpath to add to system one.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class AntJcl extends Jcl {
	
	private static final long serialVersionUID = 1L;

	private String classPath = null;

	/**
	 * Empty constructor
	 */
	public AntJcl() {
		super();
	}

	/**
	 * @return the classPath
	 */
	public String getClassPath() {
		return classPath;
	}

	/**
	 * @param classPath the classPath to set
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

}