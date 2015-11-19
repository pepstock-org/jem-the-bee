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
package org.pepstock.jem.ant;

import java.util.List;

import org.apache.tools.ant.Project;
import org.pepstock.jem.ant.tasks.DataDescription;
import org.pepstock.jem.ant.tasks.Lock;

/**
 * Entity that contains all information about the step.<br> 
 * Is used when you want to lock resources or uses referbacks
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public interface DataDescriptionStep {
	
	/**
	 * Default order when you perform referbacks and order is not set 
	 */
	String DEFAULT_ID = "1";
	
	/**
	 * @return the name
	 */
	Project getProject();
	
	/**
	 * @return the name
	 */
	String getName();
	
	/**
	 * @return the target name of item
	 */
	String getTargetName();
	
	/**
	 * @return the task name of item
	 */
	String getTaskName();
	
	/**
	 * @return the dataDescriptions
	 */
	List<DataDescription> getDataDescriptions();
	
	/**
	 * Returns the list of locks
	 * 
	 * @return the list of locks
	 */
	List<Lock> getLocks();
	
	/**
	 * @return the id
	 */
	String getId();
	
	/**
	 * Sets the id
	 * @param id new id
	 */
	void setId(String id);
	
	/**
	 * @return the order
	 */
	int getOrder();
	
	/**
	 * Sets the order
	 * @param order order
	 */
	void setOrder(int order);

}