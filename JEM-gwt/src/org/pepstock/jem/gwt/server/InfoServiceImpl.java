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
package org.pepstock.jem.gwt.server;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.services.InfoService;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.services.DefaultService;
import org.pepstock.jem.gwt.server.services.InternalsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.About;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.clients.Count;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

/**
 * Is GWT server service which can provide general information to use on headers and
 * logo management. <br>
 * This service doesn't check any authentication or authorization because is provides 
 * common information.<br>
 * It is used by the login page (before login), so it can check any user.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class InfoServiceImpl extends DefaultManager implements InfoService {
	
	// static key for url logo
	// must be put in web.xml, in context definition by init parameter 
	private static final String JEM_LOGO_URL = "jem.logo.url";

	// static key for link for logo
	// must be put in web.xml, in context definition by init parameter
	private static final String JEM_LOGO_LINK = "jem.logo.link";

	private static final long serialVersionUID = 1L;
	
	private final transient InternalsManager internalsManager = new InternalsManager();

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.InfoService#getEnvironmentInformation()
	 */
	@Override
	public String[] getEnvironmentInformation() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// creates array
		String[] infos = new String[Indexes.INFO_SIZE.getIndex()];
		try {
			HazelcastInstance localMember = SharedObjects.getInstance().getHazelcastClient();
			// Name of JEM GROUP
			infos[Indexes.NAME.getIndex()] = SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName();

			infos[Indexes.NODES_COUNT.getIndex()] = localMember.getCluster().getMembers().size() +" / "+getClients();

			// Exec job count
			IMap<String, Job> jobs = localMember.getMap(Queues.RUNNING_QUEUE);
			infos[Indexes.EXECUTION_JOB_COUNT.getIndex()] = String.valueOf(jobs.size());

			// Uptime
			// gets the coordinator of JEM cluster (the oldest one)
			Member oldest = localMember.getCluster().getMembers().iterator().next();
			IMap<String, NodeInfo> nodes = localMember.getMap(Queues.NODES_MAP);

			// to get the uptime
			// uses the started time information of JEM node info
			// try locks by uuid
			if (nodes.tryLock(oldest.getUuid(), 10, TimeUnit.SECONDS)) {
				try {
					// if coordinator is not on map (mustn't be!!)
					// set not available
					NodeInfo oldestInfo = nodes.get(oldest.getUuid());
					if (oldestInfo != null){
						infos[Indexes.STARTED_TIME.getIndex()] = String.valueOf(oldestInfo.getStartedTime().getTime());	
					} else {
						infos[Indexes.STARTED_TIME.getIndex()] = "N/A";
					}
				} finally {
					// unlocks always the key
					nodes.unlock(oldest.getUuid());
				}
			} else {
				infos[Indexes.STARTED_TIME.getIndex()] = "N/A";
			}
			
			// gets the current time. 
			// this is helpful because ould be some time differences 
			// between client and servers
			infos[Indexes.CURRENT_TIME.getIndex()] = String.valueOf(System.currentTimeMillis());
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG043E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
		return infos;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.InfoService#getLogoURL()
	 */
    @Override
    public String[] getLogoURL() throws JemException {
		// gets servlet context
		ServletContext context = super.getServletContext();

		// gets logo and its link from context
		String logoUrl = context.getInitParameter(JEM_LOGO_URL);
		String linkUrl = context.getInitParameter(JEM_LOGO_LINK);
		
		// if they are defined
		// returns them, otherwise null
		if (logoUrl != null){
			String[] infos = new String[Indexes.INFO_LOGO_SIZE.getIndex()];
			infos[Indexes.URL.getIndex()] = logoUrl;
			infos[Indexes.LINK.getIndex()] = linkUrl;
			return infos;
		}
	    return new String[0];
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.InfoService#getAbout()
	 */
	@Override
	public About getAbout() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		try {
			return internalsManager.getAbout();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG043E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @return the number of clients connected to the cluster using a distributed task
	 * @throws JemException 
	 */
	private int getClients() throws JemException {
		DefaultService ds = new DefaultService();
		DistributedTaskExecutor<Integer> task = new DistributedTaskExecutor<Integer>(new Count(), ds.getMember());
		return task.getResult();
		
	}
}
