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
package org.pepstock.jem.node.tasks.shell;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.log.JemRuntimeException;

/**
 * Container of command name and its parameters.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class Command {
	
	private String name = null;
	
	private String parameters = null;

	/**
	 * Constructor with command name
	 * 
	 * @param name command 
	 */
	public Command(String name) {
		super();
		this.name = name;
		if (name == null){
			throw new JemRuntimeException("Name is null");
		}
	}

	/**
	 * Constructor with command name and its parameters
	 * @param name command name
	 * @param parameters its parameters
	 */
	public Command(String name, String parameters) {
		this(name);
		this.parameters = parameters;
	}

	/**
	 * Returns command name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets command name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns command parameters
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * Sets command parameters
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns the complete command line with command and arguments
	 * @return command line
	 */
	public StringBuilder toCommandLine(){
		if (name == null){
			throw new JemRuntimeException("Name is null");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" ");
		if (parameters != null){
			sb.append(parameters).append(" ");
		}
		return sb;
	}
	
	/**
	 * Returns the complete command line with command and arguments, as linkedlist
	 * @return command line as linkedlist
	 */
	public List<String> toCommandLineList(){
		if (name == null){
			throw new JemRuntimeException("Name is null");
		}
		List<String> commandList = new LinkedList<String>();
		commandList.add(name);
		if (parameters != null){
			commandList.add(parameters);
		}
		return commandList;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Command [" + toCommandLine() + "]";
	}
}
