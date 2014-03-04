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
package org.pepstock.jem.gwt.client.notify;

import java.io.Serializable;
import java.util.Date;

import org.pepstock.jem.log.MessageLevel;

/**
 * Entity which contains all data of a toast.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class ToastMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private MessageLevel level = MessageLevel.INFO;

	private String title = null;

	private String message = null;

	private Date date = new Date();

	/**
	 * Empty constructor
	 */
	public ToastMessage() {

	}

	/**
	 * Returns level of toast
	 * 
	 * @return the level
	 */
	public MessageLevel getLevel() {
		return level;
	}

	/**
	 * Sets level of toast
	 * 
	 * @param level
	 *            the level to set
	 */
	public void setLevel(MessageLevel level) {
		this.level = level;
	}

	/**
	 * Returns title of toast
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets title of toast
	 * 
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns message of toast
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets message of toast
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns timestamp of toast
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return (Date) date.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ToastMessage [level=" + level + ", title=" + title + ", message=" + message + ", date=" + date + "]";
	}

}
