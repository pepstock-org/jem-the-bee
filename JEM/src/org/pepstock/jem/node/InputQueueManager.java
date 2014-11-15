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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

/**
 * Manages all activities related to input queue. It's able to listen when new
 * jobs are put in input queue and checks (using environment, domain and
 * affinity) if these jobs could be executed by itself.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class InputQueueManager implements ShutDownInterface, EntryListener<String, Job>{
	
	private static final BlockingQueue<Runnable> RUNNABLES = new SynchronousQueue<Runnable>();
	
	private final ThreadPoolDelegate delegate = new ThreadPoolDelegate();
	
	private JobComparator comparator = new JobComparator();

	private InputQueuePredicate predicate = new InputQueuePredicate();
	
	private AtomicInteger currentSubmitters = new AtomicInteger();
	
    private final CountDownLatch latch = new CountDownLatch(1);
    
    private boolean isInitializing = false;
	/**
	 * Constructs the manager. It creates a internal queue on which it moves the
	 * job to execute. It defines itself as a listener of input queue.
	 * Furthermore it creates the right SQLPredicate to extract the job which
	 * could be executed here. Where conditions are:<br>
	 * <p>
	 * 1. Hold not true<br>
	 * 2. Environment equals to
	 * org.pepstock.jem.node.ExecutionEnvironment.getEnvironment()<br>
	 * 3. Domain equals to
	 * org.pepstock.jem.node.ExecutionEnvironment.getDomain() or Job.DEFAULT_DOMAIN<br>
	 * 4. Affinity equals to
	 * org.pepstock.jem.node.ExecutionEnvironment.getAffinity() or Job.DEFAULT_AFFINITY<br>
	 * </p>
	 * It sorts by priority on descending mode.
	 * 
	 * @see org.pepstock.jem.node.ExecutionEnvironment#getEnvironment()
	 * @see org.pepstock.jem.node.ExecutionEnvironment#getDomain()
	 * @see org.pepstock.jem.node.ExecutionEnvironment#getAffinity()
	 * @see org.pepstock.jem.Job#isHold()
	 * @see org.pepstock.jem.Job#getPriority()
	 */
	public InputQueueManager() {
		predicate.setExecutionEnviroment(Main.EXECUTION_ENVIRONMENT);
	}
	
	/**
	 * Initialize the object, checking in the queue if there are any jobs to submit
	 */
	public void init(){
		IMap<String, Job> inputQueue = Main.getHazelcast().getMap(Queues.INPUT_QUEUE);
		inputQueue.addEntryListener(this, true);
		isInitializing = true;
		checkJobsInQueue();
	}
	
	/**
	 * Checks for all available possible parallel jobs if 
	 * there are any job to submit
	 */
	public synchronized void checkJobsInQueue(){
		for (int i=0; i<Main.EXECUTION_ENVIRONMENT.getParallelJobs(); i++){
			// if returns that no jobs, breaks the "for" cycle
			if (!checkJobToSubmit()){
				return;
			}
		}
	}
	
	/**
	 * Checks if there is any job on input queue to execute.
	 * @return <code>true</code> if a job has been submitted, otherwise <code>false</code>
	 */
	private synchronized boolean checkJobToSubmit() {
		// if access maint, drains the node
		if (Main.IS_ACCESS_MAINT.get()){
			if (!Main.getNode().getStatus().equals(Status.DRAINED)){
				LogAppl.getInstance().emit(NodeMessage.JEMC189I);
				NodeInfoUtility.drain();
			}
			return false;
		}

		// uses to show "active" node status message
		boolean showActiveMessage = false;

		Main.getNode().getLock().lock();
		try {
			// if node is drain* no job to submit
			if (Main.getNode().getStatus().equals(Status.DRAINED) || Main.getNode().getStatus().equals(Status.DRAINING)){
				return false;
			}
			
			// if node is active, and current jobs is equals or over the maximum jobs return 
			if (Main.getNode().getStatus().equals(Status.ACTIVE)){
				if (currentSubmitters.intValue() >= Main.EXECUTION_ENVIRONMENT.getParallelJobs()){
					return false;
				}
			} else {
				// set Active status and store the info about the node on Hazelcast map
				Main.getNode().setStatus(Status.ACTIVE);
				// set to show message
				showActiveMessage = true;
				NodeInfoUtility.storeNodeInfo(Main.getNode());
			}
		} finally {
			Main.getNode().getLock().unlock();
		}

		// get jobs which could be executed by this node, using the SQL
		// predicate prepared on constructor
		IMap<String, Job> inputQueue = Main.getHazelcast().getMap(Queues.INPUT_QUEUE);

		Collection<Job> jobs = inputQueue.values(predicate);
		// it does if there is at least a job compliant with Sql predicate
		if (!jobs.isEmpty()) {
			// creates a new collection and sort with job comparator which uses
			// the priority
			List<Job> queuedJobs = new ArrayList<Job>(jobs);
			Collections.sort(queuedJobs, comparator);
			for (Job job : queuedJobs) {

				// set mustReturn because I'm not sure if some other node has
				// already change the list of jobs
				// set to true when it has the job to execute
				boolean mustReturn = false;

				// locks Input queue of hazelcast to avoid multiple access to
				// the map
				try {
					inputQueue.lock(job.getId());
					// checks if the job is still in input queue (I'm not
					// sure if some other node has already change the list
					// of jobs)
					if (inputQueue.containsKey(job.getId())) {
						IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);

						// set member key and label (this node), and started date into job
						job.setMemberId(Main.getNode().getKey());
						job.setMemberLabel(Main.getNode().getLabel());
						job.setStartedTime(new Date());

						job.setRunningStatus(Job.RUNNING);
						// move the job to internal queue and from INPUT
						// queue to RUNNING queue
						try{
							runningQueue.lock(job.getId());
							Job storedJob = inputQueue.remove(job.getId());

							// if storedJob is null means
							// that job is not longer in QUEUE. Why?
							// see issue #270. Could be a tryLock problem (hashcode for KEY?)
							if (storedJob != null){
								// controllo again
								runningQueue.put(job.getId(), job);
								//submit job on thread pool 
								delegate.execute(new Submitter(job));
								currentSubmitters.incrementAndGet();
								// job found! I can return
								mustReturn = true;
								LogAppl.getInstance().emit(NodeMessage.JEMC018I, job.toString());
							} 
						} catch (Exception ex){
							LogAppl.getInstance().emit(NodeMessage.JEMC017E, ex);
						} finally {
							runningQueue.unlock(job.getId());
						}
					}
				} finally {
					inputQueue.unlock(job.getId());
				}
				// if I have the job to execute, then return otherwise try with
				// another job, if there is
				if (mustReturn){
					if (showActiveMessage){
						LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
					}
					return true;
				}
			}
		}

		// checks if is INACTIVE
		Main.getNode().getLock().lock();
		try {
			if (currentSubmitters.intValue() == 0){
				// change status of node in Inactive if there is any submitter running. means that there is not nay job in
				// queue to execute by this node
				Main.getNode().setStatus(Status.INACTIVE);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				// if is initializing or is already started
				if (isInitializing || Main.NUMBER_OF_JOB_SUBMITTED.intValue() > 0){
					LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
					isInitializing = false;
				}
			}
		} finally {
			Main.getNode().getLock().unlock();
		}
		return false;
	}
	
	/**
	 * Checks and move the job on routing queue because the environment
	 * attribute is not equals to cluster definition
	 * 
	 * @param job job instance to check
	 * @return <code>true</code> if moved on routing queue, <code>false</code>
	 *         otherwise
	 */
	public synchronized boolean isRouted(Job job) {
		boolean isRouted = false;

		// check the environment value of job with environment of node
		// if they are not equals, job is moved to routing queue
		Jcl jcl = job.getJcl();
		if (!jcl.getEnvironment().equalsIgnoreCase(Main.EXECUTION_ENVIRONMENT.getEnvironment())) {

			// locks Input queue of hazelcast to avoid multiple access to the
			// map
			Lock lock = Main.getHazelcast().getLock(Queues.INPUT_QUEUE);
			lock.lock();
			try {

				// checks if the job is still in input queue (I'm not sure if
				// some other node has already change the list of jobs)
				IMap<String, Job> inputQueue = Main.getHazelcast().getMap(Queues.INPUT_QUEUE);
				if (inputQueue.containsKey(job.getId())) {
					IMap<String, Job> routingQueue = Main.getHazelcast().getMap(Queues.ROUTING_QUEUE);

					// move the job from INPUT queue to ROUTING queue
					inputQueue.remove(job.getId());
					routingQueue.put(job.getId(), job);

					// set return value
					isRouted = true;
					LogAppl.getInstance().emit(NodeMessage.JEMC019I, job.toString(), jcl.getEnvironment());
				}
			} finally {
				lock.unlock();
			}
		}
		return isRouted;
	}

	/**
	 * Listen if there is new job in input queue Checks if routed otherwise
	 * checks to submit by this node
	 */
	@Override
	public void entryAdded(EntryEvent<String, Job> event) {
		// if shutting down or access maint, do nothing
		if (Main.IS_SHUTTING_DOWN.get() || Main.IS_ACCESS_MAINT.get()){
			return;
		}
		
		Job job = event.getValue();

		// if not routed and status is INACTIVE (no job is executing), it can
		// check if new job could be executed by this node
		// or if node is active and pool is not full then
		// check if job could be executed by this node
		if (!isRouted(job)) {
			if (Main.getNode().getStatus().equals(Status.INACTIVE)) {
				checkJobsInQueue();
			} else if (Main.getNode().getStatus().equals(Status.ACTIVE) && (currentSubmitters.intValue() < Main.EXECUTION_ENVIRONMENT.getParallelJobs())){
				checkJobsInQueue();
			}
		}
	}

	/**
	 * Not implemented because not necessary
	 */
	@Override
	public void entryEvicted(EntryEvent<String, Job> event) {
		// do nothing
	}

	/**
	 * Not implemented because not necessary
	 */
	@Override
	public void entryRemoved(EntryEvent<String, Job> event) {
		// do nothing
	}

	/**
	 * Listen if there is a update on job in input queue The users could change
	 * the environment, domain, affinity or hold job attributes which can affect
	 * this node Checks if routed otherwise checks to submit by this node
	 */
	@Override
	public void entryUpdated(EntryEvent<String, Job> event) {
		// if shutting down or access maint, do nothing
		if (Main.IS_SHUTTING_DOWN.get() || Main.IS_ACCESS_MAINT.get()){
			return;
		}
		
		Job job = event.getValue();

		// if not routed and status is INACTIVE (no job is executing), it can
		// check if job could be executed by this node
		// or if node is active and pool is not full then
		// check if job could be executed by this node
		if (!isRouted(job)) {
			if (Main.getNode().getStatus().equals(Status.INACTIVE)) {
				checkJobsInQueue();
			} else if (Main.getNode().getStatus().equals(Status.ACTIVE) && (currentSubmitters.intValue() < Main.EXECUTION_ENVIRONMENT.getParallelJobs())){
				checkJobsInQueue();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.ThreadPoolExecutor#shutdown()
	 */
	@Override
	public void shutdown() {
		LogAppl.getInstance().emit(NodeMessage.JEMC218I, currentSubmitters.intValue());
		// checks if there are some jobs in execution
		// if yes, wait for the last job
		if (currentSubmitters.intValue() > 0){
			try {
				latch.await();
			} catch (InterruptedException e) {
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
		// shudown the thread pool
		delegate.shutdown();
	}

	/**
	 * Custom Thread pool, with only 1 core thread and maximum set to Integer.MAX_VALUE.<br>
	 * Uses a synchronized queue because the maximum jobs in executoin are set and managed
	 * by external counters
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	class ThreadPoolDelegate extends ThreadPoolExecutor {

		/**
		 * Creates a thread pool with only 1 core thread and infinite maximum.
		 */
		public ThreadPoolDelegate() {
			super(1, Integer.MAX_VALUE, 10, TimeUnit.SECONDS, RUNNABLES);
		}

		@Override
		protected final void afterExecute(Runnable runnable, Throwable exception) {
			super.afterExecute(runnable, exception);
			// if submitter, maintain counter of current thread
			if (runnable instanceof Submitter){
				currentSubmitters.decrementAndGet();
				// sets status of node
				Main.getNode().getLock().lock();
				try {
					if (Main.getNode().getStatus().equals(Status.DRAINING)) {
						if (currentSubmitters.intValue() == 0){
							Main.getNode().setStatus(Status.DRAINED);
							LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
						}
					} else {
						if (currentSubmitters.intValue() == 0){
							Main.getNode().setStatus(Status.INACTIVE);
							LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
						}
					}
					NodeInfoUtility.storeNodeInfo(Main.getNode(), true);
				} finally {
					Main.getNode().getLock().unlock();
				}
				// shutdown is set and is the last submitter
				// counts down to close node
				if (Main.IS_SHUTTING_DOWN.get()){
					if (currentSubmitters.intValue() == 0){
						latch.countDown();
					}
				} else {
					// if not shutting down, checks for another jobs to submit
					checkJobToSubmit();
				}
			} else {
				LogAppl.getInstance().emit(NodeMessage.JEMC217E, runnable.getClass().getName());
			}
		}
	}
	
}