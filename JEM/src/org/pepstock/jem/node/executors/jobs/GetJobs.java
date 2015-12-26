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
package org.pepstock.jem.node.executors.jobs;

import java.util.ArrayList;
import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.node.persistence.OutputMapManager;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.predicates.JobPredicate;

import com.hazelcast.core.IMap;

/**
 * Returns the list of JOBs from database if evicted
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class GetJobs extends DefaultExecutor<Collection<Job>>{

	private static final long serialVersionUID = 1L;
	
	private String mapName= null;
	
	private Filter filter = null;
	
	/**
	 * Creates the executors using a filter
	 * @param mapName map name where perform query
	 * @param filter filter to apply to database
	 */
	public GetJobs(String mapName, Filter filter) {
		this.mapName = mapName;
		this.filter = filter;
	}

	/**
	 * Calls an executor to extract all JCL factories, type and description
	 * 
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public Collection<Job> execute() throws ExecutorException {
		try {
			if (Queues.OUTPUT_QUEUE.equalsIgnoreCase(mapName)){
				return OutputMapManager.getInstance().loadByFilter(filter);
			} else {
				IMap<String, Job> jobs = Main.getHazelcast().getMap(mapName);
				JobPredicate predicate = new JobPredicate(filter);
				return new ArrayList<Job>(jobs.values(predicate));
			}
		} catch (DatabaseException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			throw new ExecutorException(NodeMessage.JEMC295E, e);
		}
	}
	
}