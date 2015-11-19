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

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.tasks.StepJava;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.TimeUtils;

/**
 * Is a utility (both a task ANT and a main program) that wait forever or for seconds.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class WaitTask extends StepJava {
	
	/**
	 * Empty constructor
	 */
	public WaitTask() {
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava).
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		super.setClassname(WaitTask.class.getName());
		super.execute();
	}

	/**
	 * Main program, called by StepJava class. It reads arguments.<br>
	 * If there is the argument, is used as number of seconds to wait, otherwise waits forever.
	 * 
	 * @param args number of seconds to wait
	 * @throws InterruptedException if wait state is interrupted
	 */
	public static void main(String[] args) throws InterruptedException {
		int seconds = 0;
		// args are null, second sets to 0
		if (args != null && args.length > 0){
			seconds = Parser.parseInt(args[0], 0);
		}
		// if seconds == 0, waits forever
		Object lock = new Object();
		synchronized (lock) {
			if (seconds > 0){
				lock.wait(seconds * TimeUtils.SECOND);
			} else{
				lock.wait();
			}
		}
	}

}