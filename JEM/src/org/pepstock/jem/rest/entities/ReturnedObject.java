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
package org.pepstock.jem.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a POJO to return from method, with exception information for REST client.<br>
 * In the exception filed there is root cexception.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
@XmlRootElement
public class ReturnedObject {
	
	private String exceptionMessage = null;

	/**
	 * Empty constructor
	 */
	public ReturnedObject() {
	}
	
	/**
	 * Returns <code>true</code> if there is an exception
	 * 
	 * @return true if there the exception message
	 */
	public boolean hasException(){
		return exceptionMessage != null;
	}

	/**
	 * Returns exception message
	 *  
	 * @return the exceptionMessage
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * Sets exception message
	 *  
	 * @param exceptionMessage the exceptionMessage to set
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

}