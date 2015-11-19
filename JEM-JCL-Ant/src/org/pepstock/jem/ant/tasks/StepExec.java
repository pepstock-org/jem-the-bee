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

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Environment;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.Result;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.util.Parser;

/**
 * Is <code>Exec</code> ANT task implementation, where is possible to declare
 * all files and resources job needs to be executed.<br>
 * All the files references are passed by environment variables to command which
 * is able to have the file name just accessing to environment variables and
 * without coding them inside the code.<br>
 * <b>The idea is to have the same business logic and then the same code for
 * different customers and then using different resources</b>.<br>
 * JCL has this goal and ANT and this implementation as well.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class StepExec extends ExecTask implements DataDescriptionStep {

	/**
	 * Prefix of environment variables set to pass the file information to exec
	 * command.<br>
	 * The env variables are built with this prefix and the name of data
	 * description.<br>
	 * Example:<br>
	 * <code>datascription name="filein"</code> is exported in a environment
	 * variable named <code>DD_filein</code>, and the value of this env variable
	 * can be used everywhere inside the executed command.
	 */
	public static final String DD_PREFIX = "DD_";

	private String id = DataDescriptionStep.DEFAULT_ID;

	private String name = null;

	private int order = 0;
	
	private String resultProperty = null;

	private final List<DataDescription> dataDescriptions = new ArrayList<DataDescription>();

	private final List<Lock> locks = new ArrayList<Lock>();

	/**
	 * Calls super constructor and set fail-on-error to <code>true</code> and
	 * fail-if-execution-fails to <code>true</code>.
	 * 
	 * @see org.apache.tools.ant.taskdefs.ExecTask#setFailonerror(boolean)
	 * @see org.apache.tools.ant.taskdefs.ExecTask#setFailIfExecutionFails(boolean)
	 */
	public StepExec() {
		super();
		// stops the execution of ANT if error occurs
		super.setFailonerror(false);
		super.setFailIfExecutionFails(true);
	}

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
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.taskdefs.ExecTask#setResultProperty(java.lang.String)
	 */
	@Override
	public void setResultProperty(String resultProperty) {
		super.setResultProperty(resultProperty);
		this.resultProperty = resultProperty;
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
	 * Prepares the files required by ANT file using the data description, locks
	 * them, and prepares the right file name for GDG. Afterwards calls the
	 * executable command defined in the task.
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		StepsContainer.getInstance().setCurrent(this);
		if (resultProperty == null){
			setResultProperty(ReturnCodesContainer.getInstance().createKey(this));
		}
		int returnCode = Result.SUCCESS;
		// this boolean is necessary to understand if I have an exception
		// before calling the main class
		boolean isExecutionStarted = false;

		AntBatchSecurityManager batchSM = (AntBatchSecurityManager) System.getSecurityManager();
		batchSM.setInternalAction(true);
		// creates a list with all data description impl
		List<DataDescriptionImpl> ddList = null;

		try {
			// gets all data description requested by this task
			ddList = ImplementationsContainer.getInstance().getDataDescriptionsByItem(this);
			// if list of data description is empty, go to execute the command
			if (!ddList.isEmpty()) {

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
						addEnvVariable(ddImpl.getName(), dataset.getRealFile());
					}
				}

			}

			// calls super-method to execute the command configured into JCL
			batchSM.setInternalAction(false);
			// executes the program defined in JCL
			// setting the boolean to TRUE
			isExecutionStarted = true;
			super.execute();
		} catch (BuildException e1) {
			returnCode = Result.ERROR;
			throw e1;
		} catch (RemoteException e) {
			returnCode = Result.ERROR;
			throw new BuildException(e);
		} catch (IOException e) {
			returnCode = Result.ERROR;
			throw new BuildException(e);
		} finally {
			batchSM.setInternalAction(true);
			
			Object rcObject = PropertyHelper.getPropertyHelper(getProject()).getProperty(resultProperty);
			if (rcObject != null) {
				returnCode = Parser.parseInt(rcObject.toString(), Result.SUCCESS);
			}
			ReturnCodesContainer.getInstance().setReturnCode(this, returnCode);

			// finally and always must release the locks previously asked
			// checks datasets list
			if (ddList != null && !ddList.isEmpty()) {
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
					log(Message.ATTENTION_STRING);
					log(exceptions.toString());
				}
			}
			batchSM.setInternalAction(false);
		}
	}

	/**
	 * Add environment variable, changing the prefix to avoid to override other
	 * environment variables, about data description.<br>
	 * In this case, the MULTI datasets are not allowed. The risk is to have
	 * only the last of the list.
	 * 
	 * @see StepExec#DD_PREFIX
	 * @param key variable key, with prefix
	 * @param file file absolute path
	 */
	private void addEnvVariable(String key, File file) {
		// checks if parameters are null. if yes, return so no actions
		if (file == null || key == null) {
			return;
		}

		// creates new var, setting key and value
		Environment.Variable var = new Environment.Variable();
		var.setKey(DD_PREFIX + key);
		var.setValue(file.getAbsolutePath());

		// passes to super class where there is the container
		super.addEnv(var);
	}

}