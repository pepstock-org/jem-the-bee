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
package org.pepstock.jem.node.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

import com.google.gwt.user.client.rpc.GwtTransient;


/**
 * Entity of JEM which contains all information about statement to be committed on database.<br>
 * This object is created when the persistent engine is not able to save information on database.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class RedoStatement implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant to indicate that job must be stored
	 */
	public static final String STORE = "store";

	/**
	 * Constant to indicate that job must be deleted
	 */
	public static final String DELETE = "delete";
	
	private Long id = null;
	
	private Date creation = new Date();
	
	private String queueName = null;

	private String action = null;
	
	private String entityId = null;
	
	@GwtTransient
	private Object entity = null;

	private String entityToString = null;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
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

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entity
	 */
	@XmlTransient
	public Object getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Object entity) {
		this.entity = entity;
	}

	/**
	 * @return the creation
	 */
	public Date getCreation() {
		return creation;
	}

	/**
	 * @param creation the creation to set
	 */
	public void setCreation(Date creation) {
		this.creation = creation;
	}
	
	/**
	 * @return the entityToString
	 */
	public String getEntityToString() {
		return entityToString;
	}

	/**
	 * @param entityToString the entityToString to set
	 */
	public void setEntityToString(String entityToString) {
		this.entityToString = entityToString;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RedoStatement [id=" + id + ", creation=" + creation + ", queueName=" + queueName + ", action=" + action + ", entityId=" + entityId + "]";
	}

}