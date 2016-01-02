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

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class JobIdGenerator {

	// Max length for a long value. It uses numbers of digits of Long.MAX_VALUE
	private static final int LONG_LENGTH = String.valueOf(Long.MAX_VALUE).length();

	public static final int ID_LENGTH = LONG_LENGTH + LONG_LENGTH + 1;

	/**
	 * Job id Format
	 */
	public static final String JOBID_FORMAT = "{0,number}-{1,number}";

	private static final Random RANDOM = new Random();

	private static final AtomicLong COUNTER = new AtomicLong(0);

	/**
	 * To avoid any instantiation
	 */
	private JobIdGenerator() {
	}

	/**
	 * Creates the unique id for the job, normalizing generated id joined to
	 * started time stamp of job.<br>
	 * it pads (on left) with '0' string representation of both id and timestamp
	 * 
	 * @param job job instance, needs to extract started timestamp
	 * @param id job id
	 * @return job id in job format
	 */
	public static String createJobId(Job job, long id) {
		return createJobId(id, job.getSubmittedTime().getTime());
	}

	public static String createRandomJobId() {
		return createJobId(RANDOM.nextInt(Integer.MAX_VALUE), COUNTER.incrementAndGet());
	}

	public static String createJobId(long prefix, long suffix) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.leftPad(String.valueOf(prefix), LONG_LENGTH, "0")).append('-').append(StringUtils.leftPad(String.valueOf(suffix), LONG_LENGTH, "0"));
		return sb.toString();
	}

}
