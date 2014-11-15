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
package org.pepstock.jem.ant.tasks;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.property.LocalProperties;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.log.LogAppl;

/**
 * Is a special <code>Sequential</code> ANT task implementation, with only 1
 * task, where is possible to declare all files and resources job needs to be
 * executed.<br>
 * All the files references are passed by properties to command which is able to
 * have the file name just accessing to properties and without coding them
 * inside the code.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class WrapperTask extends Task implements TaskContainer, DataDescriptionStep {

	private String id = DataDescriptionStep.DEFAULT_ID;

	private String name = null;

	private int order = 0;

	private final List<DataDescription> dataDescriptions = new ArrayList<DataDescription>();

	private final List<Lock> locks = new ArrayList<Lock>();

	private Task nestedTask = null;

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return (name == null) ? getTaskName() : name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.ant.DataDescriptionItem#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return getOwningTarget().getName();
	}

	/**
	 * Called by ANT engine to add data description object defined inside the
	 * task element.
	 * 
	 * @see DataDescription
	 * @param dd data description object
	 */
	public void addDataDescription(DataDescription dd) {
		for (DataSet dataset : dd.getDatasets()) {
			if (dataset.isInline() && dataset.isReplaceProperties()) {
				String changed = getProject().replaceProperties(dataset.getText().toString());
				dataset.setTextBuffer(new StringBuilder(changed));
			}
		}
		dataDescriptions.add(dd);
	}

	/**
	 * @return the dataDescriptions
	 */
	@Override
	public List<DataDescription> getDataDescriptions() {
		return dataDescriptions;
	}

	/**
	 * Called by ANT engine to add lock object defined inside the task element.
	 * 
	 * @param lock
	 */
	public void addLock(Lock lock) {
		locks.add(lock);
	}

	/**
	 * Returns the list of locks
	 * 
	 * @return the list of locks
	 */
	@Override
	public List<Lock> getLocks() {
		return locks;
	}

	/**
	 * Add the task to execute
	 * 
	 * @param task task to execute
	 */
	public void addTask(Task task) {
		if (nestedTask != null) {
			throw new BuildException(AntMessage.JEMA052E.toMessage().getFormattedMessage());
		}
		this.nestedTask = task;
	}

	/**
	 * Prepares the files required by ANT file using the data description, locks
	 * them, and prepares the right file name for GDG. Afterwards calls the
	 * defined task.
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		// this boolean is necessary to understand if I have an exception
		// before calling the main class
		boolean isExecutionStarted = false;

		AntBatchSecurityManager batchSM = (AntBatchSecurityManager) System.getSecurityManager();
		batchSM.setInternalAction(true);
		// creates a list with all data description impl
		List<DataDescriptionImpl> ddList = null;

		PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(getProject());

		// creates a thread local properties
		LocalProperties localProperties = LocalProperties.get(getProject());

		try {
			// gets all data description requested by this task
			ddList = ImplementationsContainer.getInstance().getDataDescriptionsByItem(this);

			// if list of data description is empty, go to execute the command
			if (!ddList.isEmpty()) {
				// sets scope per properties and calls task
				// MUST BE CALLED BEFORE ADDLOCAL
				localProperties.enterScope();

				// this cycle must load variables to local properties
				for (DataDescriptionImpl ddImpl : ddList) {
					// MUST BE CALLED BEFORE setProperty of all proprties
					localProperties.addLocal(StepExec.DD_PREFIX + ddImpl.getName());
				}

				// after locking, checks for GDG
				// is sure here the root (is a properties file) of GDG is locked
				// (doesn't matter if in READ or WRITE)
				// so can read a consistent data from root and gets the right
				// generation
				// starting from relative position
				for (DataDescriptionImpl ddImpl : ddList) {
					// loads GDG generation!! it meeans the real file name of
					// generation
					GDGManager.load(ddImpl);
					log(AntMessage.JEMA034I.toMessage().getFormattedMessage(ddImpl));
					// scans all datasets of datadescription adding new
					// environment variable
					for (DataSetImpl dataset : ddImpl.getDatasets()) {
						propertyHelper.setProperty(StepExec.DD_PREFIX + ddImpl.getName(), dataset.getRealFile().getAbsolutePath(), true);
					}
				}

			}
			// calls super-method to execute the command configured into JCL
			batchSM.setInternalAction(false);
			// executes the program defined in JCL
			// setting the boolean to TRUE
			isExecutionStarted = true;
			nestedTask.perform();
		} catch (BuildException e1) {
			throw e1;
		} catch (RemoteException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);
		} finally {
			batchSM.setInternalAction(true);
			// finally and always must release the locks previously asked
			// checks datasets list
			if (ddList != null && !ddList.isEmpty()) {
				// clean scope of properties
				localProperties.exitScope();

				StringBuilder exceptions = new StringBuilder();
				// scans data descriptions
				for (DataDescriptionImpl ddImpl : ddList) {
					try {
						// consolidates the GDG situation
						// changing the root (is a properties file)
						// only if execution started
						if (isExecutionStarted) {
							GDGManager.store(ddImpl);
						}
					} catch (IOException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						log(AntMessage.JEMA036E.toMessage().getFormattedMessage(e.getMessage()));
						if (exceptions.length() == 0) {
							exceptions.append(AntMessage.JEMA036E.toMessage().getFormattedMessage(e.getMessage()));
						} else {
							exceptions.append(AntMessage.JEMA036E.toMessage().getFormattedMessage(e.getMessage())).append("\n");
						}
					}
				}
				if (exceptions.length() > 0) {
					log(StringUtils.center("ATTENTION", 40, "-"));
					log(exceptions.toString());
				}
			}
		}
	}
}
