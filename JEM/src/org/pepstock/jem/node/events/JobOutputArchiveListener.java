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
package org.pepstock.jem.node.events;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.pepstock.jem.Job;
//FIXME to be removed
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.archive.JobOutputArchive;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.GenericCallBack;
import org.pepstock.jem.node.executors.jobs.Purge;
import org.pepstock.jem.util.ZipUtil;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.IMap;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class JobOutputArchiveListener implements JobLifecycleListener {

	private static final String CLASS_PROPERTY_FILE = "class";
	
	private boolean isInizialized = false;
	
	private JobOutputArchive jobOutputArchive = null;

	
	/**
	 * 
	 */
	public JobOutputArchiveListener() {
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#queued(org.pepstock.jem.Job)
	 */
    @Override
    public void queued(Job job) {
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#running(org.pepstock.jem.Job)
	 */
    @Override
    public void running(Job job) {
    }
    
	/**
	 * Logs "is ended. RC="
	 * 
	 * @param job job instance
	 */
	@Override
	public void ended(Job job) {
		if (isInizialized && jobOutputArchive != null){
			File outputPath = Main.getOutputSystem().getOutputPath();
			File folderToZip = new File(outputPath, job.getId());
			//File zipFile = new File(outputPath, job.getId()+".zip");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//FileUtils.copyInputStreamToFile(new ByteArrayInputStream(baos.toByteArray()), zipFile);
			try {
				ZipUtil.createZip(folderToZip, baos);
				boolean delete = jobOutputArchive.archive(job, baos.toByteArray());
				if (delete) {
					try{
						purge(job);
						LogAppl.getInstance().emit(AntUtilMessage.JEMZ047I, job.toString());
					} catch (RemoteException re){
						LogAppl.getInstance().emit(AntUtilMessage.JEMZ044E, re, job.toString());
					}
				} else {
					LogAppl.getInstance().emit(AntUtilMessage.JEMZ045W, job.toString());	
				}
			} catch (Exception re){
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ046E, re, job.toString());
			}
		}
	}

	/**
	 * Not implemented
	 * 
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
		String classParam = properties.getProperty(CLASS_PROPERTY_FILE);
		
		Object objectTL;
        try {
	        objectTL = Class.forName(classParam).newInstance();
			if (!(objectTL instanceof JobOutputArchive)) {
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ042E, classParam,JobOutputArchive.class.getName(), objectTL.getClass().getName());
				return;
			}
			jobOutputArchive = (JobOutputArchive)objectTL;
			LogAppl.getInstance().emit(AntUtilMessage.JEMZ043I, jobOutputArchive.getClass().getName());
			isInizialized = true;
        } catch (Exception e) {
        	LogAppl.getInstance().emit(AntUtilMessage.JEMZ042E, e, classParam,JobOutputArchive.class.getName(), "null");
        }

	}

	
	private void purge(Job job) throws Exception {
		IMap<String, Job> jobs = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);
		try{
			jobs.lock(job.getId());
			if (jobs.containsKey(job.getId())) {
				jobs.remove(job.getId());
			}
		} finally {
			jobs.unlock(job.getId());
		}
		
		Cluster cluster = Main.getHazelcast().getCluster();
		// creates the future task
		DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Purge(job), cluster.getLocalMember());
		// gets executor service and executes!
		ExecutorService executorService = Main.getHazelcast().getExecutorService();
		task.setExecutionCallback(new GenericCallBack());
		executorService.execute(task);
	}

}