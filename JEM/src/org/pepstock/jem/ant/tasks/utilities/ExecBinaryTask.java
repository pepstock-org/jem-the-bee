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
package org.pepstock.jem.ant.tasks.utilities;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.StepExec;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * This ANT task is able to call an executable using <code>jem.binary</code> path as default where 
 * locate the executable. If executable is already an absolute path, it doesn't use <code>jem.binary</code>.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class ExecBinaryTask extends StepExec {
	
	private static final String SHELL_UNIX = "bash";
	
	private static final String ARG_0_UNIX = "-c";
	
	private static final String SHELL_WINDOWS = "cmd.exe";
	
	private static final String ARG_0_WINDOWS = "/c";
	
	private String executable = null;

	/**
	 * Sets executable
	 */
	@Override
	public void setExecutable(String exec) {
		this.executable = exec;
	}

	/**
	 * Command is not allowed.
	 * 
	 */
	@Override
	public void setCommand(Commandline commandLine) {
		throw new BuildException(AntMessage.JEMA054E.toMessage().getFormattedMessage());
	}
	
	/**
	 * Executes task, checking platform and loading command to execute
	 */
	@Override
	public void execute() throws BuildException {
		if (executable == null){
			throw new BuildException(AntMessage.JEMA055E.toMessage().getFormattedMessage());
		}
		// checks operating system
		if (SystemUtils.IS_OS_UNIX){
			loadUnixEnv();
		} else if (SystemUtils.IS_OS_WINDOWS){
			loadWindowsEnv();
		} else {
			throw new BuildException(AntMessage.JEMA056E.toMessage().getFormattedMessage(SystemUtils.OS_NAME));
		}
		
		// display command
		log(super.cmdl.describeCommand());
		// execute!
		super.execute();
	}

	/**
	 * Loads command for Windows
	 */
	private void loadWindowsEnv() {
		super.setExecutable(SHELL_WINDOWS);
		// adds executable because uses a commandline
		// and the method to put it at the beggining of arguments list
		super.cmdl.createArgument(true).setValue(normalizeExecutable());
		// adds /c parm because uses a commandline
		// and the method to put it at the beggining of arguments list
		// so is sure to be the first
		super.cmdl.createArgument(true).setValue(ARG_0_WINDOWS);
	}

	/**
	 * Loads command for Unix
	 */
	private void loadUnixEnv() {
		super.setExecutable(SHELL_UNIX);
		// adds executable because uses a commandline
		// and the method to put it at the beggining of arguments list
		super.cmdl.createArgument(true).setValue(normalizeExecutable());
		// adds -c parm because uses a commandline
		// and the method to put it at the beggining of arguments list
		// so is sure to be the first
		super.cmdl.createArgument(true).setValue(ARG_0_UNIX);
		
	}
	
	/**
	 * Creates a string of executable, normalizing the name. That's necessary due to you can write on ANT JCL 
	 * both the relative or the absolute file name.
	 * 
	 * @return a executable string command, normalized
	 * @throws BuildException if binaryPath is null, returns an exception
	 */
	private String normalizeExecutable() throws BuildException{
		// gets the bianry path from the environment
		// variables
		// if binary path is null, exception
		String binaryPath = System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME);
		if (binaryPath == null){
			throw new BuildException(AntMessage.JEMA053E.toMessage().getFormattedMessage());
		}
		
		// normalizes binaryPath using UNIX rules
		binaryPath = FilenameUtils.normalize(binaryPath, true);
		File file = null;

		//checks if the executable is a absolute file name
		// if absolute path is equals return the file 
		// otherwise checks binarypath
		if (executable.startsWith(binaryPath)){
			// if name is absolute
			// creates a new FILE object with full pathname 
			file =  new File(executable);
		} else { 
			// should be relative
			file = new File(executable);
			// normalizes the full path and checks again with the name
			// if equals means that is absolute
			if (!FilenameUtils.normalize(file.getAbsolutePath(), true).equalsIgnoreCase(executable)){
				// is relative!
				// creates a file with dataPath as parent, plus file name  
				file = new File(binaryPath, executable);
			}
		}
		// normalizes using UNIX rules
		return FilenameUtils.normalize(file.getAbsolutePath(), true);
	}


}
