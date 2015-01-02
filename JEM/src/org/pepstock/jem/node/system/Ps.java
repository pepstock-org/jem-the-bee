/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Andrea "Stock" Stocchero
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcCredName;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcTime;
import org.hyperic.sigar.ProcUtil;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.pepstock.jem.log.LogAppl;

/**
 * Show processes status. This is used from TOP command
 */
public class Ps {
	
	private static final String UNKNOWN = "???";
	
	/**
	 * To avoid any instantiation
	 */
	private Ps() {
		
	}

	/**
     * Formats list of string, result of gathering process info.<br>
     * One list for one process.
     * 
     * @param info collection of info for process 
     * @return info string representation of list
     */
    public static String join(List<String> info) {
        StringBuilder buf = new StringBuilder();
        // gets amount of headers
        for (int i=0; i<Top.HEADER.length; i++){
        	// gets length of headers for padding
        	int len = Top.HEADER[i].length();
        	buf.append(StringUtils.rightPad(info.get(i), len, ' ')).append(' ');
        }
        return buf.toString();
    }

    /**
     * Gathers all information for process, identified by PID.
     * 
     * @param sigar sigra instance
     * @param pid process ID
     * @return info list of information in string format 
     * @throws SigarException if any errors occurs.
     */
    public static List<String> getInfo(SigarProxy sigar, long pid)
        throws SigarException {

        ProcState state = sigar.getProcState(pid);
        ProcTime time = null;
        String cpuPerc = UNKNOWN;
        
        List<String> info = new ArrayList<String>();
        // adds PID
        info.add(String.valueOf(pid));
        try {
            ProcCredName cred = sigar.getProcCredName(pid);
            // adds USER
            info.add(cred.getUser());
        } catch (SigarException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
            info.add(UNKNOWN);
        }

        try {
            time = sigar.getProcTime(pid);
            // adds started time
            info.add(getStartTime(time.getStartTime()));
        } catch (SigarException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
            info.add(UNKNOWN);
        }

        try {
            ProcMem mem = sigar.getProcMem(pid);
            // adds memoery infos
            info.add(Sigar.formatSize(mem.getSize()));
            info.add(Sigar.formatSize(mem.getResident()));
            info.add(Sigar.formatSize(mem.getShare()));
        } catch (SigarException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
            info.add(UNKNOWN);
        }

        // gets state
        info.add(String.valueOf(state.getState()));

        if (time != null) {
        	// gets CPU time
            info.add(getCpuTime(time));
        } else {
            info.add(UNKNOWN);
        }
        try {
			ProcCpu cpu = sigar.getProcCpu(pid);
			cpuPerc = CpuPerc.format(cpu.getPercent());
			// gets cpu percentage
			info.add(cpuPerc);
		} catch (SigarException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			info.add(UNKNOWN);
		}
        // gets command which is executing in process (main program, not args)
        String name = ProcUtil.getDescription(sigar, pid);
        info.add(name);
        return info;
    }

    /**
     * Formats CPU time
     * @param total cpu time 
     * @return cpu time in string format
     */
    private static String getCpuTime(long total) {
        long t = total / 1000;
        return t/60 + ":" + t%60;
    }

    /**
     * Gets formatted CPU time
     * @param time cput iem object from SIGAR
     * @return formatted CPU time
     */
    private static String getCpuTime(ProcTime time) {
        return getCpuTime(time.getTotal());
    }

    /**
     * Formats started time
     * @param time milliseconds which represent started time
     * @return formatted started time
     */
    private static String getStartTime(long time) {
        if (time == 0) {
            return "00:00";
        }
        long timeNow = System.currentTimeMillis();
        String fmt = "MMMd";

        if ((timeNow - time) < ((60*60*24) * 1000)) {
            fmt = "HH:mm";
        }
        return new SimpleDateFormat(fmt).format(new Date(time));
    }
}