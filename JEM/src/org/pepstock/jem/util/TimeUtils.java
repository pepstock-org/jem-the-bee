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

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.util.filters.ParseException;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class TimeUtils {
	
	/**
	 * One second in milliseconds
	 */
	public static final long SECOND = 1000;
	
	/**
	 * Seconds per minute
	 */
	public static final int SECONDS_FOR_MINUTE = 60;
	
	/**
	 * One minute in milliseconds
	 */
	public static final long MINUTE = SECOND * SECONDS_FOR_MINUTE;
	
	/**
	 * Minutes for hour
	 */
	public static final int MINUTES_FOR_HOUR = 60;
	
	/**
	 * One hour in milliseconds
	 */
	public static final long HOUR = MINUTE * MINUTES_FOR_HOUR;
	
	/**
	 * Hours per day
	 */
	public static final int HOURS_FOR_DAY = 24;
	
	/**One day in milliseconds
	 * 
	 */
	public static final long DAY = HOUR * HOURS_FOR_DAY;

	/**
	 * Days string representation
	 */
	public static final String D_STRING = "d";
	
	/**
	 * Hours string representation
	 */
	public static final String H_STRING = "h";
	
	/**
	 * Minutes string representation
	 */
	public static final String M_STRING = "m";
	
	/**
	 * Days char representation
	 */
	public static final char D = 'd';
	
	/**
	 * Hours char representation
	 */
	public static final char H = 'h';
	
	/**
	 * Minutes char representation
	 */
	public static final char M = 'm';
	
	/**
	 * To avoid any instantiation
	 */
	private TimeUtils() {
	}
	
	/**
	 * Returns the milliseconds corrisponding to a string in the format:
	 * - [0-9]+[d|h|m]
	 * like:
	 * - 2d (two days) or
	 * - 3h (three hours) or
	 * - 15m (fifteen minutes)
	 * and so on.
	 * 
	 * @param tokenValue the value to be parsed
	 * @return the milliseconds identified by parameter
	 * @throws ParseException if the parameter cannot be parsed
	 */
	public static long parseDuration(String tokenValue) throws ParseException {
		// validate the value!
		if (!(StringUtils.endsWithIgnoreCase(tokenValue, D_STRING) 
				|| StringUtils.endsWithIgnoreCase(tokenValue, H_STRING) 
				|| StringUtils.endsWithIgnoreCase(tokenValue, M_STRING))) {
			throw new ParseException("Invalid time unit!");
		}
		
		// parse the date value
		int timeUnitIndex = tokenValue.length()-1;
		char timeUnit = tokenValue.charAt(timeUnitIndex);
		int timeValue;
		try {
			timeValue = Integer.parseInt(tokenValue.substring(0, timeUnitIndex));
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid time value!", e);
		}

		long time;
		switch (timeUnit) {
			case D:
				time = timeValue * DAY;
				break;
			case H:
				time = timeValue * HOUR;
				break;
			case M:
				time = timeValue * MINUTE;
				break;
			default:
				throw new ParseException("Invalid time unit!");
		}

		return time;
	}
	
}