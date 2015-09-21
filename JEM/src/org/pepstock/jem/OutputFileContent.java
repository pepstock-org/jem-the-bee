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
 * Container of log file content. Wraps a string-buffer.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class OutputFileContent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String content = null;

	/**
	 * Empty constructor
	 */
	public OutputFileContent() {
	}

	/**
	 * Returns content of file
	 * 
	 * @return content of file
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets content of file
	 * 
	 * @param content content of file
	 */
	public void setContent(String content) {
		this.content = content;
	}

}