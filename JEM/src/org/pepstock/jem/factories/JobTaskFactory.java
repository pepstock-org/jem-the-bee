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
package org.pepstock.jem.factories;

import java.io.Serializable;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.tasks.JobTask;

/**
 * This interface must be implemented to create the right task which will be
 * launched in new process. The jobtask must take care to create command line,
 * parameters, environment variable, etc. to execute the job.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface JobTaskFactory extends Serializable {
	
	/**
	 * Property KEY to define a SU behavior
	 */
	String SWITCH_USER_KEY = "jem.switch.user"; 
	

	/**
	 * Called to create a job task object, by a job object. It creates command
	 * line and prepares the environment to execute the job
	 * 
	 * @see org.pepstock.jem.node.tasks.JobTask#JobTask(Job)
	 * @param job job object to execute
	 * @return job task object, which represents the container of job to execute
	 */
	JobTask createJobTask(Job job);

}