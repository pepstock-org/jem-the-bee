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
package org.pepstock.jem.node.stats;

import java.io.Serializable;

/**
 * Is a asbtract bean with commons statistics information for all members of 
 * JEM cluster
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public abstract class AbstractMemberSample implements Serializable {

	private static final long serialVersionUID = 1L;

	private String memberKey = null;

	private String memberLabel = null;

	private String memberHostname = null;
	
	private long pid = Long.MIN_VALUE;

	private long numberOfJCLCheck = 0;
	
	private long numberOfJOBSubmitted = 0;

	private long totalNumberOfJCLCheck = 0;
	
	private long totalNumberOfJOBSubmitted = 0;

	/**
	 * Empty constructor
	 */
	public AbstractMemberSample() {
	}
	
	/**
	 * @return the memberKey
	 */
	public final String getMemberKey() {
		return memberKey;
	}
	/**
	 * @param memberKey the memberKey to set
	 */
	public final void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}
	/**
	 * @return the memberLabel
	 */
	public final String getMemberLabel() {
		return memberLabel;
	}
	/**
	 * @param memberLabel the memberLabel to set
	 */
	public final void setMemberLabel(String memberLabel) {
		this.memberLabel = memberLabel;
	}
	
	/**
	 * @return the memberHostname
	 */
	public final String getMemberHostname() {
		return memberHostname;
	}

	/**
	 * @param memberHostname the memberHostname to set
	 */
	public final void setMemberHostname(String memberHostname) {
		this.memberHostname = memberHostname;
	}

	/**
	 * @return the pid
	 */
	public final long getPid() {
		return pid;
	}
	/**
	 * @param pid the pid to set
	 */
	public final void setPid(long pid) {
		this.pid = pid;
	}
	/**
	 * @return the numberOfJCLCheck
	 */
	public final long getNumberOfJCLCheck() {
		return numberOfJCLCheck;
	}
	/**
	 * @param numberOfJCLCheck the numberOfJCLCheck to set
	 */
	public final void setNumberOfJCLCheck(long numberOfJCLCheck) {
		this.numberOfJCLCheck = numberOfJCLCheck;
	}
	/**
	 * @return the numberOfJOBSubmitted
	 */
	public final long getNumberOfJOBSubmitted() {
		return numberOfJOBSubmitted;
	}
	/**
	 * @param numberOfJOBSubmitted the numberOfJOBSubmitted to set
	 */
	public final void setNumberOfJOBSubmitted(long numberOfJOBSubmitted) {
		this.numberOfJOBSubmitted = numberOfJOBSubmitted;
	}
	/**
	 * @return the totalNumberOfJCLCheck
	 */
	public final long getTotalNumberOfJCLCheck() {
		return totalNumberOfJCLCheck;
	}
	/**
	 * @param totalNumberOfJCLCheck the totalNumberOfJCLCheck to set
	 */
	public final void setTotalNumberOfJCLCheck(long totalNumberOfJCLCheck) {
		this.totalNumberOfJCLCheck = totalNumberOfJCLCheck;
	}
	/**
	 * @return the totalNumberOfJOBSubmitted
	 */
	public final long getTotalNumberOfJOBSubmitted() {
		return totalNumberOfJOBSubmitted;
	}
	/**
	 * @param totalNumberOfJOBSubmitted the totalNumberOfJOBSubmitted to set
	 */
	public final void setTotalNumberOfJOBSubmitted(long totalNumberOfJOBSubmitted) {
		this.totalNumberOfJOBSubmitted = totalNumberOfJOBSubmitted;
	}
}