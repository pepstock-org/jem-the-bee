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
import java.util.Date;

import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

/**
 * Display how long the system has been running and the current load averages.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 * 
 */
public class Uptime {

	/**
	 * To avoid any instantiation
	 */
	private Uptime() {
	}

	/**
	 * Gets UPTIME information
	 * 
	 * @param sigar sigar instance
	 * @return return uptime info in string format
	 * @throws SigarException if any errors occurs
	 */
	public static String getInfo(SigarProxy sigar) throws SigarException {
		// gets uptime and formats it
		double uptime = sigar.getUptime().getUptime();
		return "  " + getCurrentTime() + "  up " + formatUptime(uptime);
	}

	/**
	 * Formats uptime
	 * 
	 * @param uptime uptime time number representation
	 * @return uptime string representaiton
	 */
	private static String formatUptime(double uptime) {
		String retval = "";
		// gets days
		int days = (int) uptime / (60 * 60 * 24);
		int minutes, hours;
		// if has got days, puts the labels
		if (days != 0) {
			retval += days + " " + ((days > 1) ? "days" : "day") + ", ";
		}
		// caulates minutes, hours and so on.
		minutes = (int) uptime / 60;
		hours = minutes / 60;
		hours %= 24;
		minutes %= 60;

		// creates the labels
		if (hours != 0) {
			retval += hours + ":" + minutes;
		} else {
			retval += minutes + " min";
		}

		return retval;
	}

	/**
	 * Gets current time in TOP format representation
	 * 
	 * @return current time in string representation
	 */
	private static String getCurrentTime() {
		return new SimpleDateFormat("h:mm a").format(new Date());
	}
}