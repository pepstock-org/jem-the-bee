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
package org.pepstock.jem.springbatch;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.tasks.DefaultJobTask;
import org.pepstock.jem.node.tasks.JavaUtils;
import org.pepstock.jem.node.tasks.shell.JavaCommand;
import org.pepstock.jem.springbatch.xml.JemBeanDefinitionParser;

/**
 * Implements <code>JobTask</code> abstract class, and configure method to
 * prepare the command list to start Spring Batch by command line.<br>
 * 
 * @see org.pepstock.jem.node.tasks.JobTask
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SpringBatchTask extends DefaultJobTask {

	private static final long serialVersionUID = 1L;
	
	private static final String[] FOLDER = {"spring", "springbatch"};
	
	private static final String JOB_ID_PARAMETER = "jem.job.id";
	
	private String additionalClasspathForJobRegistry = null;

	/**
	 * Constructs the object save job instance to execute
	 * 
	 * @param job job instance to execute
	 * @param factory factory which creates this job task
	 */
	public SpringBatchTask(Job job, JemFactory factory) {
		super(job, factory);
	}

	/**
	 * @return the additionalClasspathForJobRegistry
	 */
	String getAdditionalClasspathForJobRegistry() {
		return additionalClasspathForJobRegistry;
	}

	/**
	 * @param additionalClasspathForJobRegistry the additionalClasspathForJobRegistry to set
	 */
	void setAdditionalClasspathForJobRegistry(String additionalClasspathForJobRegistry) {
		this.additionalClasspathForJobRegistry = additionalClasspathForJobRegistry;
	}

	/**
	 * Implementations of abstract method.<br>
	 * Sets the classpath, adding the source path of Spring Batch JCL file and
	 * creates the command line.
	 * 
	 * @throws IOException occurs if there is any error
	 */
	@Override
	public void configure() throws IOException {
		super.configure();
	
		// gets CLASSPATH, setting the name of folders in JEM lib to add
		// to process to launch
		String currentClassPath = JavaUtils.getClassPath(FOLDER);
		
		// get job instance and get JCL file, necessary to pass to Spriong Batch
		Job job = getJob();
		File jclFile = Main.getOutputSystem().getJclFile(job);
		currentClassPath = currentClassPath + File.pathSeparator + FilenameUtils.normalize(jclFile.getParentFile().getAbsolutePath(), true);
		getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);

		// adds the custom classpath if not null/
		Jcl jcl = job.getJcl();
		
		if (jcl.getPriorClassPath() != null){
			currentClassPath = jcl.getPriorClassPath() + File.pathSeparator + currentClassPath;
			getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);
		}
		if (jcl.getClassPath() != null){
			currentClassPath = currentClassPath + File.pathSeparator + jcl.getClassPath();
			getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);
		}
		
		
		System.err.println(getAdditionalClasspathForJobRegistry());
		if (getAdditionalClasspathForJobRegistry() != null){
			currentClassPath = currentClassPath + File.pathSeparator + getAdditionalClasspathForJobRegistry();
			getEnv().put(CLASSPATH_ENVIRONMENT_VARIABLE, currentClassPath);
		}
		System.err.println(currentClassPath);
		JavaCommand jCommand = getCommand();
		jCommand.setClassPath(currentClassPath);
		
		Map<String, Object> jclMap = jcl.getProperties();
		String sbParms = null;
		if (jclMap.containsKey(JemBeanDefinitionParser.PARAMETERS_ATTRIBUTE)){
			sbParms = jclMap.get(JemBeanDefinitionParser.PARAMETERS_ATTRIBUTE).toString();
			sbParms = sbParms + " " + JOB_ID_PARAMETER + "=" + job.getId();
		} else {
			sbParms = JOB_ID_PARAMETER + "=" + job.getId();
		}

		jCommand.setClassName(SpringBatchLauncher.class.getName());
		jCommand.setClassArguments(jclFile.getName(), (jclMap.containsKey(JemBeanDefinitionParser.OPTIONS_ATTRIBUTE)) ? jclMap.get(JemBeanDefinitionParser.OPTIONS_ATTRIBUTE).toString() : "", 
				job.getName(), sbParms);
	}
}