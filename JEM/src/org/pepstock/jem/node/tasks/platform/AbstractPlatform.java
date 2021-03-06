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
import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Abstract representation of platform.
 * <br>
 * It uses to have common methods and logic cross platform. Every specific
 * logic related to the OS must be implemented extending this class.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public abstract class AbstractPlatform implements Platform {
	
	/**
	 * Gets whole chain of processes starting from a process ID.
	 *  
	 * @param pid parent process ID
	 * @return collections od processes ID
	 */
	public List<Long> getProcessesChain(long pid){
		Sigar sigar = new Sigar();
		// creates a list to have all process
		// chain starting from the parent process ID
		List<Long> processes= new ArrayList<Long>();
		processes.add(pid);
		try {
			// gets all process ids of the machine
			long[] allProcessesId = sigar.getProcList();
			 loadChildProcessId(pid, allProcessesId, processes, sigar);
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		return processes;
	}
	/**
	 * Gets child process search inside of an array of processes ID by Sigar. If there isn't any child, return -1L
	 * @param parentId parent process ID
	 * @param allProcessesId array with all Process ID
	 */
	private void loadChildProcessId(long parentId, long[] allProcessesId, List<Long> processes, Sigar sigar){
		// scans all processes id
		for (long processId : allProcessesId){
			ProcState state;
			try {
				// gets procstate to check if the process id
				// passed as argument of method is a parent 
				state = sigar.getProcState(processId);
				// checks if parent
				if (state.getPpid() == parentId){
					// adds to the list
					processes.add(processId);
					// and recursively checks if parent
					loadChildProcessId(processId, allProcessesId, processes, sigar);
				}
			} catch (SigarException e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.tasks.platform.Platform#kill(int,
	 * java.lang.String, boolean)
	 */
	@Override
	public final boolean kill(long pid, String user, boolean force, boolean sudo) {
		boolean isCancelled = true;
		Process p = null;
		try {
			// sets the this job task has been canceled
			// asks to abstract method to have the KILL command
			// because it depends on OS
			p = Runtime.getRuntime().exec(getKillCommand(pid, user, force, sudo));
			// waits the cancel command is executed
			int rc = p.waitFor();
			// if ends correctly, job cancelled
			if (rc != 0) {
				isCancelled = false;
			}
		} catch (IOException e) {
			isCancelled = false;
			LogAppl.getInstance().emit(NodeMessage.JEMC017E, e);
		} catch (InterruptedException e) {
			isCancelled = false;
			LogAppl.getInstance().emit(NodeMessage.JEMC017E, e);
		} catch (Exception e) {
			isCancelled = false;
			LogAppl.getInstance().emit(NodeMessage.JEMC017E, e);
		} finally {
			// anyway it always destroy the process
			// created to kill the job
			if (p != null){
				p.destroy();
			}
		}
		return isCancelled;
	}
	
	/**
	 * Returns OS command to use to kill the running process, where job is in execution.
	 * 
	 * @param pid Process id of job to cancel
	 * @param user user of job (needed in case of SUDO)
	 * @param force if <code>true</code>, cancel command use force attribute
	 * @param sudo if <code>true</code>, a <code>sudo</code> command it's necessary
	 * @return OS command to use to kill the running process 
	 */
	public abstract String getKillCommand(long pid, String user, boolean force, boolean sudo);
}