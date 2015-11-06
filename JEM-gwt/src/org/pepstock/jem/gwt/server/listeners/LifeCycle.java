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
package org.pepstock.jem.gwt.server.listeners;

import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.security.JemCacheManager;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.GetEvictionInfo;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Member;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
abstract class LifeCycle implements LifecycleListener{

	/**
	 * Do nothing
	 */
	LifeCycle() {
	}

	void checkEviction(){
		try {
			Member member = SharedObjects.getInstance().getHazelcastClient().getCluster().getMembers().iterator().next();
			DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(new GetEvictionInfo(Queues.OUTPUT_QUEUE), member);
			boolean isEvicted =  task.getResult();
			SharedObjects.getInstance().getMapEvictionInfo().put(Queues.OUTPUT_QUEUE, isEvicted);
		} catch (ServiceMessageException e) {
			LogAppl.getInstance().emit(e.getMessageInterface(), e);
			SharedObjects.getInstance().getMapEvictionInfo().put(Queues.OUTPUT_QUEUE, false);
		}
	}

	/**
	 * This method was added to this class to centralized all operation relative
	 * to the client start up. Some operation need the hazelcast instance that
	 * cannot be available in the listener so we use this method
	 * 
	 * @param instance
	 */
	public void atInstantiation(HazelcastInstance instance) {
		SharedObjects.getInstance().setDataClusterAvailable(true);
		SharedObjects.getInstance().setHazelcastClient(instance);
		
		checkEviction();
		
		System.err.println(SharedObjects.getInstance().getMapEvictionInfo());
		
		JemCacheManager.updateJemCache();
	}
	
}
