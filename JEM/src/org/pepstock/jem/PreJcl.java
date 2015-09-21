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
package org.pepstock.jem;

import java.io.Serializable;

/**
 * JCL (Job Control Language) is the class which contains all statements to
 * describe and execute jobs. It is possible to have different languages to
 * control jobs and for this reason is abstract about that.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public final class PreJcl implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String type = null;
	
	private String content = null;
	
	/**
	 * Constructor without any arguments
	 */
	public PreJcl() {
	}

	/**
	 * Sets the type of language (and then the factory) to parse and check the
	 * content of JCL by a short name defined on configuration.
	 * 
	 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
	 * @param type type of job control language
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the type of language of control jobs, or null if none.
	 * 
	 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
	 * @return type of job control language
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the source code representing the JCL, by a string.
	 * 
	 * @param content the string representing source code
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the source code string, representing the JCL.
	 * 
	 * @return the string representing source code
	 */
	public String getContent() {
		return content;
	}
}