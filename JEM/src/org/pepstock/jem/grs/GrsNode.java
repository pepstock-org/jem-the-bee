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
package org.pepstock.jem.grs;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeException;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.RequestLock;

/**
 * Is the default implementation of a node which can works inside of global
 * resources system (GRS).<br>
 * All GRS node must be identify by a key and must provide a request for locking
 * of resources.<br>
 * Is simply a requester of locks
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public final class GrsNode extends NodeInfo {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public GrsNode() {

	}

	/**
	 * Called before to load all data of node
	 * 
	 * @throws Exception
	 *             if exception occurs
	 */
	public void loaded() throws NodeException {
		GrsManager.createInstance(Main.getHazelcast());
		GrsManager.getInstance().setNode(this);
	}

	
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.NodeInfo#createRequestLock(org.pepstock.jem.Job)
	 */
    @Override
    public RequestLock createRequestLock(Job job) {
		GrsRequestLock rLock = new GrsRequestLock();
		rLock.setRequestorId(job.getId());
		rLock.setRequestorName(job.getName());
		return rLock;
    }

	/**
	 * Returns a string with all locks currently in JEM
	 * 
	 * @return all locks info
	 */
	public String displayRequestors() {
		return CommandsUtility.displayRequestors().toString();
	}

	/**
	 * Returns a string with all locks for argument (resource) currently in JEM.
	 * 
	 * @param resourceKey
	 *            resource name (key)
	 * @return all locks info for resource name
	 */
	public String displayRequestors(String resourceKey) {
		return CommandsUtility.displayRequestors(resourceKey).toString();
	}

	/**
	 * Unlocks all resources previously locked in case of node failover.
	 */
	@Override
	public void unlockForFailover() {
		GrsManager.getInstance().removeRequestor(this);
	}
}