/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.SubmitMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.hazelcast.ConfigProvider;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.util.Numbers;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.IQueue;

/**
 * Manages all SQL statements towards the database to persist the PreJob in CHECKING queues.<br>
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class PreJobMapManager implements Recoverable, ConfigProvider{ 

	private static PreJobMapManager INSTANCE = null;
	
	private DatabaseManager<PreJob> dbManager = null;
	
	private RedoManager<PreJob> redoManager = new RedoManager<PreJob>(Queues.JCL_CHECKING_QUEUE);
	
	/**
	 * Calls super class to create the connection
	 * 
	 * @throws Exception occurs if an error
	 */
	private PreJobMapManager(DatabaseManager<PreJob> dbManager){
		this.dbManager = dbManager;
	}

	/**
	 * Creates the instance of map store if not already initialized
	 * @param dbManager database manger to use for persistence
	 * @return the map store
	 */
	public static synchronized PreJobMapManager createInstance(DatabaseManager<PreJob> dbManager){
		if (INSTANCE == null){
			INSTANCE = new PreJobMapManager(dbManager);
		}
		return INSTANCE;
	}
	
	/**
	 * Returns Hazelcast configuration
	 * @return Hazelcast configuration
	 */
	public QueueConfig getQueueConfig(){
		QueueConfig config = new QueueConfig();
		config.setName(Queues.JCL_CHECKING_QUEUE);
		
		config.setAsyncBackupCount(0);
		config.setBackupCount(MapConfig.MAX_BACKUP_COUNT);
		
		config.setMaxSize(Numbers.N_1000);
		config.setStatisticsEnabled(true);
		
		return config;
	}
	

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.hazelcast.ConfigProvider#getMapConfig()
	 */
	@Override
	public MapConfig getMapConfig() {
		return null;
	}
	
	/**
	 * Queries on DB to get the estimated size of map
	 * @return total amount of bytes 
	 * @throws DatabaseException if any DB error occurs
	 */
	public long getSize() throws DatabaseException{
		return dbManager.getSize();
	}
	
	/**
	 * Checks if persistence objects are available and creates that if not
	 * @throws DatabaseException if any DB 
	 */
	public void checkAndCreate() throws DatabaseException {
		dbManager.checkAndCreate();
	}
	
	/**
	 * Is a static method (typical of a singleton) that returns the unique
	 * instance of JobDBManager.<br>
	 * You must ONLY one instance of this per JVM instance.<br>
	 * 
	 * @return manager instance
	 * @throws Exception
	 */
	public static PreJobMapManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Deletes a pre job instance from queue 
	 * @param preJob pre job instance to submit
	 * @throws MessageException if any error occurs
	 */
	public void delete(PreJob preJob) {
		delete(preJob.getJob().getId(), false);
	}

	/**
	 * Stores a pre job instance in queue. 
	 * @param preJob pre job instance to submit 
	 * @throws MessageException if any error occurs
	 */
	public void store(PreJob preJob) throws MessageException {
		try {
			// inserts the job in table
			dbManager.insert(preJob);
		} catch (DatabaseException e) {
			throw new MessageException(NodeMessage.JEMC043E, e);
		}
	}
	/**
	 * Loads all pre jobs saved, by a list of keys.
	 * 
	 * @throws MessageException if any error occurs
	 */
	public void loadAll() throws MessageException {
		try {
			// puts the pre job in a queue for validating and moving to right QUEUE
			// (input if is correct, output if is wrong)
			IQueue<PreJob> jclCheckingQueue = Main.getHazelcast().getQueue(Queues.JCL_CHECKING_QUEUE);
			// load job instance from table
			Map<String, PreJob> prejobs = dbManager.getAllItems();
			if (prejobs != null){
				LogAppl.getInstance().emit(NodeMessage.JEMC048I, String.valueOf(prejobs.size()), Queues.JCL_CHECKING_QUEUE);
				for (PreJob prejob : prejobs.values()){
					jclCheckingQueue.put(prejob);
				}
			}
		} catch (DatabaseException e) {
			throw new MessageException(NodeMessage.JEMC043E, e);
		} catch (Exception e) {
			throw new MessageException(SubmitMessage.JEMW003E, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.Recoverable#recover(org.pepstock.jem.node.persistence.RedoStatement)
	 */
	@Override
	public void recover(RedoStatement statement) throws JemException {
		try {
			// if action is delete, it calls the delete method
			if (statement.getAction().equalsIgnoreCase(RedoStatement.DELETE)) {
				delete(statement.getEntityId(), true);
			}
		} catch (Exception e) {
			throw new JemException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.Recoverable#check()
	 */
	@Override
	public void check() throws JemException {
		try {
			dbManager.getSize();
		} catch (DatabaseException e) {
			throw new JemException(e);
		}
	}

	/**
	 * Deletes the jobs by jobs ID. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param id job id to be deleted
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs
	 */
	void delete(String id, boolean exception) {
		try {
			// deletes the job in table
			dbManager.delete(id);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (DatabaseException e) {
			// if exception, it throws the exception
			if (exception) {
				throw new MessageRuntimeException(NodeMessage.JEMC043E, e);
			} else {
				// recovers the delete statement moving on redo ones 
				recoverDeleteStatement(id, e);
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
	 * @param id job id to delete
	 * @param e exception occurred
	 */
	private void recoverDeleteStatement(String id, Exception e){
		// if not redo, return
		if (redoManager == null){
			return;
		}
		// locks node
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// gives the delete statement to redo manager
			// to save internally
			redoManager.delete(id);
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