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
package org.pepstock.jem.gwt.server.security;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;

import com.hazelcast.core.IMap;

/**
 * Is a singleton which can manage the first installation.<rb>
 * During the first installation, only a defined (in SHIRO conf) user, with GRANTOR role, can access
 * having the ROles authorization. This user must define an administrator at least. When he exists,
 * first installation phase ends.  
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class FirstInstallationManager {
	
	private static FirstInstallationManager MANAGER = null;
	
	private FirstInstallationToken token = null;
	
	private boolean isFirstInstallationPhase = true;
	
	/**
	 * @return a manager instance, in signleton mode
	 */
	public static FirstInstallationManager getInstance(){
		if (MANAGER == null){
			MANAGER = new FirstInstallationManager();
		}
		return MANAGER;
	}

	/**
	 * Returns the token used to authenticate during the first installation phase
	 * 
	 * @return the token
	 */
	public FirstInstallationToken getToken() {
		return token;
	}

	/**
	 * Sets the token used to authenticate during the first installation phase
	 * 
	 * @param token the token to set
	 */
	public void setToken(FirstInstallationToken token) {
		this.token = token;
	}

	/**
	 * Returns <code>true</code> if is during the first installation phase
	 * 
	 * @return the isFirstInstallationPhase
	 */
	public boolean isFirstInstallationPhase() {
		return isFirstInstallationPhase;
	}

	/**
	 * Calcualtes if is during the first installation phase.<br>
	 * IF there is any administrator, then there is the first installation phase
	 * 
	 * @param roles JEM map with all roles
	 * 
	 */
	public void setFirstInstallationPhase(IMap<String, Role> roles) {
		Role admin = null;
		try {
			// locks map by key (role name)
			roles.lock(Roles.ADMINISTRATOR_ROLE.getName());
			// gets admin role
			admin = roles.get(Roles.ADMINISTRATOR_ROLE.getName());
		} finally {
			// unlocks the key
			roles.unlock(Roles.ADMINISTRATOR_ROLE.getName());
		}
		// if administrator role has not got any user, then is first installation 
		this.isFirstInstallationPhase = admin.getUsers().isEmpty();
	}

    /**
     * Adds administrator role during the first installation. 
     * If doesn't exist, adds all permission for role.
     * 
     * @param roles JEM map with all roles
     */
    public void addAdministratorIfAbsent(IMap<String, Role> roles){
    	if (roles.containsKey(Roles.ADMINISTRATOR_ROLE.getName())){
    		return;
    	}
    	List<String> perms = new ArrayList<String>();
		// ADMINISTRATOR loading
		perms.add(Permissions.STAR);
		Roles.ADMINISTRATOR_ROLE.setPermissions(perms);
		roles.putIfAbsent(Roles.ADMINISTRATOR_ROLE.getName(), Roles.ADMINISTRATOR_ROLE);
    }

    /**
     * Adds operator role during the first installation. 
     * If doesn't exist, adds all permission for role.
     * 
     * @param roles JEM map with all roles
     */
    public void addAOperatorIfAbsent(IMap<String, Role> roles){
    	if (roles.containsKey(Roles.OPERATOR_ROLE.getName())){
    		return;
    	}
    	List<String> perms = new ArrayList<String>();
		// OPERATOR loading
		perms.add(Permissions.VIEW_INPUT);
		perms.add(Permissions.VIEW_RUNNING);
		perms.add(Permissions.VIEW_ROUTING);
		perms.add(Permissions.VIEW_OUTPUT);
		perms.add(Permissions.VIEW_NODES);
		perms.add(Permissions.JOBS_STAR);
		perms.add(Permissions.NODES_STAR);
		perms.add(Permissions.SWARM_NODES_START);
		perms.add(Permissions.SWARM_NODES_DRAIN);
		perms.add(Permissions.SEARCH_JOBS+Permissions.ALL_BY_REGEX);
		perms.add(Permissions.SEARCH_NODES+Permissions.ALL_BY_REGEX);
		Roles.OPERATOR_ROLE.setPermissions(perms);
		roles.putIfAbsent(Roles.OPERATOR_ROLE.getName(), Roles.OPERATOR_ROLE);
    }
    
    /**
     * Adds developer role during the first installation. 
     * If doesn't exist, adds all permission for role.
     * 
     * @param roles JEM map with all roles
     */
    public void addADeveloperIfAbsent(IMap<String, Role> roles){
    	if (roles.containsKey(Roles.DEVELOPER_ROLE.getName())){
    		return;
    	}
    	List<String> perms = new ArrayList<String>();
		// DEVELOPER loading
		perms.add(Permissions.VIEW_INPUT);
		perms.add(Permissions.VIEW_RUNNING);
		perms.add(Permissions.VIEW_ROUTING);
		perms.add(Permissions.VIEW_OUTPUT);
		Roles.DEVELOPER_ROLE.setPermissions(perms);
		roles.putIfAbsent(Roles.DEVELOPER_ROLE.getName(), Roles.DEVELOPER_ROLE);
    }
    
    /**
     * Adds grantor role during the first installation. 
     * If doesn't exist, adds all permission for role.
     * 
     * @param roles JEM map with all roles
     */
    public void addAGrantorIfAbsent(IMap<String, Role> roles){
    	if (roles.containsKey(Roles.GRANTOR_ROLE.getName())){
    		return;
    	}
    	List<String> perms = new ArrayList<String>();
    	List<String> users = new ArrayList<String>();
    	// GRANTOR loading
    	
    	users.add(token.getUsername());
    	Roles.GRANTOR_ROLE.setUsers(users);

    	perms.add(Permissions.VIEW_ROLES);
    	perms.add(Permissions.ROLES_STAR);
    	Roles.GRANTOR_ROLE.setPermissions(perms);
    	roles.putIfAbsent(Roles.GRANTOR_ROLE.getName(), Roles.GRANTOR_ROLE);
    }

    
    /**
     * Repairs grantor role is it doesn't have all necessary permissions
     * during the first installation phase.
     * 
     * @param roles JEM map with all roles
     */
    public void repairGrantor(IMap<String, Role> roles){
		if (isFirstInstallationPhase){
			Role grantors = roles.get(Roles.GRANTOR_ROLE.getName());
			// adds first token 
			if (!grantors.getUsers().contains(token.getUsername())){
				grantors.getUsers().add(token.getUsername());
			}
			// adds roles permissions
			if (!grantors.getPermissions().contains(Permissions.VIEW_ROLES)){
				grantors.getPermissions().add(Permissions.VIEW_ROLES);
			}
			// adds roles permissions
			if (!grantors.getPermissions().contains(Permissions.ROLES_STAR)){
				grantors.getPermissions().add(Permissions.ROLES_STAR);
			}
			// stores
			roles.replace(Roles.GRANTOR_ROLE.getName(), grantors);					
		}
    }
    
    /**
     * Checks if first installation is ended. If true, cleans up all
     * permissions set to grantor for first installation.
     * 
     * @param roles JEM map with all roles
     */
    public void cleanupGrantor(IMap<String, Role> roles){
    	// sets if is during the first instalation
    	setFirstInstallationPhase(roles);
    	// if not
		if (!isFirstInstallationPhase){
			try {
				// locks map by key of GRANTOR name
				roles.lock(Roles.GRANTOR_ROLE.getName());
				Role grantor = roles.get(Roles.GRANTOR_ROLE.getName());
				// gets grantor role and
				// removes first installation token (only that)
				// this controll is done because the first installation user
				// could add new grantors as well
				List<String> users = new ArrayList<String>();
				for (String userID : grantor.getUsers()){
					if (!userID.equalsIgnoreCase(token.getUsername())){
						users.add(userID);
					}
				}
				grantor.setUsers(users);
				// replace the granto role
				roles.replace(Roles.GRANTOR_ROLE.getName(), grantor);					
			} finally {
				// unlocks always the key
				roles.unlock(Roles.GRANTOR_ROLE.getName());
			}
		}
    }
}