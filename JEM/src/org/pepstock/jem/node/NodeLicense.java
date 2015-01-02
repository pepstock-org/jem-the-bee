/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro.
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
package org.pepstock.jem.node;

import java.io.Serializable;


/**
 * This is the model for the lincense.
 * 
 * @author Simone "Busy" Businaro.
 * 
 */
public class NodeLicense implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String module;

	private String subject;

	private String email;

	private String issuedate;

	private String expiredate;

	private int maxnodes;
	
	private String fileName = null;

	/**
	 * @return the maxnodes
	 */
	public int getMaxnodes() {
		return maxnodes;
	}

	/**
	 * @param maxnodes the maxnodes to set
	 */
	public void setMaxnodes(int maxnodes) {
		this.maxnodes = maxnodes;
	}

	/**
	 * @return the name of the module. Remember that each license is relative to
	 *         a module. E.g. GRS, Ant Util ...
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the name of the module to set. Remember that each license is
	 *            relative to a module. E.g. GRS, Ant Util ...
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject that request the license
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the email of the subject that request the license
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email of the subject that request the license
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the issuedate
	 */
	public String getIssuedate() {
		return issuedate;
	}

	/**
	 * @param issuedate
	 *            the issuedate to set
	 */
	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}

	/**
	 * @return the expiredate
	 */
	public String getExpiredate() {
		return expiredate;
	}

	/**
	 * @param expiredate
	 *            the expire date to set
	 */
	public void setExpiredate(String expiredate) {
		this.expiredate = expiredate;
	}
	
	
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeLicense [module=" + module + ", subject=" + subject + ", email=" + email + ", issuedate=" + issuedate + ", expiredate=" + expiredate + ", maxnodes=" + maxnodes + ", fileName=" + fileName + "]";
	}



}