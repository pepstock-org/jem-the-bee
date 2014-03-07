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
package org.pepstock.jem.node.listeners;

import java.util.Map.Entry;
import java.util.Set;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.MapStore;

/**
 * Has the goal to load all keys from database into Hazelcast maps in case of
 * data loss. It waits for 2 seconds for ending of migration of Hazelcast to
 * reload missing objects. Only missing object are loaded because is enough a
 * <code>containsKey</code> method on map to load form database if missing.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class DataLossHandler implements Runnable {

	private long lastMigrationCompleted = -1L;

	private boolean ready = true;

	private static final long DELAY = TimeUtils.SECOND * 2L;

	/**
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * @param ready the ready to set
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * @return the lastMigrationCompleted
	 */
	public long getLastMigrationCompleted() {
		return lastMigrationCompleted;
	}

	/**
	 * @param lastMigrationCompleted the lastMigrationCompleted to set
	 */
	public void setLastMigrationCompleted(long lastMigrationCompleted) {
		this.lastMigrationCompleted = lastMigrationCompleted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// is not ready
		setReady(false);
		try {
			while (true) {
				// wait for 1 sec to check if last migration is older
				// than 2 secs
				Thread.sleep(1000L);
				if (getLastMigrationCompleted() >= 0) {
					long diff = System.currentTimeMillis() - getLastMigrationCompleted();
					if ((diff) > DELAY) {
						// loads from DB all missing entries
						load();
						// ready again
						setReady(true);
						return;
					}
				}
			}
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Retrieves all keys from database if map has got mapstore
	 */
	private void load() {
		LogAppl.getInstance().emit(NodeMessage.JEMC219I);
		long start = System.currentTimeMillis();
		for (Entry<String, MapConfig> configs : Main.getHazelcast().getConfig().getMapConfigs().entrySet()) {
			String key = configs.getKey();
			if (!key.equalsIgnoreCase(Queues.JCL_CHECKING_MAP)) {
				MapStoreConfig mStoreConfig = configs.getValue().getMapStoreConfig();

				if (mStoreConfig != null && mStoreConfig.isEnabled()) {
					try {
						@SuppressWarnings({ "unchecked" })
						MapStore<String, Object> clazz = (MapStore<String, Object>) Class.forName(mStoreConfig.getClassName()).newInstance();
						Set<String> keys = clazz.loadAllKeys();
						LogAppl.getInstance().emit(NodeMessage.JEMC220I, key);
						for (String storeKey : keys) {
							try {
								Main.getHazelcast().getMap(key).lock(storeKey);
								Main.getHazelcast().getMap(key).containsKey(storeKey);
							} finally {
								Main.getHazelcast().getMap(key).unlock(storeKey);
							}
						}
					} catch (InstantiationException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC222E, e);
					} catch (IllegalAccessException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC222E, e);
					} catch (ClassNotFoundException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC222E, e);
					}
				}
			} else {
				// nop
				// JCL CHECKING is not recovered because
				// is not consistent reload queue jcl
			}
		}
		long end = System.currentTimeMillis() - start;
		LogAppl.getInstance().emit(NodeMessage.JEMC221I, end);
	}

}
