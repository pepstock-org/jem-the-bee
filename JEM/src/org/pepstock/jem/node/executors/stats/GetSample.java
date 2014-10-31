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
	 * @param newSample sample for whole JEM cluster
	 */
	public GetSample(Sample newSample) {
		this.newSample = newSample;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#checkShutDown()
	 */
	@Override
	public void checkShutDown() throws ExecutorException {
		// NOP here
		// is called in execute method to return null!
		// to avoid that Hazelcast distributed tasks joint fails
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public LightMemberSample execute() throws ExecutorException {
		// checks if shutdown
		// if yes, return null
		// without exception
		try{
			super.checkShutDown();
		} catch (ExecutorException ex){
			// ignore
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			return null;
		}
		
		// if node is null, skip!
		// could happen if the executor is running in a starting node 
		if (Main.getNode() == null){
			return null;
		}
		// if statistics manager is null, skip!
		// could happen if the executor is running in a starting node 
		if (Main.getStatisticsManager() == null){
			return null;
		}
		
		Sigar sigar = new Sigar();
		
		// loads all member data
		MemberSample memberSample = new MemberSample();
		memberSample.setMemberKey(Main.getNode().getKey());
		memberSample.setMemberLabel(Main.getNode().getLabel());
		memberSample.setMemberHostname(Main.getNode().getHostname());
		memberSample.setPid(sigar.getPid());
		
		// gets last sample, used to calculate increments between two  samples
		MemberSample lastMemberSample = Main.getStatisticsManager().getLastMemberSample();
		
		memberSample.setNumberOfJCLCheck((lastMemberSample == null) ? Main.NUMBER_OF_JCL_CHECK.intValue() : Main.NUMBER_OF_JCL_CHECK.intValue() - lastMemberSample.getTotalNumberOfJCLCheck());
		memberSample.setNumberOfJOBSubmitted((lastMemberSample == null) ? Main.NUMBER_OF_JOB_SUBMITTED.intValue() : Main.NUMBER_OF_JOB_SUBMITTED.intValue() - lastMemberSample.getTotalNumberOfJOBSubmitted());
	
		memberSample.setTotalNumberOfJCLCheck(Main.NUMBER_OF_JCL_CHECK.intValue());
		memberSample.setTotalNumberOfJOBSubmitted(Main.NUMBER_OF_JOB_SUBMITTED.intValue());
		
		loadCpuUtilization(memberSample, sigar);
		
		loadMemoryUtilization(memberSample, sigar);
		
		loadProcessCpuUtilization(memberSample, sigar);
		
		loadProcessMemoryUtilization(memberSample, sigar);
		
		loadGFSUtilization(memberSample, sigar);
		
		loadHazelcastMapsStats(memberSample);
		
		loadHazelcastQueuesStats(memberSample);
		
		newSample.getMembers().add(memberSample);
		
		Main.getStatisticsManager().write(newSample);
		
		Main.getStatisticsManager().setLastMemberSample(memberSample);
		
		return createLightMemberSample(newSample, memberSample);
	}
	
	/**
	 * loads all info 
	 * @param sample light sample (container)
	 * @param msample set of data for specific member
	 * @return a light sample for member
	 */
	private LightMemberSample createLightMemberSample(Sample sample, MemberSample msample){
		
		LightMemberSample memberSample = new LightMemberSample();
		memberSample.setMemberKey(msample.getMemberKey());
		memberSample.setMemberLabel(msample.getMemberLabel());
		memberSample.setMemberHostname(msample.getMemberHostname());
		memberSample.setPid(msample.getPid());
		memberSample.setKey(sample.getKey());
		memberSample.setCpuPercent(msample.getCpu().getPercent());
		memberSample.setMemoryAvailable(msample.getMemory().getAvailable());
		memberSample.setMemoryFree(msample.getMemory().getFree());
		memberSample.setNumberOfJCLCheck(msample.getNumberOfJCLCheck());
		memberSample.setNumberOfJOBSubmitted(msample.getNumberOfJOBSubmitted());
		memberSample.setProcessCpuPercent(msample.getProcessCpu().getPercent());
		memberSample.setProcessTotalCpu(msample.getProcessCpu().getTotal());
		memberSample.setProcessMemoryUsed(msample.getProcessMemory().getUsed());
		memberSample.setProcessMemoryFree(msample.getProcessMemory().getFree());
		memberSample.setTime(sample.getTime());
		memberSample.setTotalNumberOfJCLCheck(msample.getTotalNumberOfJCLCheck());
		memberSample.setTotalNumberOfJOBSubmitted(msample.getTotalNumberOfJOBSubmitted());
		
		memberSample.setFileSystems(msample.getFileSystems());
		
		/**
		 * JOBS QUEUES
		 */
		MapStats map = msample.getMapsStats().get(Queues.INPUT_QUEUE);
		memberSample.getMapsStats().put(Queues.INPUT_QUEUE, createLightMapStats(map));

		map = msample.getMapsStats().get(Queues.OUTPUT_QUEUE);
		memberSample.getMapsStats().put(Queues.OUTPUT_QUEUE, createLightMapStats(map));

		map = msample.getMapsStats().get(Queues.RUNNING_QUEUE);
		memberSample.getMapsStats().put(Queues.RUNNING_QUEUE, createLightMapStats(map));

		map = msample.getMapsStats().get(Queues.ROUTING_QUEUE);
		memberSample.getMapsStats().put(Queues.ROUTING_QUEUE, createLightMapStats(map));
		
		return memberSample;

	}
	
	/**
	 * Load all maps data
	 * @param map Hazelcast map statistics
	 * @return a light map statistics
	 */
	private LightMapStats createLightMapStats(MapStats map){
		LightMapStats lmap = new LightMapStats();
		lmap.setHits(map.getHits());
		lmap.setName(map.getName());
		
		lmap.setNumberOfGets(map.getOperationsStats().getNumberOfGets());
		lmap.setNumberOfPuts(map.getOperationsStats().getNumberOfPuts());
		lmap.setNumberOfRemoves(map.getOperationsStats().getNumberOfRemoves());
		
		lmap.setOwnedEntryCount(map.getOwnedEntryCount());
		lmap.setOwnedEntryMemoryCost(map.getOwnedEntryMemoryCost());
		
		lmap.setLockedEntryCount(map.getLockedEntryCount());
		lmap.setLockWaitCount(map.getLockWaitCount());
		
		lmap.setTotalGetLatency(map.getOperationsStats().getTotalGetLatency());
		lmap.setTotalPutLatency(map.getOperationsStats().getTotalPutLatency());
		lmap.setTotalRemoveLatency(map.getOperationsStats().getTotalRemoveLatency());
		
		return lmap;
	}
	
	/**
	 * Calculate cpu consumption.
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadCpuUtilization(MemberSample sample, Sigar sigar){
		try {
			// loads all totals
			Cpu sCpu = sigar.getCpu();
			CpuUtilization cpu = sample.getCpu();
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
				// calculates max cpu that is consumable in time frame (equals to elapsed time multiply for processor number)
				long totPossibleCpu = diffTime * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				// calculates CPU load
				long cpuUsed = cpu.getUser() + cpu.getSystem() - lastCpuUtil.getUser() - lastCpuUtil.getSystem();
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				cpu.setPercent(cpuUsedPercent);
			} else {
				// uses SIGAR info without any special calculation
				long totPossibleCpu = 1 * TimeUtils.MINUTE * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				long cpuUsed = cpu.getUser() + cpu.getSystem();
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				cpu.setPercent(cpuUsedPercent);
			}
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculate memory consumption.
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadMemoryUtilization(MemberSample sample, Sigar sigar){
		try {
			Mem sMem = sigar.getMem();
			MemoryUtilization mem = sample.getMemory();
			mem.setAvailable(sMem.getTotal());
			mem.setUsed(sMem.getUsed());
			mem.setFree(sMem.getFree());
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculate JEM process cpu consumption.
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadProcessCpuUtilization(MemberSample sample, Sigar sigar){
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
				// calculates max cpu that is consumable in time frame (equals to elapsed time multiply for processor number)
				long totPossibleCpu = diffTime * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				// calculates CPU load
				long cpuUsed = cpu.getTotal() - lastCpuUtil.getTotal();
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				cpu.setPercent(cpuUsedPercent);
			} else {
				// uses SIGAR info without any special calculation
				long totPossibleCpu = 1 * TimeUtils.MINUTE * ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
				long cpuUsed = cpu.getTotal();
				double cpuUsedPercent = cpuUsed * 1D / totPossibleCpu;
				
				cpuUsedPercent = Math.min(Math.max(cpuUsedPercent, 0D), 1D);
				cpu.setPercent(cpuUsedPercent);
			}
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Calculate JEM process memory consumption.
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadProcessMemoryUtilization(MemberSample sample, Sigar sigar){
		try {
			ProcMem sMem = sigar.getProcMem(sample.getPid());
			ProcessMemoryUtilization mem = sample.getProcessMemory();
			mem.setAvailable(sMem.getSize());
			mem.setUsed(sMem.getResident());
			
			long free = mem.getAvailable() - mem.getUsed();
			mem.setFree(free);
		} catch (SigarException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC169W, e);
		}
	}

	/**
	 * Claculates GFS usage. 
	 * @param sample member sample
	 * @param sigar sigar instance to get system info
	 */
	private void loadGFSUtilization(MemberSample sample, Sigar sigar){
//			FileSystemUtilization fsUtil = sample.getFileSystem();
		sample.getFileSystems().add(getFileSystemUtilization("Output", System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Binary", System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Classpath", System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Library", System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Source", System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME), sigar));
		sample.getFileSystems().add(getFileSystemUtilization("Persistence", System.getProperty(ConfigKeys.JEM_PERSISTENCE_PATH_NAME), sigar));

		// it usesa LinkedList therefore the order is maintaned
		List<String> dataPathNames = Main.DATA_PATHS_MANAGER.getDataPathsNames();
		List<String> dataPaths = Main.DATA_PATHS_MANAGER.getDataPaths();
		
		for (int i=0; i<dataPathNames.size(); i++){
			String name = dataPathNames.get(i);
			String path = dataPaths.get(i);
			sample.getFileSystems().add(getFileSystemUtilization("Data ["+name+"]", path, sigar));
		}
	}
	
	
	private FileSystemUtilization getFileSystemUtilization(String name, String path, Sigar sigar){
		FileSystemUtilization fsUtil = new FileSystemUtilization();
		fsUtil.setName(name);
		fsUtil.setPath(path);
		try {
			FileSystemUsage usage = sigar.getFileSystemUsage(Main.getOutputSystem().getOutputPath().getAbsolutePath());
			long free = usage.getFree();
			long total  = usage.getTotal();
			long used  = usage.getUsed();
			
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
	 * @param sample member sample
	 */
	private void loadHazelcastMapsStats(MemberSample sample){
		MapStats input = loadMapStats(Queues.INPUT_QUEUE);
		MapStats running = loadMapStats(Queues.RUNNING_QUEUE);
		MapStats output = loadMapStats(Queues.OUTPUT_QUEUE);
		MapStats routing = loadMapStats(Queues.ROUTING_QUEUE);
		MapStats routed = loadMapStats(Queues.ROUTED_QUEUE);
		MapStats resources = loadMapStats(Queues.COMMON_RESOURCES_MAP);
		MapStats roles = loadMapStats(Queues.ROLES_MAP);
		MapStats stats = loadMapStats(Queues.STATS_MAP);
		MapStats preferences = loadMapStats(Queues.USER_PREFERENCES_MAP);
		
		sample.getMapsStats().put(input.getName(), input);
		sample.getMapsStats().put(running.getName(), running);
		sample.getMapsStats().put(output.getName(), output);
		sample.getMapsStats().put(routing.getName(), routing);
		
		sample.getMapsStats().put(routed.getName(), routed);
		sample.getMapsStats().put(resources.getName(), resources);
		sample.getMapsStats().put(roles.getName(), roles);
		sample.getMapsStats().put(stats.getName(), stats);
		sample.getMapsStats().put(preferences.getName(), preferences);
	}
	
	/**
	 * Loads Hazelcast queue stats
	 * @param sample member sample
	 */
	private void loadHazelcastQueuesStats(MemberSample sample){
		QueueStats jclCheck = loadQueueStats(Queues.JCL_CHECKING_QUEUE);
		sample.getQueuesStats().put(jclCheck.getName(), jclCheck);
	}
	
	/**
	 * Reads statistics info of Hazelcast MAPS
	 * 
	 * @param mapName map name to access to Hazelcast
	 * @return set of statistics of map
	 */
	private MapStats loadMapStats(String mapName){
			LocalMapStats stats = Main.getHazelcast().getMap(mapName).getLocalMapStats();
			LocalMapOperationStats ostats = stats.getOperationStats();
			
			MapStats mstats = new MapStats();
			mstats.setName(mapName);
			
			mstats.setBackupEntryCount(stats.getBackupEntryCount());
			mstats.setBackupEntryMemoryCost(stats.getBackupEntryMemoryCost());
			mstats.setDirtyEntryCount(stats.getDirtyEntryCount());
			mstats.setHits(stats.getHits());
			mstats.setLockedEntryCount(stats.getLockedEntryCount());
			mstats.setLockWaitCount(stats.getLockWaitCount());
			mstats.setOwnedEntryCount(stats.getOwnedEntryCount());
			mstats.setOwnedEntryMemoryCost(stats.getOwnedEntryMemoryCost());
			
			MapOperationsStats mostats = mstats.getOperationsStats();
			
			mostats.setNumberOfEvents(ostats.getNumberOfEvents());
			mostats.setNumberOfGets(ostats.getNumberOfGets());
			mostats.setNumberOfOtherOperations(ostats.getNumberOfOtherOperations());
			mostats.setNumberOfPuts(ostats.getNumberOfPuts());
			mostats.setNumberOfRemoves(ostats.getNumberOfRemoves());
			mostats.setTotalGetLatency(ostats.getTotalGetLatency());
			mostats.setTotalPutLatency(ostats.getTotalPutLatency());
			mostats.setTotalRemoveLatency(ostats.getTotalRemoveLatency());
			
			return mstats;
	}
	
	/**
	 * Reads statistics info of Hazelcast QUEUES
	 * 
	 * @param queueName queue name to access to Hazelcast
	 * @return set of statistics of map
	 */
	private QueueStats loadQueueStats(String queueName){
		LocalQueueStats stats = Main.getHazelcast().getQueue(queueName).getLocalQueueStats();
		LocalQueueOperationStats ostats = stats.getOperationStats();
		
		QueueStats qstats = new QueueStats();
		qstats.setName(queueName);
		
		qstats.setAveAge(stats.getAveAge());
		qstats.setBackupItemCount(stats.getBackupItemCount());
		qstats.setMaxAge(stats.getMaxAge());
		qstats.setMinAge(stats.getMinAge());
		qstats.setOwnedItemCount(stats.getOwnedItemCount());
		
		QueueOperationsStats qostats = qstats.getOperationsStats();
		qostats.setNumberOfEmptyPolls(ostats.getNumberOfEmptyPolls());
		qostats.setNumberOfOffers(ostats.getNumberOfOffers());
		qostats.setNumberOfPolls(ostats.getNumberOfPolls());
		qostats.setNumberOfRejectedOffers(ostats.getNumberOfRejectedOffers());
		
		return qstats;
	}

}