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
package org.pepstock.jem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Contains a set of static methods useful to parse DATE into string, using
 * different formats.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DateFormatter {

	@SuppressWarnings("javadoc")
	public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
	
	/**
	 * To avoid any instatiation
	 */
	private DateFormatter() {
		
	}

	/**
	 * Gets the current date in string, format <code>yyyyMMdd</code>.
	 * 
	 * @return the current date
	 */
	public static String getCurrentDate() {
		return getCurrentDate(DEFAULT_DATE_FORMAT);
	}

	/**
	 * Gets the current date in string, using passed format.
	 * 
	 * @param format format to use
	 * @return the current date
	 */
	public static String getCurrentDate(String format) {
		return getDate(new Date(), format);
	}

	/**
	 * Gets the date (by millisecond time-stamp) in string, format
	 * <code>yyyyMMdd</code>.
	 * 
	 * @param timestamp milliseconds representation
	 * @return the date string
	 */
	public static String getDate(long timestamp) {
		return getDate(timestamp, DEFAULT_DATE_FORMAT);
	}

	/**
	 * Gets the date (by millisecond time-stamp) in string, using passed format.
	 * 
	 * @param timestamp milliseconds representation
	 * @param format format to use
	 * @return the date string
	 */
	public static String getDate(long timestamp, String format) {
		return getDate(new Date(timestamp));
	}

	/**
	 * Gets the date in string, format <code>yyyyMMdd</code>.
	 * 
	 * @param date date object
	 * @return the date string
	 */
	public static String getDate(Date date) {
		return getDate(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * Gets the date in string, using passed format.
	 * 
	 * @param date date object
	 * @param format format to use
	 * @return the date string
	 */
	public static String getDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
		return formatter.format(date);
	}

	/**
	 * Gets the date using a date in string.
	 * 
	 * @param date date string object
	 * @return the date object
	 * @throws ParseException 
	 */
	public static Date getDate(String date) throws ParseException {
		return getDate(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * Gets the date using a date in string, using passed format.
	 * 
	 * @param date date string object
	 * @param format format to use
	 * @return the date object
	 * @throws ParseException 
	 */
	public static Date getDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
		return formatter.parse(date);
	}

	
}