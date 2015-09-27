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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.UpdateNode;
import org.pepstock.jem.gwt.server.services.NodesManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.ConfigType;
import org.pepstock.jem.rest.paths.CommonPaths;
import org.pepstock.jem.rest.paths.NodesManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * REST services published in the web part, to manage nodes.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Singleton
@Path(NodesManagerPaths.MAIN)
public class NodesManagerImpl extends DefaultServerResource {

	private NodesManager manager = null;
	
	
	/**
	 * REST service which returns nodes, by nodes name filter
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodes(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				return ResponseBuilder.JSON.ok(manager.getNodes(nodesFilter));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns swarm nodes, by swarn nodes name filter
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.SWARM_LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSwarmNodes(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				return ResponseBuilder.JSON.ok(manager.getSwarmNodes(nodesFilter));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns nodes, by  nodes filters
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.LIST_BY_FILTER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodesByFilter(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				return ResponseBuilder.JSON.ok(manager.getNodesByFilter(nodesFilter));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which updates node
	 * 
	 * @param node node to be updated
	 * @return returned object
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(NodesManagerPaths.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response update(@PathParam(NodesManagerPaths.NODEKEY) String key, UpdateNode update) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try{
				if (key == null || update == null){
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				NodeInfo node = manager.getNodeByKey(key);
				if (node == null){
					return ResponseBuilder.PLAIN.notFound(key);
				}
				NodeInfoBean bean = node.getNodeInfoBean();
				if (update.getAffinity() != null){
					bean.getExecutionEnvironment().getStaticAffinities().clear();
					String[] affinities = update.getAffinity().split(Jcl.AFFINITY_SEPARATOR);
					for (int i=0; i<affinities.length; i++){
						bean.getExecutionEnvironment().getStaticAffinities().add(affinities[i]);
					}
				}
				if (update.getDomain() !=null && !update.getDomain().equalsIgnoreCase(bean.getExecutionEnvironment().getDomain())){
					bean.getExecutionEnvironment().setDomain(update.getDomain());
				}
				if (update.getParallelJobs() >= ExecutionEnvironment.MINIMUM_PARALLEL_JOBS && 
						update.getParallelJobs() <= ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS){
					int jobsValue = Math.max(Math.min(update.getParallelJobs(), ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS), ExecutionEnvironment.MINIMUM_PARALLEL_JOBS);
					if (bean.getExecutionEnvironment().getParallelJobs() != jobsValue){
						bean.getExecutionEnvironment().setParallelJobs(jobsValue);
					}
				}
				if (update.getMemory() >= ExecutionEnvironment.MINIMUM_MEMORY && 
						update.getMemory() <= ExecutionEnvironment.MAXIMUM_MEMORY){
					int memoryValue = Math.max(Math.min(update.getMemory(), ExecutionEnvironment.MAXIMUM_MEMORY), ExecutionEnvironment.MINIMUM_MEMORY);
					if (bean.getExecutionEnvironment().getMemory() != memoryValue){
						bean.getExecutionEnvironment().setMemory(memoryValue);
					}
				}
				return ResponseBuilder.PLAIN.ok(manager.update(bean).toString());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns the result of TOP command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.NODE_BY_KEY)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeByKey(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				if (key == null){
					return ResponseBuilder.JSON.badRequest(NodesManagerPaths.NODEKEY);
				}
				NodeInfo node = manager.getNodeByKey(key);
				return (node == null) ? ResponseBuilder.JSON.notFound(key) : ResponseBuilder.JSON.ok(node.getNodeInfoBean());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns the result of TOP command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.TOP)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response top(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try{
				if (key == null){
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				NodeInfo node = manager.getNodeByKey(key);
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.top(node.getNodeInfoBean()));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns the result of LOG command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response log(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try{
				if (key == null){
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				NodeInfo node = manager.getNodeByKey(key);
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.log(node.getNodeInfoBean()));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns the result of DISPLAY CLUSTER command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.DISPLAY_CLUSTER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response displayCluster(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try{
				if (key == null){
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				NodeInfo node = manager.getNodeByKey(key);
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.displayCluster(node.getNodeInfoBean()));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which returns the configuration file of node
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.GET_NODE_CONFIG_FILE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeConfigFile(@PathParam(NodesManagerPaths.NODEKEY) String key,  @PathParam(NodesManagerPaths.WHAT) String what) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				if (key == null){
					return ResponseBuilder.JSON.badRequest(NodesManagerPaths.NODEKEY);
				}
				ConfigType type = ConfigType.getTypeByPath(what);
				if (type == null){
					ResponseBuilder.JSON.badRequest(NodesManagerPaths.WHAT);
				}
				NodeInfo node = manager.getNodeByKey(key);
				return (node == null) ? ResponseBuilder.JSON.notFound(key) : ResponseBuilder.JSON.ok(manager.getNodeConfigFile(node.getNodeInfoBean(), type.getName()));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * REST service which returns the configuration file of JEM environment
	 * 
	 * @param content container with all params to perform call
	 * @return configuration file
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(NodesManagerPaths.GET_ENV_CONFIG_FILE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvConfigFile(@PathParam(NodesManagerPaths.WHAT) String what) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				ConfigType type = ConfigType.getTypeByPath(what);
				return (type == null) ? ResponseBuilder.JSON.notFound(NodesManagerPaths.WHAT) : ResponseBuilder.JSON.ok(manager.getEnvConfigFile(what));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which checks a configuration file content
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(NodesManagerPaths.CHECK_CONFIG_FILE)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkConfigFile(@PathParam(NodesManagerPaths.WHAT) String what, String content) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try{				
				ConfigType type = ConfigType.getTypeByPath(what);
				return (type == null || content == null) ? ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.WHAT) : ResponseBuilder.PLAIN.ok(manager.checkConfigFile(content, what).toString());
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.PLAIN.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * REST service which checks affinity file content
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws RestException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.CHECK_AFFINITY_POLICY)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAffinityPolicy(@PathParam(NodesManagerPaths.NODEKEY) String key, String content) {
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				if (key == null || content == null){
					return ResponseBuilder.JSON.badRequest(NodesManagerPaths.NODEKEY);
				}
				NodeInfo node = manager.getNodeByKey(key);
				return (node == null) ? ResponseBuilder.JSON.notFound(key) : ResponseBuilder.JSON.ok(manager.checkAffinityPolicy(node.getNodeInfoBean(), content));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#init()
	 */
    @Override
    boolean init() throws Exception {
		if (manager == null) {
			manager = new NodesManager();
		}
		return true;
    }
}
