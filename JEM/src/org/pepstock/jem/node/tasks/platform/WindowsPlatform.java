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

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.tasks.shell.JavaCommand;
import org.pepstock.jem.node.tasks.shell.Shell;
import org.pepstock.jem.util.CharSet;

/**
 * Represents the WINDOWS platform, creating the command based on WINDOWS CMD shell.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 *
 */
public class WindowsPlatform extends AbstractPlatform {
	
	/**
	 * Default shell command for WINDOWS environment
	 */
	public static final String DEFAULT_WINDOWS_SHELL_NAME = "cmd.exe";

	/**
	 * Default shell parameters for WINDOWS environment
	 */
	public static final String DEFAULT_WINDOWS_SHELL_PARAMETERS = "/C";
	
	private static final Shell SHELL = new Shell(DEFAULT_WINDOWS_SHELL_NAME, DEFAULT_WINDOWS_SHELL_PARAMETERS);
	
	/**
	 * Constant name of JOB file to execute by shell. Value is "job.sh". The file contains the JOB
	 * start command by SUDO
	 */
	public static final String JOB_FILE_CMD = "job.cmd";

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.Platform#getShell()
	 */
	@Override
	public Shell getShell() {
		return SHELL;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.Platform#getCommand(org.pepstock.jem.node.tasks.shell.JavaCommand, boolean)
	 */
	@Override
	public String getCommand(Job job, JavaCommand command, boolean sudo) throws IOException {
		String commandToExecute = null;
		// gets log file
		File logFile = Main.getOutputSystem().getMessagesLogFile(job);
		// redirect all STD error and  output to message log file
		// of the job
		String redirect = "> "+FilenameUtils.normalize(logFile.getAbsolutePath(), true)+" 2>&1";
		// if sudo has been activated
		if (sudo){
			// it creates a job shell file
			// with all command to execute.
			// the file is created on output folder of the job
			File outputFolder = Main.getOutputSystem().getOutputPath(job);
			File scriptFile = new File(outputFolder, JOB_FILE_CMD);
			write(scriptFile, command);
			commandToExecute =  scriptFile.getAbsolutePath()+" "+redirect;
		} else {
			// executes the command as is
			commandToExecute =  command.toCommandLine()+" "+redirect;
		}
		return commandToExecute;
	}
	

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.tasks.platform.AbstractPlatform#getKillCommand(long, java.lang.String, boolean, boolean)
	 */
	@Override
	public String getKillCommand(long pid, String user, boolean force, boolean sudo) {
		// cancel all children of PID, always in FORCE mode
		// because on windows you can't cancel the children
		// without force
		return "taskkill /T /F /PID "+pid;
	}	

	/**
	 * Writes a script file, using WINDOWS CMD syntax, to execute the job
	 * 
	 * @param file file to write with all statements
	 * @param job job which must be executed
	 * @param jCommand java command to use
	 * @throws IOException if any errors occurs
	 */
	private void write(File file, JavaCommand jCommand) throws IOException{
		PrintWriter fos = null;
		try {
			// writes the job shell script
			fos = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), CharSet.DEFAULT));
			// echo off
			fos.println("@echo off");
			// if it has got the classpath
			if (jCommand.getClassPath() != null){
				// gets separator
				String pathSeparator = File.pathSeparator;
				String classPathProperty = jCommand.getClassPath();
				// splits classpath
				String[] filesNames = classPathProperty.split(pathSeparator);
				// creates a record of shell script file
				// setting all classpath				
				for (int i=0; i<filesNames.length; i++){
					if (i==0){
						fos.println("set CLASSPATH="+filesNames[i]);
					} else {
						fos.println("set CLASSPATH=%CLASSPATH%;"+filesNames[i]);
					}
				}
			}
			// writes the command
			fos.println(jCommand.toCommandLine());
		} finally {
			// ALWAYS it closes the widnows CMD file
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