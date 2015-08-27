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
import java.util.LinkedList;
import java.util.Properties;

import javax.naming.InitialContext;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.utilities.archive.Archive;
import org.pepstock.jem.ant.tasks.utilities.archive.Command;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.node.archive.JobOutputArchive;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 *
 */
public class JobOutputArchiveTask extends AntUtilTask {

	private static final String DATA_DESCRIPTION_NAME = "COMMAND";
	
	private static final String COMMAND_SEPARATOR = ";";
	
	/**
	 * Key for the class to load to transform and load data  
	 */
	private static String CLASS = "class";
	
	/**
	 * Empty constructor
	 */
	public JobOutputArchiveTask() {
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
		if (port == null)
			throw new BuildException(AntMessage.JEMA004E.toMessage().getFormattedMessage());
		
		super.setClassname(JobOutputArchiveTask.class.getName());
		super.execute();
	}
	
	/**
	 * Main program, called by StepJava class.
	 * @param args 
	 * 
	 * @throws Exception if COMMAND data description doesn't exists, if an
	 *             error occurs dduring the command parsing, if data mount point
	 *             is null.
	 * @throws java.text.ParseException 
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception{
		// -class mandatory arg
		Option classArg = OptionBuilder.withArgName(CLASS).hasArg().withDescription("class of JobOutputArchive to invoke reading the objects").create(CLASS);
		classArg.setRequired(true);
		
		// parses all arguments
		ArgumentsParser parser = new ArgumentsParser(JobOutputArchiveTask.class.getName());
		parser.getOptions().add(classArg);
		
		// saves all arguments in common variables
		Properties properties = parser.parseArg(args);
		
		String classParam = properties.getProperty(CLASS);
		
		Object objectTL = Class.forName(classParam).newInstance();
		if (!(objectTL instanceof JobOutputArchive)) {
			throw new AntException(AntUtilMessage.JEMZ042E, classParam,JobOutputArchive.class.getName(), objectTL.getClass().getName());
		}
		JobOutputArchive jobOutputArchive = (JobOutputArchive)objectTL;
		
		System.out.println(AntUtilMessage.JEMZ043I.toMessage().getFormattedMessage(jobOutputArchive.getClass().getName()));
		
		
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

				Command command = new Archive(commandLine);
				command.setJobOutputArchive(jobOutputArchive);

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