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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;


/**
 * Get the job by id and queue name.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class GetJobById extends AbstractGetJobById {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final String QUEUE_NAME = "queueName";

	@Override
	public Job retrieveJob(String jobId, HttpServletRequest request) throws ServletException, ServiceMessageException {
		String queueName = request.getParameter(QUEUE_NAME);
		if (queueName == null){
			throw new ServletException(UserInterfaceMessage.JEMG033E.toMessage().getFormattedMessage(QUEUE_NAME));
		}
		return getJobsManager().getJobById(queueName, jobId);
	}

}