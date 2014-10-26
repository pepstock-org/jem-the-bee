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
package org.pepstock.jem;

import java.io.Serializable;

/**
 * It encapsulates an independent, sequential phase of a batch job and contains
 * all of the information necessary to define and control the actual batch
 * processing. This is a necessarily vague description because the contents of
 * any given Step are at the discretion of the developer writing a Job. A Step
 * can be as simple or complex as the developer desires. A simple Step might
 * load data from a file into the database, requiring little code. (depending
 * upon the implementations used) A more complex Step may have complicated
 * business rules that are applied as part of the processing.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Step implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;

	private String description = null;

	private int returnCode = 0;

	private String exception = null;

	/**
	 * Constructor without any arguments
	 */
	public Step() {
	}

	/**
	 * Returns the name for the step, or null if none.
	 * 
	 * @return the name for the step, or null if none
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the mandatory name for the step.
	 * 
	 * @param name the name for step
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the optional description string for the step, or null if none.
	 * 
	 * @return the description string for the step
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the optional description string for the step.
	 * 
	 * @param description the description string
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the return code of step execution. Default value is 0.
	 * 
	 * @return return-code of step
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * Sets the return code of step execution.
	 * 
	 * @param returnCode return-code of step
	 */
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	/**
	 * Returns the optional exception string for the step, or null if none.
	 * Usually it is not null if the step is abended
	 * 
	 * @return the exception string
	 */
	public String getException() {
		return exception;
	}

	/**
	 * Sets the optional exception string for the step. Usually used if step is
	 * abended.
	 * 
	 * @param exception exception string
	 */
	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * Returns the string representation of step, with step name and
	 * description, if there is.
	 * 
	 * @return step name and description
	 */
	@Override
	public String toString() {
		return "Step [name=" + name + ", descritpion=" + description + "]";
	}
}