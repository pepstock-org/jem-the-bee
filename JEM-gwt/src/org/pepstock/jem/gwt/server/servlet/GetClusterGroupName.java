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
package org.pepstock.jem.gwt.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pepstock.jem.gwt.server.commons.SharedObjects;

/**
 * Calls to have the JEM environment name, so group name of Hazecast.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class GetClusterGroupName extends JemDefaultServlet {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.servlet.DefaultServlet#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.execute(request, response);
		response.setContentType("text/plain");
		response.getWriter().print(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		response.getWriter().close();
	}

}