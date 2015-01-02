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
package org.pepstock.jem.node.system;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
import org.pepstock.jem.Job;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.OSProcess;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.util.Parser;

/**
 * Extracts cpu and memory usage of job in execution.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GetJobSystemActivity implements Callable<JobSystemActivity>, Serializable {

	private static final long serialVersionUID = 1L;

	private Job job = null;

	// It waits half second because otherwise the cpu consumption is not
	// updated on Linux
	private static final long INTERVAL = 500;

	/**
	 * Constructs the command using job
	 * 
	 * @param job job instance used to get system activity
	 */
	public GetJobSystemActivity(Job job) {
		this.job = job;
	}

	/**
	 * Checks if job is the same of passed and returns system resources
	 * consumption
	 * 
	 * @return system activity object with all used resources by the job
	 * @throws SigarException 
	 * @throws Exception occurs if errors
	 */
	@Override
	public JobSystemActivity call() throws SigarException  {
		JobSystemActivity activity = new JobSystemActivity();

		// check if there's a job current in execution. If not, do nothing
		if (Main.CURRENT_TASKS.containsKey(job.getId())) {
			// Checks if the job, passed as parameter, is the same of current
			// node. If not, logs a warning

			// parse process id because the form is pid@hostname
			String pid = StringUtils.substringBefore(job.getProcessId(), "@");
			long longPid = Parser.parseLong(pid, -1L);

			// if pid is correct
			if (longPid != -1L) {
				// gets sigar clearing the cache
				Sigar sigar = new Sigar();
				SigarProxy proxyFirst = SigarProxyCache.newInstance(sigar, 0);

				// gets all processes
				OSProcess head = getProcessesTree(longPid, proxyFirst);
				// gets cpu
				long cpuFirst = getCpu(head);
				sigar.close();
				// clear SIGAR
				SigarProxyCache.clear(proxyFirst);
				// wait for another sample
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}

				// gets sigar clearing the cache
				sigar = new Sigar();
				SigarProxy proxyLast = SigarProxyCache.newInstance(sigar, 0);
				// gets new OSprocess
				head = getProcessesTree(longPid, proxyLast);
				// gets CPU
				long cpuLast = getCpu(head);

				// calculate consumed CPU
				long cpuUsed = cpuLast - cpuFirst;
				// gets possible using the processors amount
				long totPossibleCpu = INTERVAL * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				activity.setCpu(cpuLast);
				activity.setCpuPerc(cpuUsedPercent);
				// gets MEMORY
				activity.setMemory(getMemory(head));
				activity.setActive(true);
				sigar.close();

				activity.setProcess(head);
			}
		}
		return activity;
	}

	/**
	 * Gets all consumed memory by whole chain of processes
	 * 
	 * @param head root of all processes chain in OSProcess
	 * @return cpu consumption
	 */
	private long getCpu(OSProcess head) {
		return getCpu(head, 0L);
	}

	/**
	 * Gets all consumed CPU by whole chain of processes
	 * 
	 * @param child process child
	 * @param totalParm consumed CPU already calculated
	 * @return all consumed CPU
	 */
	private long getCpu(OSProcess child, long totalParm) {
		long total = totalParm + child.getCpu();
		for (OSProcess process : child.getChildren()) {
			long tot = getCpu(process, total);
			total =+ tot;
		}
		return total;
	}

	/**
	 * Gets all consumed memory by whole chain of processes
	 * 
	 * @param head root of all processes chain in OSProcess
	 * @return memory consumption
	 */
	private long getMemory(OSProcess head) {
		return getMemory(head, 0L);
	}

	/**
	 * Gets all consumed memory by whole chain of processes
	 * 
	 * @param child process child
	 * @param totalParm consumed MEMORY already calculated
	 * @return all consumed memory
	 */
	private long getMemory(OSProcess child, long totalParm) {
		long total = totalParm + child.getMemory();
		for (OSProcess process : child.getChildren()) {
			long tot = getMemory(process, total);
			total =+ tot;
		}
		return total;
	}

	/**
	 * Gets whole chain of processses starting from a process ID.
	 * 
	 * @param pid parent process ID
	 * @param proxy Sigar instance
	 * @return OSProcess with all processes
	 */
	private OSProcess getProcessesTree(long pid, SigarProxy proxy) throws SigarException {
		OSProcess head = new OSProcess();
		head.setPid(pid);
		head.setCommand(proxy.getProcState(pid).getName());
		head.setMemory(proxy.getProcMem(pid).getResident());
		head.setCpu(proxy.getProcCpu(pid).getTotal());
		long[] allProcessesId = proxy.getProcList();

		loadChildrenProcessId(head, allProcessesId, proxy);

		return head;
	}

	/**
	 * Scans all processes search inside of an array of processes ID by Sigar.
	 * Adds a process to parent creating a tree of processes.
	 * 
	 * @param parent parent OSprocess instances
	 * @param allProcessesId array with all Process ID
	 * @param proxy Sigar instance
	 */
	private void loadChildrenProcessId(OSProcess parent, long[] allProcessesId, SigarProxy proxy) {
		for (long processId : allProcessesId) {
			ProcState state;
			try {
				state = proxy.getProcState(processId);
				if (state.getPpid() == parent.getPid()) {
					OSProcess proc = new OSProcess();
					proc.setPid(processId);
					proc.setCommand(state.getName());
					proc.setMemory(proxy.getProcMem(processId).getResident());
					proc.setCpu(proxy.getProcCpu(processId).getTotal());
					parent.getChildren().add(proc);
					loadChildrenProcessId(proc, allProcessesId, proxy);
				}
			} catch (SigarException e) {
				// ignore
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
	}
}