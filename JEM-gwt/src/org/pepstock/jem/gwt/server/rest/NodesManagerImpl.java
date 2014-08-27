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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.server.services.NodesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.rest.entities.BooleanReturnedObject;
import org.pepstock.jem.rest.entities.ConfigurationFileContent;
import org.pepstock.jem.rest.entities.Nodes;
import org.pepstock.jem.rest.entities.StringReturnedObject;
import org.pepstock.jem.rest.paths.NodesManagerPaths;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Path(NodesManagerPaths.MAIN)
public class NodesManagerImpl extends DefaultServerResource {

	private NodesManager nodesManager = null;
	
	
	/**
	 * REST service which returns nodes, by nodes name filter
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.LIST)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Nodes getNodes(String nodesFilter) throws JemException {
		Nodes nodes = new Nodes();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            nodes.setNodes(nodesManager.getNodes(nodesFilter));
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
	 * REST service which returns swarm nodes, by swarn nodes name filter
	 * 
	 * @param nodesFilter nodes name filter
	 * @return a nodes container
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.SWARM_LIST)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Nodes getSwarmNodes(String nodesFilter) throws JemException {
		Nodes nodes = new Nodes();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            nodes.setNodes(nodesManager.getSwarmNodes(nodesFilter));
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
	@Path(NodesManagerPaths.LIST_BY_FILTER)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Nodes getNodesByFilter(String nodesFilter) throws JemException {
		Nodes nodes = new Nodes();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            nodes.setNodes(nodesManager.getNodesByFilter(nodesFilter));
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
	@POST
	@Path(NodesManagerPaths.UPDATE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public BooleanReturnedObject update(NodeInfoBean node) throws JemException {
		BooleanReturnedObject nodes = new BooleanReturnedObject();
		nodes.setValue(false);
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            nodes.setValue(nodesManager.update(node));
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
	 * REST service which return the result of TOP command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.TOP)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public StringReturnedObject top(NodeInfoBean node) throws JemException {
		StringReturnedObject nodes = new StringReturnedObject();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            String value = nodesManager.top(node);
	            nodes.setValue(value);
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
	 * REST service which return the result of LOG command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.LOG)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public StringReturnedObject log(NodeInfoBean node) throws JemException {
		StringReturnedObject nodes = new StringReturnedObject();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            String value = nodesManager.log(node);
	            nodes.setValue(value);
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
	 * REST service which return the result of DISPLAY CLUSTER command
	 * 
	 * @param node node where executes command
	 * @return returned object with value
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.DISPLAY_CLUSTER)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public StringReturnedObject displayCluster(NodeInfoBean node) throws JemException {
		StringReturnedObject nodes = new StringReturnedObject();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			try {
	            String value = nodesManager.displayCluster(node);
	            nodes.setValue(value);
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
	 * REST service which return the configuration file of node
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.GET_NODE_CONFIG_FILE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ConfigurationFileContent getNodeConfigFile(ConfigurationFileContent content) throws JemException {
		ConfigurationFileContent nodes = new ConfigurationFileContent();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			if (content.getNode() != null && content.getWhat() != null){
				try {
					ConfigurationFile file = nodesManager.getNodeConfigFile(content.getNode(), content.getWhat());
					nodes.setFile(file);
					nodes.setNode(content.getNode());
					nodes.setWhat(content.getWhat());
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					nodes.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(nodes);
		}
		return nodes;
	}
	
	/**
	 * REST service which save a new configuration file of node
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.SAVE_NODE_CONFIG_FILE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ConfigurationFileContent saveNodeConfigFile(ConfigurationFileContent content) throws JemException {
		ConfigurationFileContent nodes = new ConfigurationFileContent();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			if (content.getNode() != null && content.getWhat() != null && content.getFile() != null){
				try {
					ConfigurationFile file = nodesManager.saveNodeConfigFile(content.getNode(), content.getFile(), content.getWhat());
					nodes.setFile(file);
					nodes.setNode(content.getNode());
					nodes.setWhat(content.getWhat());
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					nodes.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(nodes);
		}
		return nodes;
	}
	
	
	/**
	 * REST service which return the configuration file of JEM environment
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.GET_ENV_CONFIG_FILE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ConfigurationFileContent getEnvConfigFile(ConfigurationFileContent content) throws JemException {
		ConfigurationFileContent nodes = new ConfigurationFileContent();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			if (content.getWhat() != null){
				try {
					ConfigurationFile file = nodesManager.getEnvConfigFile(content.getWhat());
					nodes.setFile(file);
					nodes.setWhat(content.getWhat());
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					nodes.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(nodes);
		}
		return nodes;
	}
	
	/**
	 * REST service which save a new configuration file of JEM environment
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.SAVE_ENV_CONFIG_FILE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ConfigurationFileContent saveEnvConfigFile(ConfigurationFileContent content) throws JemException {
		ConfigurationFileContent nodes = new ConfigurationFileContent();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			if (content.getWhat() != null && content.getFile() != null){
				try {
					ConfigurationFile file = nodesManager.saveEnvConfigFile(content.getFile(), content.getWhat());
					nodes.setFile(file);
					nodes.setWhat(content.getWhat());
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					nodes.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(nodes);
		}
		return nodes;
	}
	
	/**
	 * REST service which checks a configuration file content
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.CHECK_CONFIG_FILE)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public BooleanReturnedObject checkConfigFile(ConfigurationFileContent content) throws JemException {
		BooleanReturnedObject result = new BooleanReturnedObject();
		result.setValue(false);
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			if (content.getWhat() != null && content.getContent() != null){
				try {
					Boolean isCorrect = nodesManager.checkConfigFile(content.getContent(), content.getWhat());
					result.setValue(isCorrect);
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
	 * REST service which checks affinity file content
	 * 
	 * @param content container with all parms to perform call
	 * @return configuration file
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(NodesManagerPaths.CHECK_AFFINITY_POLICY)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ConfigurationFileContent checkAffinityPolicy(ConfigurationFileContent content) throws JemException {
		ConfigurationFileContent result = new ConfigurationFileContent();
		if (isEnable()){
			if (nodesManager == null){
				initManager();
			}
			if (content.getNode() != null && content.getContent() != null){
				try {
					Result checkedResult = nodesManager.checkAffinityPolicy(content.getNode(), content.getContent());
					result.setResult(checkedResult);
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
		if (nodesManager == null) {
			nodesManager = new NodesManager();
		}
	}
}
