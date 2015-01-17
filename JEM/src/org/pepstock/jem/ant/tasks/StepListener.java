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
package org.pepstock.jem.ant.tasks;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.taskdefs.Java;
import org.pepstock.jem.Result;
import org.pepstock.jem.Step;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntKeys;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.rmi.JobStartedObjects;
import org.pepstock.jem.node.rmi.TasksDoor;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.rmi.RegistryLocator;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * Implements the interfaces of ANT to listen all starts and ends both job and
 * steps.<br>
 * Inside ANT, steps are "targets".<br>
 * Communicates with JEM node passing all necessary information about job
 * starting and steps ending.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StepListener implements BuildListener {
	
	// due to we have to order the targets and for each targets
	// you can have more than 1 tasks, sets 100 as maximum number of tasks
	private static final int MAX_TASKS_FOR_TARGET = 100;
	
	// this is the FORK attribute name of JAVA ANT task
	// JAVA ANT task doesn't publish any get method to get it
	// and then it uses reflection to get it
	private static final String ANT_JAVA_TASK_FORK_ATTRIBUTE_NAME = "fork";
	
	private int stepOrder = MAX_TASKS_FOR_TARGET;
	
	private TasksDoor door = null;
	
	private boolean isFirst = true;
	
	private String lockingScope = AntKeys.ANT_JOB_SCOPE;
	
	private Locker locker = null;

	/**
	 * Empty constructor
	 */
	public StepListener() {
		ReturnCodesContainer.getInstance();
	}

	/**
	 * Called by ANT engine when job is started.<br>
	 * Notifies to JEM the job is starting, passing process id and receiving the
	 * authorizations of job user instance.
	 * 
	 * @param event ANT event
	 */
	@Override
	public void buildStarted(BuildEvent event) {
		if (door == null) {
			// get port number from env var
			// AntTask has passed and set this information (MUST)
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
					
					// loads data paths as properties
					int index = 0;
					for (String dataName : DataPathsContainer.getInstance().getDataPathsNames()){
						String path = DataPathsContainer.getInstance().getDataPaths().get(index); 
						String property = ConfigKeys.JEM_DATA_PATH_NAME+"."+dataName;
						System.setProperty(property, path);
						index++;
					}
					
					Collection<Role> myroles = objects.getRoles();
					// check if is already instantiated. If yes, does nothing
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new AntBatchSecurityManager(myroles));
					} else {
						throw new BuildException(AntMessage.JEMA039E.toMessage().getMessage());
					}
				} else {
					throw new BuildException(AntMessage.JEMA038E.toMessage().getFormattedMessage(TasksDoor.NAME));
				}
				// sets the SM for internal actions
				AntBatchSecurityManager batchSM = (AntBatchSecurityManager)System.getSecurityManager();
				// sets internal action to true so it can perform same authorized action
				batchSM.setInternalAction(true);
				// creates the locker to lock resources
				locker = new Locker();
				// sets internal action to false 
				batchSM.setInternalAction(false);

			} catch (AntException e) {
				throw new BuildException(AntMessage.JEMA040E.toMessage().getFormattedMessage(e.getMessage()), e);
			} catch (RemoteException e) {
				throw new BuildException(AntMessage.JEMA040E.toMessage().getFormattedMessage(e.getMessage()), e);
			} catch (UnknownHostException e) {
				throw new BuildException(AntMessage.JEMA040E.toMessage().getFormattedMessage(e.getMessage()), e);
			}
		}
		
	}

	/**
	 * Called by ANT engine when job is ended.<br>
	 * Notifies to JEM the job is ending, cleaning the subject loaded.
	 * 
	 * @param event ANT event
	 */
	@Override
	public void buildFinished(BuildEvent event) {
		// sets the SM for internal actions
		AntBatchSecurityManager batchSM = (AntBatchSecurityManager)System.getSecurityManager();
		// sets internal action to true so it can perform same authorized action
		batchSM.setInternalAction(true);
		// flush STD OUT and ERR
		// probably is useless
		System.out.flush();
		System.err.flush();
		try {
			// calls node for job ened
			door.setJobEnded(JobId.VALUE);
			
			// if job locking is set, performs unlock
			if (isJobLockingScope()){
				locker.unlock();
			}
		} catch (RemoteException e) {
			throw new BuildException(e.getMessage(), e);
		} catch (AntException e) {
			throw new BuildException(e.getMessage(), e);
		} finally {
			// sets onternal action to false
			batchSM.setInternalAction(false);
		}
	}

	/**
	 * Called by ANT engine when a step is started.<br>
	 * Notifies to JEM the current step in execution to save inside of JEM job
	 * instance.
	 * 
	 * @param event ANT event
	 */
	@Override
	public void targetStarted(BuildEvent event) {
		// checks if is the first step
		// if yes, does many activities:
		// - loading all targets and task
		// - prepares for resources locking
		if (isFirst){
			// sets locking scope
			setLockingScope(event.getProject());
			event.getProject().log(AntMessage.JEMA033I.toMessage().getFormattedMessage(lockingScope));
			
			// if Job scope is set, loads all items to lock resources
			if (isJobLockingScope()){
				loadForLock(event.getProject());
				// check procedure 
				checkProcedure();
				try {
					locker.lock();
				} catch (AntException e) {
					throw new BuildException(e);
				}
			}
			isFirst = false;
		}
		
		// if step locking is set, then locks resources
		// loading tasks info
		if (isStepLockingScope()){
			loadForLock(event.getTarget());
			checkProcedure();
			try {
				locker.lock();
			} catch (AntException e) {
				throw new BuildException(e);
			}
		}
		
		AntBatchSecurityManager batchSM = (AntBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);

		// checks if the target name is empty. if yes, does nothing
		if (!"".equals(event.getTarget().getName())) {
			try {
				// creates object to send to JEM
				Step step = new Step();
				// sets step name and description
				step.setName(event.getTarget().getName());
				step.setDescription(event.getTarget().getDescription());

				// send to JEM by RMI
				door.setStepStarted(JobId.VALUE, step);
			} catch (RemoteException e) {
				throw new BuildException(e.getMessage(), e);
			} finally{
				batchSM.setInternalAction(false);
			}
		}
		batchSM.setInternalAction(false);

	}

	/**
	 * Called by ANT engine when a step is ended.<br>
	 * Notifies to JEM a summary about step execution (i.e. return-code,
	 * exception).
	 * 
	 * @param event ANT event
	 */
	@Override
	public void targetFinished(BuildEvent event) {
		AntBatchSecurityManager batchSM = (AntBatchSecurityManager)System.getSecurityManager();
		batchSM.setInternalAction(true);
		
		// checks if the target name is empty. if yes, does nothing
		if (!"".equals(event.getTarget().getName())) {
			try {
				// creates object to send to JEM
				Step step = new Step();
				// sets step name and description
				step.setName(event.getTarget().getName());
				step.setDescription(event.getTarget().getDescription());

				// checks if has an exception.If yes, sets ERROR, otherwise
				// SUCCESS
				step.setReturnCode((event.getException() != null) ? Result.ERROR : Result.SUCCESS);
				// checks if has an exception, sets the exception message
				if (event.getException() != null){
					step.setException(event.getException().getMessage());
				}

				// send to JEM by RMI
				door.setStepEnded(JobId.VALUE, step);
				
				// if setp locking scope is st, unlocks resources
				if (isStepLockingScope()){
					locker.unlock();
				}

			} catch (AntException e) {
				throw new BuildException(e);
			} catch (RemoteException e) {
				throw new BuildException(e);
			} finally {
				batchSM.setInternalAction(false);
			}
		}
		batchSM.setInternalAction(false);
	}

	/**
	 * If a task locking scope is set, load resources definition and asks for locking
	 * 
	 * @param event ANT event
	 */
	@Override
	public void taskStarted(BuildEvent event) {
		// checks if you are using a JAVA ANT task with FORK
		// this option is not allowed because with the fork
		// the application is out of secured environment,
		// that means without security manager

		// checks if is cast-able
		if (event.getTask() != null){
			Task task = null;
			// checks if is an Unknown element
			if (event.getTask() instanceof UnknownElement){
				// gets ANT task
				UnknownElement ue = (UnknownElement)event.getTask();
				ue.maybeConfigure();
				task = (Task)ue.getTask();
			} else if (event.getTask() instanceof Task){
				// gets the task
				// here if the ANT task is already configured
				// mainly on sequential, parallel and JEM procedure
				task = (Task)event.getTask();
			}
			// if is a ANT JAVA TASK
			if (task instanceof Java && !(task instanceof StepJava)){
				// gets AJAV task
				Java java = (Java)task;
				boolean isFork = true;
				 try {
					 // reflection to understand if the attribute fork is set to true
					 // unfortunately ANT java task don't have any get method to have fork value
					Field f = java.getClass().getDeclaredField(ANT_JAVA_TASK_FORK_ATTRIBUTE_NAME);
					 isFork = (Boolean)FieldUtils.readField(f, java, true);
				} catch (SecurityException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				} catch (NoSuchFieldException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);				
				}
				// and force FORK to false
				java.setFork(false);
				if (isFork){
					// shows the message of the force of fork.
					event.getProject().log(AntMessage.JEMA077W.toMessage().getFormattedMessage(event.getTask().getTaskName()));
				}
			}

		}
		
		// if task locking scope is set, locks resources
		if (isTaskLockingScope()){
			loadForLock(event.getTask());
			checkProcedure();
			try {
				locker.lock();
			} catch (AntException e) {
				throw new BuildException(e);
			}
		}

	}

	/**
	 * If a task locking scope is set, asks for unlocking
	 * 
	 * @param event ANT event
	 */
	@Override
	public void taskFinished(BuildEvent event) {
		// if task locking scope is set, unlocks resources
		if (isTaskLockingScope()){
			try {
				locker.unlock();
			} catch (AntException e) {
				throw new BuildException(e);
			}
		}
	}

	/**
	 * Not implemented.
	 * 
	 * @param event ANT event
	 */
	@Override
	public void messageLogged(BuildEvent event) {
		// nop
	}
	
	/**
	 * @return <code>true</code> if locking scope is set to JOB
	 */
	private boolean isJobLockingScope(){
		return lockingScope.equalsIgnoreCase(AntKeys.ANT_JOB_SCOPE);
	}

	/**
	 * @return <code>true</code> if locking scope is set to STEP
	 */
	private boolean isStepLockingScope(){
		return lockingScope.equalsIgnoreCase(AntKeys.ANT_STEP_SCOPE);
	}
	
	/**
	 * @return <code>true</code> if locking scope is set to TASK
	 */
	private boolean isTaskLockingScope(){
		return lockingScope.equalsIgnoreCase(AntKeys.ANT_TASK_SCOPE);
	}
	
	/**
	 * Reads the parameters and sets the locking scope
	 * @param project JOB
	 */
	private void setLockingScope(Project project){
		// if locking is not present, uses default
		String lockingScopeProperty = project.getProperty(AntKeys.ANT_LOCKING_SCOPE);
		if (lockingScopeProperty == null){
			return;
		}
		
		// checks the parameter has a correct value
		// if not, throws an exception
		if (!lockingScopeProperty.equalsIgnoreCase(AntKeys.ANT_JOB_SCOPE) &&
				!lockingScopeProperty.equalsIgnoreCase(AntKeys.ANT_STEP_SCOPE) &&
				!lockingScopeProperty.equalsIgnoreCase(AntKeys.ANT_TASK_SCOPE)){
			throw new BuildException(AntMessage.JEMA032E.toMessage().getFormattedMessage(AntKeys.ANT_LOCKING_SCOPE, lockingScopeProperty));		
		}
		lockingScope = lockingScopeProperty.toLowerCase();
	}
	
	/**
	 * Reads all project loading all targets and tasks
	 * @param project JOB
	 */
	@SuppressWarnings("rawtypes")
	private void loadForLock(Project project){ 
		Map mm = project.getCopyOfReferences();
		// property of ProjectHelper2.REFID_CONTEXT but is not visible
		AntXMLContext context = (AntXMLContext) mm.get("ant.parsing.context");
		
		for (Object obj : context.getTargets()){
			Target target = (Target) obj;
			loadForLock(target);
			// every target starts from a new hundreds
			stepOrder = stepOrder + MAX_TASKS_FOR_TARGET;
		}
	}
	
	/**
	 * Loads all tasks for a specific target
	 * @param target STEP
	 */
	private void loadForLock(Target target){ 
		Task[] tasks = target.getTasks();
		if ((tasks != null) && (tasks.length > 0)){
			for (int i=0; i<tasks.length; i++){
				// load tasks info setting the order 
				loadForLock(tasks[i], stepOrder + i);
			}
		}
	}
	
	/**
	 * Called only when the locking scope is TASk, so order is always 0 (no sort needed)
	 * @param task TASK
	 */
	private void loadForLock(Task task){
		loadForLock(task, 0);
	}
	
	/**
	 * Called to loads all defined tasks specific target, setting the order
	 * @param task TASK
	 * @param order order of step/task
	 */
	private void loadForLock(Task task, int order){
		if (task instanceof UnknownElement){
			UnknownElement uePre = (UnknownElement)task;
			UnknownElement ue;
			try {
				// must object to be cloned
				// otherwise some actions (like ANT variables substitutions)
				// are not applied on JCL
				ue = (UnknownElement)uePre.clone();
				ue.maybeConfigure();
			
				if (ue.getTask() instanceof DataDescriptionStep){
					DataDescriptionStep item = (DataDescriptionStep)ue.getTask();
					item.setOrder(order);
					StepsContainer.getInstance().getDataDescriptionSteps().add(item);
				} else if (ue.getTask() instanceof Procedure){
					Procedure proc = (Procedure) ue.getTask();
					proc.setOrder(order);
					ProceduresContainer.getInstance().getProcedures().add(proc);
				} else if (ue.getRealThing() instanceof ProcedureDefinition) {
					ProcedureDefinition def = (ProcedureDefinition) ue.getRealThing();
					ProceduresContainer.getInstance().getProceduresDefinitions().put(def.getName(), def);
				} 
			} catch (CloneNotSupportedException e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
			}

		}
	}
	
	/**
	 * loads all information for the procedures
	 */
	private void checkProcedure(){
		if (ProceduresContainer.getInstance().getProcedures().isEmpty()){
			return;
		}

		// scans defined procedures
		for (Procedure proc : ProceduresContainer.getInstance().getProcedures()){
			if (proc.getName() != null){
				// gets procedure
				ProcedureDefinition pd = ProceduresContainer.getInstance().getProceduresDefinitions().get(proc.getName());
				// saves ProcDef
				proc.setDefinition(pd);
				// loads tasks overriding the nested
				proc.loadDefinedTask();
				proc.ovverrideDefinedTask();
				
				// adds all dat description step for lockings
				for (Task task : proc.getDefinedTasks()){
					if (task instanceof DataDescriptionStep){
						DataDescriptionStep item = (DataDescriptionStep)task;
						item.setOrder(proc.getOrder());
						StepsContainer.getInstance().getDataDescriptionSteps().add(item);
					}
				}
			}
		}
	}
	

}