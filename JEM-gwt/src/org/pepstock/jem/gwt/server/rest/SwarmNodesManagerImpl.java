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
package org.pepstock.jem.gwt.server.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.gwt.server.services.RoutingConfigManager;
import org.pepstock.jem.gwt.server.services.SwarmNodesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.entities.BooleanReturnedObject;
import org.pepstock.jem.rest.entities.Nodes;
import org.pepstock.jem.rest.entities.StringReturnedObject;
import org.pepstock.jem.rest.entities.SwarmConfig;
import org.pepstock.jem.rest.paths.SwarmNodesManagerPaths;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Path(SwarmNodesManagerPaths.MAIN)
public class SwarmNodesManagerImpl extends DefaultServerResource {

	private SwarmNodesManager swarmNodesManager = null;
	
	private RoutingConfigManager routingConfManager = null;
	
	
	/**
	 * REST service which returns nodes, by nodes name filter
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(SwarmNodesManagerPaths.LIST)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Nodes getNodes(String nodesFilter) throws JemException {
		Nodes nodes = new Nodes();
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			try {
	            nodes.setNodes(swarmNodesManager.getNodes(nodesFilter));
            } catch (Exception e) {
	            LogAppl.getInstance().ignore(e.getMessage(), e);
	            nodes.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(nodes);
		}
		return nodes;
	}
	
	/**
	 * REST service which returns nodes, by  nodes filters
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(SwarmNodesManagerPaths.LIST_BY_FILTER)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Nodes getNodesByFilter(String nodesFilter) throws JemException {
		Nodes nodes = new Nodes();
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			try {
	            nodes.setNodes(swarmNodesManager.getNodesByFilter(nodesFilter));
            } catch (Exception e) {
	            LogAppl.getInstance().ignore(e.getMessage(), e);
	            nodes.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(nodes);
		}
		return nodes;
	}
	
	/**
	 * REST service which updates node
	 * 
	 * @param node node to be updated
	 * @return returned object
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(SwarmNodesManagerPaths.START)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public BooleanReturnedObject start() throws JemException {
		BooleanReturnedObject result = new BooleanReturnedObject();
		result.setValue(false);
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			try {
	            result.setValue(swarmNodesManager.start());
            } catch (Exception e) {
	            LogAppl.getInstance().ignore(e.getMessage(), e);
	            result.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	
	/**
	 * REST service which updates node
	 * 
	 * @param node node to be updated
	 * @return returned object
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(SwarmNodesManagerPaths.DRAIN)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public BooleanReturnedObject drain() throws JemException {
		BooleanReturnedObject result = new BooleanReturnedObject();
		result.setValue(false);
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			try {
	            result.setValue(swarmNodesManager.drain());
            } catch (Exception e) {
	            LogAppl.getInstance().ignore(e.getMessage(), e);
	            result.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	
	/**
	 * REST service which updates node
	 * 
	 * @param node node to be updated
	 * @return returned object
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(SwarmNodesManagerPaths.STATUS)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public StringReturnedObject getStatus() throws JemException {
		StringReturnedObject result = new StringReturnedObject();
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			try {
	            result.setValue(swarmNodesManager.getStatus());
            } catch (Exception e) {
	            LogAppl.getInstance().ignore(e.getMessage(), e);
	            result.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	
	/**
	 * REST service which return the configuration file of JEM environment
	 * @param name 
	 * 
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(SwarmNodesManagerPaths.GET_CONFIG)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public SwarmConfig getSwarmConfiguration(String name) throws JemException {
		SwarmConfig result = new SwarmConfig();
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			try {
				result.setConfiguration(routingConfManager.getSwarmConfiguration(name));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	
	/**
	 * REST service which return the configuration file of JEM environment
	 * @param config 
	 * 
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(SwarmNodesManagerPaths.UPDATE_CONFIG)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public SwarmConfig updateSwarmConfiguration(SwarmConfig config) throws JemException {
		SwarmConfig result = new SwarmConfig();
		if (isEnable()){
			if (swarmNodesManager == null){
				initManager();
			}
			if (config.getConfiguration() != null){
				try {
					result.setConfiguration(routingConfManager.updateSwarmConfiguration(config.getConfiguration()));
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					result.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	
	/**
	 * Initialize the manager
	 */
	private synchronized void initManager() {
		if (swarmNodesManager == null) {
			swarmNodesManager = new SwarmNodesManager();
			routingConfManager = new RoutingConfigManager();
		}
	}
}
