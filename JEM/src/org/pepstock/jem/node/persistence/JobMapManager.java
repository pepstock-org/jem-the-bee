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
 * Common manager to manage the persistence of jobs (on all queues) on the database, using the Mapstore interface
 * 
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
	 * Builds the object saving the Hazelcast map of jobs and the database manager
	 * with all JDBC calls to persist the jobs
	 * 
	 * @param queueName the Hazelcast map of jobs
	 * @param dbManager the database manager
	 * 
	 */
	public JobMapManager(String queueName, JobDBManager dbManager) {
		this.queueName = queueName;
		this.redoManager = new RedoManager(queueName);
		this.dbManager = dbManager;
		// extracts the SQL container
		// with all SQL to use
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
			// if here, it's not able to read from database
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
		// use collections of keys in string format, to create SQL
		// for IN statement, put ' and , on right position
		StringBuilder sb = new StringBuilder();
		// scans all JOBIDs 
		// creating the SQL statement
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
		// creates result instance
		Map<String, Job> jobs = null;
		try {
			// load job instance from table
			jobs = dbManager.getAllItems(sqlString);
			LogAppl.getInstance().emit(NodeMessage.JEMC048I, String.valueOf(jobs.size()), getQueueName());
		} catch (SQLException e) {
			// if here, it's not able to read from database
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
		// creates the result 
		Set<String> set = null;
		try {
			// loadAll keys from table
			set = dbManager.getAllKeys(sqlContainer.getGetAllKeysStatement());
			LogAppl.getInstance().emit(NodeMessage.JEMC045I, String.valueOf(set.size()), getQueueName());
		} catch (SQLException e) {
			// if here, it's not able to read from database
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
		// scans all job ids and
		// deletes the jobs
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
		// scans all job and
		// stores the jobs
		for (Job job : jobs.values()){
			store(job.getId(), job);
		}
	}

	/**
	 * Deletes the jobs by jobs ID. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param jobid job id to be deleted
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs
	 */
	void delete(String jobid, boolean exception) {
		try {
			// deletes the job in table
			dbManager.delete(sqlContainer.getDeleteStatement(), jobid);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (SQLException e) {
			// if exception, it throws the exception
			if (exception) {
				throw new MapStoreException(NodeMessage.JEMC043E, e);
			} else {
				// recovers the delete statement moving on redo ones 
				recoverDeleteStatement(jobid, e);
			}
		}
	}

	/**
	 * Changes the attributes of node when SQL statement
	 * ended correctly.
	 * <br>
	 * For this reason, it checks here 
	 * if the node was not operational because now it works.
	 * <br>
	 * And then applies all redo statements and changes the node
	 * status.
	 */
	private void checkNodeStatus(){
		// locks the node
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// if not operational
			if (!Main.getNode().isOperational()){
				// tries to apply the redo statements however
				RecoveryManager.getInstance().applyRedoStatements();
				// if here, it was able to delete jobs
				Main.getNode().setOperational(true);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				NodeInfoUtility.start();
				LogAppl.getInstance().emit(NodeMessage.JEMC172I);
			}
		} catch (Exception e) {
			// here I've got the exception
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
		} finally {
			// always unlock
			l.unlock();
		}
	}
	
	/**
	 * It recovers the delete statement saving the statement on the REDO ones.
	 * Also checks the status of the node because if it's up and running, this is
	 * wrong because it wasn't able to access to database.
	 * <br>
	 * For this reason, changes the status of the node.
	 * 
	 * @param jobid job id to delete
	 * @param e exception occurred
	 */
	private void recoverDeleteStatement(String jobid, Exception e){
		// locks node
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// gives the delete statement to redo manager
			// to save internally
			redoManager.delete(jobid);
			if (Main.getNode().isOperational()){
				// if operation, changes the status of the node
				// putting the node in drain
				// because the database is not reachable
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				Main.getNode().setOperational(false);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				NodeInfoUtility.drain();
				LogAppl.getInstance().emit(NodeMessage.JEMC173E);						
			}
		} finally {
			// always unlock
			l.unlock();
		}
	}
	
	/**
	 * Stores the job by jobs ID. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param jobid job id of job instance
	 * @param job job instance
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs 
	 */
	void store(String jobid, Job job, boolean exception) {
		try {
			// inserts the job in table
			dbManager.insert(sqlContainer.getInsertStatement(), job);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (SQLException e1) {
			// ignore
			LogAppl.getInstance().ignore(e1.getMessage(), e1);
			// I have an exception (it happens if the key already exists, so
			// update anyway
			tryToUpdate(job, exception);
		}
	}
	
	/**
	 * This method is called when an INSERT statement went wrong (because the key is already on database).
	 * <br>
	 * Here it tries to update the job object. If it doesn't work, it will recover the statement
	 * moving it on the REDO ones.
	 * 
	 * @param job job instance to be updated
	 * @param exception if the exception must be thrown or not.
	 */
	private void tryToUpdate(Job job,  boolean exception){
		// I have an exception (it happens if the key already exists, so
		// update anyway
		try {
			// updates the job in table
			dbManager.update(sqlContainer.getUpdateStatement(), job);
			// if exception, it throws the exception
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (SQLException e3) {
			// if exception, it throws the exception
			if (exception) {
				throw new MapStoreException(NodeMessage.JEMC043E, e3);
			} else {
				// recovers the store statement moving on redo ones
				recoverStoreStatement(job, e3);
			}
		}
	}
	/**
	 * It recovers the store statement saving the statement on the REDO ones.
	 * Also checks the status of the node because if it's up and running, this is
	 * wrong because it wasn't able to access to database.
	 * <br>
	 * For this reason, changes the status of the node.
	 * 
	 * @param job job instance to store
	 * @param e exception occurred
	 */
	private void recoverStoreStatement(Job job, Exception e){
		// gets node lock
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// gives the store statement to redo manager
			// to save internally
			redoManager.store(job);
			if (Main.getNode().isOperational()){
				// if operation, changes the status of the node
				// putting the node in drain
				// because the database is not reachable
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				Main.getNode().setOperational(false);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				NodeInfoUtility.drain();
				LogAppl.getInstance().emit(NodeMessage.JEMC173E);	
			}
		} finally {
			// always unlock
			l.unlock();
		}		
	}
	
	
}