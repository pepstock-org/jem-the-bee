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
package org.pepstock.jem.node.tasks.shell;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.log.JemRuntimeException;

/**
 * Container of a JAVA command. Command name is always JAVA.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class JavaCommand extends Command {
	
	private static final String JAVA_COMMAND_NAME = "java";
	
	/**
	 * The list of command-line java options.
	 */
	private List<String> javaOptions = new LinkedList<String>();
	
	private String className = null;
	
	private String classPath = null;
	
	/**
	 * The list of command-line arguments.
	 */
	private List<String> classArguments = new LinkedList<String>();

	/**
	 * Constructor setting java as command name
	 * 
	 * @param name java command name
	 */
	public JavaCommand() {
		super(JAVA_COMMAND_NAME);
	}

	/**
	 * Returns classpath argument
	 * 
	 * @return the classPath
	 */
	public String getClassPath() {
		return classPath;
	}

	/**
	 * Sets classpath argument
	 * 
	 * @param classPath the classPath to set
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * Returns java options as linkedlist 
	 * 
	 * @return the javaOptions
	 */
	public List<String> getJavaOptions() {
		return javaOptions;
	}

	/**
	 * Sets java options as linkedlist
	 * 
	 * @param javaOptions the javaOptions to set
	 */
	public void setJavaOptions(List<String> javaOptions) {
		this.javaOptions = javaOptions;
	}

	/**
	 * Set the list of java options arguments as array.
	 * 
	 * @param commands a list of arguments as strings.
	 */
	public void setJavaOptions(String... commands) {
		for (String element : commands){
			javaOptions.add(element);
		}
	}

	/**
	 * Returns the name of main class
	 * 
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the name of main class
	 * 
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Returns the arguments for main class
	 * 
	 * @return the classArguments
	 */
	public List<String> getClassArguments() {
		return classArguments;
	}

	/**
	 * Sets the arguments for main class
	 * 
	 * @param classArguments the classArguments to set
	 */
	public void setClassArguments(List<String> classArguments) {
		this.classArguments = classArguments;
	}

	/**
	 * Set the list of command-line arguments.
	 * 
	 * @param commands a list of arguments as strings.
	 */
	public void setClassArguments(String... commands) {
		for (String element : commands){
			classArguments.add(element);
		}
	}
	
	/**
	 * Returns the complete command line with command and arguments
	 * @return command line
	 */
	@Override
	public StringBuilder toCommandLine(){
		if (className == null){
			throw new JemRuntimeException("Classname is null");
		}
		StringBuilder sb = super.toCommandLine();
		for (String token : javaOptions){
			sb.append(token).append(" ");
		}
		sb.append(className).append(" ");
		for (String token : classArguments){
			sb.append(token).append(" ");
		}
		return sb;
	}
	
	/**
	 * Returns the complete command line with command and arguments, as linkedlist
	 * @return command line as linkedlist
	 */
	@Override
	public List<String> toCommandLineList(){
		List<String> commandList = super.toCommandLineList();
		for (String token : javaOptions){
			commandList.add(token);
		}
		commandList.add(className);
		for (String token : classArguments){
			commandList.add(token);
		}
		return commandList;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaCommand [" + toCommandLine() + "]";
	}
}
