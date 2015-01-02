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
package org.pepstock.jem.commands.util;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Creates an PreJob object to put in JEM cluster.<br>
 * To do it, it must read the JCL content from a URL and normalize the format of job id. 
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Factory {
	
	//Max length for a long value. It uses numbers of digits of Long.MAX_VALUE
	private static final int LONG_LENGTH = String.valueOf(Long.MAX_VALUE).length();
	
	/**
	 * Job id Format
	 */
	public static final String JOBID_FORMAT = "{0,number}-{1,number}";
	
	/**
	 * To avoid any instantiations
	 */
	private Factory() {
		
	}

	/**
	 * Creates the unique id for the job, normalizing generated id joined to started time stamp of job.<br>
	 * it pads (on left) with '0' string representation of both id and timestamp
	 * 
	 * @param job job instance, needs to extract started timestamp 
	 * @param id job id
	 * @return job id in job format
	 */
	public static String createJobId(Job job, long id){
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.leftPad(String.valueOf(id), LONG_LENGTH, "0")).append('-').append(StringUtils.leftPad(String.valueOf(job.getSubmittedTime().getTime()), LONG_LENGTH, "0")); 
		return sb.toString();
	}

	/**
	 * Creates a PreJob instance, loading JCL content by URL
	 *  
	 * @param url JCL URL to load
	 * @return PreJob instance to put on JEM cluster for submitting
	 * @throws InstantiationException if IOException occurs
	 */
	public static PreJob createPreJob(URL url) throws InstantiationException{
		try {
			// load JCL content from url
			String content = load(url);
			// create PreJob and load content
			PreJob job = new PreJob();
			job.setJclContent(content);
			return job;
		} catch (IOException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new InstantiationException(e.getMessage());
		}
	}

	/**
	 * Reads JCL content from URL and returns it in string format
	 * 
	 * @param url JCL URL
	 * @return JCL content
	 * @throws IOException if IOExcetion occurs during reading JCL file
	 */
	private static String load(URL url) throws IOException {
		// checks if null. If yes, exception occurs
		if (url == null){
			throw new IOException(NodeMessage.JEMC97E.toMessage().getFormattedMessage());
		}
		
		StringWriter sw = new StringWriter();
		IOUtils.copy(url.openStream(), sw);
		return sw.getBuffer().toString();
	}

}