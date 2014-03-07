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
package org.pepstock.jem.node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.DateFormatter;
import org.pepstock.jem.util.TimeUtils;

import com.thoughtworks.xstream.XStream;

/**
 * Manages the common output files managing the paths. The paths are loaded
 * after the configuration loading. They are configured in JEM configuration
 * file inside the <code>&lt;output&gt;</code>, and <code>&lt;data&gt;</code>
 * elements, inside of <code>&lt;paths&gt;</code> element.<br>
 * <br>
 * <code>&lt;output&gt;</code> is the path where the stored all output files of
 * job execution. The structure is:<br>
 * <br>
 * <p>
 * <code>
 * [jobname]-[jobid]-[start-milliseconds]<br>
 *         jcl.xml<br>
 *         job.log<br>
 *         message.log<br>
 *         job.xml<br>
 *         [step 1]<br>
 *         ...<br>
 *         [step n]<br>
 * </code>
 * </p>
 * <br>
 * <code>&lt;data&gt;</code> is the path where the datasets and files should be
 * stored. Usually a link to a global file system must be defined.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class OutputSystem {

	/**
	 * Constant current directory. Value is "/."
	 */
	public static final String CURRENT_PATH = "./";

	/**
	 * Constant name of JCL file. Value is "jcl.xml". The file contains the JCL
	 * source code
	 */
	public static final String JCL_FILE = "jcl.xml";

	/**
	 * Constant name of JOB file. Value is "job.xml". The file contains the JOB
	 * xml serialization
	 */
	public static final String JOB_FILE = "job.xml";

	/**
	 * Constant type of log. Value is "log". It's used from UI
	 */
	public static final String JOB_LOG_TYPE = "job";

	/**
	 * Constant name of job file. Value is "job.log". The file contains standard
	 * log manage by JEM
	 * 
	 * @see org.pepstock.jem.node.JobLogManager#JobLogManager()
	 */
	public static final String JOBLOG_FILE = "job.log";

	/**
	 * Constant type of log. Value is "message". It's used from UI
	 */
	public static final String MESSAGES_LOG_TYPE = "message";

	/**
	 * Constant name of message job file. Value is "message.log". The file
	 * contains standard output and error of created process, used for job
	 * execution.
	 */
	public static final String MESSAGESLOG_FILE = "messages.log";

	private File currentPath = null;

	private File outputPath = null;

	private File dataPath = null;

	private File persistencePath = null;

	/**
	 * Constructs the object and unique instance inside the node.<br>
	 * Checks if output path exists, otherwise creates it. If exists but it's
	 * not a directory, an exception occurs. Checks if data path exists,
	 * otherwise an exception occurs. If exists but it's not a directory, an
	 * exception occurs.
	 * 
	 * @param outputpath is the path where the stored all output files of job
	 *            execution
	 * @param datapath is the path where the datasets and files should be
	 *            stored.
	 * @param persistencepath is the path where db files, needed for the
	 *            persistence of the clusters queue and maps, are stored
	 * @throws ConfigurationException occurs if parameters are wrong
	 */
	public OutputSystem(String outputpath, String datapath, String persistencepath) throws ConfigurationException {
		currentPath = new File(CURRENT_PATH);
		if (!currentPath.exists()) {
			throw new ConfigurationException(NodeMessage.JEMC099E.toMessage().getFormattedMessage(CURRENT_PATH));
		}

		outputPath = new File(outputpath);
		if (!outputPath.exists()) {
			boolean isCreated = outputPath.mkdir();
			if (isCreated){
				LogAppl.getInstance().emit(NodeMessage.JEMC026I, outputPath.getAbsolutePath());
			} else {
				throw new ConfigurationException(NodeMessage.JEMC153E.toMessage().getFormattedMessage(outputPath));
			}
		} else if (!outputPath.isDirectory()) {
			throw new ConfigurationException(NodeMessage.JEMC098E.toMessage().getFormattedMessage(outputPath));
		}

		dataPath = new File(datapath);
		if (!dataPath.exists()) {
			throw new ConfigurationException(NodeMessage.JEMC099E.toMessage().getFormattedMessage(dataPath));
		} else if (!dataPath.isDirectory()) {
			throw new ConfigurationException(NodeMessage.JEMC098E.toMessage().getFormattedMessage(dataPath));
		}

		persistencePath = new File(persistencepath);
		if (!persistencePath.exists()) {
			throw new ConfigurationException(NodeMessage.JEMC099E.toMessage().getFormattedMessage(persistencePath));
		} else if (!persistencePath.isDirectory()) {
			throw new ConfigurationException(NodeMessage.JEMC098E.toMessage().getFormattedMessage(persistencePath));
		}
		
		String className = FilenameUtils.getExtension(this.getClass().getName());
        Timer timer = new Timer(className, false);
        timer.schedule(new GlobalFileSystemHealthCheck(), 20 * TimeUtils.SECOND, 1 * TimeUtils.MINUTE);
	}

	/**
	 * Returns the current path file.
	 * 
	 * @return current path file
	 */
	public File getCurrentPath() {
		return currentPath;
	}

	/**
	 * Returns the persistence path file.
	 * 
	 * @return current path file
	 */
	public File getPersistencePath() {
		return persistencePath;
	}

	/**
	 * Returns the output path file.
	 * 
	 * @return output path file
	 */
	public File getOutputPath() {
		return outputPath;
	}

	/**
	 * Returns the output path file with new directory for the job.
	 * 
	 * @param job job instance
	 * @return output path file for job
	 */
	public File getOutputPath(Job job) {
		return new File(outputPath, job.getId());
	}

	/**
	 * Returns the data path file.
	 * 
	 * @return data path file
	 */
	public File getDataPath() {
		return dataPath;
	}

	/**
	 * Returns the directory created for job output logs. If doesn't exist, it
	 * creates if <code>createDirectory</code> parameter is <code>true</code>,
	 * using format "jobname-jobid-startmilliseconds".<br>
	 * The format is "jobname-jobid-startmilliseconds" because the job name is
	 * not unique, job id is not unique (together with job name as well) because
	 * Hazelcast restart from 0 generating ID for job. So that start
	 * milliseconds are used to create a unique folder name.
	 * 
	 * @param job job related to log path
	 * @param createDirectory if <code>true</code>, if the directory doesn't
	 *            exist, it creates.
	 * @return a file which represents the path of job logs
	 * @throws IOException 
	 */
	private File createDirectoryForJob(Job job, boolean createDirectory) throws IOException  {
		File dirJob = getOutputPath(job);
		if (!dirJob.exists() && createDirectory) {
			boolean isCreated = dirJob.mkdir();
			if (!isCreated){
				throw new IOException(NodeMessage.JEMC153E.toMessage().getFormattedMessage(dirJob.getAbsolutePath()));
			}
		}
		return dirJob;
	}

	/**
	 * Returns the JCL object file.
	 * 
	 * @param job job instance
	 * @return JCL object file
	 * @throws IOException 
	 */
	public File getJclFile(Job job) throws IOException {
		File dirJob = createDirectoryForJob(job, true);
		return new File(dirJob, JCL_FILE);
	}

	/**
	 * Returns the JOB object file.
	 * 
	 * @param job job instance
	 * @return JOB object file
	 * @throws IOException 
	 */
	public File getJobFile(Job job) throws IOException {
		File dirJob = createDirectoryForJob(job, true);
		return new File(dirJob, JOB_FILE);
	}

	/**
	 * Returns the JOB log object file.
	 * 
	 * @param job job instance
	 * @param createDirectory if <code>true</code>, if the directory doesn't
	 *            exist, it creates.
	 * @return JOB log object file
	 * @throws IOException 
	 */
	public File getJobLogFile(Job job, boolean createDirectory) throws IOException {
		File dirJob = createDirectoryForJob(job, createDirectory);
		return new File(dirJob, JOBLOG_FILE);
	}

	/**
	 * Returns the JOB log object file. If the directory doesn't exist, it
	 * creates.
	 * 
	 * @param job job instance
	 * @return JOB log object file
	 * @throws IOException 
	 */
	public File getJobLogFile(Job job) throws IOException {
		return getJobLogFile(job, true);
	}

	/**
	 * Returns the MESSAGE log object file. If the directory doesn't exist, it
	 * creates.
	 * 
	 * @param job job instance
	 * @return JOB log object file
	 * @throws IOException 
	 */
	public File getMessagesLogFile(Job job) throws IOException {
		return this.getMessagesLogFile(job, true);
	}

	/**
	 * Returns the MESSAGE log object file.
	 * 
	 * @param job job instance
	 * @param createDirectory if <code>true</code>, if the directory doesn't
	 *            exist, it creates.
	 * @return MESSAGE log object file
	 * @throws IOException 
	 */
	public File getMessagesLogFile(Job job, boolean createDirectory) throws IOException {
		File dirJob = createDirectoryForJob(job, true);
		return new File(dirJob, MESSAGESLOG_FILE);
	}

	/**
	 * Writes the JCL source code in JCL file.
	 * 
	 * @see OutputSystem#getJclFile(Job)
	 * @param job job instance
	 * @throws IOException 
	 */
	public void writeJcl(Job job) throws IOException {
		FileOutputStream fos = new FileOutputStream(getJclFile(job));
		IOUtils.write(job.getJcl().getContent(), fos, CharSet.DEFAULT); 
		fos.flush();
		fos.close();
	}

	/**
	 * Writes the JOB xml serialization in JOB file.
	 * 
	 * @see OutputSystem#getJobFile(Job)
	 * @param job job instance
	 * @throws FileNotFoundException if JOB file doesn't exists, an exception
	 *             occurs
	 */
	public void writeJob(Job job) {
		PrintWriter fos = null;
		try {
			fos = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getJobFile(job)), CharSet.DEFAULT));
			XStream xs = new XStream();
			xs.toXML(job, fos);
		} catch (FileNotFoundException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC168E, e);
		} catch (IOException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC168E, e);
		} finally {
			if (fos != null){
				fos.flush();
				fos.close();
			}
		}
	}

	/**
	 * Writes a record of standard job log in JOB log file.
	 * 
	 * @see OutputSystem#getJobLogFile(Job)
	 * @see JobLogManager#JobLogManager()
	 * @param job job instance
	 * @param record log record
	 */
	public void writeJobLog(Job job, String record) {
		PrintWriter fos = null;
		try {
			File file = getJobLogFile(job);
			fos = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, file.exists()), CharSet.DEFAULT));
			fos.print(DateFormatter.getCurrentDate("yyyy MM dd HH:mm:ss   "));
			fos.println(record);
		} catch (FileNotFoundException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC027E, e);
		} catch (IOException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC027E, e);
		} finally {
			if (fos != null){
				fos.flush();
				fos.close();
			}
		}
	}

	/**
	 * Writes a record of standard output or error, produced by process used to
	 * execute the job, in MESSAGE log file.
	 * 
	 * @see OutputSystem#getMessagesLogFile(Job)
	 * @param job job instance
	 * @param record message (stderr or stdout) record
	 */
	public void writeMessageLog(Job job, String record) {
		PrintWriter fos = null;
		try {
			File file = getMessagesLogFile(job);
			fos = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, file.exists()), CharSet.DEFAULT));
			fos.print(record);
		} catch (FileNotFoundException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC027E, e);
		} catch (IOException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC027E, e);
		} finally {
			if (fos != null){
				fos.flush();
				fos.close();
			}
		}
	}
	
	/**
	 * This class is a timer which checks if there is enough space on GFS, checking the output path.<br>
	 * It uses a linear regression to calculate if there will be space on next minutes.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.0	
	 *
	 */
	class GlobalFileSystemHealthCheck extends TimerTask{

		private static final long MINUTE = 60;
		
		private static final int SAMPLES_COUNT = 10;
		
		private static final long MB = 1024;
		
		private LinkedList<Space> list = new LinkedList<OutputSystem.Space>();

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			if (Main.IS_SHUTTING_DOWN.get()){
				return;
			}
			
			if (Main.getNode() != null) {
				Main.getNode().getLock().lock();
				try {
					long freeSpace = FileSystemUtils.freeSpaceKb(outputPath.getAbsolutePath(), 10 * TimeUtils.SECOND);
					
					Space space = new Space();
					space.setSpace(freeSpace);
					space.setTime(System.currentTimeMillis() / 1000);
					
					list.addLast(space);
					if (list.size() >  SAMPLES_COUNT){
						list.removeFirst();
					}
					
					if (isUnderThreshold()){
						LogAppl.getInstance().emit(NodeMessage.JEMC182E, String.valueOf(freeSpace));
						if (Main.getNode().getStatus().equals(Status.INACTIVE) || Main.getNode().getStatus().equals(Status.ACTIVE)) {
							NodeInfoUtility.drain();
						}
					}
				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC181E, e);
				} finally {
					Main.getNode().getLock().unlock();
				}
			}
		}
		
		/**
		 * Calculate if there is enough space. It uses linear regression.<br>
		 * The threshold is 100MB
		 * @return true if there is NO space
		 */
		private boolean isUnderThreshold(){

			// if we have only 1 sample, checks directly the value
	        if (list.size() == 1){
	        	Space space = list.getFirst();
	        	return space.getSpace() < (MB * 100);
	        }
	        
	        double[] values = new double[list.size()];
	        double[][] times = new double[list.size()][];
	        
	        int index = 0;
	        for (Space space : list){
	        	values[index] = space.getSpace();
	        	times[index] = new double[]{space.getTime()};
	        	index++;
	        }
	        
	        OLSMultipleLinearRegression model = new OLSMultipleLinearRegression();
	        model.newSampleData(values, times);

	        // Check expected beta values from NIST
	        double[] betaHat = model.estimateRegressionParameters();
	        
	        double m = betaHat[1];
	        double q = betaHat[0];
	        
	        // using the linear regression, try to estimate the space in 10 minutes
	        Space last = list.getLast();
	        double x = last.getTime() + (10 * MINUTE);
	        
	        double y = (m * x) + q;
			return y < (MB * 100);
		}
	}
	
	/**
	 * This bean contains the samples needed to calculate the lineaer regression
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.0	
	 *
	 */
	static class Space {
		
		private long space = 0;
		
		private long time = 0;

		/**
		 * @return the space
		 */
		public long getSpace() {
			return space;
		}

		/**
		 * @param space the space to set
		 */
		public void setSpace(long space) {
			this.space = space;
		}

		/**
		 * @return the time
		 */
		public long getTime() {
			return time;
		}

		/**
		 * @param time the time to set
		 */
		public void setTime(long time) {
			this.time = time;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Space [space=" + space + ", time=" + time + "]";
		}
		
	}
}