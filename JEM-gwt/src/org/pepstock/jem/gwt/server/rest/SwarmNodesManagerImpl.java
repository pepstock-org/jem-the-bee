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
package org.pepstock.jem.gwt.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pepstock.jem.gwt.server.services.RoutingConfigManager;
import org.pepstock.jem.gwt.server.services.SwarmNodesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.rest.paths.CommonPaths;
import org.pepstock.jem.rest.paths.SwarmNodesManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * REST services published in the web part, to manage swarm nodes and
 * configuration.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Singleton
@Path(SwarmNodesManagerPaths.MAIN)
public class SwarmNodesManagerImpl extends DefaultServerResource {

	private SwarmNodesManager swarmNodesManager = null;

	private RoutingConfigManager routingConfManager = null;

	/**
	 * REST service which returns nodes, by nodes name filter
	 * 
	 * @param nodesFilter
	 *            nodes name filter
	 * @return a list of nodes for swarm
	 */
	@GET
	@Path(SwarmNodesManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodes(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns nodes
				return ResponseBuilder.JSON.ok(swarmNodesManager.getNodes(nodesFilter));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which returns nodes, by nodes filters
	 * 
	 * @param nodesFilter
	 *            nodes name filter
	 * @return a list of nodes
	 */
	@GET
	@Path(SwarmNodesManagerPaths.LIST_BY_FILTER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodesByFilter(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns nodes
				return ResponseBuilder.JSON.ok(swarmNodesManager.getNodesByFilter(nodesFilter));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which starts SWARM!
	 * 
	 * @return returns <code>true</code> if ended correctly otherwise
	 *         <code>false</code>
	 */
	@PUT
	@Path(SwarmNodesManagerPaths.START)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response start() {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns true if OK
				return ResponseBuilder.PLAIN.ok(swarmNodesManager.start().toString());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which drains SWARM!
	 * 
	 * @return returns <code>true</code> if ended correctly otherwise
	 *         <code>false</code>
	 */
	@PUT
	@Path(SwarmNodesManagerPaths.DRAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response drain() {
		// it uses plain text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns true if OK
				return ResponseBuilder.PLAIN.ok(swarmNodesManager.drain().toString());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which returns the status of SWARM!
	 * 
	 * @return the status of SWARM!
	 */
	@GET
	@Path(SwarmNodesManagerPaths.STATUS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getStatus() {
		// it uses plain text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns SWARM status
				return ResponseBuilder.PLAIN.ok(swarmNodesManager.getStatus());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * REST service which return the configuration of SWARM environment
	 * 
	 * @return configuration of SWARM
	 */
	@GET
	@Path(SwarmNodesManagerPaths.CONFIG)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSwarmConfiguration() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns swarm configuration
				return ResponseBuilder.JSON.ok(routingConfManager.getSwarmConfiguration(SwarmConfiguration.DEFAULT_NAME));
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#init()
	 */
	@Override
	boolean init() throws JemException {
		try {
	        if (swarmNodesManager == null) {
	        	swarmNodesManager = new SwarmNodesManager();
	        }
	        if (routingConfManager == null) {
	        	routingConfManager = new RoutingConfigManager();
	        }
	        return true;
        } catch (Exception e) {
        	throw new JemException(e.getMessage(), e);
        }
	}
}
