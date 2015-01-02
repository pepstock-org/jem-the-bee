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
package org.pepstock.jem.node.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.node.UpdateableItem;

/**
 * Bean which contains all configuration of SWARM.
 * <br>
 * This bean is stored in a Hazelcast map with a constant key {@link SwarmConfiguration#DEFAULT_NAME}.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 */
public class SwarmConfiguration extends UpdateableItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Property value of default Hazelcast password, used for SWARM
	 */
	public static final String DEFAULT_PASSWORD = "jem_password";

	/**
	 * Property key used to store the object on Hazelcast map and on database
	 */
	public static final String DEFAULT_NAME = "ROUTING_CONFIGURATION";

	/**
	 * Property value of default Hazelcast group name, used for SWARM
	 */
	public static final String DEFAULT_GROUP_NAME = "SwarmGroup";

	/**
	 * Property value of default Hazelcast multicast port, used for SWARM
	 */
	public static final int DEFAULT_GROUP_PORT = 6510;
	
	private boolean enabled = false;
	
	private String groupName = DEFAULT_GROUP_NAME;
	
	private String groupPassword = DEFAULT_PASSWORD;
	
	private int port = DEFAULT_GROUP_PORT;

	private String networkInterface = null;
	
	private List<String> networks = new ArrayList<String>();
	
	/**
	 * Empty constructor, which sets ALWAYS the name of the key of HC map
	 */
	public SwarmConfiguration() {
		super.setName(DEFAULT_NAME);
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the groupPassword
	 */
	public String getGroupPassword() {
		return groupPassword;
	}

	/**
	 * @param groupPassword the groupPassword to set
	 */
	public void setGroupPassword(String groupPassword) {
		this.groupPassword = groupPassword;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the list of the network related to the Swarm
	 */
	public List<String> getNetworks() {
		return this.networks;
	}
	
	/**
	 * @param networks the users to set
	 */
	public void setNetworks(List<String> networks) {
		this.networks = networks;
	}

	/**
	 * @return the networkInterface
	 */
	public String getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * @param networkInterface the networkInterface to set
	 */
	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SwarmConfiguration [name=" + getName() + ", enabled=" + enabled + ", groupName=" + groupName + ", groupPassword=" + groupPassword + ", port=" + port + ", networkInterface=" + networkInterface + ", user=" + getUser() + ", lastModified="
				+ getLastModified() + ", networks=" + networks + "]";
	}
}