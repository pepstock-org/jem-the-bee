/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem;

/**
 * Is a JCL object with default values used after validating of submitted jobs.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class DefaultJcl extends Jcl {

	private static final long serialVersionUID = 1L;

	/**
	 * Unknown label used for all undefined attributes
	 */
	public static final String UNKNOWN = "UNKNOWN";

	/**
	 * Standard constructor. Inside all mandatory attributes are set.
	 */
	public DefaultJcl() {
		super.setType(UNKNOWN);
		super.setJobName(UNKNOWN);
		super.setEnvironment(UNKNOWN);
		super.setDomain(UNKNOWN);
		super.setAffinity(UNKNOWN);
	}
}