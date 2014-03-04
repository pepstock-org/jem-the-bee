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
package org.pepstock.jem.node.executors.jobs;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import org.pepstock.jem.Job;
import org.pepstock.jem.JobSystemActivity;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Extracts cpu and memory usage of job in execution.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GetJobSystemActivity extends DefaultExecutor<JobSystemActivity>{

	private static final long serialVersionUID = 1L;
	
	private Job job = null;
	
	/**
	 * Constructs the command using job 
	 * 
	 * @param job job instance used to get system activity
	 */
	public GetJobSystemActivity(Job job) {
		this.job = job;
	}

	/**
	 * Calls with reflection a class to extract processes tree and cpu and memory used
	 *
	 * @return system activity object with all used resources by the job
	 * @throws ExecutorException occurs if errors
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public JobSystemActivity execute() throws ExecutorException {
		try {
			// I need the reflection to avoid to distribute 
			// Sigar in web site
			Class clazz = Class.forName("org.pepstock.jem.node.system.GetJobSystemActivity");
			Constructor c = clazz.getConstructor(Job.class);
			Callable<JobSystemActivity> da = (Callable<JobSystemActivity>) c.newInstance(job);
			return da.call();
		} catch (Exception e) {
			throw new ExecutorException(NodeMessage.JEMC243E, e, job);
			
		}
	}
}