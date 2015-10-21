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
package org.pepstock.jem.node.executors.jobs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.OutputSystem;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.util.MemorySize;

/**
 * Returns the message log of the passed job. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class GetMessagesLog extends DefaultExecutor<String> {

	private static final long serialVersionUID = 1L;

	private static final int MAX_NUMBER_OF_BYTE_READABLE = MemorySize.MB * 5; // 5MB

	private Job job = null;

	/**
	 * Constructs saving job instance
	 * 
	 * @param job job instance
	 */
	public GetMessagesLog(Job job) {
		this.job = job;
	}

	/**
	 * Reads message log of jobs and returns its content
	 * 
	 * @return message log content
	 * @throws if I/O error occurs
	 */
	@Override
	public String execute() throws ExecutorException {
		// if is a routed job
		if (job.getRoutingInfo().getId() != null) {
			// if routed it can't get the log
			LogAppl.getInstance().emit(NodeMessage.JEMC197I, job, job.getJcl().getEnvironment());
			// returns the message of error as content
			 return NodeMessage.JEMC197I.toMessage().getFormattedMessage(job, job.getJcl().getEnvironment());
		} else {
			try {
				// gets the jcl file to extract the directory
				File jclFile = Main.getOutputSystem().getJclFile(job);
				
				// checks if file exists otherwise exception occurs
				if (!jclFile.exists()){
					throw new ExecutorException(NodeMessage.JEMC242E, jclFile);
				}
				
				// checks if message log exists
				File file = new File(jclFile.getParentFile(), OutputSystem.MESSAGESLOG_FILE);
				if (!file.exists()){
					throw new ExecutorException(NodeMessage.JEMC242E, file);
				}

				// loads content file into a buffer
				// must be check the file size... if too big could create problems
				// checks if file is over the maximum nuber of bytes
				if (file.length() > MAX_NUMBER_OF_BYTE_READABLE){
					return "Output log file too large. Current file size is "+file.length()+" bytes but maximum is "+MAX_NUMBER_OF_BYTE_READABLE+" bytes";
				} else {
					// creates a output container and sets job file content
					return FileUtils.readFileToString(file);
				}			
			} catch (IOException e) {
				throw new ExecutorException(NodeMessage.JEMC242E, e, OutputSystem.MESSAGESLOG_FILE);
			}
		}
	}
}