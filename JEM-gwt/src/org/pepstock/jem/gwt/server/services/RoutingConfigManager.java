/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.server.services;

import java.util.Date;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.security.User;

import com.hazelcast.core.IMap;

/**
 * This service manages the routing swarm configuration.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RoutingConfigManager extends DefaultService{

	/**
	 * Returns the swarm configuration. It always exist because it always created after the first startup of JEM. 
	 * It uses a name to use as a key in map, but this key is a constant
	 * 
     * @param name key of configuration object
     * @return swarm configuration item.
	 * @throws ServiceMessageException 
     * @throws Exception if routing configuration doesn't exist
     */
    public SwarmConfiguration getSwarmConfiguration(String name) throws ServiceMessageException  {
		// checks if the user is authorized to get swarm config
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.SWARM_NODES_VIEW_CONFIG));
		IMap<String, SwarmConfiguration> map = getInstance().getMap(Queues.ROUTING_CONFIG_MAP);
		// checks if exist (must be!)
		if (map.containsKey(name)){
			try {
				// locks the key (costant name for map of swarm config)
				map.lock(name);
				// gets  
				return map.get(name);
			} finally {
				// unlocks always the key
				map.unlock(name);
			}
		}
		// if is here, means that
		// the sotrm configuration is not in Hazelcast map
		// and this can not happen
		throw new ServiceMessageException(UserInterfaceMessage.JEMG044E, name);
    }

    /**
     * Updates the swarm configuration.  
     * 
     * @param conf configuration instance to update
     * @return return the new object
     * @throws ServiceMessageException 
     * @throws Exception if routing configuration doesn't exist 
     */
    public SwarmConfiguration updateSwarmConfiguration(SwarmConfiguration conf) throws ServiceMessageException {
		// checks if the user is authorized to update swarm config
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.SWARM_NODES_EDIT_CONFIG));
    	
		IMap<String, SwarmConfiguration> map = getInstance().getMap(Queues.ROUTING_CONFIG_MAP);
		// checks if the conf exist (must be!)
		if (map.containsKey(conf.getName())){
			try {
				// locks the key (costant name for map of swarm config)
				map.lock(conf.getName());
				// gets configuration object
				SwarmConfiguration oldConf = map.get(conf.getName());
				// gets old object and checks user
				// this is necessary to check if the new object 
				// is the same and none has updated it in the meantime
				if (oldConf.getUser() != null && !oldConf.getUser().equalsIgnoreCase(conf.getUser())){
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG047E, oldConf, conf);
					throw new ServiceMessageException(UserInterfaceMessage.JEMG047E, oldConf, conf);
				}
				// checks last modified
				// this is necessary to check if the new object 
				// is the same and none has updated it in the meantime
				if (oldConf.getLastModified() != null && !oldConf.getLastModified().equals(conf.getLastModified())){
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG047E, oldConf, conf);
					throw new ServiceMessageException(UserInterfaceMessage.JEMG047E, oldConf, conf);
				}
				
				// here the update is consistent so
				// gets user info and time storing that on
				// object
				Subject currentUser = SecurityUtils.getSubject();
				User userPrincipal = (User)currentUser.getPrincipal();
				String userId = userPrincipal.getId();
				conf.setUser(userId);
				conf.setLastModified(new Date());
				
				// replaces on map
				map.put(conf.getName(), conf);
		    	return conf;
			} finally {
				// unlocks always the key 
				map.unlock(conf.getName());
			}
		}
		// if is here, means that
		// the swarm configuration is not in Hazelcast map
		// and this can not happen		
		throw new ServiceMessageException(UserInterfaceMessage.JEMG044E, conf.getName());
    }

}