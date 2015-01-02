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

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;

import com.hazelcast.core.MapStore;

/**
 * Persistent manager for CHECKING queue.<br>
 * It uses DBPoolManager instance to perform all sqls.<br>
 * It throws MapStoreException if the database manager has errors but Hazelcast
 * is not able to catch them, so it logs all errors.<br>
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class CheckingMapManager implements MapStore<Long, PreJob> {

	private PreJobDBManager dbManager = null;
	
	private SQLContainer sql = null;

	/**
	 * Construct the object instantiating getting DB manager
	 */
	public CheckingMapManager() {
		dbManager = PreJobDBManager.getInstance();
		sql = dbManager.getSqlContainer();
	}

	/**
	 * Loads pre job instance by pre job id passed by Hazelcast
	 * 
	 * @see com.hazelcast.core.MapLoader#load(java.lang.Object)
	 * @param jobid job id of job to load
	 */
	@Override
	public PreJob load(Long jobid) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		PreJob prejob = null;
		try {
			// load job instance from table
			prejob = dbManager.getItem(sql.getGetStatement(), jobid);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return prejob;
	}

	/**
	 * Loads all keys (pre job ids) at the starting of Hazelcast
	 * 
	 * @see com.hazelcast.core.MapLoader#loadAllKeys()
	 */
	@Override
	public Set<Long> loadAllKeys() {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		Set<Long> set = null;
		try {
			// loadAll keys from table
			set = dbManager.getAllKeys(sql.getGetAllKeysStatement());
			LogAppl.getInstance().emit(NodeMessage.JEMC045I, String.valueOf(set.size()), Queues.JCL_CHECKING_QUEUE);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return set;
	}

	/**
	 * Deletes a pre job instance from queue by pre job id
	 * 
	 * @see com.hazelcast.core.MapStore#delete(java.lang.Object)
	 */
	@Override
	public void delete(Long prejobid) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		try {
			// deletes the job in table
			dbManager.delete(sql.getDeleteStatement(), prejobid);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
	}

	/**
	 * Stores a pre job instance in queue. If already exists, it updates it.
	 * 
	 * @see com.hazelcast.core.MapStore#store(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void store(Long prejobid, PreJob prejob) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		try {
			// inserts the job in table
			dbManager.insert(sql.getInsertStatement(), prejobid, prejob);
		} catch (SQLException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);

			// I have an exception (it happens if the key already exists, so
			// update anyway
			try {
				// updates the job in table
				dbManager.update(sql.getUpdateStatement(), prejobid, prejob);
			} catch (SQLException e1) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e1);
				throw new MapStoreException(NodeMessage.JEMC043E, e1);
			}
		}
	}

	/**
	 * In case of asynchronous configuration
	 * 
	 * @see com.hazelcast.core.MapStore#storeAll(java.util.Map)
	 */
	@Override
	public void storeAll(Map<Long, PreJob> map) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		for (Entry<Long, PreJob> entry : map.entrySet()){
			PreJob prejob = entry.getValue();
			Long key = entry.getKey();
			try {
				// inserts the job in table
				dbManager.insert(sql.getInsertStatement(), key, prejob);
			} catch (SQLException e) {
				// ignore
				LogAppl.getInstance().ignore(e.getMessage(), e);

				// I have an exception (it happens if the key already exists, so
				// update anyway
				try {
					// updates the job in table
					dbManager.update(sql.getUpdateStatement(), key, prejob);
				} catch (SQLException e1) {
					LogAppl.getInstance().emit(NodeMessage.JEMC043E, e1);
					throw new MapStoreException(NodeMessage.JEMC043E, e1);
				}
			}
		}
	}

	/**
	 * In case of asynchronous configuration
	 * 
	 * @see com.hazelcast.core.MapStore#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection<Long> ids) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		for (Long id : ids){
			try {
				// deletes the job in table
				dbManager.delete(sql.getDeleteStatement(), id);
			} catch (SQLException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				throw new MapStoreException(NodeMessage.JEMC043E, e);
			}
		}
	}

	/**
	 * Loads all pre jobs saved, by a list of keys.
	 * 
	 * @see com.hazelcast.core.MapLoader#loadAll(java.util.Collection)
	 * @param collaction of keys to load
	 * @return maps with all jobs
	 */
	@Override
	public Map<Long, PreJob> loadAll(Collection<Long> collection) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		// use collections of keys in strign format, to create SQL
		// for IN statement, put ' and , on right position
		StringBuilder sb = new StringBuilder();
		for (Long jobid: collection){
			sb.append("'").append(jobid).append("'").append(", ");
		}
		String inStmt = StringUtils.substringBeforeLast(sb.toString(), ",");
		// formats SQL to get all jobs by keys
		String sqlString = MessageFormat.format(sql.getGetAllStatement(), inStmt);

		Map<Long, PreJob> prejobs = null;
		try {
			// load job instance from table
			prejobs = dbManager.getAllItems(sqlString);
			LogAppl.getInstance().emit(NodeMessage.JEMC048I, String.valueOf(prejobs.size()), Queues.JCL_CHECKING_QUEUE);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return prejobs;
	}
}