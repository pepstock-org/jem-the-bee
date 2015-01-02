/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.JobsManager;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;

import com.thoughtworks.xstream.XStream;

/**
 * Abstract base class for GetJob
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class AbstractGetJobById extends JemDefaultServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final String JOB_ID = "jobId";

	/**
	 * 
	 */
	public static final String QUEUE_NAME = "queueName";
	
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

		String jobId = request.getParameter(JOB_ID);
		if (jobId == null){
			throw new ServletException(UserInterfaceMessage.JEMG033E.toMessage().getFormattedMessage(JOB_ID));
		}
		
		// retrieve the job
		try {
	        Job job = retrieveJob(jobId, request);
	        XStream streamer = new XStream();
			// return the objects in the content
			// using XStream and then in XML format
			response.setContentType("text/xml");
			response.getWriter().print(streamer.toXML((job != null) ? job : new Object()));
			response.getWriter().close();
        } catch (Exception e) {
        	throw new ServletException(e);
        }
		
	}
	
	/**
	 * @return {@link JobsManager} service object
	 */
	public JobsManager getJobsManager() {
		return jobsManager;
	}
	
	
	/**
	 * Needed by execute() method to retrieve the right job
	 * @param jobId the job id
	 * @param request the request, may be useful to get more parameters
	 * @return the job
	 * @throws ServletException
	 * @throws ServiceMessageException
	 */
	public abstract Job retrieveJob(String jobId, HttpServletRequest request) throws ServletException, ServiceMessageException;
}
