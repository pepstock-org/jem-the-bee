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
package org.pepstock.jem.node.tasks.platform;

import java.io.IOException;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.tasks.shell.JavaCommand;
import org.pepstock.jem.node.tasks.shell.Shell;

/**
 * This interface is used to get different system commands on different OS.<br>
 * That's necessary because when you start or kill a process, you need to know the shell
 * to use and the specific command.<br> One of main reason of this implementation is 
 * to be able to collect std err and out by redirection of OS (to avoid to loose data by pipes) and
 * SUDO linux implementation.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 *
 */
public interface Platform {

	/**
	 * Gets the shell to use to launch system commands
	 * 
	 * @return shell object
	 */
	Shell getShell();
	
	/**
	 * Gets the command to launch the job and this is use for the process.<br>
	 * Returns the complete command, rederection statements included.<br>
	 * We use ProcessBuilder and its <code>command</code> method as following:<br>
	 * 
	 * ProcessBuilder.command([shell], [shell-parameter], [command])
	 * 
	 * @param job job to execute
	 * @param command <code>java</code> command to launch. It has got all parameter necessary to JC framework
	 * @param sudo if <code>true</code>, a <code>sudo</code> command it's necessary
	 * @return complete command
	 * @throws IOException if any error ocurs
	 */
	String getCommand(Job job, JavaCommand command, boolean sudo) throws IOException;
	
	/**
	 * It kills the running process, where job is in execution.
	 * 
	 * @param pid Process id of job to cancel
	 * @param user user of job (needed in case of SUDO)
	 * @param force if <code>true</code>, cancel command use force attribute
	 * @param sudo if <code>true</code>, a <code>sudo</code> command it's necessary
	 * @return <code>true</code> if kill ends correctly otherwise <code>false</code> 
	 */
	boolean kill(long pid, String user, boolean force, boolean sudo); 
	
}