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
package org.pepstock.jem.node.tasks.platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.tasks.shell.JavaCommand;
import org.pepstock.jem.node.tasks.shell.Shell;
import org.pepstock.jem.util.CharSet;

/**
 * Represents the UNIX like platform, creating the command based on BASH shell.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 * 
 */
public class UnixPlatform extends AbstractPlatform {

	/**
	 * Constant name of JOB file to execute by shell. Value is "job.sh". The
	 * file contains the JOB start command by SUDO
	 */
	public static final String JOB_FILE_SHELL = "job.sh";

	/**
	 * Default shell command for UNIX environment
	 */
	public static final String DEFAULT_UNIX_SHELL_NAME = "bash";

	/**
	 * Default shell parameters for UNIX environment
	 */
	public static final String DEFAULT_UNIX_SHELL_PARAMETERS = "-c";

	private static final Shell SHELL = new Shell(DEFAULT_UNIX_SHELL_NAME, DEFAULT_UNIX_SHELL_PARAMETERS);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.tasks.platform.Platform#getShell()
	 */
	@Override
	public Shell getShell() {
		return SHELL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.node.tasks.platform.Platform#getCommand(org.pepstock
	 * .jem.Job, org.pepstock.jem.node.tasks.shell.JavaCommand, boolean)
	 */
	@Override
	public String getCommand(Job job, JavaCommand command, boolean sudo) throws IOException {
		String commandToExecute = null;
		
		// sets classpath using the variable 
		// to avoid to have a command line too long
		command.setJavaOptions("-cp $CLASSPATH");

		// gets log file
		File logFile = Main.getOutputSystem().getMessagesLogFile(job);
		// redirect all STD error and  output to message log file
		// of the job
		String redirect = "> " + FilenameUtils.normalize(logFile.getAbsolutePath(), true) + " 2>&1";
		// if sudo has been activated
		if (sudo) {
			// it creates a job shell file
			// with all command to execute.
			// the file is created on output folder of the job
			File outputFolder = Main.getOutputSystem().getOutputPath(job);
			File scriptFile = new File(outputFolder, JOB_FILE_SHELL);
			write(scriptFile, job, command);
			commandToExecute = scriptFile.getAbsolutePath() + " " + redirect;
		} else {
			// executes the command as is
			commandToExecute = command.toCommandLine() + " " + redirect;
		}
		return commandToExecute;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.tasks.platform.AbstractPlatform#getKillCommand(long, java.lang.String, boolean, boolean)
	 */
	@Override
	public String getKillCommand(long pid, String user, boolean force, boolean sudo) {
		String command = null;
		// gets list of processes to kill
		List<Long> processes = getProcessesChain(pid);
		String processesString = String.valueOf(pid);
		// scans all processes IDs
		for (Long process : processes) {
			// doesn't kill JEM node using JEM PID
			if (process != pid) {
				// creates the string with all process id,
				// blank separated
				processesString = String.valueOf(process) + " " + processesString;
			}
		}

		// sets the command to execute to cancel this job task
		// if sudo, uses sudo
		if (sudo) {
			// checks if force is used
			if (force) {
				command = "sudo -n -u " + user + " -i kill -9 " + processesString;
			} else {
				command = "sudo -n -u " + user + " -i kill " + processesString;
			}
		} else {
			// checks if force is used
			if (force) {
				command = "kill -9 " + processesString;
			} else {
				command = "kill " + processesString;
			}
		}
		return command;
	}

	/**
	 * Writes a script file, using BASH syntax, to execute the job
	 * 
	 * @param file file to write with all statements
	 * @param job job which must be executed
	 * @param jCommand java command to use
	 * @throws IOException if any errors occurs
	 */
	private void write(File file, Job job, JavaCommand jCommand) throws IOException {
		// gets user
		String user = job.isUserSurrogated() ? job.getJcl().getUser() : job.getUser();
		PrintWriter fos = null;
		try {
			// writes the job shell script
			fos = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), CharSet.DEFAULT));
			// if it has got the classpath
			if (jCommand.getClassPath() != null) {
				// gets separator
				String pathSeparator = File.pathSeparator;
				String classPathProperty = jCommand.getClassPath();
				// splits classpath
				String[] filesNames = classPathProperty.split(pathSeparator);
				// creates a record of shell script file
				// setting all classpath
				for (int i = 0; i < filesNames.length; i++) {
					if (i == 0) {
						fos.println("CLASSPATH=" + filesNames[i]);
					} else {
						fos.println("CLASSPATH=$CLASSPATH:" + filesNames[i]);
					}
				}
			}
			// writes the sudo command
			fos.println("sudo -n -u " + user + " -i " + jCommand.toCommandLine());
		} finally {
			// ALWAYS it closes the shell script file
			if (fos != null){
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		// sets the file as EXECUTABLE!!
		file.setExecutable(true, false);
	}
}