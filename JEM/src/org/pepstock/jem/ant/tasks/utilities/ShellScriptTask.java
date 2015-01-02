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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.StepExec;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.CharSet;

/**
 * Task ANT to execute a script directly written inside of task, creating a temporary file.<br>
 * Is generic shell script executor. A shell name must be indicated.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class ShellScriptTask extends StepExec {

	private StringBuilder script = new StringBuilder();
	private String shell = null;
	private File temporaryScriptFile = null;

	private String suffix = null;

	/**
	 * Empty constructor
	 */
	public ShellScriptTask() {
	}

	/**
	 * Adds line in the ANT task, changes possible variables.
	 * 
	 * @param line line of code
	 */
	public void addText(String line) {
		script.append(getProject().replaceProperties(line));
	}

	/**
	 * Sets the shell to call
	 * 
	 * @param shell to use
	 */
	public void setShell(String shell) {
		this.shell = shell;
	}

	@Override
	public void setExecutable(String exec) {
		setShell(exec);
	}

	/**
	 * Command is not allowed. Commands must be elements of task
	 * 
	 * @param commandLine command line
	 */
	public void setCommand(Commandline commandLine) {
		// nop
	}

	/**
	 * Sets the suffix for the temporary file.
	 * 
	 * @param suffix to use
	 */

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Execute the task
	 * 
	 * @throws if shell is missing or execution will end in error.
	 */
	public void execute() throws BuildException {

		// if shell is missing, throws an exception
		if (shell == null) {
			throw new BuildException(AntMessage.JEMA019E.toMessage().getFormattedMessage());
		}

		// writes scripts, sets file as argument and shell name as executable
		// and executes!
		try {
			writeScript();
			super.createArg().setValue(temporaryScriptFile.getAbsolutePath());
			super.setExecutable(shell);
			super.execute();
		} finally {
			// removes always the file
			if (temporaryScriptFile != null) {
				boolean isDeleted = temporaryScriptFile.delete();
				// delete anyway the JCL file (temporary)
				if (!isDeleted) {
					// nop
				}
			}
		}
	}

	/**
	 * Writes the script lines to a temporary file.
	 * @throws if IOException occurs during the writing of script
	 */
	protected void writeScript() throws BuildException {
		FileOutputStream fos = null;
		try {
			// creates a temporary file 
			temporaryScriptFile = File.createTempFile("script", suffix, null);
			temporaryScriptFile.deleteOnExit();
			// writes the script
			fos = new FileOutputStream(temporaryScriptFile);
			IOUtils.write(script.toString(), fos, CharSet.DEFAULT);
		} catch (Exception e) {
			throw new BuildException(e);
		} finally {
			if (fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

}