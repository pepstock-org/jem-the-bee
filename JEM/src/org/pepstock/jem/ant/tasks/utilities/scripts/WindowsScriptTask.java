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
package org.pepstock.jem.ant.tasks.utilities.scripts;

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.tasks.utilities.ShellScriptTask;

/**
 * Shell script task, which uses CMD shell to execute the content of ANT task element.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class WindowsScriptTask extends ShellScriptTask {
	
	// shell command
	private static final String SHELL = "cmd.exe";
	
	private static final String ARG_0 = "/c";

	private static final String ARG_1 = "call";
	
	// creates a file with this extension
	private static final String SUFFIX = ".bat";
	
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
    	// executes the script
       	super.execute();
    }
    
    /**
     * Sets the shell and parameters to ANT task passed as argument
     * 
     * @param antTask shell script ant task to set
     */
    public static final void setWindowsStepExec(ShellScriptTask antTask){
    	// override the shell and suffix 
    	antTask.setShell(SHELL);
    	antTask.setSuffix(SUFFIX);
    	// sets arguments
    	antTask.createArg().setValue(ARG_0);
    	antTask.createArg().setValue(ARG_1);
    }
}