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
package org.pepstock.jem.springbatch.xml;

import java.util.List;

import org.pepstock.jem.springbatch.tasks.utilities.MainLauncherTasklet;
import org.pepstock.jem.springbatch.xml.TaskletFactoryBean;

/**
 * Factory bean for complex XML element for MAIN launcher tasklet.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class MainLauncherTaskletFactoryBean extends TaskletFactoryBean {
	
	static final String ARGUMENTS = "arguments";

	static final String CLASSPATH = "classPath";
	
	private List<String> arguments = null;
	
	private List<String> classPath = null;

	/**
	 * @return the arguments
	 */
	public List<String> getArguments() {
		return arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	/**
	 * @return the classPath
	 */
	public List<String> getClassPath() {
		return classPath;
	}

	/**
	 * @param classPath the classPath to set
	 */
	public void setClassPath(List<String> classPath) {
		this.classPath = classPath;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch4.xml.TaskletFactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		// casts taskelt
		MainLauncherTasklet tasklet = (MainLauncherTasklet)super.getObject();
		// if there are arguments, sets arguments
		if (arguments != null){
			tasklet.setArguments(arguments);
		}
		// if there are classpath, sets classpath
		if (classPath != null){
			tasklet.setClassPath(classPath);
		}
		return tasklet;
	}
}