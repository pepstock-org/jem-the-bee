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
package org.pepstock.jem.gwt.server.security;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.PrincipalAttribute;
import org.pepstock.jem.node.security.User;

/**
 * Extension of SHIRO JNDI-LDAP realm, to configure all attributes LDAP to load in principal. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ExtendedJndiLdapRealm extends JndiLdapRealm {

	private static final String UID = "uid"; 
	
	private Authorizator authorizator = null;
	
	private SearchControls ctls = null;

	private Hashtable<String, String> principalEnvironment = null;

	private String[] attributes = null;
	
	private String orgUnitIdAttribute = null;

	private String orgUnitNameAttribute = null;
	
	private String userNameAttribute = null;
	
	private String firstInstallationUserid = null;
	
	/**
	 * Creates the search controls and authorization of JEM 
	 */
	public ExtendedJndiLdapRealm() {
		super();
		ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		authorizator = new Authorizator();
	}

	/**
	 * @return the orgUnitIdAttribute
	 */
	public String getOrgUnitIdAttribute() {
		return orgUnitIdAttribute;
	}



	/**
	 * @param orgUnitIdAttribute the orgUnitIdAttribute to set
	 */
	public void setOrgUnitIdAttribute(String orgUnitIdAttribute) {
		this.orgUnitIdAttribute = orgUnitIdAttribute;
	}



	/**
	 * @return the orgUnitNameAttribute
	 */
	public String getOrgUnitNameAttribute() {
		return orgUnitNameAttribute;
	}



	/**
	 * @param orgUnitNameAttribute the orgUnitNameAttribute to set
	 */
	public void setOrgUnitNameAttribute(String orgUnitNameAttribute) {
		this.orgUnitNameAttribute = orgUnitNameAttribute;
	}



	/**
	 * @return the userNameAttribute
	 */
	public String getUserNameAttribute() {
		return userNameAttribute;
	}



	/**
	 * @param userNameAttribute the userNameAttribute to set
	 */
	public void setUserNameAttribute(String userNameAttribute) {
		this.userNameAttribute = userNameAttribute;
	}



	/**
	 * @return the attributes
	 */
	public String[] getAttributes() {
		return attributes.clone();
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String[] attributes) {
		this.attributes = attributes.clone();
	}

	/**
	 * @return system prorities, helpful when you have to configure trustSSL or something like that
	 */
	public Properties getSystemProperties(){
		return System.getProperties();
	}
	
	/**
	 * @return the firstInstallationUserid
	 */
	public String getFirstInstallationUserid() {
		return firstInstallationUserid;
	}



	/**
	 * @param firstInstallationUserid the firstInstallationUserid to set
	 */
	public void setFirstInstallationUserid(String firstInstallationUserid) {
		this.firstInstallationUserid = firstInstallationUserid;
	}

	/**
	 * 
	 */
	@Override
	protected void onInit() {
		super.onInit();
		FirstInstallationManager manager = FirstInstallationManager.getInstance();
		manager.setToken(new FirstInstallationToken(getFirstInstallationUserid(), (String)null));
	}

	/**
	 * Performs the authorization by LDAP.
	 */
	@SuppressWarnings("unchecked")
    @Override
	protected AuthenticationInfo createAuthenticationInfo(AuthenticationToken token, Object ldapPrincipal, Object ldapCredentials, LdapContext ldapContext) throws NamingException {
		if (token instanceof FirstInstallationToken){
			FirstInstallationToken upToken = (FirstInstallationToken) token;
			// Creates a user object
			User user = new User(upToken.getUsername());
			// creates account
			return new SimpleAccount(user, ldapCredentials, getName());
		}
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		Collection<PrincipalAttribute> principals = null;
		try {
			// if environment null, uses the ldap context already prepared
			// this part is necessary to load attribtues from LDAP
			if (principalEnvironment == null) {
				LdapContext context = super.getContextFactory().getSystemLdapContext();
				Hashtable<String, String> currentEnvironment = (Hashtable<String, String>) context.getEnvironment();
				principalEnvironment = (Hashtable<String, String>) currentEnvironment.clone();
				// no authentication
				principalEnvironment.put(InitialDirContext.SECURITY_AUTHENTICATION, "none");
				// searchs attributes
			}
			principals = search(upToken.getUsername(), principalEnvironment);
		} catch (NamingException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG031E, e, upToken.getUsername());
		}
		// Creates a user object
		User user = new User(upToken.getUsername());
		// sets attribtues
		user.setAttributes(principals);
		if (principals != null){
			for (PrincipalAttribute pa : principals){
				if (orgUnitIdAttribute != null && pa.getName().equalsIgnoreCase(orgUnitIdAttribute)){
					user.setOrgUnitId(pa.getValue().toString());
				}
				if (orgUnitNameAttribute != null && pa.getName().equalsIgnoreCase(orgUnitNameAttribute)){
					user.setOrgUnitName(pa.getValue().toString());
				}
				if (userNameAttribute != null && pa.getName().equalsIgnoreCase(userNameAttribute)){
					user.setName(pa.getValue().toString());
				}
			}
		}
		// creates account
		return new SimpleAccount(user, token.getCredentials(), getName());
	}

	/**
	 * Use Authorizator class, for JEM authorization
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		try {
	        return authorizator.doGetAuthorizationInfo(this, principals);
        } catch (Exception e) {
	        throw new AuthenticationException(e.getMessage(), e);
        }
	}

	/**
	 * Extract from LDAP all configured attributes.
	 * 
	 * @param id user id
	 * @param environment LDAP environment 
	 * @return list of principal attributes
	 */
    public List<PrincipalAttribute> search(String id, Hashtable<String, String> environment) {
		// checks if attributes are set
		if (attributes != null && attributes.length > 0) {
			ctls.setReturningAttributes(attributes);
		}
		// if no attributes, uses UID by default
		if (ctls.getReturningAttributes() == null){
			ctls.setReturningAttributes(new String[]{UID});
		}

		// uses useDN for searching
		String userDn = super.getUserDnTemplate();
		String ldapUserContext = StringUtils.substringAfter(userDn, ",");
		try {
			// gets initial context
			InitialDirContext ctx = new InitialDirContext(environment);
			
			// creates search string
			String filter = MessageFormat.format("(uid={0})", new Object[] { id });
			// searchs!
			Object obj = ctx.search(ldapUserContext, filter, ctls);
			// scans all attributes and load into a Principal Attribute
			@SuppressWarnings("rawtypes")
			NamingEnumeration userEnum = (NamingEnumeration) obj;
			if (userEnum != null && userEnum.hasMore()) {
				SearchResult result = (SearchResult) userEnum.next();
				return loadAttributes(id, result.getAttributes());
			}
		} catch (NamingException ne) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG031E, ne, id);
		}
		return new ArrayList<PrincipalAttribute>();
	}

	/**
	 * Load all LDAP attributes in principal attributes and stores all in a list.
	 * 
	 * @param attributes LDAP attributes
	 * @return list of principal attribtues
	 */
	@SuppressWarnings("rawtypes")
    private List<PrincipalAttribute> loadAttributes(String id, Attributes attributes) {
		// creates a list
		List<PrincipalAttribute> list = new ArrayList<PrincipalAttribute>();
		if (attributes != null) {
			try {
				// scans LDAP attributes 
				for (NamingEnumeration attrEnum = attributes.getAll(); attrEnum.hasMore();) {
					Attribute attribute = (Attribute) attrEnum.next();
					// creates all principal attributes
					for (NamingEnumeration valueEnum = attribute.getAll(); valueEnum.hasMore();) {
						PrincipalAttribute attr = new PrincipalAttribute();
						attr.setName(attribute.getID());
						attr.setValue(valueEnum.next());
						list.add(attr);
					}
				}
			} catch (NamingException e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG031E, e, id);
			}
		}
		return list;
	}

}