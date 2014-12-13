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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.util.CollectionUtils;
import org.pepstock.jem.node.NodeMessage;

/**
 * Extension of wildcard permission of SHIRO, necessary to implement the regexpression premission.
 * The permission must be composed of 3 parts or less (never more than 3):<br>
 * <br>
 * <code>[part1]:[part2]:[part3]</code>
 * <br>
 * Every part can have subparts:<br>
 * <br>
 * <code>[subpart1,subpart2]:[subpart3,subpart4]:[subpart5,subpart6]</code>
 * <br>
 * 
 * @see org.apache.shiro.authz.permission.WildcardPermission
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class StringPermission extends WildcardPermission implements Permission, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Set<String>> parts;
	
	private String permission = null;

	/**
	 * Empty construct
	 */
	public StringPermission() {
	}

	/**
	 * Constructs with permission
	 * @param permission permission string
	 */
	public StringPermission(String permission) {
		super(permission, false);
		this.permission = permission;
		load();
	}

	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * Parses permission string, using ":" as separator of parts and "," for subparts  
	 */
	private void load() {
		// if string permission if not valid, throw an exception
		if (permission == null || permission.trim().length() == 0) {
			throw new IllegalArgumentException(NodeMessage.JEMC129E.toMessage().getMessage());			
		}

		// removes blanks
		permission = permission.trim();
		List<String> localParts = new LinkedList<String>();
		// parses main parts
		// splits permission string using the part divider
		String[] permissionParts = permission.split(RegExpPermission.PART_DIVIDER_TOKEN);

		// if there is only one permission (only a token) adds the token
		if (permissionParts.length == 1){
			localParts.add(permissionParts[0]);	
		} else if (permissionParts.length == 2){
			// if there are 2 tokens, adds the 2 tokens
			localParts.add(permissionParts[0]);
			localParts.add(permissionParts[1]);
		} else {
			// if more than 2, adds the first 2 token
			localParts.add(permissionParts[0]);
			localParts.add(permissionParts[1]);
			// joins all the rest in a unique part
			StringBuilder lastPart = new StringBuilder();
			for (int i=2; i<permissionParts.length; i++){
				lastPart.append(permissionParts[i]);
			}
			localParts.add(lastPart.toString());
		}
		
		this.parts = new ArrayList<Set<String>>();
		// parses all subparts
		for (String part : localParts) {
			// splits all parts in subpart using the divider
			Set<String> subparts = CollectionUtils.asSet(part.split(RegExpPermission.SUBPART_DIVIDER_TOKEN));
			if (subparts.isEmpty()) {
				throw new IllegalArgumentException(NodeMessage.JEMC130E.toMessage().getMessage());
			}
			// adds subpart
			this.parts.add(subparts);
		}
		// if not parts, the permission string syntax is wrong
		if (this.parts.isEmpty()) {
			throw new IllegalArgumentException(NodeMessage.JEMC131E.toMessage().getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.shiro.authz.permission.WildcardPermission#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// if String permission to compare, uses this method recursively passing the string permission parts  
        if (o instanceof StringPermission) {
            StringPermission sp = (StringPermission) o;
            return parts.equals(sp.getParts());
        }
        return super.equals(o);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.shiro.authz.permission.WildcardPermission#hashCode()
	 */
	@Override
	public int hashCode() {
		return parts.hashCode();
	}
	
	/**
	 * @return all parts loaded
	 */
	public List<Set<String>> getParts() {
		return this.parts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPermission();
	}
}