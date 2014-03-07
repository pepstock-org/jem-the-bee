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
 * Contains the return code and exception message (if exists) of a job.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Return code value for successful execution (RC=0).
	 */
	public static final int SUCCESS = 0;
	/**
	 * Return code value for error execution (RC=1).
	 */
	public static final int ERROR = 1;
	/**
	 * Return code value for fatal execution (RC=12).
	 */
	public static final int FATAL = 12;
	/**
	 * Return code value for severe execution (RC=16).
	 */
	public static final int SEVERE = 16;
	/**
	 * Return code value for canceled execution (RC=222).
	 */
	public static final int CANCELED = 222;

	private int returnCode = SUCCESS;

	private String exceptionMessage = null;

	/**
	 * Represents the result of job execution, in terms of return code and,
	 * eventually, exception message
	 * 
	 */
	public Result() {
	}

	/**
	 * Returns the return code of job execution. Default value is 0 (SUCCESS).
	 * 
	 * @return return-code of job
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * Sets the return code of job execution.
	 * 
	 * @param returnCode return-code of job
	 */
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	/**
	 * Returns the optional exception string for the job, or null if none.
	 * Usually it is not null if the job is abended
	 * 
	 * @return the exception string
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * Sets the optional exception string for the job. Usually used if job is
	 * abended.
	 * 
	 * @param exceptionMessage exception string
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

}