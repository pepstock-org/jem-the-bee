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
 * @author Andrea "Stock" Stocchero
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
	 * Empty constructor
	 */
	public RegExpPermission() {
	}

	/**
	 * @param permissionPattern
	 */
	public RegExpPermission(String permissionPattern) {
		this(permissionPattern, DEFAULT_CASE_SENSITIVE);
	}

	/**
	 * @param permissionPattern 
	 * @param caseSensitive
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

	private void load() {

		if (permissionPattern == null || permissionPattern.trim().length() == 0) {
			throw new IllegalArgumentException(NodeMessage.JEMC129E.toMessage().getMessage());
		}

		permissionPattern = permissionPattern.trim();

		List<String> currParts = CollectionUtils.asList(permissionPattern.split(PART_DIVIDER_TOKEN));
		
		this.parts = new ArrayList<Set<Pattern>>();
		for (String part : currParts) {
			Set<String> subparts = CollectionUtils.asSet(part.split(SUBPART_DIVIDER_TOKEN));
			if (subparts.isEmpty()) {
				throw new IllegalArgumentException(NodeMessage.JEMC130E.toMessage().getMessage());
			}
			Set<Pattern> patternSubparts = new LinkedHashSet<Pattern>(subparts.size());
			for (String subpart: subparts){
				Pattern pattern;
				if (caseSensitive){
					pattern = Pattern.compile(subpart.trim());
				} else { 
					pattern = Pattern.compile(subpart.trim(), Pattern.CASE_INSENSITIVE);
				}
				patternSubparts.add(pattern);
			}
			this.parts.add(patternSubparts);
		}

		if (this.parts.isEmpty()) {
			throw new IllegalArgumentException(NodeMessage.JEMC131E.toMessage().getMessage());
		}
	}
	
	private List<Set<Pattern>> getParts() {
		return this.parts;
	}

	public boolean implies(Permission p) {
		// By default only supports comparisons with StringPermissions
		StringPermission stringPermission;
		if (p instanceof StringPermission){
			stringPermission = (StringPermission) p;
		} else {
			return false;
		}
		
		
		List<Set<String>> otherParts = stringPermission.getParts();

		int i = 0;
		for (Set<String> otherPart : otherParts) {
			// If this permission has less parts than the other permission,
			// everything after the number of parts contained
			// in this permission is automatically implied, so return true
			
			if (getParts().size() - 1 < i) {
				return true;
			} else {
				Set<Pattern> part = getParts().get(i);
				boolean or = part.size() > 1;
				for (String stringPart : otherPart){
					boolean matches = false;
					for (Pattern pattern : part){
						boolean match = pattern.matcher(stringPart).matches();
						if (!or){
							if (!match){
								return false;
							}
						} else {
							matches = matches || match;
						}
					}
					if (or && !matches){
						return false;
					}
				}
				i++;
			}
		}
		return true;
	}

}