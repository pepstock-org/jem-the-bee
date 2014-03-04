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
package org.pepstock.jem.node.executors.jobs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Is the "Purge" command implementation.<br>
 * Removes output folder of specific job.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2
 * 
 */
public class Purge extends DefaultExecutor<ExecutionResult>{

	private static final long serialVersionUID = 1L;
	
	private Job job = null;
	
	/**
	 * Constructs the object with job instance
	 * 
	 * @param job instance used to remove its output folder 
	 */
	public Purge(Job job) {
		this.job = job;
	}

	/**
	 * Removes output folder with all files) of specific job. 
	 * 
	 * @see ExecutionResult#SUCCESSFUL
	 * @return execution result, always successful
	 * @throws Exception occurs if errors
	 */
	@Override
	public ExecutionResult execute() throws ExecutorException {
		// gets the jcl file to extract the directory
		File jclFile;
		try {
			jclFile = Main.getOutputSystem().getJclFile(job);
		} catch (IOException e) {
			// messo il job ma il node file va messo
			throw new ExecutorException(NodeMessage.JEMC242E, e, job);
		}
		// checks if file exists otherwise exception occurs
		if (!jclFile.exists()){
			throw new ExecutorException(NodeMessage.JEMC242E, jclFile);
		}

		// get the folder of JCL. is folder used for job output
		File jobOutputFolder = jclFile.getParentFile();
		
		try {
			// removes the directory of job
			FileUtils.deleteDirectory(jobOutputFolder);
			if (jobOutputFolder.exists()){
				LogAppl.getInstance().emit(NodeMessage.JEMC164W, job.toString(), jobOutputFolder.getAbsolutePath());
			}
		} catch (IOException ioe){
			LogAppl.getInstance().emit(NodeMessage.JEMC164W, ioe, job.toString(), jobOutputFolder.getAbsolutePath());	
		}
		return ExecutionResult.SUCCESSFUL;
	}
}