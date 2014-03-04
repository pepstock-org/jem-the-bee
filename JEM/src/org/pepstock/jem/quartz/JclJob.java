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
package org.pepstock.jem.quartz;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.commands.SubmitParameters;
import org.quartz.Job;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public abstract class JclJob implements Job {

	private String jclUrl = null;

	private String jclType = null;

	private String password = null;

	private boolean wait = true;

	
	/**
	 * Empty constructor
	 */
	public JclJob() {
		
	}

	/**
	 * @return the jclUrl
	 */
	public final String getJclUrl() {
		return jclUrl;
	}

	/**
	 * @param jclUrl the jclUrl to set
	 */
	public final void setJclUrl(String jclUrl) {
		this.jclUrl = jclUrl;
	}

	/**
	 * @return the jclType
	 */
	public final String getJclType() {
		return jclType;
	}

	/**
	 * @param jclType the jclType to set
	 */
	public final void setJclType(String jclType) {
		this.jclType = jclType;
	}

	/**
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public final void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the wait
	 */
	public final boolean isWait() {
		return wait;
	}

	/**
	 * @param wait the wait to set
	 */
	public final void setWait(boolean wait) {
		this.wait = wait;
	}
	
	/**
	 * Creates a list of argument to use to start Quartz job
	 * @return list of string with all arguments
	 */
	public List<String> createArgs() {
		List<String> list = new ArrayList<String>();
		if (jclUrl != null) {
			list.add("-" + SubmitParameters.JCL.getName());
			list.add(jclUrl);
		}
		if (jclType != null) {
			list.add("-" + SubmitParameters.TYPE.getName());
			list.add(jclType);
		}
		if (password != null) {
			list.add("-" + SubmitParameters.PASSWORD.getName());
			list.add(password);
		}
		if (!wait) {
			list.add("-" + SubmitParameters.WAIT.getName());
			list.add(Boolean.FALSE.toString());
		}
		return list;
	}

}
