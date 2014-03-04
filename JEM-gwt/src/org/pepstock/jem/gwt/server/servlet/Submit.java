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

import org.pepstock.jem.PreJob;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.JobsManager;

import com.thoughtworks.xstream.XStream;

/**
 * Submits the job pased as content of body.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Submit extends JemDefaultServlet {

	private static final long serialVersionUID = 1L;
	
	private transient JobsManager jobsManager = null;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.servlet.DefaultServlet#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.execute(request, response);
		
		// if jobs manager is null, creates an instance 
		if (jobsManager == null){
			jobsManager = new JobsManager();
		}
		
		// gets streamer and deserializes the prejob object
		XStream streamer = new XStream();
		Object object = streamer.fromXML(request.getReader());
		//if is not a prejob, exception
		if (object instanceof PreJob){
			PreJob preJob = (PreJob)object;
			
			String jobId = null;
			try {
				// submits the job
				jobId = jobsManager.submit(preJob);
			} catch (Exception ex){
				throw new ServletException(ex);
			}
			// returns the job id
			response.setContentType("text/plain");
			response.getWriter().print(jobId);
			response.getWriter().close();
		} else {
			throw new ServletException(UserInterfaceMessage.JEMG030E.toMessage().getFormattedMessage(object.getClass().getName()));
		}

	}
}