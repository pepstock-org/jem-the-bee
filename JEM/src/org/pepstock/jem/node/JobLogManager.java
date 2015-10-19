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
package org.pepstock.jem.node;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	private static final int KB = 1024; 
	
	private static final int STEP_NAME_LENGTH_IF_MORE_THAN_24 = 21;
	
	private static final int STEP_LENGTH = 24;
	
	private static final int RETURN_CODE_LENGTH = 4;
	
	private static final int CPU_LENGTH = 10;
	
	private static final int MEMORY_LENGTH = 10;
	
	private static final int MINIMUM_CPU_USAGE = 100;
	
	// used to save the previous cpu extracted. this is a workaround
	private static final Map<String, Long> PREVIOUS_CPU = new ConcurrentHashMap<String, Long>();

	private static final MessageFormat HEADER_JOB = new MessageFormat("J E M  job log -- Node {0}");

	private static final MessageFormat ROW_DATE = new MessageFormat("---- {0} ----");

	private static final MessageFormat ROW_USER = new MessageFormat("USERID {0} IS ASSIGNED TO THIS JOB");

	private static final MessageFormat ROW_JOB_INFO = new MessageFormat("{0} ENVIRONMENT {1} - DOMAIN {2} - AFFINITY {3}");
	
	private static final MessageFormat ROW_JOB_PID = new MessageFormat("{0} PROCESS-ID {1}");
	
	private static final MessageFormat ROW_JOB_TIME_STARTED = new MessageFormat("{0} STARTED - TIME={1}");
	
	private static final String HEADER_STEPS = "STEPNAME                 RC   CPU(ms)    MEMORY(kb)       ";

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
		PREVIOUS_CPU.put(job.getId(), 0L);
		CancelableTask task = Main.CURRENT_TASKS.get(job.getId());
		if (task != null){
			Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_PID, job.getName(), task.getProcessId()));
		} else {
			Main.getOutputSystem().writeJobLog(job, getFormattedMessage(ROW_JOB_PID, job.getName(), "N/A"));
		}
		Main.getOutputSystem().writeJobLog(job, " ");

		Main.getOutputSystem().writeJobLog(job, HEADER_STEPS);
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
		
		StringBuilder sb = new StringBuilder();
		if (step == null){
			sb.append(StringUtils.rightPad("[init]", STEP_LENGTH));
			sb.append(' ').append(StringUtils.rightPad("-", RETURN_CODE_LENGTH));
		} else {
			// if name of step more than 24 chars
			// cut the name adding "..." as to be continued
			if (step.getName().length() > STEP_LENGTH){
				sb.append(StringUtils.rightPad(StringUtils.left(step.getName(), STEP_NAME_LENGTH_IF_MORE_THAN_24), STEP_LENGTH, "."));
			} else {
				sb.append(StringUtils.rightPad(step.getName(), STEP_LENGTH));
			}
			sb.append(' ').append(StringUtils.rightPad(String.valueOf(step.getReturnCode()), RETURN_CODE_LENGTH));
		}
		

		// parse process id because the form is pid@hostname
		String pid = job.getProcessId();
		String id = pid.substring(0, pid.indexOf('@'));

		// extract, using SIGAR, CPU consumed by step, N/A otherwise
		try {
			// calculate cpu used on the step.
			// Sigar gives total amount of cpu of process so a difference with
			// previous one is mandatory
			long cpu = Math.max(proxy.getProcCpu(id).getTotal() - PREVIOUS_CPU.get(job.getId()), MINIMUM_CPU_USAGE);
			sb.append(' ').append(StringUtils.rightPad(String.valueOf(cpu), CPU_LENGTH));

			// saved for next step
			PREVIOUS_CPU.put(job.getId(), cpu);
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			
			sb.append(' ').append(StringUtils.rightPad("N/A", CPU_LENGTH));
		}

		// extract, using SIGAR, memory currently used by step, N/A otherwise
		try {
			sb.append(' ').append(StringUtils.rightPad(String.valueOf(proxy.getProcMem(id).getResident() / KB), MEMORY_LENGTH));
		} catch (SigarException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
			
			sb.append(' ').append(StringUtils.rightPad("N/A", MEMORY_LENGTH));
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
		PREVIOUS_CPU.remove(job.getId());
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