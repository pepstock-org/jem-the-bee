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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.GenericScriptFactory;
import org.pepstock.jem.ant.tasks.utilities.ShellScriptTask;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.VariableSubstituter;

/**
 * Shell script task, which uses POWERSHELL shell to execute the content of ANT task element.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class GenericShellScriptTask extends ShellScriptTask {
	
	private static final String SCRIPT_NAME_VARIABLE = "script.file.name";
	
	private static final String REGEX = "\"([^\"]*)\"|\'([^\']*)\'|(\\S+)";
	
	private List<String> args = new LinkedList<String>();
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.ShellScriptTask#execute()
	 */
	@Override
	public void execute() throws BuildException {
		try {
			// gets teh complete command to be executed
			String hexCommand = getProject().getProperty(GenericScriptFactory.GENERIC_COMMAND_PROPERTY);
			// the Script factory set the value in HEX format to avoid error during XML encoding
			Hex hex = new Hex(CharSet.DEFAULT);
			String command = new String(hex.decode(hexCommand.getBytes(CharSet.DEFAULT)), CharSet.DEFAULT);
			// parses the command
			parse(command);

			String suffix = getProject().getProperty(GenericScriptFactory.GENERIC_JCL_TYPE_PROPERTY);
			// sets suffix of file name
			setSuffix(suffix);
			// executes the script
			super.execute();
		} catch (DecoderException e) {
			throw new BuildException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.ShellScriptTask#getScriptName(java.io.File)
	 */
	@Override
	protected String getScriptName(File file) {
		String name = file.getAbsolutePath();
    	// adds arguments for generic script
    	Properties variables = new Properties();
    	variables.put(SCRIPT_NAME_VARIABLE, name);
		
    	// creates all arguments for ANT
		for (String arg : args){
			super.createArg().setValue(VariableSubstituter.substitute(arg, variables));
		}
    	return name;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.ShellScriptTask#isScriptNameLastArg()
	 */
	@Override
	protected boolean isScriptNameLastArg() {
		// sets to false because it's not sure
		// that the script file name is the last parameter
		return false;
	}

	/**
	 * Parse the command, taking care about " and ' 
	 * @param command all command to parse
	 */
	private void parse(String command){
		// creates the matcher
        Matcher m = Pattern.compile(REGEX).matcher(command);
        
        // sets the shell to false
        // to setShell only once in the cycle
        boolean isShell = false;
        // for all tokens 
        while (m.find()) {
        	String token = null;
        	// matches a token with quotes
            if (m.group(1) != null) {
            	token =  m.group(1);
            } else if (m.group(2) != null) {
            	// matches a token with single quotes
            	token =  m.group(2);
            } else {
            	// matches plain text
            	token =  m.group(3);
            }
            // if shel is not set,
            // set!! it's the first token
            if (!isShell){
            	setShell(token);
            	isShell = true;
            } else {
            	// adds all arguments
            	args.add(token);
            }
        }
	}
}