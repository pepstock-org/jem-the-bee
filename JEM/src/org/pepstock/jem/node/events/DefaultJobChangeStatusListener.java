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
package org.pepstock.jem.node.events;

import java.util.Properties;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Default implementation of job life cycle listener.<br>
 * Logs the job names with the status.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DefaultJobChangeStatusListener implements JobLifecycleListener {

	/**
	 * Empty constructor
	 */
	public DefaultJobChangeStatusListener() {
	}

	/**
	 * Logs "in INPUT queue"
	 * 
	 * @param job job instance
	 */
	@Override
	public void queued(Job job) {
		LogAppl.getInstance().emit(NodeMessage.JEMC093I, job.getName());
	}

	/**
	 * Logs "is RUNNING"
	 * 
	 * @param job job instance
	 */
	@Override
	public void running(Job job) {
		LogAppl.getInstance().emit(NodeMessage.JEMC094I, job.getName());
	}

	/**
	 * Logs "is ended. RC="
	 * 
	 * @param job job instance
	 */
	@Override
	public void ended(Job job) {
		LogAppl.getInstance().emit(NodeMessage.JEMC095I, job.getName(),job.getResult().getReturnCode());
	}

	/**
	 * Not implemented
	 * 
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
		// do nothing
	}

}