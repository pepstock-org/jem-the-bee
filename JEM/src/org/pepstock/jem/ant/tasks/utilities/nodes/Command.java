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
package org.pepstock.jem.ant.tasks.utilities.nodes;

import java.text.ParseException;

import org.pepstock.jem.ant.tasks.utilities.SubCommand;

/**
 * Utility class to use to save command line during the syntax checking and execute the command
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public abstract class Command implements SubCommand{
	
	private String commandLine = null;
	
	private String nodesPattern = null;

	/**
	 * Stores command line
	 * 
	 * @param commandLine command line 
	 * @throws ParseException  if command line has a syntax error
	 */
	public Command(String commandLine) throws ParseException {
		this.setCommandLine(commandLine);
	}

	
	/**
	 * @return the commandLine
	 */
	public String getCommandLine() {
		return commandLine;
	}


	/**
	 * @param commandLine the commandLine to set
	 */
	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * @return the nodesPattern
	 */
	public String getNodesPattern() {
		return nodesPattern;
	}


	/**
	 * @param nodesPattern the nodesPattern to set
	 */
	public void setNodesPattern(String nodesPattern) {
		this.nodesPattern = nodesPattern;
	}
}