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
	// data path, necessary for GDG and all files
	// FIXME
//	private String dataPath = "-D"+ConfigKeys.JEM_DATA_PATH_NAME+"="+System.getProperty(ConfigKeys.JEM_DATA_PATH_NAME);
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
	 * @param job
	 * @param factory
	 */
	public DefaultJobTask(Job job, JemFactory factory) {
		super(job, factory);

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.tasks.JobTask#configure()
	 */
	@Override
	public void configure() throws IOException {
		Job job = getJob();
		
		String initHeap = JavaUtils.getInitialHeapSize();
		String maxHeap = JavaUtils.getMaximumHeapSize();
		File jclFile = Main.getOutputSystem().getJclFile(job);
		
		JavaCommand command = new JavaCommand();
		command.setJavaOptions(initHeap, 
						maxHeap,
						getHome(),
						getRmiPort(),
						"-D"+ConfigKeys.JEM_JOB_ID+"="+job.getId(),
						getClassPath(),
						getBinPath(),
//						getDataPath(), FIXME
						getLibPath(),
						"-D"+ConfigKeys.JEM_OUTPUT_PATH_NAME+"="+FilenameUtils.normalize(jclFile.getParentFile().getAbsolutePath(), true),
						getSrcPath(),
						getPersistencePath());
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
	 * @return the dataPath
	 */
// FIXME	
//	public String getDataPath() {
//		return dataPath;
//	}

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