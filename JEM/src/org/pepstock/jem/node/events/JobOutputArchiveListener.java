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
import java.util.Properties;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.archive.JobOutputArchive;
import org.pepstock.jem.node.executors.TaskExecutor;
import org.pepstock.jem.node.executors.jobs.Purge;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.util.ZipUtil;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IMap;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class JobOutputArchiveListener implements JobLifecycleListener {

	private static final String CLASS_PROPERTY_FILE = "class";
	
	private boolean isInizialized = false;
	
	private JobOutputArchive jobOutputArchive = null;

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#queued(org.pepstock.jem.Job)
	 */
    @Override
    public void queued(Job job) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#running(org.pepstock.jem.Job)
	 */
    @Override
    public void running(Job job) {
    	// do nothing
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ZipUtil.createZip(folderToZip, baos);
				boolean delete = jobOutputArchive.archive(job, baos.toByteArray());
				if (delete) {
					purge(job);
				} else {
					LogAppl.getInstance().emit(NodeMessage.JEMC300W, job.toString());	
				}
			} catch (Exception re){
				LogAppl.getInstance().emit(NodeMessage.JEMC301E, re, job.toString());
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
				LogAppl.getInstance().emit(NodeMessage.JEMC297E, classParam,JobOutputArchive.class.getName(), objectTL.getClass().getName());
				return;
			}
			jobOutputArchive = (JobOutputArchive)objectTL;
			LogAppl.getInstance().emit(NodeMessage.JEMC298I, jobOutputArchive.getClass().getName());
			isInizialized = true;
        } catch (Exception e) {
        	LogAppl.getInstance().emit(NodeMessage.JEMC297E, e, classParam,JobOutputArchive.class.getName(), "null");
        }

	}

	
	private void purge(Job job) {
		try{
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
			TaskExecutor.submit(new Purge(job), cluster.getLocalMember());
			LogAppl.getInstance().emit(NodeMessage.JEMC302I, job.toString());
		} catch (Exception re){
			LogAppl.getInstance().emit(NodeMessage.JEMC299E, re, job.toString());
		}
	}

}