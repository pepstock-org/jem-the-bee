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
package org.pepstock.jem.node.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.tasks.shell.JavaCommand;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * This is default JOB task, which all customization should extend.
 * <br>
 * Is able to manage the JAVA commandline to create to execute a JAVA process.
 * <br>
 * It cares also of a java system properties with all JEM information.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class DefaultJobTask extends JobTask {

	private static final long serialVersionUID = 1L;

	// jem home, necessary for security
	private String home = "-D"+ConfigKeys.JEM_HOME+"="+System.getProperty(ConfigKeys.JEM_HOME);
	// rmi port for job in execution
	private String rmiPort = "-D"+RmiKeys.JEM_RMI_PORT+"="+Main.getNode().getRmiPort();
	// binary path folder, eventually needed to the jcl
	private String binPath = "-D"+ConfigKeys.JEM_BINARY_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME);
	// classpath folder, eventually needed to the jcl
	private String classPath = "-D"+ConfigKeys.JEM_CLASSPATH_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME);
	// library folder, eventually needed to the jcl
	private String libPath = "-D"+ConfigKeys.JEM_LIBRARY_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME);
	// source folder, eventually needed to the jcl
	private String srcPath = "-D"+ConfigKeys.JEM_SOURCE_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME);
	// persistence folder, needed into security managaer
	private String persistencePath = "-D"+ConfigKeys.JEM_PERSISTENCE_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_PERSISTENCE_PATH_NAME);

	/**
	 * Environment variable to set classpath for JAVA 
	 */
	public static final String CLASSPATH_ENVIRONMENT_VARIABLE = "CLASSPATH";
	
	/**
	 * Default constructor which calls the parent
	 * @param job job to be executed
	 * @param factory JEM factory which has created the job
	 */
	public DefaultJobTask(Job job, JemFactory factory) {
		super(job, factory);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.tasks.JobTask#configure()
	 */
	@Override
	public void configure() throws IOException {
		// gets joib
		Job job = getJob();
		// gets initial and max heap size
		String initHeap = JavaUtils.getInitialHeapSize();
		String maxHeap = JavaUtils.getMaximumHeapSize();
		// gets JCL file
		File jclFile = Main.getOutputSystem().getJclFile(job);
		// gest the use of job
		String user = job.isUserSurrogated() ? job.getJcl().getUser() : job.getUser();
		//creates the JAVA command to execute
		JavaCommand command = new JavaCommand();
		// sets all java option
		// heap sizes
		// system properties
		command.setJavaOptions(initHeap, 
						maxHeap,
						getHome(),
						getRmiPort(),
						"-D"+ConfigKeys.JEM_JOB_ID+"="+job.getId(),
						getDataPath(),
						getClassPath(),
						getBinPath(),
						getLibPath(),
						"-D"+ConfigKeys.JEM_OUTPUT_PATH_NAME+"="+FilenameUtils.normalize(jclFile.getParentFile().getAbsolutePath(), true),
						getSrcPath(),
						getPersistencePath(),
						"-D"+ConfigKeys.JAVA_USER_NAME+"="+user);
		
		// loads system properties that has been set
		// during the submit of job
		if (!job.getInputArguments().isEmpty()){
			// only the system properties 
			// which start with "jem.custom." can be passed
			for (String prop : job.getInputArguments()){
				if (prop.startsWith("-Djem.custom.")){
					command.setJavaOptions(prop);
				}
			}
		}
		// sets command to be executed
		setCommand(command);
	}

	/**
	 * @return the home
	 */
	public String getHome() {
		return home;
	}

	/**
	 * @return the rmiport
	 */
	public String getRmiPort() {
		return rmiPort;
	}
	
	/**
	 * @return the binPath
	 */
	public String getDataPath() {
		// checks if the data path must be passed
		return System.getProperties().containsKey(ConfigKeys.JEM_DATA_PATH_NAME) ? 
				"-D"+ConfigKeys.JEM_DATA_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_DATA_PATH_NAME) : 
					"";
	}

	/**
	 * @return the binPath
	 */
	public String getBinPath() {
		return binPath;
	}

	/**
	 * @return the classPath
	 */
	public String getClassPath() {
		return classPath;
	}

	/**
	 * @return the libPath
	 */
	public String getLibPath() {
		return libPath;
	}

	/**
	 * @return the srcPath
	 */
	public String getSrcPath() {
		return srcPath;
	}

	/**
	 * @return the persistencePath
	 */
	public String getPersistencePath() {
		return persistencePath;
	}

	/**
	 * @param persistencePath the persistencePath to set
	 */
	public void setPersistencePath(String persistencePath) {
		this.persistencePath = persistencePath;
	}
}