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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.executors.stats.GetSample;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.LightSample;
import org.pepstock.jem.node.stats.MemberSample;
import org.pepstock.jem.node.stats.Sample;
import org.pepstock.jem.node.stats.TimeComparator;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.DateFormatter;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiTask;
import com.thoughtworks.xstream.XStream;

/**
 * Internal manager of node which is responsible to call distributed task to get all information for all others. Only the coordinator
 * does this job. All other members use this managers to store statistics on file system (when configured)
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class StatisticsManager {
	
	private static final int MAXIMUM_NUMBER_OF_SAMPLES = 20;

	private static final long POLLING_INTERVAL = 1 * TimeUtils.MINUTE;
	
	private static final long THRESHOLD_ELAPSED_TIME = 2 * TimeUtils.SECOND;

	private String savedDay = DateFormatter.getCurrentDate(DateFormatter.DEFAULT_DATE_FORMAT);

	private Timer timer = null;

	private TimeComparator comparator = null;

	private File folderStatsLog = null;

	private File statsLog = null;

	private XStream xs = new XStream();

	private MemberSample lastMemberSample = null;

	private boolean enable = false;

	/**
	 * Enables statistics management
	 */
	public StatisticsManager() {
		this(null);
	}
	/**
	 * Constructs the object. Uses the passed path to store stats. 
	 * @param enable <code>true</code> if statistics are managed
	 * @param path folder where to store stats
	 */
	public StatisticsManager(String path) {
		try {
			if (path != null){
				// checks on storage manager the complete path
				// starting from path of put in config file
				PathsContainer checkedPath = Main.DATA_PATHS_MANAGER.getPaths(path);
				// the folder put in config file
				folderStatsLog = new File(checkedPath.getCurrent().getContent(), path);
				// if folder doesn't exist
				if (!folderStatsLog.exists()) {
					// creates the folder
					boolean isCreated = folderStatsLog.mkdirs();
					// if created
					if (isCreated) {
						// logs the creation nad the status
						// has been set to enable
						LogAppl.getInstance().emit(NodeMessage.JEMC075I, FilenameUtils.normalize(folderStatsLog.getAbsolutePath()));
						this.enable = true;
					} else {
						// 
						LogAppl.getInstance().emit(NodeMessage.JEMC153E, FilenameUtils.normalize(folderStatsLog.getAbsolutePath()));
						LogAppl.getInstance().emit(NodeMessage.JEMC183W);
					}
				} else {
					// the folder already exists
					// therefore the manager is enable
					LogAppl.getInstance().emit(NodeMessage.JEMC076I, FilenameUtils.normalize(folderStatsLog.getAbsolutePath()));
					this.enable = true;
				}
			}
		} catch (InvalidDatasetNameException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(e.getMessageInterface(), e.getObjects());
		}
		// if not enable, put another warning
		if (!enable){
			LogAppl.getInstance().emit(NodeMessage.JEMC183W);	
		}
	}

	/**
	 * Initialize the manager
	 */
	public void init() {
		// if enable, write a file with the node key plus the date as extension
		if (enable){
			statsLog = new File(folderStatsLog, Main.getNode().getKey() + "." + savedDay);
		}
		// creates a time comparator to extract from HC map
		// the oldest sample, beyond the threshold of 20.
		// The map on HC must contains max 20 samples for node
		comparator = new TimeComparator();
		// gets the name of the class and use it as name of the timer (thread name)
		String className = FilenameUtils.getExtension(this.getClass().getName());
		timer = new Timer(className, false);
		// every minute extract the stats from all nodes, if the node is
		// the coordinator
		timer.schedule(new StatsTimerTask(), 1, POLLING_INTERVAL);
		// avoid recursive on xSTREAM
		xs.omitField(LightMemberSample.class, "sample");
	}

	/**
	 * @return the enable
	 */
	public boolean isEnable() {
		return enable;
	}
	
	/**
	 * @return the folderStatsLog
	 */
	public File getFolderStatsLog() {
		return folderStatsLog;
	}

	/**
	 * Closes the timer and then to extract stats from node.
	 * This method is called during the shutdown of the node
	 */
	public void stop() {
		if (timer != null) {
			// cancels the timer
			timer.cancel();
		}
		// shows the closing message
		LogAppl.getInstance().emit(NodeMessage.JEMC077I);
	}

	/**
	 * @return the lastMemberSample
	 */
	public MemberSample getLastMemberSample() {
		return lastMemberSample;
	}

	/**
	 * @param lastMemberSample the lastMemberSample to set
	 */
	public void setLastMemberSample(MemberSample lastMemberSample) {
		this.lastMemberSample = lastMemberSample;
	}

	/**
	 * Writes a sample on file, define to collect all node stats
	 * @param sample
	 * @throws FileNotFoundException
	 */
	public void write(Sample sample) {
		// it writes only if enable
		if (enable) {
			try {
				// every write it calculates the date
				// to write always the sample in a file with the date in the file name (extension)
				String currentDay = DateFormatter.getCurrentDate(DateFormatter.DEFAULT_DATE_FORMAT);
				if (!savedDay.equalsIgnoreCase(currentDay)) {
					savedDay = currentDay;
					// in this way, when you are after midnight, it writes automatically
					// in a new file with new date
					statsLog = new File(folderStatsLog, Main.getNode().getKey() + "." + savedDay);
					LogAppl.getInstance().emit(NodeMessage.JEMC082I, statsLog.getAbsolutePath());
				}
				// de-serializes the sample in XML by XStream
				String ee = xs.toXML(sample);
				// removes line.separator and blanks
				// in this way in the file, every record is a sample
				// easier to read and manage
				ee = StringUtils.remove(ee, '\n');
				ee = StringUtils.remove(ee, ' ');
				// writes always in append mode even if new
				PrintWriter ps = new PrintWriter(new OutputStreamWriter(new FileOutputStream(statsLog, true), CharSet.DEFAULT));
				ps.println(ee);
				ps.flush();
				ps.close();
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC080E, e, statsLog.getAbsolutePath());
			}
		}
	}

	/**
	 * This is a timer which will coall all the nodes to write their own samples on the files 
	 * and it puts all sample on HC map, maintain the maximum amount of elements on HC map.<br>
	 * This kind of action is done ONLY if the node is the COORDINATOR of the JEM cluster.
	 * If not, the node executes inside a distributed task to write the sample.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.0
	 * 
	 */
	class StatsTimerTask extends TimerTask {

		private boolean isExecuting = false;

		private boolean manager = Main.IS_COORDINATOR.get();

		/**
		 * Writes log to inform what file is using
		 */
		public StatsTimerTask() {
			// writes if is enable
			if (enable){
				LogAppl.getInstance().emit(NodeMessage.JEMC082I, statsLog.getAbsolutePath());
			}
			// writes log id is the coordinator
			if (manager) {
				LogAppl.getInstance().emit(NodeMessage.JEMC078I);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// if the node is shutting down, do nothing
			if (Main.IS_SHUTTING_DOWN.get()){
				return;
			}
			// if the node has been started in access maint, do nothing
			if (Main.IS_ACCESS_MAINT.get()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC189I);
				return;
			}
			// the time checks if the previous run is still running
			// if yes, it shows a message and skip the run
			// this is a warning but it should never happen
			if (isExecuting) {
				LogAppl.getInstance().emit(NodeMessage.JEMC161W);
				return;
			}
			// get time of execution
			long start = System.currentTimeMillis();
			// checks if is the coordinator
			// ONLY coordinator will manage the samples management 
			// asking to all members to write and get the sample
			if (Main.IS_COORDINATOR.get()) {
				// if the node wasn't the manager at the beginning
				// because it was another node,
				// it writes on the log that now it manages the statistics for all cluster
				if (!manager) {
					LogAppl.getInstance().emit(NodeMessage.JEMC078I);
					// saves that is the manager
					manager = Main.IS_COORDINATOR.get();
				}
				// creates a set of members of HC members
				// necesary to schedule a distributed task on all of them
				Cluster cluster = Main.getHazelcast().getCluster();
				Set<Member> listOfNodes = new HashSet<Member>();
				for (Member member : cluster.getMembers()) {
					listOfNodes.add(member);
				}
				// if there is a member
				if (!listOfNodes.isEmpty()) {
					// uses as KEY the timestamp
					String key = DateFormatter.getCurrentDate(Sample.FORMAT);
					// divides date and time
					String[] times = StringUtils.split(key, ' ');
					// creates a sample
					// setting key, time and date
					Sample environmentSample = new Sample();
					environmentSample.setKey(key);
					environmentSample.setDate(times[0]);
					environmentSample.setTime(times[1]);
					// adds the environment as well
					environmentSample.setEnvironment(Main.EXECUTION_ENVIRONMENT.getEnvironment());
					// creates a light samples
					// to collect all light samples from all nodes
					LightSample lightEnvironmentSample = new LightSample();
					// sa a sample, sets key, time and date
					lightEnvironmentSample.setKey(key);
					lightEnvironmentSample.setDate(times[0]);
					lightEnvironmentSample.setTime(times[1]);
					// creates the distributed task
					// and schedule the task on all members
					MultiTask<LightMemberSample> task = new MultiTask<LightMemberSample>(new GetSample(environmentSample), listOfNodes);
					ExecutorService executorService = Main.getHazelcast().getExecutorService();
					executorService.execute(task);
					try {
						// sets if is in executing 
						isExecuting = true;
						// gets the results from nodes
						Collection<LightMemberSample> results = task.get();
						for (LightMemberSample result : results) {
							// adds all results on the light sample
							// for each member
							if (result != null) {
								lightEnvironmentSample.getMembers().add(result);
							}
						}
						// if the container is not empty, therefore has got results
						if (!lightEnvironmentSample.getMembers().isEmpty()) {
							// gets HC map
							IMap<String, LightSample> samples = Main.getHazelcast().getMap(Queues.STATS_MAP);
							// gets lock for the map
							Lock lock = Main.getHazelcast().getLock(Queues.STATS_MAP_LOCK);
							boolean isLock = false;
							try {
								// locks the map
								isLock = lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
								// adds the new sample
								samples.put(lightEnvironmentSample.getKey(), lightEnvironmentSample);
								// here checks if the map has got more than
								// maximum number of samples
								int size = samples.size();
								if (size > MAXIMUM_NUMBER_OF_SAMPLES) {
									// if is beyond the maximum, using the comparator time
									// removes the oldest one
									String oldestKey = Collections.min(samples.keySet(), comparator);
									samples.remove(oldestKey);
								}
							} finally {
								// always unlocks
								if (isLock){
									lock.unlock();
								}
							}
						}
					} catch (ExecutionException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC079E, e);
					} catch (InterruptedException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC079E, e);
					}
					// setsis not longer in executing
					isExecuting = false;
				}
			}
			// calculates the elapsed time to collect the stats
			// print a warning on the log the elapsed time
			// if beyond of 2 seconds. It should never happen
			long elapsed = System.currentTimeMillis() - start;
			if (elapsed > THRESHOLD_ELAPSED_TIME) {
				LogAppl.getInstance().emit(NodeMessage.JEMC081W, String.valueOf(elapsed));
			}
		}
	}
}