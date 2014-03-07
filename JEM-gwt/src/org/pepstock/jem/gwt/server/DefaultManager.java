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
package org.pepstock.jem.gwt.server;

import org.pepstock.jem.gwt.server.commons.ExceptionsUtility;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.JemException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This is generic GWT remote service, that all other services must extend.<br>
 * It has got some methods about the availability of JEM cluster.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class DefaultManager extends RemoteServiceServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public DefaultManager() {
		super();
	}

	/**
	 * The delegate constructor
	 * 
	 * @param delegate delegate instance
	 */
	public DefaultManager(Object delegate) {
		super(delegate);
	}

	/**
	 * Returns is JEM cluster is available
	 * 
	 * @return <code>true</code> if JEM is available, otherwise <code>false</code>
	 */
	protected boolean isEnable(){
		return SharedObjects.getInstance().isDataClusterAvailable();
	}
	
	/**
	 * Checks if JEM cluster is available. if not, throws an exception
	 * @throws Exception if JEM cluster is not availabel, an exception occurs
	 */
	protected void checkIsEnable() throws JemException{
		if (!isEnable()){
			throw ExceptionsUtility.throwGroupNotAvailableException();
		}
	}

}