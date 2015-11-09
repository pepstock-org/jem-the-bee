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
package org.pepstock.jem.commands;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.util.DefaultClientLifeCycle;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.IMap;

/**
 * It handles the connection error from client and the cluster of Hazelcast.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class HazelcastClientLifeCycler extends DefaultClientLifeCycle {
	
	private AbstractConnectedClusterSubmit submitter =null;
	
	private HazelcastClient client = null;
	
	private boolean isClosed = false;

	/**
	 * It's built with the submitter and  its HC client instance
	 * @param submitter submitter instance
	 * @param client HC client instance
	 */
	public HazelcastClientLifeCycler(AbstractConnectedClusterSubmit submitter, HazelcastClient client) {
		this.submitter = submitter;
		this.client = client;
	}

	
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.DefaultClientLifeCycle#clientConnectionOpened()
	 */
	@Override
	public void clientConnectionOpened() {
		// if the connection if opened
		// it checks if the the job is ended (in the meanwhile of its reconnection)
		Job savedJob = submitter.getJob();
		if (savedJob != null){
			  IMap<String, Job> output = client.getMap(Queues.OUTPUT_QUEUE);
			  if (output.containsKey(savedJob.getId())){
					// if here after the restart, the job ended
					// therefore try to close the client because
				    // the topic couldn't receive the info
					submitter.clientDisconnect(output.get(savedJob.getId()));
			  }
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.DefaultClientLifeCycle#shuttedDown()
	 */
	@Override
	public void shuttedDown() {
		// if here no way....
		// the client lost the JEM cluster, therefore 
		// shutdown and ends in error
		if (!isClosed){
			isClosed = true;
			submitter.clientDisconnect();
		}
	}
}
