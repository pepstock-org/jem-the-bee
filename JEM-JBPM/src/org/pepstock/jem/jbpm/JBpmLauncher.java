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
package org.pepstock.jem.jbpm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.pepstock.jem.Result;
import org.pepstock.jem.jbpm.tasks.CompleteTasksList;
import org.pepstock.jem.jbpm.tasks.Factory;
import org.pepstock.jem.jbpm.tasks.JemWorkItemHandler;
import org.pepstock.jem.jbpm.tasks.StepListener;
import org.pepstock.jem.jbpm.xml.TaskDescription;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.xml.sax.SAXException;

/**
 * This is the main program to launch a JPBM process as JOB inside the JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JBpmLauncher {

	/**
	 * To avoid any instantiation
	 */
    private JBpmLauncher() {
    }

	/**
	 * Main method which receives in input 2 arguments:<br>
	 * <ul>
	 * <li> ProcessID: value of ID attribute of process element, necessary to load the process inside of JBPM 
	 * <li> JCL file: the absolute path of JCL file 
	 * </ul>
	 * 
	 * @param args processID and JCL file
	 */
	public static void main(String[] args) {
		// initialize LOG
		LogAppl.getInstance();
		// checks if there is the right amount of arguments
		if (args != null && args.length == 2){
			
			// args[0] is process ID
			// args[1] is jcl file
			String processID = args[0];
			String jclFile = args[1];
			
			// scans the JCL to get all task descriptors for the JEM custom task
			try {
				Map<String, Task> tasks = new HashMap<String, Task>();
				// scans all tak description to create a map of tasks
	            for (TaskDescription taskDescription : XmlParser.getTaskDescription(jclFile)){
	            	Task task = Factory.createTask(taskDescription);
	            	tasks.put(task.getId(), task);
	            }
	            // loads all task in a singleton, to use in the step listener
	            CompleteTasksList.getInstance(tasks);
            } catch (ParserConfigurationException e) {
	            throw new JemRuntimeException(e);
            } catch (SAXException e) {
            	throw new JemRuntimeException(e);
            } catch (IOException e) {
            	throw new JemRuntimeException(e);
            } catch (JBpmException e) {
            	throw new JemRuntimeException(e);
            }
			
			// creates the step listener
			StepListener listener = new StepListener();
			
			// creates the JBPM environment and load JCL file
			SimpleRegisterableItemsFactory factory = new SimpleRegisterableItemsFactory();
			SimpleRuntimeEnvironment environment = new SimpleRuntimeEnvironment(factory);
			environment.setUsePersistence(false);
			Resource res = ResourceFactory.newFileResource(new File(jclFile));
			environment.addAsset(res, ResourceType.BPMN2);

			// gets runtime environment
			RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
		    RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
			KieSession ksession = runtime.getKieSession();
			// adds always the custom JEM workitem
			ksession.getWorkItemManager().registerWorkItemHandler(JBpmKeys.JBPM_JEM_WORKITEM_NAME, new JemWorkItemHandler());
			
			// adds listener
			ksession.addEventListener(listener);
			try { 
				// starts process
				ksession.startProcess(processID);
			
				// check if are tasks are still pending
				// if yes, there has been an errors
				if (!listener.getNodeEvents().isEmpty()){
					// sets Excpetion text
					String exception = null;
					// scans all pending tasks and call end of them method
					for (ProcessNodeEvent event : listener.getNodeEvents().values()){
						exception = JBpmMessage.JEMM048E.toMessage().getFormattedMessage(event.getNodeInstance().getNodeName());
						listener.beforeNodeLeft(event, Result.ERROR, JBpmMessage.JEMM048E.toMessage().getFormattedMessage(event.getNodeInstance().getNodeName()));	
					}
					// calls the end of job method
					listener.afterProcessCompletedFinally(listener.getProcessEvent());
					// exits with error
					// throwing an exception
					throw new JemRuntimeException(exception);
				}
			} catch( WorkflowRuntimeException wfre ) {
				// if we are here, means that a RUNTIME exception has been thrown by a work item
				LogAppl.getInstance().emit(JBpmMessage.JEMM029E, wfre, wfre.getCause().getClass().getName(),wfre.getProcessInstanceId(),
						wfre.getProcessId(), wfre.getNodeId(), wfre.getNodeName());
				String msg = JBpmMessage.JEMM029E.toMessage().getFormattedMessage(wfre.getCause().getClass().getName(),wfre.getProcessInstanceId(),
						wfre.getProcessId(), wfre.getNodeId(), wfre.getNodeName());

				// check if are tasks are still pending
				if (!listener.getNodeEvents().isEmpty()){
					// scans all pending tasks and call end of them method
					for (ProcessNodeEvent event : listener.getNodeEvents().values()){
						if (event.getNodeInstance().getNodeId() == wfre.getNodeId()){
							listener.beforeNodeLeft(event, Result.ERROR, msg);
						} else {
							listener.beforeNodeLeft(event, Result.ERROR, JBpmMessage.JEMM048E.toMessage().getFormattedMessage(event.getNodeInstance().getNodeName()));
						}
					}
				}
				// calls the end of job method
				listener.afterProcessCompletedFinally(listener.getProcessEvent());
				// exits with error
				throw new JemRuntimeException(msg);
			} finally {
				// cleans up of JBPM environment 
				environment.getKieBase().removeProcess(processID);
				manager.disposeRuntimeEngine(runtime);
			}
		} else {
			throw new IllegalArgumentException(JBpmMessage.JEMM049E.toMessage().getFormattedMessage());
		}
	}

}
