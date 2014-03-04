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
package org.pepstock.jem.node.persistence;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;

import com.hazelcast.core.MapStore;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class JobMapManager implements MapStore<String, Job> {
	
	private String queueName = null; 
	
	private JobDBManager dbManager = null;
	
	private SQLContainer sqlContainer = null;
	
	private RedoManager redoManager = null;

	/**
	 * @param queueName 
	 * @param dbManager 
	 * 
	 */
	public JobMapManager(String queueName, JobDBManager dbManager) {
		this.queueName = queueName;
		this.redoManager = new RedoManager(queueName);
		this.dbManager = dbManager;
		sqlContainer = dbManager.getSqlContainer();
	}
	
	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}


	/**
	 * @return the redoManager
	 */
	public RedoManager getRedoManager() {
		return redoManager;
	}

	/**
	 * @param redoManager the redoManager to set
	 */
	public void setRedoManager(RedoManager redoManager) {
		this.redoManager = redoManager;
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapLoader#load(java.lang.Object)
	 */
	@Override
	public Job load(String jobid){
		// check if I have the database manager, otherwise log error and
		// exception
		Job job = null;
		try {
			// load job instance from table
			job = dbManager.getItem(sqlContainer.getGetStatement(), jobid);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return job;
		
	}
	
	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapLoader#loadAll(java.util.Collection)
	 */
	@Override
	public Map<String, Job> loadAll(Collection<String> jobids){
		// use collections of keys in strign format, to create SQL
		// for IN statement, put ' and , on right position
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = jobids.iterator();
		for (;;){
		    String jobid = iter.next();
		    sb.append("'").append(jobid).append("'");
		    if (!iter.hasNext()){
		    	break;
		    }
		    sb.append(", ");
		}
		// formats SQL to get all jobs by keys 
		String sqlString = MessageFormat.format(sqlContainer.getGetAllStatement(), sb.toString());
		
		Map<String, Job> jobs = null;
		try {
			// load job instance from table
			jobs = dbManager.getAllItems(sqlString);
			LogAppl.getInstance().emit(NodeMessage.JEMC048I, String.valueOf(jobs.size()), getQueueName());
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return jobs;
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapLoader#loadAllKeys()
	 */
	@Override
	public Set<String> loadAllKeys(){
		Set<String> set = null;
		try {
			// loadAll keys from table
			set = dbManager.getAllKeys(sqlContainer.getGetAllKeysStatement());
			LogAppl.getInstance().emit(NodeMessage.JEMC045I, String.valueOf(set.size()), getQueueName());
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapStore#delete(java.lang.Object)
	 */
	@Override
	public void delete(String jobid) {
		delete(jobid, false);
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapStore#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection<String> ids) {
		for (String id : ids){
			delete(id);
		}
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapStore#store(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void store(String jobid, Job job) {
		store(jobid, job, false);
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MapStore#storeAll(java.util.Map)
	 */
	@Override
	public void storeAll(Map<String, Job> jobs) {
		for (Job job : jobs.values()){
			store(job.getId(), job);
		}
	}
	

	/**
	 * 
	 * @param jobid
	 * @param exception
	 */
	public void delete(String jobid, boolean exception) {
		try {
			// deletes the job in table
			dbManager.delete(sqlContainer.getDeleteStatement(), jobid);
			if (!exception){
				Lock l = Main.getNode().getLock();
				try {
					l.lock();
					if (!Main.getNode().isOperational()){
						RecoveryManager.getInstance().applyRedoStatements();
						Main.getNode().setOperational(true);
						NodeInfoUtility.storeNodeInfo(Main.getNode());
						NodeInfoUtility.start();
						LogAppl.getInstance().emit(NodeMessage.JEMC172I);
					}
				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				} finally {
					l.unlock();
				}
			}
		} catch (SQLException e) {
			if (exception) {
				throw new MapStoreException(NodeMessage.JEMC043E, e);
			} else {
				Lock l = Main.getNode().getLock();
				try {
					l.lock();
					redoManager.delete(jobid);
					if (Main.getNode().isOperational()){
						LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
						Main.getNode().setOperational(false);
						NodeInfoUtility.storeNodeInfo(Main.getNode());
						NodeInfoUtility.drain();
						LogAppl.getInstance().emit(NodeMessage.JEMC173E);						
					}
				} finally {
					l.unlock();
				}
			}
		}
	}


	/**
	 * 
	 * @param jobid
	 * @param job
	 * @param exception
	 */
	public void store(String jobid, Job job, boolean exception) {
		try {
			// inserts the job in table
			dbManager.insert(sqlContainer.getInsertStatement(), job);
			if (!exception){
				Lock l = Main.getNode().getLock();
				try {
					l.lock();
					if (!Main.getNode().isOperational()){
						RecoveryManager.getInstance().applyRedoStatements();
						Main.getNode().setOperational(true);
						NodeInfoUtility.storeNodeInfo(Main.getNode());
						NodeInfoUtility.start();
						LogAppl.getInstance().emit(NodeMessage.JEMC172I);
					}
				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				} finally {
					l.unlock();
				}
			}
		} catch (SQLException e1) {
			// ignore
			LogAppl.getInstance().ignore(e1.getMessage(), e1);

			// I have an exception (it happens if the key already exists, so
			// update anyway
			try {
				// updates the job in table
				dbManager.update(sqlContainer.getUpdateStatement(), job);
				if (!exception){
					Lock l = Main.getNode().getLock();
					try {
						l.lock();
						if (!Main.getNode().isOperational()){
							RecoveryManager.getInstance().applyRedoStatements();
							Main.getNode().setOperational(true);
							NodeInfoUtility.storeNodeInfo(Main.getNode());
							NodeInfoUtility.start();
							LogAppl.getInstance().emit(NodeMessage.JEMC172I);
						}
					} catch (Exception e2) {
						LogAppl.getInstance().emit(NodeMessage.JEMC043E, e2);
					} finally {
						l.unlock();
					}
				}
			} catch (SQLException e3) {
				if (exception) {
					throw new MapStoreException(NodeMessage.JEMC043E, e3);
				} else {
					Lock l = Main.getNode().getLock();
					try {
						l.lock();
						redoManager.store(job);
						
						if (Main.getNode().isOperational()){
							LogAppl.getInstance().emit(NodeMessage.JEMC043E, e3);
							Main.getNode().setOperational(false);
							NodeInfoUtility.storeNodeInfo(Main.getNode());
							NodeInfoUtility.drain();
							LogAppl.getInstance().emit(NodeMessage.JEMC173E);	
						}
					
					} finally {
						l.unlock();
					}
				}
			}
		}
		
	}
}