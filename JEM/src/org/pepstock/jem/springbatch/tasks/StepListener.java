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
package org.pepstock.jem.springbatch.tasks;

import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;

import org.pepstock.jem.Result;
import org.pepstock.jem.Step;
import org.pepstock.jem.node.rmi.TasksDoor;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.SpringBatchRuntimeException;
import org.pepstock.jem.springbatch.tasks.managers.DefinitionsLoader;
import org.pepstock.jem.springbatch.tasks.managers.Locker;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.rmi.RegistryLocator;
import org.pepstock.jem.util.rmi.RmiKeys;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Implements the interfaces of SpringBatch to listen all starts and ends both
 * job and steps.<br>
 * Communicates with JEM node passing all necessary information about job
 * starting and steps ending.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class StepListener implements StepExecutionListener, JobExecutionListener, ApplicationContextAware {

	private TasksDoor door = null;
	
	private Locker locker = null;
	
	private boolean isFirst = true;

	/**
	 * Empty construct
	 */
	public StepListener() {
	}

	/**
	 * Called by SpringBatch engine when the job is ended.<br>
	 * Cleans SHIRO subject and notifies JEM node for ending.
	 * 
	 * @see org.springframework.batch.core.JobExecutionListener#afterJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void afterJob(JobExecution arg0) {
		System.out.flush();
		System.err.flush();
		if (DefinitionsLoader.getInstance().isJobLockingScope()){
			try {
				locker.unlock();
			} catch (SpringBatchException e) {
				throw new SpringBatchRuntimeException(e.getMessageInterface(), e, e.getObjects().toArray());
			}
		}
		try {
			door.setJobEnded(JobId.VALUE);
		} catch (RemoteException e) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS042E, e);
		}
	}

	/**
	 * Called by SpringBatch engine when the job is started.<br>
	 * Gets RMI port from environment variables and open RMI connection with JEM
	 * node to send it current job process id.<br>
	 * Receives all roles for job user and starts SHIRO for security authorizations.
	 * 
	 * @see org.springframework.batch.core.JobExecutionListener#beforeJob(org.springframework.batch.core.JobExecution)
	 * @param jobExecution execution context
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {
		if (locker == null){
			try {
				locker = new Locker();
			} catch (SpringBatchException e) {
				throw new SpringBatchRuntimeException(e.getMessageInterface(), e, e.getObjects().toArray());
			}
		}
		
		// check if is already instatiated. If yes, does nothing
		if (door == null) {
			// get port number from env var
			// SpringBatchTask has passed and set this information (MUST)
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
					Collection<Role> myroles = door.setJobStarted(JobId.VALUE, ManagementFactory.getRuntimeMXBean().getName());
					// check if is already instantiated. If yes, does nothing
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new SpringBatchSecurityManager(myroles));
					} else {
						throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS027E);
					}
				} else {
					throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS026E, TasksDoor.NAME);
				}
			} catch (RemoteException e) {
				throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS028E, e, TasksDoor.NAME, jobExecution.getJobInstance().getJobName());
			} catch (UnknownHostException e) {
				throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS028E, e, TasksDoor.NAME, jobExecution.getJobInstance().getJobName());
			}
		}
	}

	/**
	 * Called by SpringBatch engine when a step is ended.<br>
	 * Notifies to JEM a summary about step execution (i.e. return-code,
	 * exception).
	 * 
	 * @see org.pepstock.jem.Step
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 * @param execution context
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (DefinitionsLoader.getInstance().isStepLockingScope()){
			try {
				locker.unlock();
			} catch (SpringBatchException e) {
				throw new SpringBatchRuntimeException(e.getMessageInterface(), e, e.getObjects().toArray());
			}
		}
		// creates object to send to JEM
		Step step = new Step();
		// sets step name and description (uses summary information)
		step.setName(stepExecution.getStepName());
		step.setDescritpion(stepExecution.getSummary());

		// gets Spring Batch Exit status
		ExitStatus eStatus = stepExecution.getExitStatus();
		// if completed is SUCCESS for JEM
		if (eStatus.equals(ExitStatus.COMPLETED)) {
			step.setReturnCode(Result.SUCCESS);
		} else {
			// otherwise is a ERROR, and sets the exceptions text
			step.setReturnCode(Result.ERROR);
			step.setException(stepExecution.getFailureExceptions().toString());
		}
		// send to JEM by RMI
		try {
			door.setStepEnded(JobId.VALUE, step);
		} catch (RemoteException e) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS042E, e);
		}
		return eStatus;
	}

	/**
	 * Called by SpringBatch engine when a step is started.<br>
	 * Notifies to JEM the current step in execution to save inside of JEM job
	 * instance.
	 * 
	 * @see org.pepstock.jem.Step
	 * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
	 * @param execution context
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		if (isFirst){
			if (DefinitionsLoader.getInstance().isJobLockingScope()){
				DefinitionsLoader.getInstance().loadForLock();
				try {
					locker.lock();
				} catch (SpringBatchException e) {
					throw new SpringBatchRuntimeException(e.getMessageInterface(), e, e.getObjects().toArray());
				}
			}
			isFirst = false;
		}
		if (DefinitionsLoader.getInstance().isStepLockingScope()){
			DefinitionsLoader.getInstance().loadForLock(stepExecution.getStepName());
			try {
				locker.lock();
			} catch (SpringBatchException e) {
				throw new SpringBatchRuntimeException(e.getMessageInterface(), e, e.getObjects().toArray());
			}
		}
		// creates object to send to JEM
		Step step = new Step();

		// sets step name and description (uses summary information)
		step.setName(stepExecution.getStepName());
		step.setDescritpion(stepExecution.getSummary());

		// send to JEM by RMI
		try {
			door.setStepStarted(JobId.VALUE, step);
		} catch (RemoteException e) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS042E, e);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		DefinitionsLoader.getInstance().setContext((ConfigurableApplicationContext)context);
	}
}