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
package org.pepstock.jem.jbpm.tasks;

import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.pepstock.jem.Result;
import org.pepstock.jem.Step;
import org.pepstock.jem.jbpm.JBpmKeys;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.Task;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.rmi.JobStartedObjects;
import org.pepstock.jem.node.rmi.TasksDoor;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.rmi.RegistryLocator;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * Implements the standard event listener of JBPM to prepare the right integration with the JEM.<br>
 * It listen ONLY start and end of process and tasks.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class StepListener extends DefaultProcessEventListener{
	
	private TasksDoor door = null;
	
	// it contains all task events. This is necessary because if there is an exception, the method of task end
	// is not called
	private Map<Long, ProcessNodeEvent> nodeEvents = new ConcurrentHashMap<Long, ProcessNodeEvent>();
	
	private String lockingScope = JBpmKeys.JBPM_JOB_SCOPE;
	
	private boolean hasDivergingGateway = false;
	
	private Locker locker = null;
	
	private ProcessEvent processEvent = null;
	
	/**
	 * Empty constructor
	 */
	public StepListener() {

	}

	/**
	 * @return the processEvent
	 */
	public ProcessEvent getProcessEvent() {
		return processEvent;
	}

	/**
	 * @return the nodeEvents
	 */
	public Map<Long, ProcessNodeEvent> getNodeEvents() {
		return nodeEvents;
	}


	/* (non-Javadoc)
	 * @see org.kie.api.event.process.ProcessEventListener#beforeNodeLeft(org.kie.api.event.process.ProcessNodeLeftEvent)
	 */
	@Override
	public void beforeNodeLeft(ProcessNodeLeftEvent arg0) {
		beforeNodeLeft(arg0, Result.SUCCESS, null);
	}
		
	/**
	 * Notify the end of step checking the status
	 * @param processNodeEvent process event
	 * @param result result of the step
	 * @param exception message exception
	 */
	public void beforeNodeLeft(ProcessNodeEvent processNodeEvent, int result, String exception) {
		// removes from the map the event, becaus ethe task is ended
		nodeEvents.remove(processNodeEvent.getNodeInstance().getNodeId());
		
		LogAppl.getInstance().emit(JBpmMessage.JEMM019I, processNodeEvent.getNodeInstance().getNodeName(), processNodeEvent.getNodeInstance().getId());

		// sets security manager internal action
		JBpmBatchSecurityManager batchSM = (JBpmBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);

		// checks if the target name is empty. if yes, does nothing
		try {
			// creates object to send to JEM
			Step step = new Step();
			// sets step name and description
    		step.setName(processNodeEvent.getNodeInstance().getNodeName()+"["+processNodeEvent.getNodeInstance().getNodeId()+"]");
    		step.setDescription(processNodeEvent.getNodeInstance().getNodeName()+"["+processNodeEvent.getNodeInstance().getNodeId()+"]");
    		
    		Task currentTask = CompleteTasksList.getInstance().getTaskByNodeID(processNodeEvent.getNodeInstance().getNodeId());
    		if (currentTask != null && exception == null){
    			step.setReturnCode(currentTask.getReturnCode());
    		} else {
    			// checks if has an exception.If yes, sets ERROR, otherwise
    			// SUCCESS
    			step.setReturnCode(result);
    		}

			// checks if has an exception, sets the exception message
			if (exception != null){
				step.setException(exception);
			}

			// send to JEM by RMI
			door.setStepEnded(JobId.VALUE, step);

			// only workitems JEM have JEM resources 
			if (processNodeEvent.getNodeInstance() instanceof WorkItemNodeInstance){
				// if step or task locking scope, unlocks resources
				if (isStepLockingScope() || isTaskLockingScope()){
					locker.unlock();
				}
			}

		} catch (MessageException e) {
			throw new JemRuntimeException(e);
		} catch (RemoteException e) {
			throw new JemRuntimeException(e);
		} finally {
			batchSM.setInternalAction(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.kie.api.event.process.ProcessEventListener#beforeNodeTriggered(org.kie.api.event.process.ProcessNodeTriggeredEvent)
	 */
    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent processNodeEvent) {
    	// stores the event in a map to avoid inconsistent situation when an exception occurs
    	nodeEvents.put(processNodeEvent.getNodeInstance().getNodeId(), processNodeEvent);
    	
		// - update TASk adding the workitem id
    	// because is different from node id
    	if (processNodeEvent.getNodeInstance() instanceof WorkItemNodeInstance){
    		WorkItemNodeInstance workItemNodeInstance = (WorkItemNodeInstance) processNodeEvent.getNodeInstance();
   		
    		// sets the wrkitem id to task
    		CompleteTasksList.getInstance().setWorkItemID(workItemNodeInstance.getNodeId(), workItemNodeInstance.getId());
    		
			// if step locking is set, then locks resources
			// loading tasks info
    		if (isStepLockingScope() || isTaskLockingScope()){
    			loadForLock(workItemNodeInstance.getNodeId());
    			try {
    				locker.lock();
    			} catch (MessageException e) {
    				throw new JemRuntimeException(e);
    			}
    		}
    	}
    	// sets security manager internal action
    	JBpmBatchSecurityManager batchSM = (JBpmBatchSecurityManager)System.getSecurityManager();
    	batchSM.setInternalAction(true);

    	// checks if the target name is empty. if yes, does nothing
    	try {
    		// creates object to send to JEM
    		Step step = new Step();
    		// sets step name and description
    		step.setName(processNodeEvent.getNodeInstance().getNodeName()+"["+processNodeEvent.getNodeInstance().getNodeId()+"]");
    		step.setDescription(processNodeEvent.getNodeInstance().getNodeName()+"["+processNodeEvent.getNodeInstance().getNodeId()+"]");

    		// send to JEM by RMI
    		door.setStepStarted(JobId.VALUE, step);
    	} catch (RemoteException e) {
    		throw new JemRuntimeException(e.getMessage(), e);
    	} finally{
    		batchSM.setInternalAction(false);
    	}
    	LogAppl.getInstance().emit(JBpmMessage.JEMM018I, processNodeEvent.getNodeInstance().getNodeName(), processNodeEvent.getNodeInstance().getId());
    }

	/* (non-Javadoc)
	 * @see org.kie.api.event.process.ProcessEventListener#beforeProcessCompleted(org.kie.api.event.process.ProcessCompletedEvent)
	 */
    @Override
    public void afterProcessCompleted(ProcessCompletedEvent processEvent) {
    	afterProcessCompletedFinally(processEvent);
    }
    
    /**
     * This method is the same of JBPM one. But it can be called from a different Process event, when an exception occurs.
     * @param processEvent event to use to clean up
     */
    public void afterProcessCompletedFinally(ProcessEvent processEvent) {
    	LogAppl.getInstance().emit(JBpmMessage.JEMM025I, processEvent.getProcessInstance().getProcessName());
		// flush STD OUT and ERR
		// probably is useless
		System.out.flush();
		System.err.flush();
		// sets the SM for internal actions
		JBpmBatchSecurityManager batchSM = (JBpmBatchSecurityManager)System.getSecurityManager();
		// sets internal action to true so it can perform same authorized action
		batchSM.setInternalAction(true);
		try {
			// calls node for job ened
			door.setJobEnded(JobId.VALUE);
			// if job locking is set, performs unlock
			if (isJobLockingScope()){
				locker.unlock();
			}
		} catch (RemoteException e) {
			throw new JemRuntimeException(e.getMessage(), e);
		} catch (MessageException e) {
			throw new JemRuntimeException(e.getMessage(), e);
		}
    }

	/* (non-Javadoc)
	 * @see org.kie.api.event.process.ProcessEventListener#beforeProcessStarted(org.kie.api.event.process.ProcessStartedEvent)
	 */
    @Override
    public void beforeProcessStarted(ProcessStartedEvent processEvent) {
    	LogAppl.getInstance().emit(JBpmMessage.JEMM024I, processEvent.getProcessInstance().getProcessName());
    	// save process event if a error occurs
    	this.processEvent = processEvent;  
    	
		if (door == null) {
			// get port number from env var
			// has passed and set this information (MUST)
			String port = System.getProperty(RmiKeys.JEM_RMI_PORT);
			try {
				// creates RMI connection with localhost (default) and using
				// port number.
				// if port is null or not a number, -1 is return and a RMI
				// Exception will occur
				RegistryLocator locator = new RegistryLocator(Parser.parseInt(port, -1));
				// check the taskdoor object is binded, if not, a exception
				// occurs
				if (locator.hasRmiObject(TasksDoor.NAME)) {
					// gets remote object
					door = (TasksDoor) locator.getRmiObject(TasksDoor.NAME);
					// send to JEM node the current process id.
					// uses JMX implementation of JDK.
					// BE CAREFUL! Not all JVM returns the value in same format
					// receives all roles for job user and stores in a static
					// reference
					// of realm
					JobStartedObjects objects = door.setJobStarted(JobId.VALUE, ManagementFactory.getRuntimeMXBean().getName());

					// PAY attention: after creating data paths container
					// calls a getabsolutepath method to load all necessary classes in classloader.
					// This is MANDATORY to avoid StackOverFlow in the SecurityManager 
					// during the CheckRead on files.
					DataPathsContainer.createInstance(objects.getStorageGroupsManager());
					DataPathsContainer.getInstance().getAbsoluteDataPath(JobId.VALUE);
					
					Collection<Role> myroles = objects.getRoles();
					// check if is already instantiated. If yes, does nothing
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new JBpmBatchSecurityManager(myroles));
					} else {
						throw new JemRuntimeException(JBpmMessage.JEMM039E.toMessage().getMessage());
					}
					
					
				} else {
					throw new JemRuntimeException(JBpmMessage.JEMM038E.toMessage().getFormattedMessage(TasksDoor.NAME));
				}
				// creates the locker to lock resources
				locker = new Locker();
			} catch (RemoteException e) {
				throw new JemRuntimeException(JBpmMessage.JEMM040E.toMessage().getFormattedMessage(e.getMessage()), e);
			} catch (UnknownHostException e) {
				throw new JemRuntimeException(JBpmMessage.JEMM040E.toMessage().getFormattedMessage(e.getMessage()), e);
			} catch (MessageException e) {
				throw new JemRuntimeException(e.getMessage(), e);
            }
		}
		loadNodeIds(processEvent.getProcessInstance().getProcess());
		
    	// sets locking scope
    	setLockingScope(processEvent.getProcessInstance().getProcess().getMetaData());
    	
		// sets locking scope
    	LogAppl.getInstance().emit(JBpmMessage.JEMM033I, lockingScope);
		
		// if Job scope is set, loads all items to lock resources
		if (isJobLockingScope()){
			loadForLock();
			try {
				locker.lock();
			} catch (MessageException e) {
				throw new JemRuntimeException(e);
			}
		}
    }
    
	/**
	 * @return <code>true</code> if locking scope is set to JOB
	 */
	private boolean isJobLockingScope(){
		return lockingScope.equalsIgnoreCase(JBpmKeys.JBPM_JOB_SCOPE);
	}

	/**
	 * @return <code>true</code> if locking scope is set to STEP
	 */
	private boolean isStepLockingScope(){
		return lockingScope.equalsIgnoreCase(JBpmKeys.JBPM_STEP_SCOPE);
	}
	
	/**
	 * @return <code>true</code> if locking scope is set to TASK
	 */
	private boolean isTaskLockingScope(){
		return lockingScope.equalsIgnoreCase(JBpmKeys.JBPM_TASK_SCOPE);
	}
	
	/**
	 * Reads the parameters and sets the locking scope
	 * @param process metadata
	 */
	private void setLockingScope(Map<String, Object> metaData){
		// if has got a gateway, force locking scope to JOB
		if (hasDivergingGateway){
			lockingScope = JBpmKeys.JBPM_JOB_SCOPE.toLowerCase();
			return;
		}
		
		// if locking is not present, uses default
		Object lockingScopeProperty = metaData.get(JBpmKeys.JBPM_LOCKING_SCOPE);
		if (lockingScopeProperty == null){
			return;
		}
		
		// checks the parameter has a correct value
		// if not, throws an exception
		if (!lockingScopeProperty.toString().equalsIgnoreCase(JBpmKeys.JBPM_JOB_SCOPE) &&
				!lockingScopeProperty.toString().equalsIgnoreCase(JBpmKeys.JBPM_STEP_SCOPE) &&
				!lockingScopeProperty.toString().equalsIgnoreCase(JBpmKeys.JBPM_TASK_SCOPE)){
			throw new JemRuntimeException(JBpmMessage.JEMM032E.toMessage().getFormattedMessage(JBpmKeys.JBPM_LOCKING_SCOPE, lockingScopeProperty));		
		}
		lockingScope = lockingScopeProperty.toString().toLowerCase();
	}
	
	/**
	 * Loads all tasks for locking scope of job
	 */
	private void loadForLock(){
		loadForLock(Task.NO_ID);
	}
	
	/**
	 * Loads the task by task id. if task id == -1, loads all tasks
	 * @param taskId node id to use for searching
	 */
	private void loadForLock(long taskId){
		Map<String, Task> tasks = CompleteTasksList.getInstance().getTasks();
		// if there isn't any JEM workitem, return
		if (tasks.isEmpty()){
			return;
		}
		// if -1, that means we have JOB lockingScope, so loads all tasks
		if (taskId == Task.NO_ID){
			TaskContainer.getInstance().getTasks().putAll(tasks);
		} else {
			// here there is a step or task lockingScope
			// loads TASk only of current task in execution, by NODE ID
			Task task = CompleteTasksList.getInstance().getTaskByNodeID(taskId);
			if (task != null){
				// stores the task in another singleton of current task in execution 
				TaskContainer.getInstance().getTasks().put(task.getId(), task);
			}
		}
	}
	
	/**
	 * Loads all NODE ids for all workitems
	 * @param process process to scan
	 */
	private void loadNodeIds(org.kie.api.definition.process.Process process){
		// only WORKFLOW PROCESS can be managed because it has got 
		// all nodes
		if (process instanceof  WorkflowProcess){
			WorkflowProcess wp = (WorkflowProcess)process;
			// scans all nodes
			for (Node node : wp.getNodes()){
				if (node instanceof NodeImpl){
					NodeImpl ni = (NodeImpl)node;
					// JBPM uses this label "UniqueId" to save the ID of XML
					// unfortunately there isn't any constant in JBPM to use
					Object objectId = ni.getMetaData().get("UniqueId");
					if (objectId != null){
						CompleteTasksList.getInstance().setNodeID(objectId.toString(), node.getId());
					}
				}
				// means there is a Gateway// for this reason force
				// lockingScope to JOB
				if (node instanceof Split){
					hasDivergingGateway = true;
				}
			}
		}
	}
}
