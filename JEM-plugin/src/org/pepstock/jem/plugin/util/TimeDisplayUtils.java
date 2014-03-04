/*******************************************************************************
 * Copyright (c) 2012-2014 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.util;

import java.util.Date;

import org.pepstock.jem.util.TimeUtils;

/**
 * Contains utility method for displaying a human-readable String of a time diff
 * 
 * @author Marco "Fuzzo" Cuccato
 */
public class TimeDisplayUtils {

	/**
	 * NORMAL type of time display
	 */
	public static final int NORMAL = 0;

	/**
	 * VERBOSE type of time display
	 */
	public static final int VERBOSE = 1;
	
	/**
	 * Timestamp format
	 */
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Private constructor to avoid new instantiations 
	 */
	private TimeDisplayUtils() {
	}

	/**
	 * Return the elapsed time String until start
	 * @param startMillis start millisecond  point of time
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(long startMillis) {
		return getReadableTimeDiff(startMillis, NORMAL);
	}

	/**
	 * Return the elapsed time String until start
	 * @param startMillis  start millisecond point of time
	 * @param display type of time display
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(long startMillis, int display) {
		return getReadableTimeDiff(System.currentTimeMillis(), startMillis, display);
	}

	/**
	 * Return the elapsed time String from start to end
	 * 
	 * @param end end date point of time
	 * @param start start date point of time
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(Date end, Date start) {
		return getReadableTimeDiff(end, start, NORMAL);
	}

	/**
	 * Return the elapsed time String from start to end
	 * 
	 * @param end end date point of time
	 * @param start start date point of time
	 * @param display type of time display
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(Date end, Date start, int display) {
		return getReadableTimeDiff(end.getTime(), start.getTime(), display);
	}

	/**
	 * Return the elapsed time String until start
	 * @param start start date  point of time
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(Date start) {
		return getReadableTimeDiff(start, NORMAL);
	}

	/**
	 * Return the elapsed time String until start
	 * @param start  start date  point of time
	 * @param display type of time display
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(Date start, int display) {
		return getReadableTimeDiff(System.currentTimeMillis(), start.getTime(), display);
	}

	/**
	 * Return the elapsed time String until start
	 * @param endMillis  end millisecond point of time
	 * @param startMillis  start millisecond  point of time
	 * @param display type of time display
	 * @return readable time difference
	 */
	public static String getReadableTimeDiff(long endMillis, long startMillis, int display) {
		long diff = endMillis - startMillis;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		long days = 0;
		if (diff > TimeUtils.DAY) {
			days = diff / TimeUtils.DAY;
			diff = diff - days * TimeUtils.DAY;
		}

		if (diff > TimeUtils.HOUR) {
			hours = diff / TimeUtils.HOUR;
			diff = diff - hours * TimeUtils.HOUR;
		}

		if (diff > TimeUtils.MINUTE) {
			minutes = diff / TimeUtils.MINUTE;
			diff = diff - minutes * TimeUtils.MINUTE;
		}
		seconds = diff / TimeUtils.SECOND;
		
		return getText(days, hours, minutes, seconds, display);

	}

	/**
	 * Creates string representation of tiem difference
	 * 
	 * @param days number od days
	 * @param hours number of hour
	 * @param minutes number of minutes
	 * @param seconds number of seconds
	 * @param display  type of time display
	 * @return readable time difference
	 */
	private static String getText(long days, long hours, long minutes, long seconds, int display){
		StringBuilder sb = new StringBuilder();
		if (display == VERBOSE){
			if (days != 0) {
				sb.append(days).append(" d ");
				sb.append(hours).append(" h ");
				sb.append(minutes).append(" m");
			} else if (hours != 0) {
				sb.append(hours).append(" h ");
				sb.append(minutes).append(" m");
			} else{
				sb.append(minutes).append(" m");
			}
		} else {
			if (days != 0) {
				sb.append(days).append(".");
				sb.append((hours < 10) ? "0" + hours : hours).append(":");
				sb.append((minutes < 10) ? "0" + minutes : minutes).append(":");
				sb.append((seconds < 10) ? "0" + seconds : seconds);
			} else if (hours != 0) {
				sb.append(hours).append(":");
				sb.append((minutes < 10) ? "0" + minutes : minutes).append(":");
				sb.append((seconds < 10) ? "0" + seconds : seconds);
			} else if (minutes != 0) {
				sb.append(minutes).append(":");
				sb.append((seconds < 10) ? "0" + seconds : seconds);
			} else {
				sb.append(seconds).append(" s");
			}
		}

		return sb.toString();
	}
}