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
package org.pepstock.jem.node.executors.stats;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.stats.CpuUtilization;
import org.pepstock.jem.node.stats.FileSystemUtilization;
import org.pepstock.jem.node.stats.LightMapStats;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.MapOperationsStats;
import org.pepstock.jem.node.stats.MapStats;
import org.pepstock.jem.node.stats.MemberSample;
import org.pepstock.jem.node.stats.MemoryUtilization;
import org.pepstock.jem.node.stats.ProcessCpuUtilization;
import org.pepstock.jem.node.stats.ProcessMemoryUtilization;
import org.pepstock.jem.node.stats.QueueOperationsStats;
import org.pepstock.jem.node.stats.QueueStats;
import org.pepstock.jem.node.stats.Sample;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.monitor.LocalMapOperationStats;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.LocalQueueOperationStats;
import com.hazelcast.monitor.LocalQueueStats;

/**
 * Gathers all statistics for whole JEM cluster
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2
 * 
 */
public class GetSample extends DefaultExecutor<LightMemberSample> {

	private static final long serialVersionUID = 1L;

	private Sample newSample = null;

	/**
	 * Constructs the object using a sample (for whole JEM cluster)
	 * 
	 * @param newSample sample for whole JEM cluster
	 */
	public GetSample(Sample newSample) {
		this.newSample = newSample;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#checkShutDown()
	 */
	@Override
	public void checkShutDown() throws ExecutorException {
		// NOP here
		// is called in execute method to return null!
		// to avoid that Hazelcast distributed tasks joint fails
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public LightMemberSample execute() throws ExecutorException {
		// checks if shutdown
		// if yes, return null
		// without exception
		try {
			super.checkShutDown();
		} catch (ExecutorException ex) {
			// ignore
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			return null;
		}

		// if node is null, skip!
		// could happen if the executor is running in a starting node
		if (Main.getNode() == null) {
			return null;
		}
		// if statistics manager is null, skip!
		// could happen if the executor is running in a starting node
		if (Main.getStatisticsManager() == null) {
			return null;
		}

		// gets SIGAR
		Sigar sigar = new Sigar();

		// loads all member data
		MemberSample memberSample = new MemberSample();
		// the key is the node key
		memberSample.setMemberKey(Main.getNode().getKey());
		// sets the label (readable) of node
		memberSample.setMemberLabel(Main.getNode().getLabel());
		// sets the host name
		memberSample.setMemberHostname(Main.getNode().getHostname());
		// sets the process id
		memberSample.setPid(sigar.getPid());

		// gets last sample, used to calculate increments between two samples
		MemberSample lastMemberSample = Main.getStatisticsManager().getLastMemberSample();

		// calculates number of JCL checked
		memberSample.setNumberOfJCLCheck((lastMemberSample == null) ? Main.NUMBER_OF_JCL_CHECK.intValue() : Main.NUMBER_OF_JCL_CHECK.intValue() - lastMemberSample.getTotalNumberOfJCLCheck());
		// calculate number of JOB executed
		memberSample.setNumberOfJOBSubmitted((lastMemberSample == null) ? Main.NUMBER_OF_JOB_SUBMITTED.intValue() : Main.NUMBER_OF_JOB_SUBMITTED.intValue() - lastMemberSample.getTotalNumberOfJOBSubmitted());

		// sets the total job and jcl managed
		memberSample.setTotalNumberOfJCLCheck(Main.NUMBER_OF_JCL_CHECK.intValue());
		memberSample.setTotalNumberOfJOBSubmitted(Main.NUMBER_OF_JOB_SUBMITTED.intValue());

		// calculates CPU of machine
		loadCpuUtilization(memberSample, sigar);
		// calculates MEMORY of machine
		loadMemoryUtilization(memberSample, sigar);
		// calculates CPU of process
		loadProcessCpuUtilization(memberSample, sigar);
		// calculates MEMORY of process
		loadProcessMemoryUtilization(memberSample, sigar);
		// calculates GFS util
		loadGFSUtilization(memberSample, sigar);
		// gets HC maps statistics
		loadHazelcastMapsStats(memberSample);
		// gets HC queues statistics
		loadHazelcastQueuesStats(memberSample);

		// adds the member sample to cluster sample
		newSample.getMembers().add(memberSample);

		// writes the sample to the file system
		Main.getStatisticsManager().write(newSample);
		// sets the sample as the last one
		Main.getStatisticsManager().setLastMemberSample(memberSample);

		// returns the light sample
		return createLightMemberSample(newSample, memberSample);
	}

	/**
	 * Loads all info of the JEM member
	 * 
	 * @param sample light sample (container)
	 * @param msample set of data for specific member
	 * @return a light sample for member
	 */
	private LightMemberSample createLightMemberSample(Sample sample, MemberSample msample) {
		// creates the member sample
		LightMemberSample memberSample = new LightMemberSample();
		// sets node key, label and hostname
		memberSample.setMemberKey(msample.getMemberKey());
		memberSample.setMemberLabel(msample.getMemberLabel());
		memberSample.setMemberHostname(msample.getMemberHostname());
		// sets process ID
		memberSample.setPid(msample.getPid());
		// sets the sample key (time stamp)
		memberSample.setKey(sample.getKey());
		// sets the machine CPU
		memberSample.setCpuPercent(msample.getCpu().getPercent());
		// sets the machine memory avail
		memberSample.setMemoryAvailable(msample.getMemory().getAvailable());
		// sets the machine memory free
		memberSample.setMemoryFree(msample.getMemory().getFree());
		// sets number of checked JCL and job executed
		memberSample.setNumberOfJCLCheck(msample.getNumberOfJCLCheck());
		memberSample.setNumberOfJOBSubmitted(msample.getNumberOfJOBSubmitted());
		// sets the process CPU
		memberSample.setProcessCpuPercent(msample.getProcessCpu().getPercent());
		// sets the process total CPU
		memberSample.setProcessTotalCpu(msample.getProcessCpu().getTotal());
		// sets the process memory used
		memberSample.setProcessMemoryUsed(msample.getProcessMemory().getUsed());
		// sets the process memory free (respect with max heap size)
		memberSample.setProcessMemoryFree(msample.getProcessMemory().getFree());
		// sets time
		memberSample.setTime(sample.getTime());
		// sets total number of checked JCL and job executed
		memberSample.setTotalNumberOfJCLCheck(msample.getTotalNumberOfJCLCheck());
		memberSample.setTotalNumberOfJOBSubmitted(msample.getTotalNumberOfJOBSubmitted());
		// loads file system inforamtion
		memberSample.setFileSystems(msample.getFileSystems());

		// gets stats of INPUT map
		MapStats map = msample.getMapsStats().get(Queues.INPUT_QUEUE);
		memberSample.getMapsStats().put(Queues.INPUT_QUEUE, createLightMapStats(map));
		// gets stats of OUTPUT map
		map = msample.getMapsStats().get(Queues.OUTPUT_QUEUE);
		memberSample.getMapsStats().put(Queues.OUTPUT_QUEUE, createLightMapStats(map));
		// gets stats of RUNNING map
		map = msample.getMapsStats().get(Queues.RUNNING_QUEUE);
		memberSample.getMapsStats().put(Queues.RUNNING_QUEUE, createLightMapStats(map));
		// gets stats of ROUTING map
		map = msample.getMapsStats().get(Queues.ROUTING_QUEUE);
		memberSample.getMapsStats().put(Queues.ROUTING_QUEUE, createLightMapStats(map));
		return memberSample;
	}

	/**
	 * Load all maps data of HC for a specific member
	 * 
	 * @param map Hazelcast map statistics
	 * @return a light map statistics
	 */
	private LightMapStats createLightMapStats(MapStats map) {
		// creates the container bean
		LightMapStats lmap = new LightMapStats();
		// sets hits and name
		lmap.setHits(map.getHits());
		lmap.setName(map.getName());
		// gets the workload on map
		lmap.setNumberOfGets(map.getOperationsStats().getNumberOfGets());
		lmap.setNumberOfPuts(map.getOperationsStats().getNumberOfPuts());
		lmap.setNumberOfRemoves(map.getOperationsStats().getNumberOfRemoves());
		// loads the entry counts and memory used
		lmap.setOwnedEntryCount(map.getOwnedEntryCount());
		lmap.setOwnedEntryMemoryCost(map.getOwnedEntryMemoryCost());
		// loads the locks info
		lmap.setLockedEntryCount(map.getLockedEntryCount());
		lmap.setLockWaitCount(map.getLockWaitCount());
		// loads the latency on different actions on map
		lmap.setTotalGetLatency(map.getOperationsStats().getTotalGetLatency());
		lmap.setTotalPutLatency(map.getOperationsStats().getTotalPutLatency());
		lmap.setTotalRemoveLatency(map.getOperationsStats().getTotalRemoveLatency());
		// returns sample
		return lmap;
	}

	/**
	 * Calculates and loads cpu consumption.
	 * 
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadCpuUtilization(MemberSample sample, Sigar sigar) {
		try {
			// loads all totals
			Cpu sCpu = sigar.getCpu();
			CpuUtilization cpu = sample.getCpu();
			// loads all cpu info
			cpu.setIdle(sCpu.getIdle());
			cpu.setSystem(sCpu.getSys());
			cpu.setTotal(sCpu.getTotal());
			cpu.setUser(sCpu.getUser());

			// checks if this is not the first sample
			// if not, it calculates the differences between previous sample
			MemberSample lastMemberSample = Main.getStatisticsManager().getLastMemberSample();
			if (lastMemberSample != null) {
				// gets consumed cpu
				CpuUtilization lastCpuUtil = lastMemberSample.getCpu();
				// gets spent time between samples
				long diffTime = sample.getCurrentTimeMillis() - lastMemberSample.getCurrentTimeMillis();
				// calculates max cpu that is consumable in time frame (equals
				// to elapsed time multiply for processor number)
				long totPossibleCpu = diffTime * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				// calculates CPU load
				long cpuUsed = cpu.getUser() + cpu.getSystem() - lastCpuUtil.getUser() - lastCpuUtil.getSystem();
				// transforms on double
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				// sets maximum (1) and minimum (0)
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				// sets percentage
				cpu.setPercent(cpuUsedPercent);
			} else {
				// uses SIGAR info without any special calculation
				long totPossibleCpu = 1 * TimeUtils.MINUTE * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				// gets cpu used
				long cpuUsed = cpu.getUser() + cpu.getSystem();
				// transforms in double
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				// sets maximum (1) and minimum (0)
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				// sets percentage
				cpu.setPercent(cpuUsedPercent);
			}
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculate memory consumption.
	 * 
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadMemoryUtilization(MemberSample sample, Sigar sigar) {
		try {
			// gets memory from SIGAR
			Mem sMem = sigar.getMem();
			MemoryUtilization mem = sample.getMemory();
			// loads all values to the bean
			mem.setAvailable(sMem.getTotal());
			mem.setUsed(sMem.getUsed());
			mem.setFree(sMem.getFree());
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculate JEM process cpu consumption.
	 * 
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadProcessCpuUtilization(MemberSample sample, Sigar sigar) {
		try {
			// loads all totals
			ProcCpu sCpu = sigar.getProcCpu(sample.getPid());
			ProcessCpuUtilization cpu = sample.getProcessCpu();
			cpu.setSystem(sCpu.getSys());
			cpu.setTotal(sCpu.getTotal());
			cpu.setUser(sCpu.getUser());
			// checks if this is not the first sample
			// if not, it calculates the differences between previous sample
			MemberSample lastMemberSample = Main.getStatisticsManager().getLastMemberSample();
			if (lastMemberSample != null) {
				// gets consumed cpu
				ProcessCpuUtilization lastCpuUtil = lastMemberSample.getProcessCpu();
				// gets spent time between samples
				long diffTime = sample.getCurrentTimeMillis() - lastMemberSample.getCurrentTimeMillis();
				// calculates max cpu that is consumable in time frame (equals
				// to elapsed time multiply for processor number)
				long totPossibleCpu = diffTime * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				// calculates CPU load
				long cpuUsed = cpu.getTotal() - lastCpuUtil.getTotal();
				// transforms in double
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				// sets maximum (1) and minimum (0)
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				// sets percentage
				cpu.setPercent(cpuUsedPercent);
			} else {
				// uses SIGAR info without any special calculation
				long totPossibleCpu = 1 * TimeUtils.MINUTE * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				// gets CPU
				long cpuUsed = cpu.getTotal();
				// transforms in double
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				// sets maximum (1) and minimum (0)
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				// sets percentage
				cpu.setPercent(cpuUsedPercent);
			}
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculate JEM process memory consumption.
	 * 
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadProcessMemoryUtilization(MemberSample sample, Sigar sigar) {
		try {
			// gets the process memory utilization
			ProcMem sMem = sigar.getProcMem(sample.getPid());
			// gets bean
			ProcessMemoryUtilization mem = sample.getProcessMemory();
			// loads all data into bean
			mem.setAvailable(sMem.getSize());
			mem.setUsed(sMem.getResident());
			// calculates the free memory
			long free = mem.getAvailable() - mem.getUsed();
			mem.setFree(free);
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculates GFS usage.
	 * 
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadGFSUtilization(MemberSample sample, Sigar sigar) {
		// adds the utilization of all GFS paths (without datapath)
		sample.getFileSystems().add(getFileSystemUtilization("Output", System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Binary", System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Classpath", System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Library", System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Source", System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Persistence", System.getProperty(ConfigKeys.JEM_PERSISTENCE_PATH_NAME), sigar));

		// here calculates the data path,
		// because i paths are not defined in advance but depends on
		// configuration
		// it uses a LinkedList therefore the order is maintained
		List<String> dataPathNames = Main.DATA_PATHS_MANAGER.getDataPathsNames();
		List<String> dataPaths = Main.DATA_PATHS_MANAGER.getDataPaths();

		// scans all data paths names
		for (int i = 0; i < dataPathNames.size(); i++) {
			// gets name
			String name = dataPathNames.get(i);
			// gets path
			String path = dataPaths.get(i);
			// calculated the utilization
			sample.getFileSystems().add(getFileSystemUtilization("Data [" + name + "]", path, sigar));
		}
	}

	/**
	 * Calculates the storage free of a specific GFS path.
	 * 
	 * @param name GFS path name
	 * @param path path used to calculate the amount of free space
	 * @param sigar sigar instance to check the file sustem usage
	 * @return the files system utilization bean
	 */
	private FileSystemUtilization getFileSystemUtilization(String name, String path, Sigar sigar) {
		// creates the bean
		FileSystemUtilization fsUtil = new FileSystemUtilization();
		// sets name and path
		fsUtil.setName(name);
		fsUtil.setPath(path);
		try {
			// uses SIGAR to have the file system utilization
			FileSystemUsage usage = sigar.getFileSystemUsage(Main.getOutputSystem().getOutputPath().getAbsolutePath());
			// gets the total, free and used
			long free = usage.getFree();
			long total = usage.getTotal();
			long used = usage.getUsed();
			// loads the total, free and used into the bean
			fsUtil.setFree(free);
			fsUtil.setTotal(total);
			fsUtil.setUsed(used);
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
		return fsUtil;
	}

	/**
	 * Loads all Hazelcast maps stats
	 * 
	 * @param sample member sample
	 */
	private void loadHazelcastMapsStats(MemberSample sample) {
		// gets all statistics
		// of all maps, also the internal ones
		MapStats input = loadMapStats(Queues.INPUT_QUEUE);
		MapStats running = loadMapStats(Queues.RUNNING_QUEUE);
		MapStats output = loadMapStats(Queues.OUTPUT_QUEUE);
		MapStats routing = loadMapStats(Queues.ROUTING_QUEUE);
		MapStats routed = loadMapStats(Queues.ROUTED_QUEUE);
		MapStats resources = loadMapStats(Queues.COMMON_RESOURCES_MAP);
		MapStats roles = loadMapStats(Queues.ROLES_MAP);
		MapStats stats = loadMapStats(Queues.STATS_MAP);
		MapStats preferences = loadMapStats(Queues.USER_PREFERENCES_MAP);

		// loads the common maps statistics
		sample.getMapsStats().put(input.getName(), input);
		sample.getMapsStats().put(running.getName(), running);
		sample.getMapsStats().put(output.getName(), output);
		sample.getMapsStats().put(routing.getName(), routing);
		// loads the internal maps statistics
		sample.getMapsStats().put(routed.getName(), routed);
		sample.getMapsStats().put(resources.getName(), resources);
		sample.getMapsStats().put(roles.getName(), roles);
		sample.getMapsStats().put(stats.getName(), stats);
		sample.getMapsStats().put(preferences.getName(), preferences);
	}

	/**
	 * Loads Hazelcast queue stats
	 * 
	 * @param sample member sample
	 */
	private void loadHazelcastQueuesStats(MemberSample sample) {
		// JEM has got only 1 queue
		QueueStats jclCheck = loadQueueStats(Queues.JCL_CHECKING_QUEUE);
		sample.getQueuesStats().put(jclCheck.getName(), jclCheck);
	}

	/**
	 * Reads statistics info of Hazelcast MAPS, the local information
	 * 
	 * @param mapName map name to access to Hazelcast
	 * @return set of statistics of map
	 */
	private MapStats loadMapStats(String mapName) {
		// gets the local map stats
		LocalMapStats stats = Main.getHazelcast().getMap(mapName).getLocalMapStats();
		// gets the local operations info
		LocalMapOperationStats ostats = stats.getOperationStats();

		// creates the bean for MAP stats to return
		MapStats mstats = new MapStats();
		// sets map name
		mstats.setName(mapName);
		// loads backup data
		mstats.setBackupEntryCount(stats.getBackupEntryCount());
		mstats.setBackupEntryMemoryCost(stats.getBackupEntryMemoryCost());
		// loads dirty entries and hits
		mstats.setDirtyEntryCount(stats.getDirtyEntryCount());
		mstats.setHits(stats.getHits());
		// loads lock entries
		mstats.setLockedEntryCount(stats.getLockedEntryCount());
		mstats.setLockWaitCount(stats.getLockWaitCount());
		// loads entry count and used memory
		mstats.setOwnedEntryCount(stats.getOwnedEntryCount());
		mstats.setOwnedEntryMemoryCost(stats.getOwnedEntryMemoryCost());

		// creates the bean for MAP OPERATION stats to return
		MapOperationsStats mostats = mstats.getOperationsStats();
		// sets the amount of actions
		mostats.setNumberOfEvents(ostats.getNumberOfEvents());
		mostats.setNumberOfGets(ostats.getNumberOfGets());
		mostats.setNumberOfOtherOperations(ostats.getNumberOfOtherOperations());
		mostats.setNumberOfPuts(ostats.getNumberOfPuts());
		mostats.setNumberOfRemoves(ostats.getNumberOfRemoves());
		// loads the latency information on 
		// different actions
		mostats.setTotalGetLatency(ostats.getTotalGetLatency());
		mostats.setTotalPutLatency(ostats.getTotalPutLatency());
		mostats.setTotalRemoveLatency(ostats.getTotalRemoveLatency());
		return mstats;
	}

	/**
	 * Reads statistics info of Hazelcast QUEUES, the local information
	 * 
	 * @param queueName queue name to access to Hazelcast
	 * @return set of statistics of map
	 */
	private QueueStats loadQueueStats(String queueName) {
		// gets the local queue stats 
		LocalQueueStats stats = Main.getHazelcast().getQueue(queueName).getLocalQueueStats();
		// gets operation stats 
		LocalQueueOperationStats ostats = stats.getOperationStats();
		
		// creates the bean about QUEUE stats to return
		QueueStats qstats = new QueueStats();
		// sets queue name
		qstats.setName(queueName);

		// sets age information
		qstats.setAveAge(stats.getAveAge());
		qstats.setMaxAge(stats.getMaxAge());
		qstats.setMinAge(stats.getMinAge());
		// sets item counts
		qstats.setBackupItemCount(stats.getBackupItemCount());
		qstats.setOwnedItemCount(stats.getOwnedItemCount());

		// creates the bean about QUEUE OPERATION stats to return
		QueueOperationsStats qostats = qstats.getOperationsStats();
		// sets the amount of actions
		qostats.setNumberOfEmptyPolls(ostats.getNumberOfEmptyPolls());
		qostats.setNumberOfOffers(ostats.getNumberOfOffers());
		qostats.setNumberOfPolls(ostats.getNumberOfPolls());
		qostats.setNumberOfRejectedOffers(ostats.getNumberOfRejectedOffers());
		return qstats;
	}
}