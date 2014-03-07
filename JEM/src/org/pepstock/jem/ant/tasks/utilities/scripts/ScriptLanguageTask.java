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

import org.apache.commons.lang3.SystemUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.tasks.utilities.ShellScriptTask;

/**
 * Shell script task, which uses PERL shell to execute the content of ANT task element
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public abstract class ScriptLanguageTask extends ShellScriptTask {
	
	@Override
    public void execute() throws BuildException {
		// checks if is Windows
		// if yes, should add cmd.exe shell
		if (SystemUtils.IS_OS_WINDOWS){
			WindowsScriptTask.setWindowsStepExec(this);
			super.createArg().setValue(getShell());
		} else {
			// otherwise use perl directly.
			// that's valid for UNIX
	    	setShell(getShell());
		}
		// sets suffix of file as .pl, as PERL scripts
		setSuffix(getLanguageSuffix());
       	super.execute();
    }
	
	/**
	 * Returns the shell command or interpreter to use 
	 * @return name of shell or interpreter
	 */
	public abstract String getShell(); 
	
	/**
	 * Returns the standard file name suffix usually used for sources of script languages
	 * @return the standard file name suffix usually used for sources of script languages
	 */
	public abstract String getLanguageSuffix();
	
}