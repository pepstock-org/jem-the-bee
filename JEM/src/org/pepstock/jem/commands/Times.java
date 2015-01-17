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
package org.pepstock.jem.commands;

/**
 * Utility that collects all times rekated to the start, submit and end.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class Times {
	
	private static long start = 0L;
	
	private static long submit = 0L;
	
	private static long end = 0L;

	/**
	 * To avoid any instantiation
	 */
	private Times() {
	}

	/**
	 * @return the start
	 */
	public static long getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public static void start() {
		Times.start = System.currentTimeMillis();
	}

	/**
	 * @param submitted the submitted to set
	 */
	public static void submit() {
		Times.submit = System.currentTimeMillis();
	}

	/**
	 * @param ended the ended to set
	 */
	public static void end() {
		Times.end = System.currentTimeMillis();
	}
	
	/**
	 * Returns a string with the times between different steps
	 * @return a string with the times between different steps
	 */
	public static String toReadable(){
		return "time to submit: "+(submit-start)+", elapsed time: "+(end-start);
	}
}
