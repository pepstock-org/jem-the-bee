/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.taskdefs.Sequential;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.ant.tasks.managers.ProceduresContainer;
import org.pepstock.jem.ant.tasks.managers.StepsContainer;

/**
 * Procedure is a reference to a Procedure definition, with the possibility to
 * override data description definition.<br>
 * The binding with the definition is done by name.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Procedure extends Task implements TaskContainer {

	private String name = null;

	private int order = 0;

	private ProcedureDefinition definition = null;

	// Array list holding the defined tasks
	private List<Task> definedTasks = new LinkedList<Task>();

	// Array list holding the nested tasks
	private List<Task> nestedTasks = new ArrayList<Task>();

	/**
	 * Empty constructor
	 */
	public Procedure() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the order necessary to sort all steps before to ask for data set
	 * containers
	 * 
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @return the definition
	 */
	public ProcedureDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(ProcedureDefinition definition) {
		this.definition = definition;
	}

	/**
	 * Load all defined tasks, cloning them from definition to this object
	 */
	public void loadDefinedTask() {
		// if defintion is null, does nothing
		if (definition == null) {
			return;
		}

		// configure ant task
		UnknownElement nt = definition.getNestedTask();
		nt.maybeConfigure();

		// there are 2 ways to define procedure:
		// - with a sequential task, when you have to define more tasks
		// - witha single task
		if (nt.getTask() instanceof Sequential) {
			
			for (Object child : nt.getChildren()) {
				if (child instanceof UnknownElement) {
					// loads all children
					UnknownElement childElement = (UnknownElement) child;
					childElement.maybeConfigure();
					try {
						// adds the defined task, cloning it
						// if it can't be cloned, exception
						Task task = (Task) childElement.getTask().clone();
						
						addDefinedTask(task);
					} catch (CloneNotSupportedException e) {
						throw new BuildException(e);
					}
				}
			}
		} else {
			try {
				// adds the defined task, cloning it
				// if it can't be cloned, exception
				Task task = (Task) nt.getTask().clone();
				addDefinedTask(task);
			} catch (CloneNotSupportedException e) {
				throw new BuildException(e);
			}
		}

		// load all the unknown element of overriding tasks
		for (Object object : nestedTasks) {
			if (object instanceof UnknownElement) {
				UnknownElement nested = (UnknownElement) object;
				nested.maybeConfigure();
			}
		}
	}

	/**
	 * Performs the overriding of data description
	 */
	public void ovverrideDefinedTask() {
		for (Object object : nestedTasks) {
			UnknownElement nested = (UnknownElement) object;
			Task nestedTask = nested.getTask();
			if (!checkAndLoadNestedtask(nestedTask)) {
				throw new BuildException(AntMessage.JEMA024E.toMessage().getFormattedMessage(nestedTask.getTaskName(), getName()));
			}

		}
	}

	/**
	 * Checks if trying to overrind an unexisting defined taks and load the new
	 * data description
	 * 
	 * @param nestedTask
	 * @return
	 */
	private boolean checkAndLoadNestedtask(Task nestedTask) {
		// scans all defined tasks
		for (Task task : definedTasks) {
			// checks if they have same name
			// ovverides only if they are Data Descritpion STEP
			if (task.getTaskName().equalsIgnoreCase(nestedTask.getTaskName()) && task instanceof DataDescriptionStep && nestedTask instanceof DataDescriptionStep) {
				DataDescriptionStep taskStep = (DataDescriptionStep) task;
				DataDescriptionStep nestedTaskStep = (DataDescriptionStep) nestedTask;

				// chekcs if they have the same name
				if (taskStep.getName().equalsIgnoreCase(nestedTaskStep.getName())) {
					// scans data descritpion
					for (DataDescription nestedTaskDD : nestedTaskStep.getDataDescriptions()) {
						for (Iterator<DataDescription> iterTask = taskStep.getDataDescriptions().iterator(); iterTask.hasNext();) {
							DataDescription dd = iterTask.next();
							if (dd.getName().equalsIgnoreCase(nestedTaskDD.getName())) {
								// removes if there is override
								iterTask.remove();
							}
						}
					}
					// adds all new datadescriptions
					taskStep.getDataDescriptions().addAll(nestedTaskStep.getDataDescriptions());
					// adds all locks
					taskStep.getLocks().addAll(nestedTaskStep.getLocks());
					// set ID of nested task
					taskStep.setId(nestedTaskStep.getId());
					// missing and  to do datasource!!
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Add a nested task.
	 * <p>
	 * 
	 * @param nestedTask Nested task to execute
	 *            <p>
	 */
	private void addDefinedTask(Task definedTask) {
		definedTasks.add(definedTask);
		// sets new target
		definedTask.setOwningTarget(getOwningTarget());
	}

	/**
	 * @return the definedTasks
	 */
	public List<Task> getDefinedTasks() {
		return definedTasks;
	}

	/**
	 * @return the nestedTasks
	 */
	public List<Task> getNestedTasks() {
		return nestedTasks;
	}

	/**
	 * Add a nested task.
	 * <p>
	 * 
	 * @param nestedTask Nested task to execute
	 *            <p>
	 */
	public void addTask(Task nestedTask) {
		nestedTasks.add(nestedTask);
	}

	/**
	 * Executes the defined tasks, updated if there is any override task
	 */
	@Override
	public void execute() throws BuildException {
		checkProcedure();

		LocalProperties localProperties = LocalProperties.get(getProject());
		localProperties.enterScope();
		try {
			for (Task definedTask : definedTasks) {
				definedTask.perform();
			}
		} finally {
			localProperties.exitScope();
		}
	}

	private void checkProcedure(){
		if (ProceduresContainer.getInstance().getProcedures().isEmpty()){
			return;
		}

		// scans defined procedures
		if (getName() != null){
				// gets procedure
				ProcedureDefinition pd = ProceduresContainer.getInstance().getProceduresDefinitions().get(getName());
				// saves ProcDef
				setDefinition(pd);
				// loads tasks overriding the nested
				loadDefinedTask();
				ovverrideDefinedTask();
				
				// adds all dat description step for lockings
				for (Task task : getDefinedTasks()){
					if (task instanceof DataDescriptionStep){
						DataDescriptionStep item = (DataDescriptionStep)task;
						item.setOrder(getOrder());
						StepsContainer.getInstance().getDataDescriptionSteps().add(item);
					}
				}
		}
	}

}