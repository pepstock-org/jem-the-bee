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
package org.pepstock.jem.gwt.server.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.security.FirstInstallationManager;
import org.pepstock.jem.gwt.server.security.FirstInstallationToken;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.OrganizationalUnit;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.node.security.User;
import org.pepstock.jem.node.security.UserPreference;

import com.hazelcast.core.IMap;

/**
 * This service manages all aspects about login and log-off of users, preparing 
 * the profile necessary to client to authorize actions on UI.
 *   
 * @author Andrea "Stock" Stocchero
 * 
 */
public class LoginManager extends DefaultService {

	/**
	 * Key used to store logged user into HTTP session
	 */
	public static final String USER_KEY = "JEM_USER";
	
	/**
	 * At the first call of constructor, it initializes 
	 * all roles, checking if it's at first installation.<br>
	 * Is in FIRST installation phase if there is not any administrator.
	 * 
	 * @throws Exception 
	 * 
	 */
	public LoginManager() {
		super();
		initRoles();
	}

	/**
	 * Returns the logged user user if
	 * the current user is already authenticated.<br>
	 * if not, returns null.<br>
	 * This method is necessary to understand client side
	 * if a login must be done or is already done. 
	 * 
	 * @return logged user or null, if is not logged in.
	 */
	public LoggedUser getUser() {
		// get the currently executing user:
		Subject currentUser = SecurityUtils.getSubject();

		LoggedUser user = null;
		// if is authenticated, try to return the logged user
		if (currentUser.isAuthenticated()) {
			// goes to HTTP session to return the user
			// previously saved
			Session session = currentUser.getSession();
			Object object = session.getAttribute(USER_KEY);
			if (object instanceof LoggedUser) {
				user = (LoggedUser) object;
				user.setPreferences(getUserPreferences(user.getId()));
				// stores logged user in HTTP session
				session.setAttribute(USER_KEY, user);

				// load all permission based on user roles
				loadAllAuthorizations(user);

				// for administrator role, there is a specific call
				// to add if is administrator or not
				user.addAuthorized(Roles.ADMINISTRATOR, currentUser.hasRole(Roles.ADMINISTRATOR));

				user.setPreferences(getUserPreferences(user.getId()));
			}
		}
		return user;
	}

	/**
	 * Performs LOGIN in to JEM, by a userid and password.<br>
	 * After login, loads the profile with all authorizations 
	 * based on roles of user.<br>
	 * If is in first installation, checks the right token.
	 * 
	 * @param userid user id of client
	 * @param password password of client
	 * @return logged user with all authorizations
	 * @throws ServiceMessageException if any exception occurs 
	 */
	public LoggedUser login(String userid, String password) throws ServiceMessageException {
		// gets first installation manager
		FirstInstallationManager fManager = FirstInstallationManager.getInstance();
		
		// get the currently executing user
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = null;

		// is FIRST Installation?
		if (fManager.isFirstInstallationPhase()){
			// checks the token, if the userid is the same
			// used for the first installation phase
			// this first installation user is set in SHIRO configuration.
			token = fManager.getToken();
			if (!userid.equalsIgnoreCase(token.getUsername())){
				// is not the first installation user, throws an exception
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG010E,  userid);
				throw new ServiceMessageException(UserInterfaceMessage.JEMG010E, userid);
			}
			// sets password
			token.setPassword(password.toCharArray());
		} else {
			// this is normal token creation, not first installation 
			token = new UsernamePasswordToken(userid, password);
			token.setRememberMe(true);
		}

		// checks authentication
		try {
			currentUser.login(token);
		} catch (UnknownAccountException uae) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG011E,  token.getPrincipal().toString());
			throw new ServiceMessageException(UserInterfaceMessage.JEMG012E, uae);
		} catch (IncorrectCredentialsException ice) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG013E,  token.getPrincipal().toString());
			throw new ServiceMessageException(UserInterfaceMessage.JEMG012E, ice);
		} catch (LockedAccountException lae) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG014E,  token.getPrincipal().toString());
			throw new ServiceMessageException(UserInterfaceMessage.JEMG012E, lae);
		} catch (AuthenticationException ae) {
			// ... catch more exceptions here (maybe custom ones specific to your
			// application?
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG015E, ae, token.getPrincipal().toString(), ae.getMessage());
			throw new ServiceMessageException(UserInterfaceMessage.JEMG012E, ae);
		}

		// gets proncipal, load by authentication engine of Shiro
		// extracts userid, user name, organizational unit and name
		User userPrincipal = (User)currentUser.getPrincipal();
		String userId = userPrincipal.getId();
		String userName = userPrincipal.getName();
		String ouId = userPrincipal.getOrgUnitId();
		String ouName = userPrincipal.getOrgUnitName();
		
		// sets user and org unit attributes
		LoggedUser user = new LoggedUser();
		user.setId(userId);
		if (userName != null){
			user.setName(userName);
		} else {
			user.setName(userId);
		}
		
		OrganizationalUnit ou = new OrganizationalUnit();
		ou.setId(ouId);
		if (ouName != null){
			ou.setName(ouName);
		} else {
			ou.setName(ouId);
		}
		user.setOrganizationalUnit(ou);

		// stores logged user in HTTP session
		Session shiroSession = currentUser.getSession();
		shiroSession.setAttribute(USER_KEY, user);

		// load all permission based on user roles
		loadAllAuthorizations(user);

		// for administrator role, there is a specific call
		// to add if is administrator or not
		user.addAuthorized(Roles.ADMINISTRATOR, currentUser.hasRole(Roles.ADMINISTRATOR));
		
		user.setPreferences(getUserPreferences(userId));
		
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG016I, user.toString());
		return user;
	}

	/**
	 * Load all used permissions 
	 * @param user
	 */
	private static void loadAllAuthorizations(LoggedUser user) {
		loadAuthorization(Permissions.VIEW_ALL, user);
		loadAuthorization(Permissions.JOBS_ALL, user);
		loadAuthorization(Permissions.NODES_ALL, user);
		loadAuthorization(Permissions.SWARM_NODES_ALL, user);
		loadAuthorization(Permissions.ROLES_ALL, user);
		loadAuthorization(Permissions.CERTIFICATES_ALL, user);
		loadAuthorization(Permissions.RESOURCES_ALL, user);
		loadAuthorization(Permissions.ADMINISTRATION_ITEMS_ALL, user);
		loadAuthorization(Permissions.ADMINISTRATION_ALL, user);
		loadAuthorization(Permissions.GFS_ALL, user);
	}
	
	/**
	 * Loads the permissions (by boolean value) to user.
	 * This permissions will be used on UI.
	 * 
	 * @param permissions arrays of permissions by domain
	 * @param user user used to add permissions
	 */
	private static void loadAuthorization(String[] permissions, LoggedUser user){
		Subject currentUser = SecurityUtils.getSubject();
		// checks if user has got permissions
		boolean[] b = currentUser.isPermitted(permissions);
		// scans and adds values
		for (int i=0; i<permissions.length; i++){
			user.addAuthorized(permissions[i], b[i]);
		}
	}

	/**
	 * Returns the user preferences by a userid
	 * @param userId key to use to get user preferences
	 * @return a map with all user preferences
	 */
	private Map<String, UserPreference> getUserPreferences(String userId){
		try {
	        IMap<String, Map<String, UserPreference>> map = getInstance().getMap(Queues.USER_PREFERENCES_MAP);
	        try {
	        	map.lock(userId);
	        	if (map.containsKey(userId)){
	        		return map.get(userId);
	        	} else {
	        		return new HashMap<String, UserPreference>();
	        	}
	        } finally {
	        	map.unlock(userId);
	        }
        } catch (Exception e) {
        	LogAppl.getInstance().debug(e.getMessage(), e);
        	return new HashMap<String, UserPreference>();
        }
	}
	
	/**
	 * Logoff without saving user preferences
	 * @return always true
	 */
	public Boolean logoff() {
		return logoff(null);
	}
	
	/**
	 * Performs the log off of current user. If is a first installation, 
	 * checks if can clean up the phase, otherwise maintains the first
	 * installation phase.
	 * 
	 * @param preferences preferences to store
	 * @return Always true
	 */
	public Boolean logoff(Map<String, UserPreference> preferences) {
		if (preferences != null){
			try {
	            storePreferences(preferences);
            } catch (Exception e) {
            	LogAppl.getInstance().debug(e.getMessage(), e);
            }
		}
		// gets the first installation manager
		FirstInstallationManager fManager = FirstInstallationManager.getInstance();
		// get the currently executing user:
		Subject currentUser = SecurityUtils.getSubject();
		// if there's the user
		if (currentUser != null) {
			Session shiroSession = currentUser.getSession();
			// gets user from HTTP session
			// and logs out by SHIRO
			LoggedUser user = (LoggedUser)shiroSession.getAttribute(USER_KEY);
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG017I, (user != null) ? user.toString() : currentUser.toString());
			currentUser.logout();
			
			// if is first installation and the user is the same of first installation token
			FirstInstallationToken token = fManager.getToken();
			if (fManager.isFirstInstallationPhase() && user.getId().equalsIgnoreCase(token.getUsername())){
				IMap<String, Role> roles = getInstance().getMap(Queues.ROLES_MAP);
				// remove the grantor authorization to the first installation user id
				fManager.cleanupGrantor(roles);
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Stores the user preferences for current user
	 * @param preferences map with all preferences
	 * @return Always <code>true</code>
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Boolean storePreferences(Map<String, UserPreference> preferences) throws ServiceMessageException {
		// get the currently executing user:
		Subject currentUser = SecurityUtils.getSubject();
		
		if (!currentUser.isAuthenticated()) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG011E,  "N/A");
			throw new ServiceMessageException(UserInterfaceMessage.JEMG012E);
		}
		
		User userPrincipal = (User)currentUser.getPrincipal();
		String userId = userPrincipal.getId();
		
		IMap<String, Map<String, UserPreference>> map = getInstance().getMap(Queues.USER_PREFERENCES_MAP);
		try {
			map.lock(userId);
			if (map.containsKey(userId)){
				map.replace(userId, preferences);
			} else {
				map.put(userId, preferences);
			}
		} finally {
			map.unlock(userId);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * Initializes roles, checking if is a first installation.<br>
	 * Tries also to repair the out-of-the-box configuration
	 * if is not compliant with what is expected
	 */
    private void initRoles() {
    	// gets the first installation manager
    	FirstInstallationManager fManager = FirstInstallationManager.getInstance();
    	// gets the first installation defined token an display it
    	FirstInstallationToken token = fManager.getToken();
    	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG036I, token.getUsername());

		IMap<String, Role> roles = getInstance().getMap(Queues.ROLES_MAP);
		// try to repair because
		// not all ootb roles are in the systems
		try {
			fManager.addAdministratorIfAbsent(roles);
			fManager.addAOperatorIfAbsent(roles);
			fManager.addADeveloperIfAbsent(roles);
			fManager.addAGrantorIfAbsent(roles);
			
			// checks and sets if is a first installation
			fManager.setFirstInstallationPhase(roles);
			
			// grantor is important because in first installation
			// ONLY the grantor che login in and remove the first
			// installation adding a new administrator
			fManager.repairGrantor(roles);
			
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG032E, e);
		}
		
		// final check if is first installation and display it
		if (fManager.isFirstInstallationPhase()){
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG019W);
		}
    }

}
