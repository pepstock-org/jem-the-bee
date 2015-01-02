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
package org.pepstock.jem.node.affinity;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.system.Ps;

/**
 * This object is used to collect all system information to use to decide the right affinities for node.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class SystemInfo {
	private static final Sigar SIGAR = new Sigar();

	private Properties runtime = null;

	private Properties environment = null;

	private Properties network = null;

	/**
	 * Empty constructor
	 */
	public SystemInfo() {
	}

	/**
	 * Returns java system properties
	 * 
	 * @return java system properties
	 */
	public Properties getSystemProperties() {
		return System.getProperties();
	}

	/**
	 * Returns runtime information, like processors and memory, in a property object.<br>
	 * Keys are:<br>
	 * <ul>
	 * <li><code>availableProcessors</code>: number of processor</li>
	 * <li><code>freeMemory</code>: amount of free memory in the system</li>
	 * <li><code>totalMemory</code>: amount of available memory in the system</li>
	 * </ul>
 	 * 
	 * @return a properties object.
	 */
	public Properties getRuntimeProperties() {
		// if the instance is null,
		// creates a new instance
		// otherwise it returns the previous instance
		if (runtime == null) {
			runtime = new Properties();
			String s = null;
			try {
				// gets the amount of processor
				s = String.valueOf(Runtime.getRuntime().availableProcessors());
				runtime.setProperty("availableProcessors", s);

				Mem memory = SIGAR.getMem();
				// gets the amount of free memory
				// using SIGAR
				s = String.valueOf(memory.getFree());
				runtime.setProperty("freeMemory", s);
				// gets the amount of total memory
				// using SIGAR
				s = String.valueOf(memory.getTotal());
				runtime.setProperty("totalMemory", s);
			} catch (Exception e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
		return runtime;
	}

	/**
	 * Returns a properties object with all environment variables.
	 * 
	 * @return a properties object with all environment variables.
	 */
	public Properties getEnvironment() {
		// if the instance is null,
		// creates a new instance
		// otherwise it returns the previous instance
		if (environment == null) {
			environment = new Properties();
			// loads all environment variables
			Map<String, String> props = System.getenv();
			for (Map.Entry<String, String> entry : props.entrySet()) {
				environment.setProperty(entry.getKey(), entry.getValue());
			}
		}
		return environment;
	}

	/**
	 * Returns a properties object with network information.<br>
	 * Keys are:<br>
	 * <ul>
	 * <li><code>ipaddresses</code>: list of ipaddresses of system</li>
	 * <li><code>hostnames</code>: list of hostnames of system</li>
	 * </ul>
	 * 
	 * @return a mapping of environment variables to their value.
	 */
	public Properties getNetworkProperties() {
		// if the instance is null,
		// creates a new instance
		// otherwise it returns the previous instance		
		if (network == null) {
			network = new Properties();
			try {
				List<InetAddress> list = loadInetAddress();
				// gets hostname from management
				String hostname = StringUtils.substringAfter(ManagementFactory.getRuntimeMXBean().getName(), "@");
				// adds all ip addresses, formatting them
				network.setProperty("ipaddresses", formatAddresses(list));
				network.setProperty("hostnames", hostname);
			} catch (Exception e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
		return network;
	}

	/**
	 * Returns the list of running processes on machine.<br>
	 * These are the list of fields:<br>
	 * 
	 *  "PID ","USER", "STIME", "SIZE", "RSS", "SHARE", "STATE", "TIME", "%CPU", "COMMAND"
	 *  
	 * @return list of running processes
	 */
	public List<String> getRunningProcesses(){
		List<String> processes = new ArrayList<String>();
		try {
			// gets the list of all processes ID 
			long[] pids = SIGAR.getProcList();
			for (int i = 0; i < pids.length; i++) {
				long pid = pids[i];
				// gets info about processes
				List<String> info = getProcessInfo(pid);
				if (!info.isEmpty()){
					// parses the process info 
					// adding them to the collection
					processes.add(Ps.join(info));
				}
			}
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		return processes;
	}
	/**
	 * Formats a list of InetAddress, comma separated
	 * 
	 * @param addresses a List of <code>InetAddress</code> instances.
	 * @return a string containing a comma-separated list of ipaddresses
	 */
	private String formatAddresses(final List<? extends InetAddress> addresses) {
		StringBuilder sb = new StringBuilder();
		// builds a string
		// with all ip address, comma separated
		for (InetAddress addr : addresses) {
			String ip = addr.getHostAddress();
			if (sb.length() > 0){
				sb.append(", ");
			}
			sb.append(ip);
		}
		return sb.toString();
	}
	
	/**
	 * Returns the list of inet addresses scanning the network interfaces of machine
	 * @return the list of inet addresses scanning the network interfaces of machine
	 */
	private List<InetAddress> loadInetAddress(){
		List<InetAddress> list = new ArrayList<InetAddress>();
		try {
			// scans all network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				// scans all ip addresses
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (addresses.hasMoreElements()) {
					// adds Ip address to the collection
					InetAddress addr = addresses.nextElement();
					list.add(addr);
				}
			}
		} catch (Exception e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		return list;
	}
	
	/**
	 * Returns a list of information about the process ID 
	 * @param pid process ID of the process to gather info
	 * @return a list of information about the process
	 */
	private List<String> getProcessInfo(long pid){
		List<String> info = null;
		try {
			// gets information
			info = Ps.getInfo(SIGAR, pid);
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			// process may have gone away
			return new ArrayList<String>();
		}
		return info;
	}
}