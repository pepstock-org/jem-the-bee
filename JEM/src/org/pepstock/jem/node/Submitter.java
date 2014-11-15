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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.pepstock.jem.Job;
import org.pepstock.jem.Result;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.events.JobLifecycleEvent;
import org.pepstock.jem.node.tasks.JobTask;

import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

/**
 * Is a thread, responsible to execute jobs which are removed from input queue
 * to be executed here. It creates JobTask, it submits it, manages exceptions
 * which are thrown by job execution, moves the job from RUNNING to OUTPUT queue
 * and manages different status of node.
 * 
 * @see org.pepstock.jem.node.InputQueueManager#InputQueueManager()
 * @see org.pepstock.jem.node.Status#Status(int, String)
 * @see org.pepstock.jem.node.Queues#RUNNING_QUEUE
 * @see org.pepstock.jem.node.Queues#OUTPUT_QUEUE
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Submitter implements Runnable {
	
	private final SynchronousExecutorService executor = new SynchronousExecutorService();
	
	private Job job = null;

	/**
	 * Constructs a submitter with the job to submit.
	 * @param job job instance to execute
	 */
	public Submitter(Job job) {
		this.job = job;
	}

	/**
	 * Register 2 RMI objects to RMI registry, necessary during the job
	 * execution to notify the end of steps and ask for global resource locks.<br>
	 * Then it takes job from queue and execute it.
	 * 
	 * @see org.pepstock.jem.node.rmi.ResourceLocker
	 * @see org.pepstock.jem.node.rmi.TasksDoor
	 * @see org.pepstock.jem.util.rmi.RegistryContainer#addRmiObject(String,
	 *      org.pepstock.jem.util.rmi.RmiObject)
	 * @see org.pepstock.jem.node.InputQueueManager#takeJob()
	 */
	public void run() {	
		if (job == null){
			return;
		}
		
		Exception exception = null;
		CancelableTask task = null;
		
		Main.NUMBER_OF_JOB_SUBMITTED.incrementAndGet();

		// synchronizes the updates on node status 
		// to avoid inconsistent data in multi threading
		Main.getNode().getLock().lock();
		try {

			// sets current job which is executed
			Main.getNode().getJobs().put(job.getId(), job.getName());

			// sets job name as requestor for GRS name
			// each node can have only one request for grs
			RequestLock requestLock = Main.getNode().createRequestLock(job);
			Main.getNode().getRequests().put(job.getId(), requestLock);

			// store node info in NODES_QUEUE map
			NodeInfoUtility.storeNodeInfo(Main.getNode(), true);

			// gets the JEM factory to have jobtask
			JemFactory factory = Main.FACTORIES_LIST.get(job.getJcl().getType());
			JobTask at = factory.createJobTask(job);

			// saves jobtask in a static reference so accessible everywhere
			task = new CancelableTask(at);
			Main.CURRENT_TASKS.put(job.getId(), task);
			
		} catch( Exception e){
			exception = e;
		} finally {
			Main.getNode().getLock().unlock();
		}

		Result result = null;

		try {
			if ((exception != null) || (task == null)){
				throw new ExecutionException(exception);
			}
			// fires event that job is running
			Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addJobLifecycleEvent(new JobLifecycleEvent(Queues.RUNNING_QUEUE, job));

			// executes jobtask
			executor.submit(task);
			LogAppl.getInstance().emit(NodeMessage.JEMC020I, job.toString());

			// it waits for the end of job
			result = task.get();

			// if no exceptions and no canceled, prepares Result and finalizes
			// job end
			if (result.getReturnCode() != Result.CANCELED) {
				job.setResult(result);
				job.setEndedTime(new Date());
				jobEnded(job);
			} else {
				throw new CancellationException();
			}
		} catch (CancellationException e) {
			// System error during executor running
			result = getResultForException(Result.CANCELED, e);
		} catch (InterruptedException e) {
			// System error during executor running
			result = getResultForException(Result.SEVERE, e);
		} catch (ExecutionException e) {
			// System error during executor running
			result = getResultForException(Result.SEVERE, e);
		} catch (Exception e) {
			// System error during executor running
			result = getResultForException(Result.SEVERE, e);
		}


		// prints job log footer and clean jobtask static reference
		JobLogManager.printFooter(job, result.getReturnCode(), result.getExceptionMessage());
		// writes JOB
		Main.getOutputSystem().writeJob(job);

		Main.getNode().getLock().lock();
		try {
			RequestLock requestLock = Main.getNode().getRequests().remove(job.getId());
			// checks id there is any resources and then locks still active.
			// could happen when the job has been cancelled
			if (requestLock != null && !requestLock.getResources().isEmpty()){
				requestLock.unlock();
				requestLock.getResources().clear();
			}
			Main.CURRENT_TASKS.remove(job.getId());
			// cleans nodeinfo data
			Main.getNode().getJobs().remove(job.getId());

			// store node info in NODES_QUEUE map
			// using the hook of threadpool in inputqueuemanager
		} finally {
			Main.getNode().getLock().unlock();
		}
	}
	
	/**
	 * Creates a result object whenan exception occurs.
	 * @param returnCode return code to set
	 * @param exception exception occurred
	 * @return result object
	 */
	private Result getResultForException(int returnCode, Exception exception){
		StringWriter sw = new StringWriter();
		// save the complete stack trace
		exception.printStackTrace(new PrintWriter(sw));
		// System error during executor running
		Result result = new Result();
		result.setReturnCode(returnCode);
		result.setExceptionMessage(sw.getBuffer().toString());
		job.setResult(result);
		job.setEndedTime(new Date());
		jobEnded(job);
		return result;
	}

	/**
	 * Moves the job instance from RUNNING queue to OUTPUT queue.<br>
	 * It notify the message for job end, using Hazelcast topic structure.
	 * 
	 * @see org.pepstock.jem.node.Queues#RUNNING_QUEUE
	 * @see org.pepstock.jem.node.Queues#OUTPUT_QUEUE
	 * @see org.pepstock.jem.node.Queues#ENDED_JOB_TOPIC
	 * @param job job instance
	 */
	private void jobEnded(Job job) {
		job.setRunningStatus(Job.NONE);
		LogAppl.getInstance().emit(NodeMessage.JEMC021I, job.toString(), String.valueOf(job.getResult().getReturnCode()));

		// moves job from running to output
		IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);
		IMap<String, Job> outputQueue = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);

		try {
			outputQueue.lock(job.getId());
			runningQueue.lock(job.getId());

			runningQueue.remove(job.getId());
			outputQueue.put(job.getId(), job);
			
		} catch (Exception ex){
			LogAppl.getInstance().emit(NodeMessage.JEMC175E, ex, job.getName());			
		} finally {
			runningQueue.unlock(job.getId());
			outputQueue.unlock(job.getId());
		}

		// fires event that the job is ended
		Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addJobLifecycleEvent(new JobLifecycleEvent(Queues.OUTPUT_QUEUE, job));

		// send a topic to client which is wait for
		ITopic<Job> topic = Main.getHazelcast().getTopic(Queues.ENDED_JOB_TOPIC);
		topic.publish(job);
	}
	
	/**
	 * Simple and synchronous executor service , to use Future tasks.
	 *  
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	public static class SynchronousExecutorService extends AbstractExecutorService {
		
		private volatile boolean shutdown;

		/* (non-Javadoc)
		 * @see java.util.concurrent.ExecutorService#shutdown()
		 */
		@Override
		public void shutdown() {
			shutdown = true;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.ExecutorService#shutdownNow()
		 */
		@Override
		public List<Runnable> shutdownNow() {
			return new ArrayList<Runnable>();
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.ExecutorService#isShutdown()
		 */
		@Override
		public boolean isShutdown() {
			return shutdown;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.ExecutorService#isTerminated()
		 */
		@Override
		public boolean isTerminated() {
			return shutdown;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
		 */
		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			return true;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
		 */
		@Override
		public void execute(Runnable command) {
			// simply execute task
			command.run();
		}
	}

}