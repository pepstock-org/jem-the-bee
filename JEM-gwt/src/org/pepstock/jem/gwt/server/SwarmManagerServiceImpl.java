/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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
package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.services.SwarmManagerService;
import org.pepstock.jem.gwt.server.services.SwarmNodesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.swarm.SwarmNodeMessage;

/**
 * The manager for the services relative to the swarm nodes
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class SwarmManagerServiceImpl extends DefaultManager implements SwarmManagerService {

	private static final long serialVersionUID = 1L;

	private transient SwarmNodesManager swarmNodesManager = null;


	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.SwarmNodesManagerService#getNodes(java.lang.String)
	 */
	@Override
	public Collection<NodeInfoBean> getNodes(String nodesFilter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (swarmNodesManager == null){
			initManager();
		}
		try {
			return swarmNodesManager.getNodes(nodesFilter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO014I, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.SwarmNodesManagerService#getNodesByFilter(java.lang.String)
	 */
    @Override
    public Collection<NodeInfoBean> getNodesByFilter(String filterString) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (swarmNodesManager == null){
			initManager();
		}
		try {
			return swarmNodesManager.getNodesByFilter(filterString);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO014I, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
    }
    
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.SwarmNodesManagerService#start()
	 */
	@Override
	public Boolean start() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (swarmNodesManager == null){
			initManager();
		}
		try {
			return swarmNodesManager.start();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO014I, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}



	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.SwarmNodesManagerService#shutdown()
	 */
	@Override
	public Boolean drain() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (swarmNodesManager == null){
			initManager();
		}
		try {
			return swarmNodesManager.drain();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO014I, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.SwarmManagerService#getStatus()
	 */
    @Override
    public String getStatus() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (swarmNodesManager == null){
			initManager();
		}
		try {
			return swarmNodesManager.getStatus();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO014I, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
    }
   
	/**
     * Initializes a manager
     * @throws JemException if any exception occurs 
	 */
	private synchronized void initManager() throws JemException {
		if (swarmNodesManager == null) {
			try {
				swarmNodesManager = new SwarmNodesManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(SwarmNodeMessage.JEMO014I, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}
}