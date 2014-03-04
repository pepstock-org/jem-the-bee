/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet base of all servlet. All servelts must extend and implements <code>execute(HttpServletRequest request, HttpServletResponse response)</code>.<br>
 * Checks if cluster is available otherwise will throw a Exception.
 */

public abstract class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see  javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse) 
	 */
	@Override
	public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		execute(request, response);
	}

	/**
	 * @see  javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		execute(request, response);
	}

	/**
	 * Both get and post requests will call this method which must be implemented.
	 * 
	 * @param request http request
	 * @param response http response
	 * @throws IOException it I/O error occurs
	 * @throws ServletException if servlet errors occurs
	 */
	public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}