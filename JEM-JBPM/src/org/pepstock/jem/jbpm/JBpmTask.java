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
package org.pepstock.jem.jbpm;

import java.io.File;
import java.io.IOException;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.tasks.DefaultJobTask;
import org.pepstock.jem.node.tasks.JavaUtils;
import org.pepstock.jem.node.tasks.shell.JavaCommand;

/**
 * Implements <code>JobTask</code> abstract class, and configure method to
 * prepare the command list to start JBPM by command line.<br>
 * 
 * @see org.pepstock.jem.node.tasks.JobTask
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class JBpmTask extends DefaultJobTask {

	private static final long serialVersionUID = -1L;

	/**
	 * Constructs the object save job instance to execute
	 * 
	 * @param job job instance to execute
	 * @param factory instance of factory of JBPM task
	 */
	public JBpmTask(Job job, JemFactory factory) {
		super(job, factory);


	}
	/**
	 * Implementations of abstract method.<br>
	 * Sets the command to start the JBPM Launcher
	 * 
	 * @throws IOException occurs if there is any error
	 */
	@Override
	public void configure() throws IOException {
		super.configure();
		
		// gets CLASSPATH, setting the name of folder in JEM lib to add
		// to process to launch
		String currentClassPath = JavaUtils.getClassPath();
		
		// using a custom classloadr, CLASSPATH of factory couldn't be empty
		if (getFactory().getClassPath() != null && !getFactory().getClassPath().isEmpty()){
			StringBuilder builder = new StringBuilder(currentClassPath);
			for (String path : getFactory().getClassPath()){
				 builder.append(File.pathSeparator).append(path);
			}
			currentClassPath = builder.toString();
		} else {
			throw new IOException("classPath is empty");
		}
		// sets classpath
		getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);
	
		// get job instance and get JCL file, necessary to pass to JBPM Batch
		Job job = getJob();
		File jclFile = Main.getOutputSystem().getJclFile(job);
		
		// adds the custom classpath if not null
		Jcl jcl =job.getJcl();
		
		if (jcl.getPriorClassPath() != null){
			currentClassPath = jcl.getPriorClassPath() + File.pathSeparator + currentClassPath;
			getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);
		}
		
		if (jcl.getClassPath() != null){
			currentClassPath = currentClassPath + File.pathSeparator + jcl.getClassPath();
			getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);
		}
		// creates a Java command
		JavaCommand jCommand = getCommand();
		
		// sests classpath
		jCommand.setClassPath(currentClassPath);

		// main class is JBPM Laucnher
		jCommand.setClassName(JBpmLauncher.class.getName());
		
		// gets from JCL the JBPM process ID 
		String jobName = (jcl.getProperties().containsKey(JBpmKeys.JBPM_JOB_NAME)) ? jcl.getProperties().get(JBpmKeys.JBPM_JOB_NAME).toString() : job.getName();
		
		// sets Process ID and JCL to execute
		jCommand.setClassArguments(jobName, jclFile.getAbsolutePath());
	}
}