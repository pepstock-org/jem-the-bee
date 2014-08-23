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
 * Represents JOB id, submitted by REST
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@XmlRootElement
public class JobId extends ReturnedObject {

	private String id = null;
	
	private String queueName = null;
	
	/**
	 * Empty constructor
	 */
	public JobId() {
	}

	/**
	 * Gets Job ID
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets JOB id
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "Id [id=" + id + "]";
    }
}