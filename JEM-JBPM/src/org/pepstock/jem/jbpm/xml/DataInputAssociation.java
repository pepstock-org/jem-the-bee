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
package org.pepstock.jem.jbpm.xml;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class DataInputAssociation {
	
	private String targetRef = null;
	
	private String assignmentFrom = null;
	
	private String assignmentTo = null;

	/**
	 * 
	 */
	public DataInputAssociation() {
		
	}

	/**
	 * @return the targetRef
	 */
	public String getTargetRef() {
		return targetRef;
	}

	/**
	 * @param targetRef the targetRef to set
	 */
	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}

	/**
	 * @return the assignmentFrom
	 */
	public String getAssignmentFrom() {
		return assignmentFrom;
	}

	/**
	 * @param assignmentFrom the assignmentFrom to set
	 */
	public void setAssignmentFrom(String assignmentFrom) {
		this.assignmentFrom = assignmentFrom;
	}

	/**
	 * @return the assignmentTo
	 */
	public String getAssignmentTo() {
		return assignmentTo;
	}

	/**
	 * @param assignmentTo the assignmentTo to set
	 */
	public void setAssignmentTo(String assignmentTo) {
		this.assignmentTo = assignmentTo;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isValid(){
		if (targetRef == null || assignmentFrom == null || assignmentTo == null){
			return false;
		}
		return targetRef.equalsIgnoreCase(assignmentTo);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "DataInputAssociation [targetRef=" + targetRef + ", assignmentFrom=" + assignmentFrom + ", assignmentTo=" + assignmentTo + "]";
    }
}
