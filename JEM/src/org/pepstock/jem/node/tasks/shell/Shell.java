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


/**
 * Contains the name of shell to execute by ProcessBuilder 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 *
 */
public class Shell extends Command {

	/**
	 * Constructor with shell name and its parameters
	 * @param name name of shell
	 * @param parameters parameters for shell
	 */
	public Shell(String name, String parameters) {
		super(name, parameters);
	}

	/**
	 * Constructor with command name
	 * 
	 * @param name shell name
	 */
	public Shell(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Shell [" + toCommandLine() + "]";
	}
}