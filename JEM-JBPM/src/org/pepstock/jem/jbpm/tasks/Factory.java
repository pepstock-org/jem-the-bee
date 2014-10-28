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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pepstock.jem.jbpm.JBpmException;
import org.pepstock.jem.jbpm.JBpmKeys;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.Task;
import org.pepstock.jem.jbpm.xml.DataInput;
import org.pepstock.jem.jbpm.xml.DataInputAssociation;
import org.pepstock.jem.jbpm.xml.IoSpecification;
import org.pepstock.jem.jbpm.xml.TaskDescription;

/**
 * Factory to create a TASk starting from TaskDescription, loading all data descriptions, data sources and locks.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class Factory {

	private static final String STRING_SEPARATOR = ",";

	/**
	 * To avoid any instantiation
	 */
	private Factory() {
		
	}
	
	/**
	 * Creates a task using a task description, loading all data descriptions, data sources and locks.
	 * @param taskDescription task description, created from XML
	 * @return a task, represents a JEM work item
	 * @throws JBpmException if any error occurs
	 */
	public static final Task createTask(TaskDescription taskDescription) throws JBpmException{
		// creates a task, setting ID and name
		Task task = new Task();
		task.setId(taskDescription.getId());
		task.setName(taskDescription.getName());
		
		// gets IO Specification
		// if null, no JEM entities to load
		IoSpecification ioSpecification = taskDescription.getIoSpecification();
		
		if (ioSpecification != null){
			// scans all data inputs
			for (DataInput dataInput : ioSpecification.getDataInputs()){
				// checks if associations contains the data input
				// if no, could be a error on BPMN file
				if (ioSpecification.getAssociations().containsKey(dataInput.getId())){
					// gets data associations
					DataInputAssociation association = ioSpecification.getAssociations().get(dataInput.getId());
					// checks if is a data description
					if (dataInput.getName().startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX)){
						DataDescription dd = createDataDescription(dataInput, association);
						task.getDataDescriptions().add(dd);
					} else if (dataInput.getName().startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX)){
						// checks if is a data source
						DataSource dSrc = createDataSource(dataInput, association);
						task.getDataSources().add(dSrc);
					} else if (dataInput.getName().equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY)){
						// checks if are locks
						List<Lock> locks = createLocks(dataInput, association);
						if (!locks.isEmpty()){
							task.getLocks().addAll(locks);
						}
					}
				}
			}
		}
		return task;
	}

	/**
	 * Creates a data description using parameters defined in the task
	 * @param dataInput data Input of the task
	 * @param association association of the task
	 * @return data description created using the parameters of the task
	 * @throws JBpmException if any error occurs
	 */
	private static DataDescription createDataDescription(DataInput dataInput, DataInputAssociation association) throws JBpmException{
		// extracts DD NAME from parameter key
		String ddName = StringUtils.substringAfter(dataInput.getName(), JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX);
		// if not a valid string, exception
		if (ddName == null || ddName.trim().length() == 0){
			throw new JBpmException(JBpmMessage.JEMM005E);
		}
		//creates data description
		DataDescription dd = new DataDescription();
		dd.setName(ddName.trim());
		String toParse = association.getAssignmentFrom();
		// parses data description value
		ValueParser.loadDataDescription(dd, toParse);
		return dd;
	}
	
	/**
	 * Creates a data source using parameters defined in the task
	 * @param dataInput data Input of the task
	 * @param association association of the task
	 * @return data source created using the parameters of the task
	 * @throws JBpmException if any error occurs
	 */
	private static DataSource createDataSource(DataInput dataInput, DataInputAssociation association) throws JBpmException{
		// extracts DATA SOURCE NAME from parameter key
		String dSrcName = StringUtils.substringAfter(dataInput.getName(), JBpmKeys.JBPM_DATA_SOURCE_PREFIX);
		// if not a valid string, exception
		if (dSrcName == null || dSrcName.trim().length() == 0){
			throw new JBpmException(JBpmMessage.JEMM021E);
		}
		//creates data source
		DataSource dSrc = new DataSource();
		dSrc.setName(dSrcName.trim());
		String toParse = association.getAssignmentFrom();
		// parses data description value
		ValueParser.loadDataSource(dSrc, toParse);
		return dSrc;
	}

	/**
	 * Creates a list of locks using parameters defined in the task
	 * @param dataInput data Input of the task
	 * @param association association of the task
	 * @return list of locks created using the parameters of the task
	 * @throws JBpmException if any error occurs
	 */
	private static List<Lock> createLocks(DataInput dataInput, DataInputAssociation association) throws JBpmException{
		// creates the list
		List<Lock> locks = new ArrayList<Lock>();
		String toParse = association.getAssignmentFrom();
		// parser all value, comma separated
		if (toParse != null && toParse.trim().length() > 0){
			String[] parsed = toParse.split(STRING_SEPARATOR);
			for (String name : parsed){
				// creates lock
				Lock lock = new Lock();
				lock.setName(name.trim());
				locks.add(lock);
			}
			
		}
		return locks;
	}
}
