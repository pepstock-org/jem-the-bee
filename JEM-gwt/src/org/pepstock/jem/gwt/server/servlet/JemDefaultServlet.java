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
package org.pepstock.jem.gwt.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;

/**
 * Generic servlet for JEM services, which checks if the cluster is available.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class JemDefaultServlet extends DefaultServlet {

    private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.servlet.DefaultServlet#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// checks if cluster is available. If not, exception
		if (!SharedObjects.getInstance().isDataClusterAvailable()){
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			throw new ServletException(msg);
		}
	}

}