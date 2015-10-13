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

import java.util.Arrays;

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
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.node.configuration.ConfigKeys;
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
	 * @param nodesFilter
	 *            nodes name filter
	 * @return a list of nodes
	 */
	@GET
	@Path(NodesManagerPaths.LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodes(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns the list of nodes
				return ResponseBuilder.JSON.ok(manager.getNodes(nodesFilter));
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
	 * REST service which returns swarm nodes, by swarm nodes name filter
	 * 
	 * @param nodesFilter
	 *            nodes name filter
	 * @return a list of nodes
	 */
	@GET
	@Path(NodesManagerPaths.SWARM_LIST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSwarmNodes(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns the list of nodes
				return ResponseBuilder.JSON.ok(manager.getSwarmNodes(nodesFilter));
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
	@Path(NodesManagerPaths.LIST_BY_FILTER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodesByFilter(@DefaultValue(CommonPaths.DEFAULT_FILTER) @QueryParam(CommonPaths.FILTER_QUERY_STRING) String nodesFilter) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// returns the lsit of nodes
				return ResponseBuilder.JSON.ok(manager.getNodesByFilter(nodesFilter));
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
	 * REST service which starts node
	 * 
	 * @param key
	 *            node key where executes command
	 * @return <code>true</code> if ended correctly
	 */
	@PUT
	@Path(NodesManagerPaths.START)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response start(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if node key is null, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// get node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns true is OK
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.start(Arrays.asList(node.getNodeInfoBean())).toString());
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
	 * REST service which drains node
	 * 
	 * @param key
	 *            node key where executes command
	 * @return <code>true</code> if ended correctly
	 */
	@PUT
	@Path(NodesManagerPaths.DRAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response drain(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if node key is null, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// get node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns true is OK
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.drain(Arrays.asList(node.getNodeInfoBean())).toString());
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
	 * REST service which updates node
	 * 
	 * @param key
	 *            node key where executes command
	 * @param update
	 *            container of attributes to change on node
	 * @return <code>true</code> if ended correctly
	 */
	@PUT
	@Path(NodesManagerPaths.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response update(@PathParam(NodesManagerPaths.NODEKEY) String key, UpdateNode update) {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if key or update attributes are missing, bad request
				if (key == null || update == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found
				if (node == null) {
					return ResponseBuilder.PLAIN.notFound(key);
				}
				// creates info bean
				// IT DOESN'T USE the INTERNAL OBJECT of NODEINFO
				// TO AVOID MUTLI ACCESS TO THE SAME OBJECT
				NodeInfoBean bean = createNodeInfoBean(node);
				// checks if wants to change affinity
				if (update.getAffinity() != null) {
					// clear the affinities
					bean.getExecutionEnvironment().getStaticAffinities().clear();
					// scans and add all affinities
					String[] affinities = update.getAffinity().split(Jcl.AFFINITY_SEPARATOR);
					for (int i = 0; i < affinities.length; i++) {
						bean.getExecutionEnvironment().getStaticAffinities().add(affinities[i]);
					}
				}
				// checks if wants to change the domain
				if (update.getDomain() != null && !update.getDomain().equalsIgnoreCase(bean.getExecutionEnvironment().getDomain())) {
					// update the domain
					bean.getExecutionEnvironment().setDomain(update.getDomain());
				}
				// checks if wants to change the parallel jobs
				if (update.getParallelJobs() >= ExecutionEnvironment.MINIMUM_PARALLEL_JOBS && update.getParallelJobs() <= ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS) {
					// checks if inside of right range
					int jobsValue = Math.max(Math.min(update.getParallelJobs(), ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS), ExecutionEnvironment.MINIMUM_PARALLEL_JOBS);
					if (bean.getExecutionEnvironment().getParallelJobs() != jobsValue) {
						bean.getExecutionEnvironment().setParallelJobs(jobsValue);
					}
				}
				// checks if wants to change memory attribute
				if (update.getMemory() >= ExecutionEnvironment.MINIMUM_MEMORY && update.getMemory() <= ExecutionEnvironment.MAXIMUM_MEMORY) {
					// gets value and checks again if it ust be changed
					int memoryValue = Math.max(Math.min(update.getMemory(), ExecutionEnvironment.MAXIMUM_MEMORY), ExecutionEnvironment.MINIMUM_MEMORY);
					if (bean.getExecutionEnvironment().getMemory() != memoryValue) {
						bean.getExecutionEnvironment().setMemory(memoryValue);
					}
				}
				// returns true if OK
				return ResponseBuilder.PLAIN.ok(manager.update(bean).toString());
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
	 * REST service which returns the result of TOP command
	 * 
	 * @see NodeInfoBeans
	 * @param key
	 *            node key where executes command
	 * @return a node info bean object
	 */
	@GET
	@Path(NodesManagerPaths.GET)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeByKey(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if key is missing, bad request
				if (key == null) {
					return ResponseBuilder.JSON.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns the node
				return (node == null) ? ResponseBuilder.JSON.notFound(key) : ResponseBuilder.JSON.ok(node.getNodeInfoBean());
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
	 * REST service which returns the result of TOP command
	 * 
	 * @param key
	 *            node key where executes command
	 * @return the string of top command result
	 */
	@GET
	@Path(NodesManagerPaths.TOP)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response top(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if key is missing, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns the top
				// command result
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.top(node.getNodeInfoBean()));
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
	 * REST service which returns the result of LOG command
	 * 
	 * @param key
	 *            node key where executes command
	 * @return the string of log command result
	 */
	@GET
	@Path(NodesManagerPaths.LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response log(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if key is missing, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns the log
				// result
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.log(node.getNodeInfoBean()));
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
	 * REST service which returns the result of DISPLAY CLUSTER command
	 * 
	 * @param key
	 *            node key where executes command
	 * @return the string of disCluster command result
	 */
	@GET
	@Path(NodesManagerPaths.DISPLAY_CLUSTER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response displayCluster(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses PLAIN text response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if key is missing, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns the command
				// result
				return (node == null) ? ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(manager.displayCluster(node.getNodeInfoBean()));
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
	 * REST service which returns the affinity policy file of node
	 * 
	 * @param key
	 *            node key where executes command
	 * @param type
	 *            type of configuration file requested
	 * @see ConfigType
	 * @return a configuration file instance
	 */
	@GET
	@Path(NodesManagerPaths.GET_AFFINITY_POLICY)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAffinityPolicy(@PathParam(NodesManagerPaths.NODEKEY) String key) {
		// it uses PLAIN response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// if key is missing, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns the
				// configuration file
				if (node ==null){
					return ResponseBuilder.PLAIN.notFound(key);
				}
				ConfigurationFile file = manager.getNodeConfigFile(node.getNodeInfoBean(), ConfigKeys.AFFINITY);
				return (file == null) ?  ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(file.getContent());
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
	 * REST service which checks affinity file content
	 * 
	 * @param key
	 *            node key where executes command
	 * @param content
	 *            affinity policy content to check
	 * @return result object
	 * @see Result
	 */
	@POST
	@Path(NodesManagerPaths.CHECK_AFFINITY_POLICY)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAffinityPolicy(@PathParam(NodesManagerPaths.NODEKEY) String key, String content) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// checks if there is a valid content
				if (content == null || content.trim().length() == 0){
					return ResponseBuilder.JSON.noContent();
				}
				// if key or content are missing, bad request
				if (key == null) {
					return ResponseBuilder.JSON.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found, otherwise returns the result
				// of checking
				return (node == null) ? ResponseBuilder.JSON.notFound(key) : ResponseBuilder.JSON.ok(manager.checkAffinityPolicy(node.getNodeInfoBean(), content));
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
	 * REST service which checks affinity file content
	 * 
	 * @param key
	 *            node key where executes command
	 * @param content
	 *            affinity policy content to check
	 * @return result object
	 * @see Result
	 */
	@POST
	@Path(NodesManagerPaths.PUT_AFFINITY_POLICY)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response putAffinityPolicy(@PathParam(NodesManagerPaths.NODEKEY) String key, String content) {
		// it uses PLAIN response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// checks if there is a valid content
				if (content == null || content.trim().length() == 0){
					return ResponseBuilder.PLAIN.noContent();
				}
				// if key or content are missing, bad request
				if (key == null) {
					return ResponseBuilder.PLAIN.badRequest(NodesManagerPaths.NODEKEY);
				}
				// gets node by key
				NodeInfo node = manager.getNodeByKey(key);
				// if node is missing, not found
				if (node == null){
					return ResponseBuilder.PLAIN.notFound(key);
				}
				// gets the current affinity file
				// because we need to maintain the last update attribute
				ConfigurationFile file = manager.getNodeConfigFile(node.getNodeInfoBean(), ConfigKeys.AFFINITY);
				// sets the content
				file.setContent(content);
				//sets type
				file.setType(ConfigKeys.AFFINITY);
				// stores the affinity policy
				ConfigurationFile resultFile = manager.saveNodeConfigFile(node.getNodeInfoBean(), file, ConfigKeys.AFFINITY);
				return (resultFile == null) ?  ResponseBuilder.PLAIN.notFound(key) : ResponseBuilder.PLAIN.ok(resultFile.getContent());
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
	 * Creates a Node info bean starting from a node
	 * 
	 * @param node
	 *            node to clone into a info bean
	 * @return the nod info bean instance
	 */
	private NodeInfoBean createNodeInfoBean(NodeInfo node) {
		NodeInfoBean nodeInfoBean = new NodeInfoBean();
		nodeInfoBean.setKey(node.getKey());
		nodeInfoBean.setLabel(node.getLabel());
		nodeInfoBean.setHostname(node.getHostname());
		nodeInfoBean.setIpaddress(node.getIpaddress());
		if (!node.getJobs().isEmpty()) {
			for (String jobName : node.getJobs().values()) {
				nodeInfoBean.getJobNames().add(jobName);
			}
		}
		nodeInfoBean.setPort(node.getPort());
		nodeInfoBean.setProcessId(node.getProcessId());
		nodeInfoBean.setRmiPort(node.getRmiPort());
		nodeInfoBean.setStatus(node.getStatus().getDescription());
		nodeInfoBean.setExecutionEnvironment(node.getExecutionEnvironment());
		nodeInfoBean.setStartedTime(node.getStartedTime());
		nodeInfoBean.setOperational(node.isOperational());
		nodeInfoBean.setJemVersion(node.getJemVersion());
		nodeInfoBean.setSwarmNode(node.isSwarmNode());
		nodeInfoBean.setType(node.getClass().getName());
		nodeInfoBean.setJavaVendor(node.getJavaVendor());
		nodeInfoBean.setJavaVersion(node.getJavaVersion());
		return nodeInfoBean;
	}

	/*
	 * (non-Javadoc)
	 * 
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
