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
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean with information about JEM version, build time and all licenses installed.
 * This is used only from user interface or by REST call.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class About implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String version = NodeInfo.UNKNOWN_VERSION;
			
	private String creationTime = NodeInfo.UNKNOWN_VERSION;		
	
	private List<NodeLicense> licenses = new ArrayList<NodeLicense>();

	/**
	 * Empty constructor
	 */
	public About() {
	}

	/**
	 * @return the licenses
	 */
	public List<NodeLicense> getLicenses() {
		return licenses;
	}

	/**
	 * @param licenses the licenses to set
	 */
	public void setLicenses(List<NodeLicense> licenses) {
		this.licenses = licenses;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the creationTime
	 */
	public String getCreationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "About [version=" + version + ", creationTime=" + creationTime + ", licenses=" + licenses + "]";
	}
}