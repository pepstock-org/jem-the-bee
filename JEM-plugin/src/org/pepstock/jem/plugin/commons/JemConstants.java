/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.plugin.commons;

/**
 * Constants class for common strings and objects
 * @author Marco "Fuzzo" Cuccato
 */
public final class JemConstants {
	
	/*
	 * BASE TYPES
	 */
	
	/**
	 * kilobyte
	 */
	public static final long KB = 1024;
	
	/**
	 * megabyte
	 */
	public static final long MB = KB * 1024;
	
	/*
	 * STRINGs
	 */
	
	/**
	 * (none) 
	 */
	public static final String NONE_BRACKETS = "(none)";
	
	/**
	 * (updating)
	 */
	public static final String UPDATING_BRACKETS = "(updating)";
	
	/**
	 * (unavailable)
	 */
	public static final String UNAVAILABLE_BRACKETS = "(unavailable)";

	/**
	 * (unknown)
	 */
	public static final String UNKNOWN_BRACKETS = "(unknown)";

	/**
	 * yes
	 */
	public static final String YES = "yes";
	
	/**
	 * no
	 */
	public static final String NO = "no";
	
	/**
	 * To avoid any instantiation
	 */
	private JemConstants() {
		
	}
	
}