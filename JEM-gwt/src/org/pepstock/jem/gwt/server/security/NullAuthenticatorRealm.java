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
package org.pepstock.jem.gwt.server.security;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.PrincipalAttribute;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.node.security.User;

/**
 * Is authorization realm which allows to access to JEM with a specific users (defined in SHIRO configuration
 * file) without password.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NullAuthenticatorRealm extends AuthorizingRealm {

	private Authorizator authorizator = null;

	private String users = null;

	private final Properties properties = new Properties();

	private static final MessageFormat USER_FORMAT = new MessageFormat("{0}[{1}]");

	/**
	 * All credentials are OK and initializes the authorizator that uses JEM roles   
	 */
	public NullAuthenticatorRealm() {
		this(new AllowAllCredentialsMatcher());
		authorizator = new Authorizator();
	}

	/**
	 * @param cacheManager cache manager to speed up the authorization search 
	 */
	public NullAuthenticatorRealm(CacheManager cacheManager) {
		this(cacheManager, new AllowAllCredentialsMatcher());
	}

	/**
	 * @param matcher uses "allow all credentials
	 */
	public NullAuthenticatorRealm(CredentialsMatcher matcher) {
		super(matcher);
	}

	/**
	 * @param cacheManager cache manager to speed up the authorization search 
	 * @param matcher uses "allow all credentials
	 */
	public NullAuthenticatorRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
		super(cacheManager, matcher);
	}

	/**
	 * @return the users
	 */
	public String getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(String users) {
		this.users = users;
	}

	/**
	 * Loads users information from SHIRO file.
	 * Users are defined in list comma separated.<br>
	 * Inside of squared brackets there is the org unit name.<br>
	 * Format is:<br>
	 * <code>user[organizational-unit]</code>
	 * 
	 */
	@Override
	protected void onInit() {
		super.onInit();
		if (users != null) {
			// is comma separated
			String[] usersArray = users.split(",");
			for (int i = 0; i < usersArray.length; i++) {
				try {
					// reads information by format
					Object[] userAndGroup = USER_FORMAT.parse(usersArray[i]);
					properties.setProperty(userAndGroup[0].toString().trim(), userAndGroup[1].toString().trim());
				} catch (ParseException e) {
					LogAppl.getInstance().debug(e.getMessage(), e);
					properties.setProperty(usersArray[i].trim(), usersArray[i].trim());
				}
			}
		}
		// for this realm, it user "grantor" with any password (not null) as first installation token
		FirstInstallationManager manager = FirstInstallationManager.getInstance();
		manager.setToken(new FirstInstallationToken(Roles.DEFAULT_GRANTOR_ID, (String) null));
	}

	/*
	 * @see
	 * org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org
	 * .apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		if (token instanceof FirstInstallationToken) {
			FirstInstallationToken upToken = (FirstInstallationToken) token;
			// Creates a user object
			User user = new User(upToken.getUsername());
			// creates account
			return new SimpleAccount(user, "nothing", getName());
		}

		// creates a normal token
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		// if userid is not on list of SHIRO conf, throws an excpetion
		if (!properties.containsKey(upToken.getUsername())) {
			throw new UnknownAccountException(upToken.getUsername() + " is unknown");
		}
		// load account using information of SHIRo config file
		List<PrincipalAttribute> principals = new ArrayList<PrincipalAttribute>();
		PrincipalAttribute group = new PrincipalAttribute();
		group.setName("organizationalUnit");
		group.setValue(properties.getProperty(upToken.getUsername()));
		principals.add(group);
		User user = new User(upToken.getUsername());
		user.setAttributes(principals);
		user.setOrgUnitId(properties.getProperty(upToken.getUsername()));
		return new SimpleAccount(user, token.getCredentials(), getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache
	 * .shiro.subject.PrincipalCollection)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		try {
			return authorizator.doGetAuthorizationInfo(this, principals);
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
	}

}