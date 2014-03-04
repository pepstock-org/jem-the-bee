/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
import java.util.List;
import java.util.concurrent.Callable;

import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.TimeUtils;

/**
 * Using SIGAR, returns system information in TOP UNIX command format.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Top implements Callable<String>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final long INTERVAL = TimeUtils.SECOND;

	/**
	 * Header and then fields showed by UI
	 */
	static final String[] HEADER = new String[] { "PID ", "USER    ", "STIME   ", "SIZE    ", "RSS     ", "SHARE   ", "STATE   ", "TIME    ", "%CPU  ", "COMMAND" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws SigarException {
		Sigar sigar = new Sigar();
		StringBuilder sb = new StringBuilder();
		// performs twice the same commands
		// to have a consistent information.
		for (int k = 0; k < 2; k++) {
			// commons information 
			// about system
			sb.append(Uptime.getInfo(sigar));
			sb.append('\n');
			sb.append(toString(sigar.getProcStat()));
			sb.append('\n');
			sb.append(sigar.getCpuPerc());
			sb.append('\n');
			sb.append(sigar.getMem());
			sb.append('\n');
			sb.append(sigar.getSwap());
			sb.append('\n');
			sb.append('\n');
			for (int i = 0; i < HEADER.length; i++) {
				sb.append(HEADER[i]).append(' ');
			}
			sb.append('\n');
			long[] pids = sigar.getProcList();

			// scans all pid and extracts their info
			for (int i = 0; i < pids.length; i++) {
				long pid = pids[i];

				List<String> info;
				try {
					info = Ps.getInfo(sigar, pid);
				} catch (SigarException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// process may have gone away
					continue; 
				}

				sb.append(Ps.join(info));
				sb.append('\n');
			}
			// if first excution, removes all
			// and wait for interval
			if (k == 0) {
				sb.delete(0, sb.length());
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Returns System information in string format
	 * @param stat system statistics
	 * @return string representation of stats 
	 */
	private String toString(ProcStat stat) {
		return stat.getTotal() + " processes: " + stat.getSleeping() + " sleeping, " + stat.getRunning() + " running, " + stat.getZombie() + " zombie, " + stat.getStopped() + " stopped... " + stat.getThreads() + " threads";
	}

}