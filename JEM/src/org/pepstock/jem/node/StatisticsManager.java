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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.thoughtworks.xstream.XStream;

/**
 * Internal manager of node which is responsbile to call distributed task to get all information for all others. Only the coordinator
 * does this job. All other members use this managers to store statistics on file system (when configured)
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class StatisticsManager {
	
	private static final int MAXIMUM_NUMBER_OF_SAMPLES = 20;

	private static final long POLLING_INTERVAL = 1 * TimeUtils.MINUTE;

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
				if (!folderStatsLog.exists()) {
					boolean isCreated = folderStatsLog.mkdirs();
					if (isCreated) {
						LogAppl.getInstance().emit(NodeMessage.JEMC075I, FilenameUtils.normalize(folderStatsLog.getAbsolutePath()));
						this.enable = true;
					} else {
						LogAppl.getInstance().emit(NodeMessage.JEMC153E, FilenameUtils.normalize(folderStatsLog.getAbsolutePath()));
						LogAppl.getInstance().emit(NodeMessage.JEMC183W);
					}
				} else {
					LogAppl.getInstance().emit(NodeMessage.JEMC076I, FilenameUtils.normalize(folderStatsLog.getAbsolutePath()));
					this.enable = true;
				}
			}
		} catch (InvalidDatasetNameException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(e.getMessageInterface(), e.getObjects());
		}
		if (!enable){
			LogAppl.getInstance().emit(NodeMessage.JEMC183W);	
		}
	}

	/**
	 * Initialize the manager
	 */
	public void init() {
		if (enable){
			statsLog = new File(folderStatsLog, Main.getNode().getKey() + "." + savedDay);
		}
		comparator = new TimeComparator();
		String className = FilenameUtils.getExtension(this.getClass().getName());
		timer = new Timer(className, false);
		timer.schedule(new StatsTimerTask(), 1, POLLING_INTERVAL);

		// avoid recursive
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
	 * 
	 */
	public void stop() {
		if (timer != null) {
			timer.cancel();
		}
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
	 * @param sample
	 * @throws FileNotFoundException
	 */
	public void write(Sample sample) {
		if (enable) {
			try {
				String currentDay = DateFormatter.getCurrentDate(DateFormatter.DEFAULT_DATE_FORMAT);
				if (!savedDay.equalsIgnoreCase(currentDay)) {
					savedDay = currentDay;
					statsLog = new File(folderStatsLog, Main.getNode().getKey() + "." + savedDay);
					LogAppl.getInstance().emit(NodeMessage.JEMC082I, statsLog.getAbsolutePath());
				}
				String ee = xs.toXML(sample);
				ee = StringUtils.remove(ee, '\n');
				ee = StringUtils.remove(ee, ' ');
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
	 * Dimensione degli oggetti; Sample = 3904,3800 Su file = 8747
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
			if (enable){
				LogAppl.getInstance().emit(NodeMessage.JEMC082I, statsLog.getAbsolutePath());
			}
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
			if (Main.IS_SHUTTING_DOWN.get()){
				return;
			}
			
			if (Main.IS_ACCESS_MAINT.get()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC189I);
				return;
			}

			if (isExecuting) {
				LogAppl.getInstance().emit(NodeMessage.JEMC161W);
				return;
			}
			long start = System.currentTimeMillis();
			if (Main.IS_COORDINATOR.get()) {
				if (!manager) {
					LogAppl.getInstance().emit(NodeMessage.JEMC078I);
					manager = Main.IS_COORDINATOR.get();
				}

				Cluster cluster = Main.getHazelcast().getCluster();
				Set<Member> listOfNodes = new HashSet<Member>();
				for (Member member : cluster.getMembers()) {
					listOfNodes.add(member);
				}
				if (!listOfNodes.isEmpty()) {
					String key = DateFormatter.getCurrentDate(Sample.FORMAT);
					String[] times = StringUtils.split(key, ' ');

					Sample environmentSample = new Sample();
					environmentSample.setKey(key);
					environmentSample.setDate(times[0]);
					environmentSample.setTime(times[1]);
					environmentSample.setEnvironment(Main.EXECUTION_ENVIRONMENT.getEnvironment());

					LightSample lightEnvironmentSample = new LightSample();
					lightEnvironmentSample.setKey(key);
					lightEnvironmentSample.setDate(times[0]);
					lightEnvironmentSample.setTime(times[1]);

					IExecutorService executorServie = Main.getHazelcast().getExecutorService(Queues.JEM_EXECUTOR_SERVICE);
					Map<Member, Future<LightMemberSample>> futures = executorServie.submitToMembers(new GetSample(environmentSample), listOfNodes);
					
					try {
						isExecuting = true;
						for (Future<LightMemberSample> future : futures.values()) {
							LightMemberSample result = future.get();
							if (result != null) {
								lightEnvironmentSample.getMembers().add(result);
							}
						}
						if (!lightEnvironmentSample.getMembers().isEmpty()) {
							IMap<String, LightSample> samples = Main.getHazelcast().getMap(Queues.STATS_MAP);
							Lock lock = Main.getHazelcast().getLock(Queues.STATS_MAP_LOCK);
							boolean isLock = false;
							try {
								isLock = lock.tryLock(10, TimeUnit.SECONDS);
								samples.put(lightEnvironmentSample.getKey(), lightEnvironmentSample);
								int size = samples.size();
								if (size > MAXIMUM_NUMBER_OF_SAMPLES) {
									String oldestKey = Collections.min(samples.keySet(), comparator);
									samples.remove(oldestKey);
								}
							} finally {
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
					isExecuting = false;
				}
			}
			long elapsed = System.currentTimeMillis() - start;
			if (elapsed > (2 * TimeUtils.SECOND)) {
				LogAppl.getInstance().emit(NodeMessage.JEMC081W, String.valueOf(elapsed));
			}
		}

	}

}