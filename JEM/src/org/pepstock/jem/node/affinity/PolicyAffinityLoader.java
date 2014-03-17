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
package org.pepstock.jem.node.affinity;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.events.JobLifecycleListener;
import org.pepstock.jem.util.TimeUtils;

/**
 * Is a loader of affinity and uses a script languages to load simply all
 * affinities for the node.<br>
 * The policy file in script languages must be passed in the properties in
 * <code>init</code> method.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public abstract class PolicyAffinityLoader extends FileAlterationListenerAdaptor implements ScriptAffinityLoader, JobLifecycleListener {

	private static final String POLICY_FILENAME_KEY = "jem.affinity.loader.policy";

	private File scriptFile = null;

	private boolean reloadAfterJobEnded = false;

	private static final long POLLING_INTERVAL = 5 * TimeUtils.SECOND;

	/**
	 * Empty constructor
	 */
	public PolicyAffinityLoader() {
	}

	/**
	 * Reads <code>jem.affinity.loader.policy</code> properties, passed by
	 * configuration file
	 * 
	 * @see org.pepstock.jem.node.affinity.AffinityLoader#init(java.util.Properties)
	 */
	@Override
	public final void init(Properties properties) {
		String fileName = properties.getProperty(POLICY_FILENAME_KEY);
		if (fileName != null) {
			scriptFile = new File(fileName);
			if (scriptFile.exists()) {
				Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addListener(JobLifecycleListener.class, this);
				FileAlterationObserver observer = new FileAlterationObserver(scriptFile.getParent());
				FileAlterationMonitor monitor = new FileAlterationMonitor(POLLING_INTERVAL);
				observer.addListener(this);
				monitor.addObserver(observer);
				try {
					monitor.start();
				} catch (Exception e) {
					// debug
					LogAppl.getInstance().debug(e.getMessage(), e);
				}
				String className = FilenameUtils.getExtension(this.getClass().getName());
				Timer timer = new Timer(className, false);
				timer.schedule(new PeriodicallyAffinitiesReloader(), 5 * TimeUtils.MINUTE, 5 * TimeUtils.MINUTE);
			}
		}
	}

	/**
	 * Executes the JS file to calculate the affinities labels and memory value
	 * 
	 * @see org.pepstock.jem.node.affinity.AffinityLoader#load(org.pepstock.jem.node.affinity.SystemInfo)
	 */
	@Override
	public final Result load(SystemInfo info) throws IOException {
		// checks file name of JS. if null, exception
		if (scriptFile == null) {
			throw new IOException(NodeMessage.JEMC114E.toMessage().getFormattedMessage(POLICY_FILENAME_KEY));
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC051I, scriptFile.getAbsolutePath());
		return runScript(scriptFile, info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.affinity.ScriptAffinityLoader#getScript()
	 */
	@Override
	public File getScriptFile() {
		return scriptFile;
	}

	// Is triggered when a file is deleted from the monitored folder
	@Override
	public synchronized void onFileChange(File file) {
		// checks if the file changed is JS file
		if (!Main.IS_SHUTTING_DOWN.get() && file.getAbsolutePath().equalsIgnoreCase(scriptFile.getAbsolutePath())) {
			try {
				// calculate new policy
				// and loaded
				Result newResult = load(new SystemInfo());
				// if we have a new result (so no exception in JS execution)
				if (newResult != null) {
					// checks if it has affinities, otherwise it doesn't remove
					// them
					if (!newResult.getAffinities().isEmpty()) {
						// removes all old affinities
						Main.EXECUTION_ENVIRONMENT.getDynamicAffinities().clear();
						// loads affinities and memory
						Main.EXECUTION_ENVIRONMENT.getDynamicAffinities().addAll(newResult.getAffinities());
					}
					// if memory is set less then 0
					// don't change the value
					if (newResult.getMemory() >= 0) {
						// MEMORY
						int value = Main.EXECUTION_ENVIRONMENT.getMemory();
						if (newResult.getMemory() < ExecutionEnvironment.MINIMUM_MEMORY) {
							// too low
							LogAppl.getInstance().emit(NodeMessage.JEMC215W, newResult.getMemory(), ExecutionEnvironment.MINIMUM_MEMORY);
						} else if (newResult.getMemory() >= ExecutionEnvironment.MAXIMUM_MEMORY) {
							// too high
							LogAppl.getInstance().emit(NodeMessage.JEMC214W, newResult.getMemory(), ExecutionEnvironment.MAXIMUM_MEMORY);
						} else {
							value = newResult.getMemory();
						}
						Main.EXECUTION_ENVIRONMENT.setMemory(value);
					}
					LogAppl.getInstance().emit(NodeMessage.JEMC216I, Main.EXECUTION_ENVIRONMENT.getMemory());
					
					// if parallel jobs is set less then 0
					// don't change the value
					if (newResult.getParallelJobs() >= 0) {
						// PARALLEL JOBS
						int value = Main.EXECUTION_ENVIRONMENT.getParallelJobs();
						if (newResult.getParallelJobs() < ExecutionEnvironment.MINIMUM_PARALLEL_JOBS) {
							LogAppl.getInstance().emit(NodeMessage.JEMC211W, newResult.getParallelJobs(), ExecutionEnvironment.MINIMUM_PARALLEL_JOBS);
						} else if (newResult.getParallelJobs() >= ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS) {
							LogAppl.getInstance().emit(NodeMessage.JEMC210W, newResult.getParallelJobs(), ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS);
						} else {
							value = newResult.getParallelJobs();
						}
						// sets pool
						if (value != Main.EXECUTION_ENVIRONMENT.getParallelJobs()) {
							Main.EXECUTION_ENVIRONMENT.setParallelJobs(value);
						}
					}
					LogAppl.getInstance().emit(NodeMessage.JEMC212I, Main.EXECUTION_ENVIRONMENT.getParallelJobs());

					NodeInfoUtility.storeNodeInfo(Main.getNode());
					Main.INPUT_QUEUE_MANAGER.checkJobsInQueue();
				}
			} catch (IOException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC031E, e, this.getClass().getName());
			}
			LogAppl.getInstance().emit(NodeMessage.JEMC050I, Main.EXECUTION_ENVIRONMENT);
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.0
	 * 
	 */
	class PeriodicallyAffinitiesReloader extends TimerTask {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			if (!Main.IS_SHUTTING_DOWN.get()) {
				if (Main.getNode().getStatus().equals(Status.DRAINED) || Main.getNode().getStatus().equals(Status.INACTIVE)) {
					onFileChange(scriptFile);
				} else {
					reloadAfterJobEnded = true;
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.node.events.JobLifecycleListener#queued(org.pepstock
	 * .jem.Job)
	 */
	@Override
	public void queued(Job job) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.node.events.JobLifecycleListener#running(org.pepstock
	 * .jem.Job)
	 */
	@Override
	public void running(Job job) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.node.events.JobLifecycleListener#ended(org.pepstock.
	 * jem.Job)
	 */
	@Override
	public void ended(Job job) {
		if (reloadAfterJobEnded) {
			reloadAfterJobEnded = false;
			onFileChange(scriptFile);
		}
	}
}