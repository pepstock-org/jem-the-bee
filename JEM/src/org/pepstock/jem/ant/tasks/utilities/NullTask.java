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

import javax.naming.NamingException;

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.annotations.ReturnCode;
import org.pepstock.jem.ant.tasks.StepJava;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;

/**
 * Is a utility (both a task ANT and a main program) that return doing nothing.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class NullTask extends StepJava {
	
	@ReturnCode
	private static int returnCode = 15;
	
	/**
	 * Empty constructor
	 */
	public NullTask() {
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava).
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		super.setClassname(NullTask.class.getName());
		super.execute();
	}

	/**
	 * Main program, called by StepJava class. It does nothing.
	 * 
	 * @param args not used
	 * @throws NamingException 
	 * @throws InvalidDatasetNameException 
	 */
	public static void main(String[] args) throws NamingException, InvalidDatasetNameException {
		// do nothing
	}

}