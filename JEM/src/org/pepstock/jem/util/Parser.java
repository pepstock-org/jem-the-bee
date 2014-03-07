/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.util;

/**
 * Contains a set of static methods useful to parse string into numbers of
 * boolean, assigning a default value if an format exception occurs.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Parser {

	/**
	 * To avoid any instatiation
	 */
	private Parser() {
		
	}

	/**
	 * Parses a string into a integer, with default 0 if a exception occurs.
	 * 
	 * @param s string to parse
	 * @return integer value
	 */
	public static int parseInt(String s) {
		return parseInt(s, 0);
	}

	/**
	 * Parses a string into a integer, with default value if a exception occurs.
	 * 
	 * @param s string to parse
	 * @param defaultValue default value
	 * @return integer value
	 */
	public static int parseInt(String s, int defaultValue) {
		try {
			return s == null ? defaultValue : Integer.parseInt(s.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * Parses a string into a long, with default 0L if a exception occurs.
	 * 
	 * @param s string to parse
	 * @return long value
	 */
	public static long parseLong(String s) {
		return parseLong(s, 0L);
	}

	/**
	 * Parses a string into a long, with default value if a exception occurs.
	 * 
	 * @param s string to parse
	 * @param defaultValue default value
	 * @return long value
	 */
	public static long parseLong(String s, long defaultValue) {
		try {
			return s == null ? defaultValue : Long.parseLong(s.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * Parses a string into a double, with default 0.0 if a exception occurs.
	 * 
	 * @param s string to parse
	 * @return double value
	 */
	public static double parseDouble(String s) {
		return parseDouble(s, 0.0);
	}

	/**
	 * Parses a string into a double, with default value if a exception occurs.
	 * 
	 * @param s string to parse
	 * @param defaultValue default value
	 * @return double value
	 */
	public static double parseDouble(String s, double defaultValue) {
		try {
			return s == null ? defaultValue : Double.parseDouble(s.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * Parses a string into a float, with default 0F if a exception occurs.
	 * 
	 * @param s string to parse
	 * @return float value
	 */
	public static float parseFloat(String s) {
		return parseFloat(s, 0F);
	}

	/**
	 * Parses a string into a float, with default value if a exception occurs.
	 * 
	 * @param s string to parse
	 * @param defaultValue default value
	 * @return float value
	 */
	public static float parseFloat(String s, float defaultValue) {
		try {
			return s == null ? defaultValue : Float.parseFloat(s.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * Parses a string into a boolean, with default <code>false</code> if a
	 * exception occurs.
	 * 
	 * @param s string to parse
	 * @return boolean value
	 */
	public static boolean parseBoolean(String s) {
		return parseBoolean(s, false);
	}

	/**
	 * Parses a string into a boolean, with default value if a exception occurs.
	 * 
	 * @param s string to parse
	 * @param defaultValue default value
	 * @return boolean value
	 */
	public static boolean parseBoolean(String s, boolean defaultValue) {
		return s == null ? defaultValue : Boolean.valueOf(s).booleanValue();
	}

	
	/**
	 * Checks if the value is the range suggested
	 * 
	 * @param value value to evaluate if is the range
	 * @param minimum minimum value acceptable
	 * @param maximum maximum value acceptable
	 * @return the value after evaluation in range
	 */
	public static int evaluate(int value, int minimum, int maximum){
		return  Math.max(Math.min(value, maximum), minimum);
	}
	
}