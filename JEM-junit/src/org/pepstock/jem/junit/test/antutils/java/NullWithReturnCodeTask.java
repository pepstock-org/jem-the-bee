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
package org.pepstock.jem.junit.test.antutils.java;

import org.pepstock.jem.Result;
import org.pepstock.jem.util.Parser;

/**
 * Is a utility (both a task ANT and a main program) that return doing nothing.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class NullWithReturnCodeTask {
	
	/**
	 * Main program, called by StepJava class. Sets the exit code passed by argument.
	 * 
	 * @param args exit code to use
	 */
	public static void main(String[] args) {
		int exitCode = Result.SUCCESS;
		if (args != null && args.length == 1){
			exitCode = Parser.parseInt(args[0], Result.SUCCESS);
		}
		System.exit(exitCode);
	}

}