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
package org.pepstock.jem.gwt.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pepstock.jem.gwt.server.services.LoginManager;
import org.pepstock.jem.log.LogAppl;

/**
 * Logs out from JEM web app
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Logout extends JemDefaultServlet {

	private static final long serialVersionUID = 1L;
	
	private transient LoginManager loginManager = null;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.servlet.DefaultServlet#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (loginManager == null){
			try {
				loginManager = new LoginManager();
			} catch (Exception e) {
				LogAppl.getInstance().debug(e.getMessage(), e);
				throw new ServletException(e.getMessage());
			}
		}
		try {
			// logoff the user associated to this session
			loginManager.logoff(null);
		} catch (Exception e){
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new ServletException(e);
		}
		// returns successful message
		response.setContentType("text/plain");
		response.getWriter().print("Logged out successfully!");
		response.getWriter().close();
	}

}