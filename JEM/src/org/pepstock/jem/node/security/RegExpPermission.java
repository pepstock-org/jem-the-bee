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
package org.pepstock.jem.node.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.util.CollectionUtils;
import org.pepstock.jem.node.NodeMessage;

/**
 * This is an specific implementation of SHIRO permission to use regular expression
 * instead of normal string, to check the access to any resource of JEM.<br>
 * This is very helpful for some specific permission, like files or resources ones, where the grantor of JEM
 * can set the permission using paths of resources or files names.
 * 
 * @see java.util.regex.Pattern
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class RegExpPermission implements Permission, Serializable {

	private static final long serialVersionUID = 1L;

	protected static final String PART_DIVIDER_TOKEN = Permissions.PERMISSION_SEPARATOR;
	protected static final String SUBPART_DIVIDER_TOKEN = ",";
	protected static final boolean DEFAULT_CASE_SENSITIVE = false;

	private List<Set<Pattern>> parts;

	private String permissionPattern = null;

	private boolean caseSensitive = DEFAULT_CASE_SENSITIVE;

	/**
	 * Empty constructor, used to be serialized
	 * to the user interface
	 */
	public RegExpPermission() {
	}

	/**
	 * Creates a permission with a regular expression, case sensitive
	 * @param permissionPattern regular expression string
	 */
	public RegExpPermission(String permissionPattern) {
		this(permissionPattern, DEFAULT_CASE_SENSITIVE);
	}

	/**
	 * Creates a permission with a regular expression and sets if the matching must be
	 * done using case sensitive or not.
	 * 
	 * @param permissionPattern regular expression string
	 * @param caseSensitive if <code>true</code> uses a case sensitive match
	 */
	public RegExpPermission(String permissionPattern, boolean caseSensitive) {
		this.permissionPattern = permissionPattern;
		this.caseSensitive = caseSensitive;
		load();
	}

	/**
	 * @return the permissionPattern
	 */
	public String getPermissionPattern() {
		return permissionPattern;
	}

	/**
	 * @param permissionPattern the permissionPattern to set
	 */
	public void setPermissionPattern(String permissionPattern) {
		this.permissionPattern = permissionPattern;
	}

	/**
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive the caseSensitive to set
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Parses permission string, creating the right patterns  
	 */
	private void load() {
		// if string permission if not valid, throw an exception
		if (permissionPattern == null || permissionPattern.trim().length() == 0) {
			throw new IllegalArgumentException(NodeMessage.JEMC129E.toMessage().getMessage());
		}

		// removes blanks
		permissionPattern = permissionPattern.trim();
		
		// parses main parts
		// splits permission string using the part divider
		List<String> currParts = CollectionUtils.asList(permissionPattern.split(PART_DIVIDER_TOKEN));
		this.parts = new ArrayList<Set<Pattern>>();
		
		// scans all parts
		for (String part : currParts) {
			// loads sub parts of permission
			Set<String> subparts = CollectionUtils.asSet(part.split(SUBPART_DIVIDER_TOKEN));
			if (subparts.isEmpty()) {
				throw new IllegalArgumentException(NodeMessage.JEMC130E.toMessage().getMessage());
			}
			Set<Pattern> patternSubparts = new LinkedHashSet<Pattern>(subparts.size());
			// scans all subparts
			for (String subpart: subparts){
				// creates patterns
				Pattern pattern;
				if (caseSensitive){
					pattern = Pattern.compile(subpart.trim());
				} else { 
					pattern = Pattern.compile(subpart.trim(), Pattern.CASE_INSENSITIVE);
				}
				// adds patterns
				patternSubparts.add(pattern);
			}
			this.parts.add(patternSubparts);
		}
		// if not parts, the permission string syntax is wrong
		if (this.parts.isEmpty()) {
			throw new IllegalArgumentException(NodeMessage.JEMC131E.toMessage().getMessage());
		}
	}
	
	/**
	 * Returns the list of patterns
	 * @return the list of patterns
	 */
	private List<Set<Pattern>> getParts() {
		return this.parts;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.shiro.authz.Permission#implies(org.apache.shiro.authz.Permission)
	 */
	@Override
	public boolean implies(Permission p) {
		// By default only supports comparisons with StringPermissions
		StringPermission stringPermission;
		if (p instanceof StringPermission){
			stringPermission = (StringPermission) p;
		} else {
			return false;
		}
		// gets permission parts from string permission
		List<Set<String>> otherParts = stringPermission.getParts();

		int i = 0;
		for (Set<String> otherPart : otherParts) {
			// If this permission has less parts than the other permission,
			// everything after the number of parts contained
			// in this permission is automatically implied, so return true
			if (getParts().size() - 1 < i) {
				// always matches
				return true;
			} else {
				// scans all patterns 
				Set<Pattern> part = getParts().get(i);
				boolean or = part.size() > 1;
				for (String stringPart : otherPart){
					// checks if the patterns matches with the part of string permission
					boolean matches = false;
					for (Pattern pattern : part){
						// matches
						boolean match = pattern.matcher(stringPart).matches();
						// if more that pattern
						if (!or){
							// if not match return false
							if (!match){
								return false;
							}	
						} else {
							// works in OR, adding all matches
							matches = matches || match;
						}
					}
					// if doesn't match, FALSE
					if (or && !matches){
						return false;
					}
				}
				i++;
			}
		}
		// if here, means all checks are OK 
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegExpPermission [permissionPattern=" + permissionPattern + "]";
	}
}