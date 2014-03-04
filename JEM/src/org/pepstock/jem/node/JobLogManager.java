/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.Step;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.DateFormatter;

/**
 * Is a utility class which creates and maintain the information inside of
 * "job.log" file.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobLogManager {
	// used to save the previous cpu extracted. this is a workaround
	private static long PREVIOUS_CPU = 0L;

	private static final MessageFormat HEADER_JOB = new MessageFormat("J E M  job log -- Node {0}");

	private static final MessageFormat ROW_DATE = new MessageFormat("---- {0} ----");

	private static final MessageFormat ROW_USER = new MessageFormat("USERID {0} IS ASSIGNED TO THIS JOB");

	private static final MessageFormat ROW_JOB_INFO = new MessageFormat("{0} ENVIRONMENT {1} - DOMAIN {2} - AFFINITY {3}");
	
	private static final MessageFormat ROW_JOB_PID = new MessageFormat("{0} PROCESS-ID {1}");
	
	private static final MessageFormat ROW_JOB_TIME_STARTED = new MessageFormat("{0} STARTED - TIME={1}");

	private static final String HEADER_JOB_NAME = "JOBNAME";
	
	private static final String HEADER_STEPS = "STEPNAME         RC   CPU(ms)    MEMORY(kb)       ";

	private static final MessageFormat ROW_JOB_TIME_ENDED = new MessageFormat("{0} ENDED - TIME={1} - RC={2}");
	
	/**
	 * To avoid any instantiation
	 */
	private JobLogManager() {
		
	}

	/**
	 * Prints header of job log
	 * 
	 * @param job job instance
	 * @param processId process id of job
	 */
	public static void printHeader(Job job) {
		Main.getOutputSystem().writeJobLog(job, getFormattedMessage(HEADER_JOB, Main.getNode().getLabel()));

		Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_DATE, DateFormatter.getCurrentDate("EEEE, dd MMMM yyyy").toUpperCase()));

		Jcl jcl = job.getJcl();
		Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_USER, (job.isUserSurrogated()) ? job.getJcl().getUser() : job.getUser()));
		Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_INFO, job.getName(), jcl.getEnvironment(), jcl.getDomain(), jcl.getAffinity()));
		Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_TIME_STARTED, job.getName(), DateFormatter.getDate(job.getStartedTime(), "HH:mm:ss ")));
	}

	/**
	 * Prints header of job log
	 * 
	 * @param job job instance
	 * @param processId process id of job
	 */
	public static void printJobStarted(Job job) {
		// clear cpu because here a new process is started
		PREVIOUS_CPU = 0;
		CancelableTask task = Main.CURRENT_TASKS.get(job.getId());
		if (task != null){
			Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_PID, job.getName(), task.getProcessId()));
		} else {
			Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_PID, job.getName(), "N/A"));
		}
		Main.getOutputSystem().writeJobLog(job, " ");

		int length = Math.max(job.getName().length(), 16);
		Main.getOutputSystem().writeJobLog(job, StringUtils.rightPad(HEADER_JOB_NAME, length)+" "+HEADER_STEPS);
		printStepResult(job, null);
	}
	
	
	/**
	 * Prints steps information after the end of execution of each step.
	 * 
	 * @param job job instance
	 * @param step step ended
	 */
	public static void printStepResult(Job job, Step step) {
		// JOBNAME STEPNAME RC CPU (ms) I/O (counts) MEMORY (mb) "
		// 1234567890123456 1234567890123456 1234 1234567890 1234567890123456
		// 1234567890123456
		//
		Sigar sigar = new Sigar();
		SigarProxy proxy = SigarProxyCache.newInstance(sigar, 0);
		
		int length = Math.max(job.getName().length(), 16);
		
		StringBuilder sb = new StringBuilder(StringUtils.rightPad(job.getName(), length));
		if (step == null){
			sb.append(' ').append(StringUtils.rightPad("[init]", 16));
			sb.append(' ').append(StringUtils.rightPad("-", 4));
		} else {
			sb.append(' ').append(StringUtils.rightPad(step.getName(), 16));
			sb.append(' ').append(StringUtils.rightPad(String.valueOf(step.getReturnCode()), 4));
		}
		

		// parse process id because the form is pid@hostname
		String pid = job.getProcessId();
		String id = pid.substring(0, pid.indexOf('@'));

		// extract, using SIGAR, CPU consumed by step, N/A otherwise
		try {
			// calculate cpu used on the step.
			// Sigar gives total amount of cpu of process so a difference with
			// previous one is mandatory
			long cpu = proxy.getProcCpu(id).getTotal() - PREVIOUS_CPU;
			sb.append(' ').append(StringUtils.rightPad(String.valueOf(cpu), 10));

			// saved for next step
			PREVIOUS_CPU = cpu;
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			
			sb.append(' ').append(StringUtils.rightPad("N/A", 10));
		}

		// extract, using SIGAR, memory currently used by step, N/A otherwise
		try {
			sb.append(' ').append(StringUtils.rightPad(String.valueOf(proxy.getProcMem(id).getResident() / 1024), 10));
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			
			sb.append(' ').append(StringUtils.rightPad("N/A", 10));
		}

		Main.getOutputSystem().writeJobLog(job, sb.toString());
		sigar.close();
		SigarProxyCache.clear(proxy);
	}

	/**
	 * Prints footer of job log, after the end of job execution.
	 * 
	 * @param job job instance
	 * @param returnCode returnCode of the job
	 * @param exception exception string, null if none
	 */
	public static void printFooter(Job job, int returnCode, String exception) {
		// clear cpu because here the process is ended
		PREVIOUS_CPU = 0;
		Main.getOutputSystem().writeJobLog(job, " ");
		Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_TIME_ENDED, job.getName(), DateFormatter.getDate(job.getEndedTime(), "HH:mm:ss "), returnCode));
		if (exception != null){
			Main.getOutputSystem().writeJobLog(job, exception);
		}
	}

	/**
	 * Formats the constant message to write on job log using objects parameters
	 * 
	 * @param format format string
	 * @param objects objects to fill format
	 * @return formatted message to write
	 */
	private static synchronized String getFormattedMessage(MessageFormat format, Object... objects) {
		return format.format(objects);
	}
}