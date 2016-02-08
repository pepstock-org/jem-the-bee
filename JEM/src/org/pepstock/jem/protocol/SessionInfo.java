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
package org.pepstock.jem.protocol;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.pepstock.jem.util.JobIdGenerator;

/**
 * Contains the session information from client perspective.<br>
 * The client configuration feeds it.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class SessionInfo extends Credentials implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private final String id = UUID.randomUUID().toString();
	
	private final String futureId = JobIdGenerator.createRandomJobId();
	
	private final Set<String> jobIds = new HashSet<String>();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the futureId
	 */
	public String getFutureId() {
		return futureId;
	}

	/**
	 * @return the jobIds
	 */
	public Set<String> getJobIds() {
		return jobIds;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SessionInfo [id=" + id + ", getGroup()=" + getGroup() + ", getUser()=" + getUser() + "]";
	}
	
}
