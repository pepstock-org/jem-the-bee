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
package org.pepstock.jem.node.rmi;

import java.rmi.RemoteException;
import java.util.Map;

import org.pepstock.jem.Job;
import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.Result;
import org.pepstock.jem.Step;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.CancelableTask;
import org.pepstock.jem.node.JobLogManager;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.hazelcast.Locks;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.util.locks.LockException;
import org.pepstock.jem.util.locks.ReadLock;
import org.pepstock.jem.util.rmi.DefaultRmiObject;

import com.hazelcast.core.IMap;

/**
 * is RMI implementation uses by StepListener of executing job.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class TasksDoorImpl extends DefaultRmiObject implements TasksDoor {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the object calling the super class.
	 * 
	 * @throws RemoteException occurs if errors
	 */
	public TasksDoorImpl() throws RemoteException {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.rmi.TasksDoor#setJobStarted()
	 */
	@Override
	public JobStartedObjects setJobStarted(String jobId, String processId) throws RemoteException {
		return setJobStarted(jobId, processId, null);
	}
	

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.TasksDoor#setJobStarted(java.lang.String, java.lang.String, java.util.Properties)
	 */
	@Override
	public JobStartedObjects setJobStarted(String jobId, String processId,  Map<String, String> props) throws RemoteException {
		// gets current task by job id
		CancelableTask task = getCurrentTask(jobId);
		
		// saves process-id in current-task instance of node
		task.setProcessId(processId);

		// gets job object from static reference to print info
		Job job = task.getJobTask().getJob();
		job.setProcessId(processId);
		// gets job object from Hazelcast queue of RUNNING to change current
		// process ID
		IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);

		try {
			runningQueue.lock(job.getId());
			if (runningQueue.containsKey(job.getId())){
				Job storedJob = runningQueue.get(job.getId());
				storedJob.setProcessId(processId);
				// replaces job instance in queue
				if (props != null && !props.isEmpty()){
					PropertiesWrapper jclProps = storedJob.getJcl().getProperties();
					if (jclProps != null){
						jclProps.putAll(props);
					} else {
						PropertiesWrapper newProps = new PropertiesWrapper();
						newProps.putAll(props);
						storedJob.getJcl().setProperties(newProps);
					}
				}
				runningQueue.replace(storedJob.getId(), storedJob);
			} else {
				runningQueue.put(job.getId(), job);
			}
		} catch (Exception ex){
			throw new RemoteException(ex.getMessage(), ex);					
		} finally{
			runningQueue.unlock(job.getId());	
		}
		
		LogAppl.getInstance().emit(NodeMessage.JEMC022I, job.toString(), processId);

		// prints header into job-log
		JobLogManager.printJobStarted(job);
		
		ReadLock read = new ReadLock(Main.getHazelcast(), Locks.DATASETS_RULES);
		try {
			read.acquire();	
			JobStartedObjects result = new JobStartedObjects();
			result.setRoles(task.getJobTask().getRoles());
			result.setStorageGroupsManager(Main.DATA_PATHS_MANAGER);
			return result;
		} catch (LockException e) {
			throw new RemoteException(NodeMessage.JEMC260E.toMessage().getFormattedMessage(Locks.DATASETS_RULES), e);
		} finally {
			try {
				read.release();
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC261E, e, Locks.DATASETS_RULES);
			}
		}
	}

	/**
	 * Called when a step of job is started.<br>
	 * Access to RUNNING queue to change current step of job.
	 * 
	 * @param step step instance
	 * @throws RemoteException occurs if errors
	 */
	@Override
	public void setStepStarted(String jobId, Step step) throws RemoteException {
		// gets current task by job id
		CancelableTask task = getCurrentTask(jobId);
		// gets job object from static reference to change current step
		Job job = task.getJobTask().getJob();
		job.setCurrentStep(step);

		// gets job object from Hazelcast queue of RUNNING to change current
		// step
		IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);

		try {
			runningQueue.lock(job.getId());
			Job storedJob = runningQueue.get(job.getId());
			storedJob.setCurrentStep(step);
			// replaces job instance in queue
			runningQueue.replace(storedJob.getId(), storedJob);

		} catch (Exception ex){
			throw new RemoteException(ex.getMessage(), ex);					
		} finally{
			runningQueue.unlock(job.getId());	
		}
	}

	/**
	 * Called when a step of job is ended.<br>
	 * Sets info into Result object (whole result of job) using result of step
	 * execution.<br>
	 * Prints the step info into job-log
	 * 
	 * @param step step instance
	 * @throws RemoteException occurs if errors
	 */
	@Override
	public void setStepEnded(String jobId, Step step) throws RemoteException {
		// gets current task by job id
		CancelableTask task = getCurrentTask(jobId);
		
		// gets result object from static reference to change return code and
		// exception if exists
		Result result = task.getJobTask().getResult();

		// Updates return code (and then exception) only if return code of step
		// is
		// greater than previously return codes.
		if (result.getReturnCode() < step.getReturnCode()) {
			result.setReturnCode(step.getReturnCode());
			result.setExceptionMessage(step.getException());
		}

		// gets job object from static reference to print info
		Job job = task.getJobTask().getJob();
		JobLogManager.printStepResult(job, step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.rmi.TasksDoor#setJobEnded()
	 */
	@Override
	public void setJobEnded(String jobId) throws RemoteException {
		// do nothing
	}

}