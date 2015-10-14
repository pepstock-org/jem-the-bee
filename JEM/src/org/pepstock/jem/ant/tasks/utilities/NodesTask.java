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
package org.pepstock.jem.ant.tasks.utilities;

import java.io.InputStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.utilities.nodes.Command;
import org.pepstock.jem.ant.tasks.utilities.nodes.Drain;
import org.pepstock.jem.ant.tasks.utilities.nodes.Start;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * Is a utility (both a task ANT and a main program) that is able to srain and start JEM node.<br>
 * Needs a <code>COMMAND</code> data description which must contain all
 * commands.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class NodesTask extends AntUtilTask {

	private static final String DATA_DESCRIPTION_NAME = "COMMAND";
	
	private static final String COMMAND_SEPARATOR = ";";

	/**
	 * Empty constructor
	 */
	public NodesTask() {
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava) to prepare the COMMAND data description.
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		String port = System.getProperty(RmiKeys.JEM_RMI_PORT);
		if (port == null){
			throw new BuildException(AntMessage.JEMA004E.toMessage().getFormattedMessage());
		}
		super.setClassname(NodesTask.class.getName());
		super.execute();
	}

	/**
	 * Main program, called by StepJava class. It reads the InputStream defined
	 * as :<br>
	 * <ul>
	 * <li> COMMAND data description with all define Nodes commands</li>
	 * </ul>
	 * <br>
	 * Before to start commands, checks all command syntax
	 * 
	 * @param args
	 * 
	 * @throws Exception if COMMAND data description doesn't exists, if an
	 *             error occurs during the command parsing
	 */
	public static void main(String[] args) throws Exception {
		// new initial context to access by JNDI to COMMAND DataDescription
		InitialContext ic = ContextUtils.getContext();

		// gets inputstream
		Object filein = (Object) ic.lookup(DATA_DESCRIPTION_NAME);
		// reads content of inout stream
		StringBuilder recordsSB = read((InputStream) filein);
		// trims result to see if is empty
		String records = recordsSB.toString().trim();
		if (records.length() > 0) {
			// list with all gdgs because it checks command syntax before starting creation
			List<Command> list = new LinkedList<Command>();

			// splits with command separator ";"
			String[] commands = records.toString().split(COMMAND_SEPARATOR);
			for (int i = 0; i < commands.length; i++) {
				// removes all useless blanks leaving a single blank
				String[] s = StringUtils.split(commands[i], " ");
				String commandLine = StringUtils.join(s, ' ');
				// logs which command is parsing
				System.out.println(AntUtilMessage.JEMZ025I.toMessage().getFormattedMessage(commandLine));

				Command command = null;
				if (StringUtils.startsWith(commandLine, Start.COMMAND_KEYWORD)){
					command = new Start(commandLine);
				} else if (StringUtils.startsWith(commandLine, Drain.COMMAND_KEYWORD)){
					command = new Drain(commandLine);
				} else {
					throw new ParseException(AntUtilMessage.JEMZ026E.toMessage().getFormattedMessage("NODES", commandLine), 0);
				}
				// adds to list
				list.add(command);
			}

			// compute all valid commands
			for (Command cmd : list) {
				cmd.execute();
			}
		}
	}
}