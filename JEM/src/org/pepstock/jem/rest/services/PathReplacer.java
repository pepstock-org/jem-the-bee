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
package org.pepstock.jem.rest.services;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class PathReplacer {

	private String path = null;

	/**
	 * @param path
	 */
	private PathReplacer(String path) {
		super();
		this.path = path;
	}

	static PathReplacer path(String path){
		PathReplacer replacer = new PathReplacer(path);
		return replacer;
	}
	
	PathReplacer replace(String parm, String value){
		path = path.replace(parm, value);
		return this;
	}

	String build(){
		return path;
	}
}
