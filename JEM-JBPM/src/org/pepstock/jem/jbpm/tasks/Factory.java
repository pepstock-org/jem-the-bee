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
	 * 
	 * @param taskDescription
	 * @return
	 * @throws JBpmException 
	 */
	public static final Task createTask(TaskDescription taskDescription) throws JBpmException{
		Task task = new Task();
		task.setId(taskDescription.getId());
		task.setName(taskDescription.getName());
		
		IoSpecification ioSpecification = taskDescription.getIoSpecification();
		
		if (ioSpecification != null){
			for (DataInput dataInput : ioSpecification.getDataInputs()){
				if (ioSpecification.getAssociations().containsKey(dataInput.getId())){
					DataInputAssociation association = ioSpecification.getAssociations().get(dataInput.getId());
					if (dataInput.getName().startsWith(JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX)){
						DataDescription dd = createDataDescription(dataInput, association);
						task.getDataDescriptions().add(dd);
					} else if (dataInput.getName().startsWith(JBpmKeys.JBPM_DATA_SOURCE_PREFIX)){
						DataSource dSrc = createDataSource(dataInput, association);
						task.getDataSources().add(dSrc);
					} else if (dataInput.getName().equalsIgnoreCase(JBpmKeys.JBPM_LOCK_KEY)){
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
	 * 
	 * @param dataInput
	 * @param association
	 * @return
	 * @throws JBpmException
	 */
	private static DataDescription createDataDescription(DataInput dataInput, DataInputAssociation association) throws JBpmException{
		String ddName = StringUtils.substringAfter(dataInput.getName(), JBpmKeys.JBPM_DATA_DESCRIPTION_PREFIX);
		if (ddName == null || ddName.trim().length() == 0){
			throw new JBpmException(JBpmMessage.JEMM005E);
		}
		DataDescription dd = new DataDescription();
		dd.setName(ddName.trim());
		String toParse = association.getAssignmentFrom();
		
		ValueParser.loadDataDescription(dd, toParse);
		return dd;
	}
	
	/**
	 * 
	 * @param dataInput
	 * @param association
	 * @return
	 * @throws JBpmException
	 */
	private static DataSource createDataSource(DataInput dataInput, DataInputAssociation association) throws JBpmException{
		String dSrcName = StringUtils.substringAfter(dataInput.getName(), JBpmKeys.JBPM_DATA_SOURCE_PREFIX);
		if (dSrcName == null || dSrcName.trim().length() == 0){
			throw new JBpmException(JBpmMessage.JEMM021E);
		}
		DataSource dSrc = new DataSource();
		dSrc.setName(dSrcName.trim());

		String toParse = association.getAssignmentFrom();
		
		ValueParser.loadDataSource(dSrc, toParse);
		return dSrc;
	}

	/**
	 * 
	 * @param dataInput
	 * @param association
	 * @return
	 * @throws JBpmException
	 */
	private static List<Lock> createLocks(DataInput dataInput, DataInputAssociation association) throws JBpmException{
		List<Lock> locks = new ArrayList<Lock>();
		String toParse = association.getAssignmentFrom();
		if (toParse != null && toParse.trim().length() > 0){
			String[] parsed = toParse.split(STRING_SEPARATOR);
			for (String name : parsed){
				Lock lock = new Lock();
				lock.setName(name.trim());
				locks.add(lock);
			}
			
		}
		return locks;
	}
}
