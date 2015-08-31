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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * It is used before to move in projob queue. Afterwards the factory will check
 * the jcl (creating that) and then will be moved the job in "input" queue for
 * execution.
 * 
 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
 * @author Andrea "Stock" Stocchero
 * 
 */
@XmlRootElement
public class PreJob implements Serializable {

	private static final long serialVersionUID = 1L;

	private Job job = null;

	private String jclContent = null;

	private String jclType = null;
	
	private String url = null;

	/**
	 * Constructor without any arguments
	 */
	public PreJob() {
	}

	/**
	 * Returns the job object, or null if none.
	 * 
	 * @return the job object
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * Sets the job object
	 * 
	 * @param job the job object
	 */
	public void setJob(Job job) {
		this.job = job;
	}

	/**
	 * Returns the source code string, representing the JCL.
	 * 
	 * @return the string representing source code
	 */
	public String getJclContent() {
		return jclContent;
	}

	/**
	 * Sets the source code representing the JCL, by a string.
	 * 
	 * @param jclContent the string representing source code
	 */
	public void setJclContent(String jclContent) {
		this.jclContent = jclContent;
	}

	/**
	 * Returns the type of language of control jobs, or null if none.
	 * 
	 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
	 * @return type of job control language
	 */
	public String getJclType() {
		return jclType;
	}

	/**
	 * Sets the type of language (and then the factory) to parse and check the
	 * content of JCL by a short name defined on configuration.
	 * 
	 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
	 * @param jclType type of job control language
	 */
	public void setJclType(String jclType) {
		this.jclType = jclType;
	}
	
	/**
	 * Gets the JEM url (ONLY if the submit uses a JEM URL), otherwise always null.<br>
	 * JEM URL is the locator of file in JEM GFS.
	 * 
	 * @return the url JEM url to locate a resource in JEM GFS
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the JEM url (ONLY if the submit uses a JEM URL)<br>
	 * JEM URL is the locator of file in JEM GFS.
	 * @param url the JEM url to locate a resource in JEM GFS
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PreJob [job=" + job + ", jclType=" + jclType + ", url=" + url + "]";
	}

}