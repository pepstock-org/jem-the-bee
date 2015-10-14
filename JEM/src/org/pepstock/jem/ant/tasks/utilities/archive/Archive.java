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
package org.pepstock.jem.ant.tasks.utilities.archive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.rmi.InternalUtilities;
import org.pepstock.jem.node.rmi.UtilsInitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.util.ZipUtil;

/**
 * ANT task to archive job output, removing from output queue, zipping the content and delegating a plugin to do any action.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 *
 */
public class Archive extends Command {

	/**
	 * Constructs task using command line (Hazelcast SQL to filter jobs).
	 * @param commandLine command passed by DD and which is a Hazelcast SQL to filter job
	 * @throws ParseException if any parse exception occurs
	 */
	public Archive(String commandLine) throws ParseException {
		super(commandLine);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.archive.Command#execute()
	 */
	@Override
	public void execute() throws JemException {
		// gets output path for job. If null, exception
		String jobOutputPath = System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME);
		if (jobOutputPath == null){
			throw new MessageException(AntMessage.JEMA007E);
		}
		
		// creates a file with job output path
		File jobOutputFile = new File(jobOutputPath);
		// gest parent which represent OUTPUT folder for all jobs
		File outputPath = jobOutputFile.getParentFile();
		
		// gets internal RMI utilities
		InternalUtilities util;
		// gets all jobs which match with command (Hazelcast SQL)
		Collection<Job> jobs;
		try {
			util = UtilsInitiatorManager.getInternalUtilities();
			jobs = util.getJobs(JobId.VALUE, getCommandLine());
		} catch (RemoteException e) {
			throw new JemException(e);
		} catch (UnknownHostException e) {
			throw new JemException(e);
		}
		
		int count = 0;
		// scans all jobs
		for (Job job : jobs){
			// gets output folder of job
			File folderToZip = new File(outputPath, job.getId());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				// zips all folder
				ZipUtil.createZip(folderToZip, baos);
				// calls plugin heaving statement if job must delete or not
				boolean delete = super.getJobOutputArchive().archive(job, baos.toByteArray());
				if (delete) {
					// deletes job from queue, output folder and its content
					// calling (remotely) a purge command
					util.purge(JobId.VALUE, job);
					count++;
					LogAppl.getInstance().emit(AntUtilMessage.JEMZ047I, job.toString());
				} else {
					LogAppl.getInstance().emit(AntUtilMessage.JEMZ045W, job.toString());
				}
			} catch (RemoteException re){
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ044E, re, job.toString());
			} catch (IOException re){
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ046E, re, job.toString());
			} catch (Exception re){
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ046E, re, job.toString());
			}
		}
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ048I, String.valueOf(count), String.valueOf(jobs.size()));
	}
}