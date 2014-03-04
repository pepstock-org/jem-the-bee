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
package org.pepstock.jem.node.tasks;

/**
 * Enumeration with the list of folder to include on classpath of process, launched to execute the job.<br>
 * This has been done because the environment variables are a limit of 8k and command line of 2k.<br>
 * Furthermore it seems that ANT has got a problem with GWT jar files, because GWT has another ANT inside. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public enum Libraries {
	/**
	 * folder library
	 */
	CLI("cli"),
	/**
	 * folder library
	 */
	COMMONS_CODEC("commons-codec"),
	/**
	 * folder library
	 */
	COMMONS_IO("commons-io"),
	/**
	 * folder library
	 */
	COMMONS_NET("commons-net"),
	/**
	 * folder library
	 */
	DBCP("dbcp"),
	/**
	 * folder library
	 */
	EXT("ext"),
	/**
	 * folder library
	 */
	HTTPCOMPONENTS("httpcomponents"),
	/**
	 * folder library
	 */
	JMS("jms"),
	/**
	 * folder library
	 */
	JPPF("jppf"),
	/**
	 * folder library
	 */
	LANG("lang"),
	/**
	 * folder library
	 */
	LOGGING("logging"),
	/**
	 * folder library
	 */
	SHIRO("shiro"),
	/**
	 * folder library
	 */
	XALAN("xalan"),
	/**
	 * folder library
	 */
	XSTREAM("xstream");
	
	private String path = null;
	
	private Libraries(String path) {
		this.path = path;
	}
	
	/**
	 * @return the path to check
	 */
	public String getPath(){
		return path;
	}
}