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
package org.pepstock.jem.node.listeners;

import org.pepstock.jem.node.Main;

import com.hazelcast.core.Member;
import com.hazelcast.partition.MigrationEvent;
import com.hazelcast.partition.MigrationListener;

/**
 * Listen when a data migration in Hazelcast is started. Checks if OLD member is null because in this case
 * some data loss is possible.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class NodeMigrationListener implements MigrationListener {
	
	private final Object lock= new Object();
	
	private Thread dataLossHandlerThread = null;
	
	private DataLossHandler dataLossHandler = null;

	/* (non-Javadoc)
	 * @see com.hazelcast.partition.MigrationListener#migrationCompleted(com.hazelcast.partition.MigrationEvent)
	 */
	@Override
	public void migrationCompleted(MigrationEvent event) {
		// checks if new member where moved data is up (in the cluster)
		boolean newIsUp = false;
		for (Member member : Main.getHazelcast().getCluster().getMembers()){
			if (event.getNewOwner().getUuid().equalsIgnoreCase(member.getUuid())){
				newIsUp = true;
				break;
			}
		}
		// checks if old member is null, data loss is sure!		
		if ((event.getOldOwner() == null || !newIsUp) && Main.IS_COORDINATOR.get()){			
			synchronized (lock) {
				// creates the runnable only once
				if (dataLossHandler == null){
					dataLossHandler = new DataLossHandler();
				}
				dataLossHandler.setLastMigrationCompleted(System.currentTimeMillis());

				// creates a thread for recovery 
				if (dataLossHandlerThread == null){
					dataLossHandlerThread = new Thread(dataLossHandler);
					dataLossHandlerThread.start();
				} else if (dataLossHandler.isReady()){
					dataLossHandlerThread.interrupt();
					dataLossHandlerThread = null;
					dataLossHandlerThread = new Thread(dataLossHandler);
					dataLossHandlerThread.start();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.partition.MigrationListener#migrationFailed(com.hazelcast.partition.MigrationEvent)
	 */
	@Override
	public void migrationFailed(MigrationEvent event) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.partition.MigrationListener#migrationStarted(com.hazelcast.partition.MigrationEvent)
	 */
	@Override
	public void migrationStarted(MigrationEvent event) {
		// do nothing
	}

}
