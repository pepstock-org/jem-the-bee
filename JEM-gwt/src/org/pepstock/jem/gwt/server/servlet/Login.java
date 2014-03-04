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
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pepstock.jem.commands.util.HttpUtil;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.LoginManager;
import org.pepstock.jem.log.LogAppl;

/**
 * Logs in JEM web application. Is used by HttpUtil of commands.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Login extends JemDefaultServlet {

	private static final long serialVersionUID = 1L;
	
	private transient LoginManager loginManager = null;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.servlet.DefaultServlet#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.execute(request, response);

		// if login manager is null, creates an instance 
		if (loginManager == null){
			try {
	            loginManager = new LoginManager();
            } catch (Exception e) {
    			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG012E,  e);
    			throw new ServletException(UserInterfaceMessage.JEMG012E.toMessage().getFormattedMessage(), e);
            }
		}

		// parameters like userid and password are passed 
		// as content in properties format.
		Properties properties = new Properties();
		properties.load(request.getInputStream());

		// uses this keys to read password and userid
		String user = properties.getProperty(HttpUtil.USER_PROPERTY_KEY);
		String password = properties.getProperty(HttpUtil.PASSWORD_PROPERTY_KEY);
		if (user == null){
			throw new ServletException(UserInterfaceMessage.JEMG023E.toMessage().getFormattedMessage("user"));
		}
		if (password == null){
			throw new ServletException(UserInterfaceMessage.JEMG023E.toMessage().getFormattedMessage("password"));
		}

		try {
			// uses userid and password to log in
			loginManager.login(user, password);
		} catch (Exception e){
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG012E,  e);
			throw new ServletException(UserInterfaceMessage.JEMG012E.toMessage().getFormattedMessage(), e);
		}
		// returns the successful message
		response.setContentType("text/plain");
		response.getWriter().print("User "+user+" logged in successfully!");
		response.getWriter().close();

	}

}