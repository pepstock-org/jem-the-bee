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

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.LinkedList;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.StringRefAddr;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.utilities.gdg.CleanUp;
import org.pepstock.jem.ant.tasks.utilities.gdg.Command;
import org.pepstock.jem.ant.tasks.utilities.gdg.Define;
import org.pepstock.jem.ant.tasks.utilities.gdg.Delete;
import org.pepstock.jem.ant.tasks.utilities.gdg.Rebuild;
import org.pepstock.jem.ant.tasks.utilities.gdg.Rename;
import org.pepstock.jem.jppf.DataStreamNameClassPair;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;

import com.thoughtworks.xstream.XStream;

/**
 * Is a utility (both a task ANT and a main program) that manages GDGs.<br>
 * Needs a <code>COMMAND</code> data description which must contain all
 * commands.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class GDGTask extends AntUtilTask {

	private static final String DATA_DESCRIPTION_NAME = "COMMAND";
	
	private static final String COMMAND_SEPARATOR = ";";
	
	private static final XStream STREAMER = new XStream();

	/**
	 * Empty constructor
	 */
	public GDGTask() {
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava) to prepare the COMMAND data description.
	 * 
	 * @throws BuildException occurs if an error occurs
	 * @throws InterruptedException 
	 */
	@Override
	public void execute() throws BuildException {
		super.setClassname(GDGTask.class.getName());
		super.execute();
	}

	/**
	 * Main program, called by StepJava class. It reads the InputStream defined
	 * as :<br>
	 * <ul>
	 * <li> a INLINE data decription defined at runtime which contains all data description. Necessary to get GDG name by DDname. Done also to lock GDG</li>
	 * <li> COMMAND data description with all define GDG commands</li>
	 * </ul>
	 * <br>
	 * Before to start creating GDG, checks all command syntax
	 * 
	 * @param args uuid of data description which contains all data descriptions so commands are able to get GDG name
	 * @throws Exception if COMMAND data description doesn't exists, if an
	 *             error occurs dduring the command parsing, if data mount point
	 *             is null.
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
			LinkedList<Command> list = new LinkedList<Command>();

			// splits with command separator ";"
			String[] commands = records.toString().split(COMMAND_SEPARATOR);
			for (int i = 0; i < commands.length; i++) {
				// removes all useless blanks leaving a single blank
				String[] s = StringUtils.split(commands[i], " ");
				String commandLine = StringUtils.join(s, ' ');
				// logs which command is parsing
				System.out.println(AntUtilMessage.JEMZ025I.toMessage().getFormattedMessage(commandLine));

				Command command = null;
				if (StringUtils.startsWith(commandLine, Define.COMMAND_KEYWORD)){
					command = new Define(commandLine);
				} else if (StringUtils.startsWith(commandLine, Rebuild.COMMAND_KEYWORD)){
					command = new Rebuild(commandLine);
				} else if (StringUtils.startsWith(commandLine, CleanUp.COMMAND_KEYWORD)){
					command = new CleanUp(commandLine);
				} else if (StringUtils.startsWith(commandLine, Delete.COMMAND_KEYWORD)){
					command = new Delete(commandLine);
				} else if (StringUtils.startsWith(commandLine, Rename.COMMAND_KEYWORD)){
					command = new Rename(commandLine);
				} else {
					throw new ParseException(AntUtilMessage.JEMZ026E.toMessage().getFormattedMessage("GDG", commandLine), 0);
				}
				// load DD datasets
				NamingEnumeration<NameClassPair> lists = ic.list(command.getDDName());
				if (lists.hasMore()){
					NameClassPair pair = lists.next();
					// checks if is datastream
					// only datastreams are changed
					if (pair instanceof DataStreamNameClassPair){
						DataStreamNameClassPair dsPair = (DataStreamNameClassPair) pair;
						DataStreamReference prevReference = (DataStreamReference)dsPair.getObject();
						// gets data description XML defintion
						// adding it to a new reference, for remote access
						StringRefAddr sra = (StringRefAddr) prevReference.get(StringRefAddrKeys.DATASTREAMS_KEY);
						// creates DataDescritpionImpl object using XStream (used to
						// serialize)
						DataDescriptionImpl ddImpl = (DataDescriptionImpl) STREAMER.fromXML((String) sra.getContent());
						command.setDataDescriptionImpl(ddImpl);
					} else {
						throw new IOException(AntMessage.JEMA002E.toMessage().getFormattedMessage(command.getDDName()));
					}
				} else {
					throw new IOException(AntMessage.JEMA002E.toMessage().getFormattedMessage(command.getDDName()));
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