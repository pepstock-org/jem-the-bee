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
package org.pepstock.jem.node.executors;

import java.io.Serializable;

/**
 * Represents if that call-back task ended correctly or not.<br>
 * This is the object that all call-back must use if they haven't any return
 * value.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class ExecutionResult extends Object implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Successful result of task
	 */
	public static final ExecutionResult SUCCESSFUL = new ExecutionResult(0, "SUCCESSFUL");

	/**
	 * Unsuccessful result of task
	 */
	public static final ExecutionResult UNSUCCESSUL = new ExecutionResult(1, "UNSUCCESSFUL");

	private int index = 0;

	private String description = null;

	/**
	 * Constructs the object using a index and description.<br>
	 * This is protected and you can use only constants already defined (only
	 * SUCCESSUL or UNSUCCESSFUL).
	 * 
	 * @param index internal index of result
	 * @param description he description of result
	 */
	protected ExecutionResult(int index, String description) {
		this.index = index;
		this.description = description;
	}

	/**
	 * Returns internal index of result. 0 is successful, 1 is unsuccessful.
	 * 
	 * @return 0 is successful, 1 is unsuccessful
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the description of result (readable value)
	 * 
	 * @return the description of result
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Compares the object with result. Returns true if:<br>
	 * 1. the object parameter is instance of ExecutionResult and the index is
	 * the same.<br>
	 * 2. the object parameter is instance of String and the description is the
	 * same.<br>
	 * otherwise always false.
	 * 
	 * @param o object to compare
	 * @return <code>true</code> if equal, otherwise <code>false</code>
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ExecutionResult) {
			ExecutionResult st = (ExecutionResult) o;
			return st.getIndex() == getIndex();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getIndex();
	}

	/**
	 * Returns the string representation of result, using only the description.
	 * 
	 * @return description of result
	 */
	@Override
	public String toString() {
		return description;
	}

}