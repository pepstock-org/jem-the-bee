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
package org.pepstock.jem.ant.tasks.utilities.scripts;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.tasks.utilities.ShellScriptTask;

/**
 * Shell script task, which uses POWERSHELL shell to execute the content of ANT task element.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class PowerShellScriptTask extends ShellScriptTask {
	
	// shell command
	private static final String SHELL = "powershell.exe";
	
	private static final String ARG_0 = "-noninteractive";

	private static final String ARG_1 = "-executionpolicy";

	private static final String ARG_2 = "bypass";

	private static final String ARG_3 = "-nologo";
	
	private static final String ARG_4 = "-command";
	
	// creates a file with this extension
	private static final String SUFFIX = ".ps1";
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.ShellScriptTask#execute()
	 */
	@Override
	public void execute() throws BuildException {
		// sets shell and suffix of file name
    	setShell(SHELL);
    	setSuffix(SUFFIX);
    	// adds arguments for
    	// CMD windows shell
    	super.createArg().setValue(ARG_0);
    	super.createArg().setValue(ARG_1);
    	super.createArg().setValue(ARG_2);
    	super.createArg().setValue(ARG_3);
    	super.createArg().setValue(ARG_4);
    	// executes the script
       	super.execute();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.ShellScriptTask#getScriptName(java.io.File)
	 */
	@Override
	protected String getScriptName(File file) {
		String name = file.getAbsolutePath();
		// checks if the script file name contains a blank.
		// if yes, it uses the syntax &"[filename]"
		if (name.contains(" ")){
			return "&\""+name+"\"";
		} else {
			return name;
		}
	}
	
	
}